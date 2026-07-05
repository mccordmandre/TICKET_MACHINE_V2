# Guia de Discussão — Ticket Machine (VHDL + Kotlin)

> Documento de estudo para a **discussão/defesa**. HW (VHDL) e SW (Kotlin) entrelaçados, com código real (cada trecho identifica o ficheiro), **perguntas espalhadas** ao longo do texto (❓ com resposta em baixo), e **mini-fluxos** do que acontece.
> Código de referência: `TICKET_MACHINE_GRUPO/KOTLIN/TICKET_MACHINE` e `.../VHDL` (a pasta `OLD` é antiga — ignorar).
> Diagramas: ver os PDFs do enunciado/datasheets do prof.

## Princípio-chave

O **Kotlin foi validado contra a simul do prof** (`TicketMachine.jar` + `.simul`), que emula o hardware ideal. Logo o Kotlin e o mapeamento de pinos são a "verdade"; os erros aparecem no **VHDL** e em **como ele encaixa nos pinos**. É a resposta a "porque funciona na simul mas não na placa?".

---

## As ligações com os pinos (ler com calma — é a base de tudo)

O software e o hardware comunicam por **16 fios**: 8 que o software **lê** (entradas, `I0`–`I7`) e 8 que o software **escreve** (saídas, `O0`–`O7`). Esses 16 fios são o **UsbPort**.

**O software nunca mexe num fio "pelo nome" — mexe por um número (máscara).** Cada fio é um bit dentro de um byte:

```
bit:   7   6   5   4   3   2   1   0
       O7  O6  O5  O4  O3  O2  O1  O0     (saídas: UsbPort.write)
       I7  I6  I5  I4  I3  I2  I1  I0     (entradas: UsbPort.read)
```

Uma **máscara** é um número que tem `1` só no bit que interessa. Exemplos:
- `0x01` = `0000_0001` → bit 0 (O0/I0)
- `0x02` = `0000_0010` → bit 1
- `0x10` = `0001_0000` → bit 4
- `0x80` = `1000_0000` → bit 7

O `HAL.kt` usa estas máscaras para tocar num bit sem estragar os outros:
```kotlin
// HAL.kt
fun setBits(mask: Int) { lastOutput = lastOutput or  mask;      UsbPort.write(lastOutput) }  // põe a 1
fun clrBits(mask: Int) { lastOutput = lastOutput and mask.inv(); UsbPort.write(lastOutput) } // põe a 0
fun isBit (mask: Int): Boolean = (UsbPort.read() and mask) == mask   // lê: o bit está a 1?
```

**Onde cada sinal está ligado** (esta tabela é o "contrato" entre HW e SW — decorá-la ajuda muito):

| Fio | Sinal | Máscara | Quem escreve | Quem lê |
|---|---|---|---|---|
| I0–I2 | `cid` (código da moeda) | `0x07` | moedeiro (HW) | `CoinAcceptor` |
| I3 | `coin` (entrou moeda) | `0x08` | moedeiro | `CoinAcceptor.checkCoin` |
| I4 | `Fn` (bilhete pronto) | `0x10` | Ticket Dispenser | `TicketDispenser` |
| I6 | `M` (chave manutenção) | `0x40` | switch | `App`/`Maintenance` |
| I7 | `TXd` (bit da tecla) | `0x80` | Key Transmitter | `KBD` |
| O0 | `SDX` (dados série) | `0x01` | `SerialEmitter` | PE_LCD / PE_TD |
| O1 | `SCLK` (relógio série) | `0x02` | `SerialEmitter` | PE_LCD / PE_TD |
| O2 | `LCDsel` (fala com LCD) | `0x04` | `SerialEmitter` | PE_LCD |
| O3 | `TDsel` (fala com TD) | `0x08` | `SerialEmitter` | PE_TD |
| O4 / O5 / O6 | `accept` / `eject` / `collect` | `0x10`/`0x20`/`0x40` | `CoinAcceptor` | moedeiro |
| O7 | `TXclk` (relógio do teclado) | `0x80` | `KBD` | Key Transmitter |

Repara: I5 não é usado. E o mesmo valor de máscara (ex. `0x10`) significa coisas diferentes conforme seja **entrada** (I4=`Fn`) ou **saída** (O4=`accept`) — porque `read` e `write` são registos diferentes.

❓ **Q1.** O software faz `HAL.isBit(0x08)`. Que fio está a ler, e o que significa esse fio estar a 1?
> Lê o **I3** (`coin`). A 1 = o moedeiro recebeu uma moeda. É o que o `CoinAcceptor.checkCoin()` faz.

❓ **Q2 (reescrever).** Escreve uma função `HAL.toggleBit(mask)` que **inverte** o(s) bit(s) da máscara nas saídas.
> ```kotlin
> // HAL.kt
> fun toggleBit(mask: Int) { lastOutput = lastOutput xor mask; UsbPort.write(lastOutput) }
> ```
> O `xor` com `1` inverte; com `0` mantém — por isso só os bits da máscara mudam.

---

## Fluxo completo de uma venda (HW ↔ SW)

> A secção que amarra tudo. Segue uma compra do início ao fim.

**1. Escolher estação.** Carregas numa tecla → `Key Decode` deteta → `Ring Buffer` guarda → `Key Transmitter` fica pronto. No SW, `KBD.getKeySerial()` gera pulsos de `TXclk`(O7) e lê `TXd`(I7), reconstruindo o código; a `App.browseStations()` navega. O que aparece no LCD passa por `App → TUI → LCD → SerialEmitter → PE_LCD → ecrã`.

**2. Pagar.** Inseres moeda → `coin`(I3)=1, `cid`(I0-2)=valor. No SW, `checkCoin` deteta, `readCoin` lê, `coinAccept` faz o handshake. A `App` soma e mostra o que falta.

**3. Imprimir.** Crédito suficiente → `collect` (moedas para o cofre) → `TicketDispenser` monta a trama e envia → `PE_TD` recebe → `TICKET_DISPENSER` mostra nos HEX e gera `Fn`(I4) → `waitTicket` completa o handshake → `Stations.sellTicket` conta e grava.

❓ **QF1.** Quando carregas numa tecla, quantos blocos de **hardware** ela atravessa antes de chegar ao `KBD` (software)?
> Três: **Key Decode** (deteta) → **Ring Buffer** (guarda) → **Key Transmitter** (envia em série). Só depois é que o `KBD` a lê pelo `TXd`.

❓ **QF2.** Na fase de pagamento, porque é que o loop **não pode bloquear** à espera de uma tecla?
> Porque tem de ir **vendo as moedas ao mesmo tempo** que espera as teclas (`*`/`#`). Se bloqueasse em `readKey`, não detetava moedas durante esse tempo. Por isso usa `readKey(100)` (timeout curto) e faz polling do moedeiro a cada volta.

❓ **QF3.** Assim que há crédito suficiente, o que acontece primeiro: recolher as moedas ou mandar imprimir?
> Primeiro **`collect`** (recolhe as moedas ao cofre e persiste), só depois `activatePrintingTicket`. Faz sentido: a venda só se confirma quando o dinheiro está garantido.

❓ **QF4.** Onde é que o crédito inserido "vive" durante a compra, antes de ir para o cofre?
> Numa lista temporária no `CoinAcceptor` (`inserted_coins`), somada pela `App` a cada moeda. Só passa ao `CoinDeposit` (cofre) no `collect`, ou é devolvido no `eject` se cancelares.

---

## Paragem 1 — UsbPort (a fronteira)

```vhdl
-- UsbPort.vhd
ENTITY UsbPort IS
    PORT ( inputPort  : IN  STD_LOGIC_VECTOR(7 DOWNTO 0);    -- I0..I7 (HW escreve, SW lê)
           outputPort : OUT STD_LOGIC_VECTOR(7 DOWNTO 0) );  -- O0..O7 (SW escreve, HW lê)
END UsbPort;
```

❓ **Q3.** Porque é que se chama `inputPort` se é o hardware que o preenche?
> Os nomes são do ponto de vista do **software**: "input" = o que o software recebe/lê. O hardware é que o alimenta.

❓ **Q3a.** A que está ligado o **I5**?
> A **nada** — é o único bit de entrada não usado. As entradas necessárias são `cid` (I0-2), `coin` (I3), `Fn` (I4), `M` (I6) e `TXd` (I7); sobrou o I5. O mapeamento é o definido pela simul do prof, que deixou o I5 livre. (Não há nenhuma razão funcional para ser o I5 em concreto — foi só o que não foi preciso.)

❓ **Q3b.** Se precisasses de mais um sinal do hardware para o software (ex.: um botão extra), que pino usarias?
> O **I5**, por ser o único de entrada que está livre. Terias de o ligar no top-level (`usb_in(5) <= ...`) e ler no software com `HAL.isBit(0x20)`.

❓ **Q3c.** Podia o `TXclk` (que o software gera) estar numa **entrada** em vez de numa saída?
> Não. O `TXclk` é o relógio que o **software gera** para o Key Transmitter — logo tem de ser uma **saída** (O7). As entradas são só o que o hardware põe para o software ler.

---

## Paragem 2 — Keyboard Reader (a cadeia)

Três blocos em série (`Keyboard_Reader.vhd`):
```vhdl
-- Keyboard_Reader.vhd
U1 : KeyDecode      port map (... Kval => Kval_DAV, K => K_D, ...);
U2 : RingBuffer     port map (... DAV => Kval_DAV, D => K_D,
                                  CTS => KBfree_CTS, Wreg => Wreg_Load, Q => Q_D ...);
U3 : KeyTransmitter port map (... Load => Wreg_Load, D => Q_D,
                                  KBfree => KBfree_CTS, TXd => TXd ...);
```
Key Decode deteta → Ring Buffer guarda (FIFO) → Key Transmitter entrega ao software em série. Os sinais mudam de nome mas é o mesmo fio (`Kval`→`DAV`, `Wreg`→`Load`).

**Mini-fluxo (uma tecla):** carregas → Key Decode põe `Kval=1` e o código em `K` → o Ring Buffer vê `DAV=1`, escreve e responde `DAC=1` → mais tarde, quando o Key Transmitter está livre (`CTS=1`), o Ring Buffer lê a palavra e ativa `Wreg`/`Load` → o Key Transmitter envia-a bit a bit em `TXd`.

❓ **Q4.** Para que serve o Ring Buffer no meio?
> Para não perder teclas se o software demora a ler. Guarda-as por ordem (FIFO, 16). Sem ele, uma tecla nova esmagaria a anterior.

---

## Paragem 3 — Key Decode (Key Scan + Key Control)

**Key Scan** varre o teclado (um contador percorre linhas/colunas, um MUX lê):
```vhdl
-- KeyScan.vhd
U1 : MUX4to1 port map( A(0)=>L(0), A(1)=>L(1), A(2)=>L(2), A(3)=>L(3),
                       S => counterValue(1 downto 0), Y => f_mux);
U2 : KD_Cont port map( CE => f_ce, CLK => CLK, Reset => Reset, Q => counterValue);
U3 : Dec2to4 port map( S1 => counterValue(3), S0 => counterValue(2), o => decValue);
f_ce   <= Kscan and f_mux;   -- (correção do bug: para no índice certo)
Kpress <= not f_mux;
K      <= counterValue;
```

**Key Control** — FSM que garante 1 código por pressão:
```vhdl
-- Keycontrol.vhd
Kscan <= '1' when CurrentState = STATE_SCANNING   else '0';
Kval  <= '1' when CurrentState = STATE_REGISTERING else '0';
-- SCANNING (varre) -> REGISTERING (código estável + Kval até Kack) -> WAITING_RELEASE (espera largar)
```

❓ **Q5.** Em que estado o `Kval` está a 1, e porquê?
> Em REGISTERING — é aí que o código está estável em `K`; o `Kval` avisa "há código válido" até o consumidor confirmar com `Kack`.

❓ **Q6 (bug real).** Na placa, cada tecla saía com o código **+1** e repetia-se sozinha. Porquê e como resolveram?
> O contador avançava um passo a mais antes de congelar: ao detetar `Kpress`, a FSM transita no flanco seguinte, mas o contador (com `CE=Kscan`) ainda contava nesse flanco → parava no índice seguinte (o +1). E em WAITING_RELEASE lia a coluna errada → auto-repeat. **Correção:** `f_ce <= Kscan and f_mux` (para no mesmo ciclo em que deteta a tecla). Um bug, dois sintomas.

### Perguntas sobre o Key Control

❓ **Q6a.** Descreve as 3 transições da FSM do Key Control.
> **SCANNING → REGISTERING** quando `Kack='0' and Kpress='1'` (detetou tecla e o consumidor ainda não confirmou). **REGISTERING → WAITING_RELEASE** quando `Kack='1'` (o consumidor leu o código). **WAITING_RELEASE → SCANNING** quando `Kpress='0'` (a tecla foi largada).

❓ **Q6b.** Para que serve o estado **WAITING_RELEASE**? O que aconteceria sem ele?
> Garante **um código por pressão**: só volta a varrer quando a tecla é largada. Sem ele, enquanto segurasses a tecla, a FSM voltava logo a SCANNING e registava a mesma tecla vezes sem conta (auto-repeat indesejado).

❓ **Q6c.** Porque é que o `Kscan` está a 1 só em SCANNING (e alimenta o `CE` do contador)?
> Para o contador **só varrer** enquanto procura tecla. Assim que deteta (`Kpress`), sai de SCANNING → `Kscan=0` → o contador para e o código fica congelado durante o registo.

### Perguntas sobre a Versão III (Key Scan) do relatório

❓ **Q6d.** Como funciona a **Versão III** do Key Scan e porque é melhor que a Versão I (a vossa)?
> A Versão III usa um **priority encoder (PENC)** que lê as **4 linhas de uma coluna ao mesmo tempo** e codifica logo qual está premida (`Y1Y0`), com o `GS` (group select) a indicar "há tecla premida". Só precisa de varrer **4 colunas** (em vez de percorrer as 16 combinações linha×coluna uma a uma), e o `Kpress` sai direto do `GS` sem o AND final. É mais rápida e mais limpa.

❓ **Q6e.** Na Versão I, quantos ciclos de clock são precisos para percorrer o teclado todo? E na Versão III?
> Versão I: **16** (um por cada combinação linha/coluna, já que o contador é de 4 bits). Versão III: **4** (só as colunas — o PENC resolve a linha em paralelo). É a razão de a III ser considerada melhoria no vosso "Trabalho Futuro".

---

## Paragem 4 — Ring Buffer (FIFO de 16) + detalhe

```vhdl
-- RingBuffer.vhd
U1 : RB_Control port map (DAV=>DAV, CTS=>CTS, Full=>F_full, Empty=>F_empty,
                          Wr=>F_Wr, selPG=>F_selPG, Wreg=>Wreg, DAC=>DAC,
                          incPut=>F_incPut, incGet=>F_incGet);
U2 : MA_Control port map (putget=>F_selPG, incPut=>F_incPut, incGet=>F_incGet,
                          A=>MA_RAM, full=>F_full, empty=>F_empty);
U3 : RAM        port map (address=>MA_RAM, wr=>F_Wr, din=>D, dout=>Q);
```

**A RAM** tem um só `address` → só acede a uma posição por ciclo. **Dois ponteiros:** `PUT` (onde escrever, anda com `incPut`) e `GET` (de onde ler, anda com `incGet`). Como a RAM só aceita um endereço, um **MUX** (dentro do `MA_Control`) escolhe qual ponteiro vai ao `address`; o seletor é o `selPG`/`putget`.

**Incrementar ≠ escolher:** `incPut`/`incGet` **movem** os ponteiros; `selPG` **escolhe** qual usar agora (escrita→PUT, leitura→GET).

**Full vs Empty:** quando `PUT == GET`, é ambíguo (cheio ou vazio?). O `Equal` só diz "iguais"; o **Mini_Control** (FSM `ST_EMPTY/ST_MID/ST_FULL`) desfaz a ambiguidade pela **última operação**: última=escrita → FULL; última=leitura → EMPTY.

**Coordenação (RB_Control):** ST_Write → `Wr=1`, `selPG=0` (escreve no PUT); ST_Read → `Wr=0`, `selPG=1` (lê do GET). `DAC=1` no Write avisa o produtor; `Wreg=1` no Read entrega ao consumidor (`Load`).

❓ **Q7.** Se `PUT == GET`, como sabes se está cheio ou vazio?
> Pela última operação, memorizada pelo `Mini_Control`: a encher → FULL; a esvaziar → EMPTY. O `Equal` sozinho não chega.

❓ **Q8 (bug real).** O buffer lia em ciclo infinito — nunca dava `Empty`. Porquê?
> O `Mini_Control` recebia o `putget` **atrasado 1 ciclo** (por um registo), mas o `selPG=1` só dura o `ST_Read` (1 ciclo). Com leituras rápidas, o `putget=1` chegava tarde e a condição `ST_MID→ST_EMPTY` nunca disparava. **Correção:** ligar o `putget` **direto** ao `Mini_Control` (tirar o registo de atraso).

### Perguntas sobre o Mini_Control (gera Full/Empty)

❓ **Q8a.** Descreve a FSM do `Mini_Control` (estados e transições).
> 3 estados: **ST_EMPTY**, **ST_MID**, **ST_FULL**. **EMPTY → MID** quando os ponteiros deixam de ser iguais (escreveste algo). **MID → FULL** se voltam a igualar E a última operação foi **escrita**. **MID → EMPTY** se voltam a igualar E a última foi **leitura**. **FULL → MID** quando deixam de ser iguais (leste algo). `Empty=1` só em EMPTY; `Full=1` só em FULL.

❓ **Q8b.** Que dois sinais entram no `Mini_Control` e o que representam?
> O **`equal`** (do comparador `Equal`): os dois ponteiros PUT e GET estão na mesma posição? E o **`putget`**: qual foi a última operação (escrita ou leitura). Juntos desfazem a ambiguidade do `PUT==GET`.

❓ **Q8c.** Porque é que o `Full` só interessa à escrita e o `Empty` só à leitura?
> Não faz sentido **escrever** quando está cheio (esmagarias uma tecla ainda não lida) → o RB_Control só escreve se `Full=0`. Não faz sentido **ler** quando está vazio (não há nada) → só lê se `Empty=0`.

❓ **Q8d.** O que é o `Equal` e o que faria se, em vez de igualdade, precisasses de saber **quantos** elementos há no buffer?
> O `Equal` é um comparador de 4 bits (`1` se PUT==GET). Para saber a **quantidade** não bastariam os estados EMPTY/MID/FULL — precisarias de **subtrair** os dois ponteiros (`PUT − GET`, com módulo 16), o que exigiria um subtrator, não só um comparador.

❓ **Q8e.** Para que serve o `Wreg` (que vai para o `Load` do Key Transmitter)?
> É o sinal de **entrega ao consumidor**: quando o Ring Buffer lê uma palavra (`ST_Read`), põe-na em `Q` e ativa `Wreg`, que chega ao `Load` do Key Transmitter e o faz registar a palavra a transmitir. O par simétrico é o `DAC` (no Write), que avisa o produtor que a tecla foi aceite.

---

## Paragem 5 — Key Transmitter (envio série)

Envia 4 bits em série: start → bit0..bit3 → stop, ao ritmo de `TXclk`.
```vhdl
-- KeyTransmitter.vhd
U2 : MUX8to1 port map ( A(0)=>inv, A(1)=>'1', A(5 downto 2)=>Hrg_Mux,
                        A(6)=>'0', A(7)=>'1', S=>Cont_MuxTC, Y=>TXd);
-- KtControl.vhd:  WAITING_LOAD -> SENDING quando Load='1'; KBfree=1 em WAITING_LOAD
```
Um contador (`S`) percorre as entradas do MUX, montando a trama. O `A(7)='1'` é o stop bit.

❓ **Q9.** O `KBfree` está a 1 em que estado, e o que significa para o Ring Buffer?
> Em WAITING_LOAD — significa "estou livre, podes entregar-me uma palavra". Liga ao `CTS` do Ring Buffer.

---

## Paragem 6 — PE_LCD e o protocolo do LCD (explicado)

**O que é o protocolo do LCD.** O software quer escrever no LCD, mas não tem 10 fios para o ligar — só tem os 3 fios série (SDX, SCLK, LCDsel). Então **manda a informação bit a bit** e o PE_LCD remonta-a e entrega ao LCD real.

**A trama tem 10 bits:**
```
bit 0 = RS   (0 = comando, 1 = dado/caractere)
bits 1..8 = os 8 bits de dados/comando
bit 9 = E    (o "enable" que faz o LCD registar a informação)
```

**Como o software monta a trama** (`LCD.kt`):
```kotlin
// LCD.kt
const val E = 0x200   // bit 9
private fun writeByteSerial(rs: Boolean, data: Int) {
    val rsBit = if (rs) 1 else 0
    val base = (data shl 1) or rsBit          // desloca os dados p/ bits 1-8, RS no bit 0
    SerialEmitter.send(Peripheral.LCD, base or E)  // 1ª emissão: E = 1
    SerialEmitter.send(Peripheral.LCD, base)       // 2ª emissão: E = 0
}
```

**Porque duas emissões (E=1 depois E=0)?** O LCD real (controlador HD44780) só "aceita" a informação na **descida do `E`** (flanco descendente). Por isso o software envia a mesma palavra duas vezes: primeiro com `E=1` (prepara), depois com `E=0` (regista). É como um "clique": carregar e largar.

**O que é o RS?** Diz ao LCD se os 8 bits são um **comando** (`RS=0`, ex.: "limpa o ecrã", "posiciona o cursor") ou um **caractere** a mostrar (`RS=1`, ex.: a letra 'A'). Por isso há `writeCMD` (RS=0) e `writeDATA` (RS=1):
```kotlin
// LCD.kt
private fun writeCMD (data: Int) = writeByteSerial(false, data)  // RS=0
private fun writeDATA(data: Int) = writeByteSerial(true,  data)  // RS=1
```

**O hardware (PE_LCD)** recebe a trama e fatia-a de volta:
```vhdl
-- PE_LCD.vhd
U1 : PE_LCD_Serial_Receiver port map (
        SCLK=>SCLK, reset=>reset, SS=>invLCDsel, SDX=>SDX,
        D(8 downto 1)=>D, D(0)=>RS, D(9)=>E);   -- desfatia: RS, 8 dados, E
invLCDsel <= not LCDsel;
```
Dentro do Serial Receiver há um **Shift Register** (recolhe os bits um a um) e um **Hold Register** (copia a palavra completa só no fim, para o LCD não ver os estados intermédios do shift).

❓ **Q10.** Porque é que a mesma palavra é enviada duas vezes ao LCD?
> Para criar o pulso do `E`: 1ª vez com `E=1`, 2ª com `E=0`. O LCD regista a informação no **flanco descendente** do `E`.

❓ **Q11.** Porque é que o Serial Receiver precisa do Hold Register além do Shift Register?
> Enquanto o shift recebe os bits, a sua saída muda a cada `SCLK` e passaria lixo intermédio ao LCD. O Hold copia a palavra completa só no fim e mantém-na estável.

❓ **Q12 (reescrever).** Escreve um `LCD.writeAt(line, col, text)` que posiciona o cursor e escreve.
> ```kotlin
> // LCD.kt (usa cursor + write já existentes)
> fun writeAt(line: Int, col: Int, text: String) { cursor(line, col); write(text) }
> ```

---

## Paragem 7 — PE_TD (Port Expander Ticket Dispenser)

Igual ao PE_LCD, mas a palavra de 10 bits é fatiada nos campos do bilhete:
```vhdl
-- PE_TD.vhd
U1 : PE_TD_Serial_Receiver port map (
        SCLK=>SCLK, reset=>reset, SS=>invTDsel, SDX=>SDX,
        D(8 downto 5)=>D, D(0)=>RT, D(9)=>PRT, D(4 downto 1)=>O);
```
RT=bit0, PRT=bit9, origem `O` nos bits 1-4, destino `D` nos bits 5-8.

No software, o `TicketDispenser.kt` monta a trama e faz o handshake:
```kotlin
// TicketDispenser.kt
lastBits = (destination shl 1) or (origin shl 5) or rt   // destino 1-4, origem 5-8
SerialEmitter.send(Peripheral.TICKET, lastBits or PRT)   // PRT=bit9
fun waitTicket() {
    while (!HAL.isBit(TDdone_MASK)) {}      // espera Fn=1 (bilhete pronto)
    SerialEmitter.send(Peripheral.TICKET, lastBits)  // baixa Prt (mantém dados)
    while (HAL.isBit(TDdone_MASK)) {}        // espera Fn=0
}
```

❓ **Q13.** Descreve o handshake de impressão.
> `Prt=1` → espera `Fn=1` → `Prt=0` (mantendo os dados) → espera `Fn=0`. Baixar o `Prt` só depois do `Fn` evita apagar o bilhete cedo demais.

---

## Paragem 8 — Top-level (`Ticket_Machine.vhd`)

Junta os blocos, o UsbPort, um divisor de clock e o Ticket Dispenser dos HEX:
```vhdl
-- Ticket_Machine.vhd
U2 : CLKDIV          port map (clk_in => CLK, clk_out => CLK_signal);
U3 : Keyboard_Reader port map (CLK => CLK_signal, ... TXd => usb_in(7), TXclk => usb_out(7));
U4 : PE_LCD          port map (... SCLK => usb_out(1), SDX => usb_out(0), LCDsel => usb_out(2) ...);
U5 : PE_TD           port map (... TDsel => usb_out(3) ..., Prt=>PE_TD_TD(9), ...);
U6 : TICKET_DISPENSER port map (... Fn => usb_in(4), HEX0..HEX5 => HEX0..HEX5);
```
O `CLKDIV` abranda o clock **só do Keyboard Reader** (o LCD/PE_TD são clocados pelo `SCLK` que vem do software).

❓ **Q14 (bug real).** Na placa, "o KBD não corria". Porquê?
> O `CLKDIV` estava a ~1 Hz — o teclado andava lento demais e o software (que lê em tempo real) nunca recebia dados no `TXd`. O LCD/PE_TD funcionavam porque são clocados pelo `SCLK` do software, não pelo `CLK_signal`. Solução: baixar o divisor.

---

## Software — HAL

Só o `HAL` toca no `UsbPort`. Guarda `lastOutput` porque o software não relê as saídas (ver secção dos pinos).

❓ **Q15.** Porque não dá para o software fazer `if (O1 == 1)` para saber se pôs o SCLK a 1?
> Porque `UsbPort.read()` lê as **entradas** (I), não as saídas (O). Para saber o que pôs nas saídas, o HAL guarda `lastOutput`.

---

## Software — SerialEmitter

Envia tramas de 10 bits para o LCD **e** o TD, partilhando `SDX`(O0)/`SCLK`(O1); escolhe o destino baixando `nLCDsel`(O2) ou `nTDsel`(O3).
```kotlin
// SerialEmitter.kt
fun send(addr: Peripheral, data: Int) {
    val SEL = if (addr == Peripheral.LCD) nLCDsel_MASK else nTDsel_MASK
    HAL.clrBits(SEL)                                   // baixa o select (início da trama)
    for (n in 0 until 10) {
        HAL.clrBits(SCLK_MASK)
        HAL.writeBits(SDX_MASK, (data shr n) and 1)    // bit n para o SDX (LSB-first)
        HAL.setBits(SCLK_MASK)                         // sobe SCLK -> HW amostra o bit
    }
    HAL.clrBits(SCLK_MASK)
    HAL.setBits(SEL)                                   // sobe o select (fim -> latch no Hold)
}
```

❓ **Q16.** Como é que o LCD e o TD não se baralham, se partilham SDX e SCLK?
> Só um select está baixo de cada vez (`nLCDsel` ou `nTDsel`). O recetor cujo select está alto ignora os dados.

❓ **Q17 (reescrever).** E se o prof quisesse enviar **MSB-first** em vez de LSB-first?
> Trocar o índice do bit: `HAL.writeBits(SDX_MASK, (data shr (9 - n)) and 1)`. Assim o bit 9 sai primeiro e o bit 0 por último.

---

## Software — KBD

Lê a tecla em série (Paragem 2). O `getKeySerial` gera pulsos de `TXclk`, lê os 4 dados em `i in 1..4`, e **valida** os bits fixos:
```kotlin
// KBD.kt
for (i in 0 until 7) {
    HAL.setBits(TXCLKMASK)
    if (i in 1..4 && HAL.isBit(RXDMASK)) bits = bits or (1 shl (i-1))  // dados
    if (i == 5 && HAL.isBit(RXDMASK))  { HAL.clrBits(TXCLKMASK); return NONE.toChar() } // devia ser 0
    if (i == 6 && !HAL.isBit(RXDMASK)) { HAL.clrBits(TXCLKMASK); return NONE.toChar() } // stop devia ser 1
    HAL.clrBits(TXCLKMASK)
}
character = teclado[bits]
```

❓ **Q18.** Para que serve validar os bits `i==5` e `i==6`?
> Para detetar trama desalinhada. Se os bits fixos (0 no i=5, stop=1 no i=6) não baterem, devolve `NONE` em vez de uma tecla errada.

---

## Software — TUI (a camada de interface)

A `TUI.kt` fica **entre a `App` e os módulos LCD/KBD**. A `App` não escreve pixéis nem lê pinos — pede à TUI coisas de alto nível ("escreve centrado", "lê uma tecla com timeout"). Isto separa a *lógica* (App) da *apresentação* (TUI).

```kotlin
// TUI.kt
fun init() { LCD.init(); KBD.init() }
fun clear() = LCD.clear()
fun writeCentered(line: Int, text: String) = writeInCursor(line, (LCD.COLS - text.count())/2, text)
fun writeSides(line: Int, l: String, r: String) { writeInCursor(line,0,l); writeInCursor(line, LCD.COLS-r.count(), r) }
fun writemenuHome(title: String, left: String, right: String) { writeCentered(0,title); writeSides(1,left,right) }

fun readKey(timeout: Int): Char? {                 // ESPERA até timeout; null se nada
    val key = KBD.waitKey(timeout.toLong())
    return if (key == KBD.NONE.toChar()) null else key
}
fun getKey(): Char = KBD.getKey()                  // NÃO espera; NONE se nada agora
```

**Diferença crucial `readKey` vs `getKey`:** o `readKey(timeout)` **bloqueia** até haver tecla ou passar o tempo (devolve `null` no timeout) — usa-se quando queres esperar pela escolha do utilizador. O `getKey()` devolve **já** o que houver (ou NONE) — usa-se em polling, quando não podes bloquear (ex.: enquanto lês moedas).

**Mini-fluxo (mostrar uma estação):** `App.showStation(idx)` → `TUI.writemenuHome(nome, "idx↑↓", "preço€")` → `TUI.writeCentered/writeSides` → `LCD.cursor` + `LCD.write` → `SerialEmitter.send` → PE_LCD → ecrã.

❓ **Q19.** Quando usarias `readKey` e quando `getKey`?
> `readKey(timeout)` quando queres **esperar** por uma tecla (menu, seleção). `getKey()` quando **não podes bloquear** e só queres saber se há tecla agora (ex.: no loop de pagamento, onde tens de ir vendo moedas ao mesmo tempo).

❓ **Q20 (reescrever).** Escreve um `TUI.confirm(msg)` que mostra `msg` + "*-Yes other-No" e devolve `true` se o utilizador carregar em `*` (timeout 5s → false).
> ```kotlin
> // TUI.kt
> fun confirm(msg: String): Boolean {
>     clear(); writeCentered(0, msg); writeSides(1, "*-Yes", "other-No")
>     return readKey(5000) == '*'
> }
> ```

---

## Software — CoinAcceptor

```kotlin
// CoinAcceptor.kt
fun checkCoin(): Boolean = HAL.isBit(COIN_MASK)                 // I3 a 1?
fun readCoin(): Int? { if (!checkCoin()) return null
    val v = coinArray[HAL.readBits(COIN_ID_MASK)]; inserted_coins.add(v); return v }  // cid -> valor
fun coinAccept() { if (checkCoin()) {
    HAL.setBits(ACCEPT_MASK)          // pulsa accept (O4)
    while (checkCoin()) {}            // espera o moedeiro retirar a moeda (coin baixa)
    HAL.clrBits(ACCEPT_MASK) } }
```

❓ **Q21.** Porque é que o `coinAccept()` espera o `checkCoin()` ficar false?
> É o handshake: só retira o `accept` quando o moedeiro confirma que a moeda saiu (`coin` baixa). Sem isto, a mesma moeda seria lida em loop e o crédito disparava.

---

## Software — CoinDeposit / Stations / FileAccess

`CoinDeposit` = cofre (`CoinDeposit.txt`, `VALOR;Nº`). `Stations` = estações (`stations.txt`, `PREÇO;VENDIDOS;NOME`). `FileAccess` = ler/escrever ficheiros, partilhado.
```kotlin
// Stations.kt
init { load("stations.txt") }                       // carrega no arranque
fun load(f: String) { stationsList.clear()
    for (line in FileAccess.readLines(f)) { val p = line.split(";")
        stationsList.add(Station(p[0].toInt(), p[1].toInt(), p[2])) } }
fun sellTicket(idx: Int) { stationsList[idx].soldTickets++ }
fun saveStations() = FileAccess.writeLines("stations.txt",
        stationsList.map { "${it.price};${it.soldTickets};${it.name}" })
```

❓ **Q22.** Quando é que os ficheiros são lidos e gravados?
> Lidos no arranque (o `init{}` de cada objeto chama `load`). Gravados após cada venda e no reset/desligar da manutenção.

---

## Software — TicketDispenser

Ver Paragem 7 (monta a trama + handshake `Prt`/`Fn`).

---

## Software — App (o controlo) e o flicker

A `App.kt` é a máquina de estados de alto nível:
`idleDisplay` (ecrã inicial) → `browseStations` (escolher com dígitos/A/B) → `purchase` (pagar) → `processTicket` (imprimir). A chave `M` (`HAL.isBit(0x40)`) desvia para o `Maintenance`.

### O flicker no `purchase` (explicado passo a passo)

**O sintoma:** durante o pagamento, o ecrã piscava sem parar.

**A causa:** o `purchase` está num `while` que corre muito depressa (o `readKey(100)` só espera 100 ms). O código **reescrevia o LCD a cada volta**, mesmo quando nada tinha mudado:
```kotlin
// ANTES (má versão) — reescreve sempre
while (true) {
    ...
    TUI.clear()                                    // <- limpa o ecrã TODAS as voltas
    TUI.writemenuHome(nome, tipo, "${remaining}€") // <- e reescreve
    TUI.readKey(100)
}
```
O LCD é **série e lento** (cada caractere é uma trama de 10 bits enviada bit a bit). Limpar+reescrever ~10 vezes por segundo faz o ecrã nunca estabilizar → **flicker**.

**A solução:** guardar o que já está no ecrã (`lastRemaining`, `lastRoundTrip`) **fora** do loop, e só reescrever quando algo **muda de verdade**:
```kotlin
// App.kt (DEPOIS — só redesenha quando muda)
fun purchase(idx: Int) {
    var roundTrip = false
    var lastRemaining = -1          // guarda o valor mostrado da última vez
    var lastRoundTrip = !roundTrip  // valor impossível -> força desenhar a 1ª vez
    while (true) {
        val price = if (roundTrip) Stations.getPrice(idx) * 2 else Stations.getPrice(idx)
        if (CoinAcceptor.checkCoin()) { CoinAcceptor.readCoin(); CoinAcceptor.coinAccept() }
        val inserted  = CoinAcceptor.inserted_coins.sum()
        val remaining = price - inserted
        if (inserted >= price) return processTicket(idx, roundTrip, inserted)

        if (remaining != lastRemaining || roundTrip != lastRoundTrip) {   // <- só se mudou
            val tipo = if (roundTrip) "$ARROW_UP$ARROW_DOWN" else "$ARROW_UP"
            TUI.clear()
            TUI.writemenuHome(Stations.getName(idx), tipo, "${formatPrice(remaining)}$EURO")
            lastRemaining = remaining; lastRoundTrip = roundTrip          // memoriza o novo estado
        }
        val key = TUI.readKey(100)
        when (key) { '*' -> roundTrip = !roundTrip
                     '#' -> { vendingAborted(inserted); return idleDisplay() } }
    }
}
```
Agora o ecrã só é redesenhado quando **entra uma moeda** (muda o `remaining`) ou carregas em **`*`** (muda o `roundTrip`). Nas outras voltas do loop, não toca no LCD → sem flicker. O `lastRemaining = -1` inicial garante que desenha a primeira vez.

❓ **Q23.** Porque é que o `lastRoundTrip` começa em `!roundTrip`?
> Para **forçar** o primeiro desenho: como `lastRoundTrip != roundTrip` na 1ª volta, a condição é verdadeira e o ecrã é escrito uma vez. Depois passa a igualar e só redesenha quando muda.

❓ **Q24 (reescrever).** O timeout de compra é infinito. Reescreve o `purchase` para **abortar** se passarem 5 s sem inserir moeda nem carregar tecla.
> Guardar o instante da última atividade e comparar com `Time.getTimeInMillis()`:
> ```kotlin
> var lastActivity = Time.getTimeInMillis()
> while (true) {
>     ...
>     if (CoinAcceptor.checkCoin()) { ...; lastActivity = Time.getTimeInMillis() }
>     ...
>     val key = TUI.readKey(100)
>     if (key != null) lastActivity = Time.getTimeInMillis()
>     if (Time.getTimeInMillis() - lastActivity > 5000) { vendingAborted(inserted); return idleDisplay() }
>     when (key) { ... }
> }
> ```

❓ **Q25 (reescrever).** A navegação por dígitos só cobre 1–9. Como farias para chegar às estações 10–16?
> Se carregar duas vezes no mesmo dígito, salta +9 (ex.: '1' depois '1' → estação 10). É o padrão `if (idx == digit) { val next = digit + 9; if (next < count()) next else digit }`.

---

## Software — Maintenance

Deteta a chave `M` e corre o menu (Teste, Consulta Bilhetes [A], Consulta Moedas [B], Reset [C], Desligar [D]).
```kotlin
// Maintenance.kt
fun stilMaintenance(): Boolean = HAL.isBit(M)      // M = 0x40 (I6)
fun startmaintenance() { if (HAL.isBit(M)) idleMaintenance() }
```

❓ **Q26.** Porque é que a chave M se lê com `HAL.isBit(0x40)` e não com `getKey()`?
> Porque M é um **switch de hardware** (I6), não uma tecla do teclado. Lê-se como um bit de entrada, não pela via do teclado.

---

## Os Testbenches (Kotlin) — o que cada um faz

> Os testbenches (`*_tb.kt`) são pequenos `main()` que exercitam um módulo e imprimem `ok`/`FAIL`. Os que tocam hardware têm uma parte automática (lógica pura) + uma parte manual (no SimDig).

**`HAL_tb.kt`** — verifica a manipulação de bits. Faz `setBits`, `clrBits`, `writeBits` e confirma o `lastOutput` esperado (ex.: `setBits(0x0F)` depois `setBits(0xF0)` → `0xFF`; `writeBits(0x0F, 0xFF)` só mexe nos 4 bits baixos). Não testa `isBit` isolado (lê entradas, escreve saídas — pinos diferentes).
```kotlin
// HAL_tb.kt
HAL.setBits(0x0F); tHal("setBits 0x0F", HAL.lastOutput == 0x0F)
HAL.writeBits(0xF0, 0xA0); tHal("mantem bits 0-3", HAL.lastOutput == 0xAF)
```

**`KBD_tb.kt`** — verifica a **tabela** índice→carácter (`teclado[0]=='1'`, `teclado[15]=='D'`), o valor de `NONE`, que `getKey()` sem tecla dá NONE, e que `waitKey(500)` demora ~500 ms e devolve NONE. Tem um teste manual (carregar '5' no SimDig).

**`TicketDispenser_tb.kt`** — extrai a lógica da trama para uma **função pura** `buildTicketData(rt, o, d)` e verifica os bits: RT no bit 0, PRT no bit 9, destino nos bits 1-4, origem nos 5-8. Testa vários bilhetes sem precisar de hardware, e no fim imprime um bilhete real (integração no SimDig).
```kotlin
// TicketDispenser_tb.kt
fun buildTicketData(roundTrip: Boolean, origin: Int, destination: Int): Int {
    val rt = if (roundTrip) 1 else 0
    return (1 shl 9) or (destination shl 1) or (origin shl 5) or rt
}
```

**`CoinAcceptor_tb.kt`** — confirma a tabela `coinArray` (cid 0→5c … cid 5→200c), que após `init` os sinais `accept/collect/eject` estão a 0, e que `collect`/`eject` desligam no fim. Parte manual: inserir uma moeda de 50c e confirmar que `readCoin()==50`.

**`LCD_tb.kt`** e **`TUI_tb.kt`** — são **visuais**: escrevem nos cantos, linhas cheias, custom chars (setas/€), e o homescreen; olhas para o SimDig e confirmas. O `TUI_tb` testa `writeCentered`, `writeSides`, `writemenuHome`, `readKey`, `getKey`.

**`SerialEmitter_tb.kt` / `App_tb.kt`** — o do SerialEmitter reaproveita a lógica da trama do TD; o `App_tb` testa `formatPrice` e a navegação circular A/B (mas está comentado no ficheiro atual).

❓ **Q27.** Porque é que o `HAL_tb` não consegue testar o `isBit` diretamente?
> Porque o `isBit` lê **entradas** (I) e o `setBits`/`writeBits` escrevem **saídas** (O) — são pinos diferentes. O que escreves nas saídas não aparece nas entradas, por isso não dá para verificar em ciclo fechado sem hardware.

❓ **Q28.** Porque é que o `TicketDispenser_tb` extrai a lógica para `buildTicketData` em vez de chamar `activatePrintingTicket`?
> Para testar a **codificação da trama** sem precisar de hardware (o `activatePrintingTicket` chama o `SerialEmitter`, que precisa do UsbPort). A função pura devolve o inteiro e podes comparar os bits.

---

## Código que o prof pode pedir para refazer

**Padrão de FSM** (VHDL — KeyControl, KtControl, RB_Control, Mini_Control):
```vhdl
CurrentState <= ST_Init when Reset='1' else NextState when rising_edge(CLK);
process (CurrentState, <entradas>) begin
  case CurrentState is
    when ST_A => if (cond) then NextState <= ST_B; else NextState <= ST_A; end if;
    ...
  end case;
end process;
saida <= '1' when CurrentState = ST_X else '0';   -- saída de Moore
```
**Comparador (Equal, VHDL):**
```vhdl
Q <= (A(0) xnor B(0)) and (A(1) xnor B(1)) and (A(2) xnor B(2)) and (A(3) xnor B(3));
```

---

## Estado atual — correções e limitações

**Feito (saber explicar):** KeyScan `+1`/repeat (`CE=Kscan and f_mux`); Ring Buffer `Empty` (`putget` direto); KBD validação do stop bit; **flicker** (só redesenha se muda); top-level com CLKDIV, TICKET_DISPENSER, `Fn→I4` e `.qsf`.

**Validação:** testbenches VHDL (Key Control/Decode/Scan, Hold_Rg, Shift_Rg, Serial_Receiver, Key Transmitter) e Kotlin (todos os `*_tb`) + SimDig.

**Limitações:** auto-repeat com `Tdelay` (não feito); Key Scan Versão III (não feito); PE_TD origem/destino divergem VHDL vs Kotlin (funciona na simul, alinhar); RAM assíncrona (devia ser síncrona).

---

## Mapa-resumo

| Bloco | O que faz | Peça-chave |
|---|---|---|
| Key Scan | varre o teclado | KD_Cont + MUX4to1 + Dec2to4 |
| Key Control | 1 código por pressão | FSM SCANNING/REGISTERING/WAITING_RELEASE |
| Ring Buffer | FIFO de 16 | RB_Control + MA_Control + RAM; Full/Empty via Mini_Control |
| Key Transmitter | envia tecla em série | FSM WAITING_LOAD/SENDING + MUX8to1 |
| PE_LCD / PE_TD | recebem trama de 10 bits | Shift + Hold Register |
| SerialEmitter | envia trama série | select + SDX/SCLK, LSB-first |
| TUI | apresentação (LCD+KBD) | writeCentered/readKey/getKey |
| App | lógica de venda + manutenção | FSM idle→browse→purchase→print |
| Top-level | junta tudo + pinos | UsbPort + CLKDIV + TICKET_DISPENSER |
