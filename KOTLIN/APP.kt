
import isel.leic.utils.Time
object APP {
    fun init() {
        HAL.init()
        SerialEmitter.init()
        LCD.init()
        KBD.init()
        TicketDispenser.init()

    }
    var countS =0
    const val M = 0x40 // i6
    val ARROW_UP   = "\u0000"
    val ARROW_DOWN = "\u0001"
    val EURO       = "\u0002"      // euro

    // FUNÇÕES GENERICAS DA APP


    /**
     * Obtém a hora atual do sistema, formatada para o menu de início.
     * Sem parâmetros.
     * @return String no formato "HH:mm" (ex: "09:05").
     */
    fun getTime(): String {
        val cal = java.util.Calendar.getInstance()
        val h = cal.get(java.util.Calendar.HOUR_OF_DAY).toString().padStart(2, '0')
        val m = cal.get(java.util.Calendar.MINUTE).toString().padStart(2, '0')
        return "$h:$m"
    }

    /**
     * Obtém a data atual do sistema, formatada para o menu de início.
     * Sem parâmetros.
     * @return String no formato "dd/MM/yyyy".
     */
    fun getDate(): String {
        val cal = java.util.Calendar.getInstance()
        val d = cal.get(java.util.Calendar.DAY_OF_MONTH).toString().padStart(2, '0')
        val mo = (cal.get(java.util.Calendar.MONTH) + 1).toString().padStart(2, '0')
        val year = (cal.get(java.util.Calendar.YEAR)).toString().padStart(2, '0')
        return "$d/$mo/$year"
    }

    /**
     * Converte um valor em cêntimos para uma string de preço legível.
     * @param cents Valor em cêntimos (ex: 155).
     * @return String no formato "euros.cêntimos" (ex: "1.55").
     */
    fun formatPrice(cents: Int): String { //155 -> 1.55
        return "${cents / 100}.${(cents % 100).toString().padStart(2, '0')}"
    }

    /**
     * Mostra no LCD a informação da estação no índice indicado
     * (nome, índice + setas de navegação, preço formatado).
     * @param idx Índice da estação a mostrar (0-based).
     * Sem retorno; efeito lateral: escreve no LCD via TUI.
     */
    fun showStation(idx: Int) {
        TUI.writemenuHome(Stations.getName(idx), "$idx$ARROW_UP$ARROW_DOWN", "${formatPrice(Stations.getPrice(idx))}$EURO")
    }

    /**
     * Mostra no LCD uma linha genérica de manutenção (usado tanto para
     * listar estações vendidas como contagem de moedas).
     * @param idx Índice do item a mostrar.
     * @param nome Nome/etiqueta a apresentar na 1ª linha.
     * @param sold Valor numérico associado (quantidade vendida / quantidade de moedas).
     * Sem retorno; efeito lateral: escreve no LCD via TUI.
     */
    fun browseMaintenance(idx: Int, nome : String, sold : Int ) {
        TUI.clear()
        TUI.writemenuHome(nome, "$idx$ARROW_UP$ARROW_DOWN", sold.toString())
    }

    //////////////////////////////////
    // MANUTENÇÃO
    //////////////////////////////////

    /**
     * Loop principal do modo de manutenção. Mostra um "slideshow" com as
     * opções disponíveis (#, A, B, C, D) e despacha para a função
     * correspondente consoante a tecla premida. Sai automaticamente do
     * modo de manutenção quando Maintenance.stilMaintenance() deixa de ser true.
     * Sem parâmetros. Sem retorno (Unit) — é um loop infinito que só
     * termina através de `return` para outra função de navegação.
     */

    fun idleMaintenance() {
        // lista de opções mostradas em "slideshow" no ecrã de manutenção
        val slides = listOf("# - Print Ticket", "A - Station Cnt.", "B - Coins Cnt.", "C - Reset Cnt.", "D - Shutdown")
        var slideIdx = 0 // arranca sempre no 1º slide ("# - Print Ticket")
        var lastSlideTime = Time.getTimeInMillis()
        val SLIDE_INTERVAL = 2000

        TUI.clear()
        TUI.writeCentered(0, "Maintenance")
        TUI.writeLeftSide(1, slides[slideIdx]) // desenha o 1º slide antes de entrar no loop

        while(true) {
            // só mostra o slideshow e aceita teclas enquanto estivermos em modo manutenção

            if (Maintenance.stilMaintenance()) {

                //se passou o tempo suficiente desde a última troca? -> avança para o próximo slide
                if (Time.getTimeInMillis() - lastSlideTime >= SLIDE_INTERVAL) {
                    slideIdx = (slideIdx + 1) % slides.size // avança circularmente pela lista de slides
                    lastSlideTime = Time.getTimeInMillis() // reinicia o cronómetro
                    TUI.clear()
                    TUI.writeCentered(0, "Maintenance")
                    TUI.writeLeftSide(1, slides[slideIdx]) // redesenha o novo slide
                }
                val key = TUI.getKey()
                when (key) {
                    '#' -> {  showStation(0); browseTest() }
                    'A' -> { browseMaintenance(0, Stations.getName(0), Stations.getSold(0)); browseStationsM()}
                    'B' -> {browseMaintenance(0,CoinDeposit.getCoinType(0).toString(),CoinDeposit.getAmmount(0)); browsecoinsM() }
                    'C' -> { resetcounters()}
                    'D' -> { shutdown()}
                }
            } else idleDisplay()
        }
    }

    // 1º CASO - PRINT TICKET TEST

    /**
     * Sub-menu de manutenção para navegar entre estações e testar a impressão de um bilhete (sem envolver moedas/pagamento).
     * Sem parâmetros. Sem retorno
     * devolve o controlo a idleMaintenance() por timeout (TUI.NONE) ou a purchaseTest() quando '#' é premido.
     */

    fun browseTest() {
        var idx = 0
        while (true) {
            val key = TUI.readKey(5000)
            if (key == TUI.NONE ) return idleMaintenance()

            when (key) {
                in '1'..'9' -> {
                    val digit = key!! - '0'
                    idx = if (idx == digit) {
                        // se tiveremos a clicar pela segunda vez na tecla interpreta-se como pedido para ir para a estação que nao se enquadra do primeiros 9 idx
                        val next = digit + 9
                        if (next < Stations.count()) next else digit
                    } else digit
                    TUI.clear()
                    showStation(idx)
                }
                // recua 1 posição; soma Stations.count() antes do % para evitar índices negativos
                'A' -> { idx = (idx - 1 + Stations.count()) % Stations.count()
                    TUI.clear()
                    showStation(idx) }
                'B' -> { idx = (idx + 1) % Stations.count()
                    TUI.clear()
                    showStation(idx) }
                '#' -> purchaseTest(idx)
            }
        }
    }

    /**
     * Ecrã de confirmação de teste de impressão para a estação indicada.
     * @param idx Índice da estação selecionada.
     * Sem retorno — chama processTicketTest() ao confirmar ('*') ou
     * cancela e volta ao ecrã inicial ('#').
     */

    fun purchaseTest(idx: Int) {
        while (true) {
            TUI.writeCentered(0, Stations.getName(idx))
            TUI.writeLeftSide(1,"$ARROW_UP *- to Print")
            val key = TUI.readKey(100)
            when (key) {
                '*' -> {processTicketTest(idx,roundTrip = true)}
                '#' -> {
                    vendingAbortedTest()
                    return idleDisplay() }
            }

        }
    }

    /**
     * Mostra a mensagem de "Vending Aborted" durante 5 segundos.
     * Sem parâmetros. Sem retorno; efeito lateral: pausa a execução (Time.sleep).
     */
    fun vendingAbortedTest() {
        TUI.clear()
        TUI.writeCentered(0, "Vending Aborted")
        Time.sleep(5000) // bloqueia 5s para o utilizador ler a mensagem
    }

    /**
     * Executa o teste de impressão de bilhete (sem pagamento real) para
     * a estação indicada e aguarda a recolha do bilhete pelo utilizador.
     * @param idx Índice da estação.
     * @param roundTrip true = bilhete ida e volta, false = só ida.
     * Sem retorno — no fim reencaminha sempre para idleMaintenance().
     */
    fun processTicketTest(idx: Int, roundTrip: Boolean) {
        TicketDispenser.activatePrintingTicket(roundTrip, 6, idx)
        TUI.clear()
        TUI.writeCentered(0, Stations.getName(idx))
        TUI.writeCentered(1, "Collect Ticket")

        TicketDispenser.waitTicket()

        TUI.writeCentered(0, "Thank you!")
        TUI.writeCentered(1, "Have a nice trip")
        Time.sleep(2000)

        idleMaintenance()
    }

    // 2º CASO - VER A QUANTIDADE DE BILHETES VENDIDOS
    /**
     * Sub-menu de manutenção que permite navegar pelas estações e ver
     * quantos bilhetes foram vendidos em cada uma.
     * Sem parâmetros. Sem retorno — volta a idleMaintenance() por
     * timeout ou ao premir '#'.
     */
    fun browseStationsM() {
        var idx = 0
        while (true) {
            val key = TUI.readKey(5000)
            if (key == TUI.NONE) return idleMaintenance()

            //
            when (key) {
                in '1'..'9' -> {
                    val digit = key!! - '0'
                    idx = if (idx == digit) {
                        val next = digit + 9  // para se clicar duas vezes na tecla ir para a cidade correspondente dessa tecla (após os primeiros 9 idx)
                        if (next < Stations.count()) next else digit
                    } else digit
                    browseMaintenance(idx, Stations.getName(idx), Stations.getSold(idx))
                }
                // recua 1 posição; soma Stations.count() antes do % para evitar índices negativos
                'A' -> { idx = (idx - 1 + Stations.count()) % Stations.count()
                    browseMaintenance(idx, Stations.getName(idx), Stations.getSold(idx)) }
                'B' -> { idx = (idx + 1) % Stations.count()
                    browseMaintenance(idx, Stations.getName(idx), Stations.getSold(idx)) }
                '#' -> idleMaintenance()
            }
        }
    }

    // 3º CASO - VER A QUANTIDADE DE MOEDAS DEPOSITADAS
    /**
     * Sub-menu de manutenção que permite navegar pelos tipos de moeda e
     * ver a quantidade depositada de cada um.
     * Sem parâmetros. Sem retorno — volta a idleMaintenance() por
     * timeout ou ao premir '#'.
     */
    fun browsecoinsM() {
        var idx = 0
        while (true) {
            val key = TUI.readKey(5000)
            if (key == TUI.NONE) return idleMaintenance()

            when (key) {
                in '1'..'9' -> {
                    val digit = key!! - '0'
                    idx = if (idx == digit) {
                        val next = digit + 9
                        if (next < CoinDeposit.countCoin()) next else digit
                    } else digit
                    browseMaintenance(
                        idx,
                        CoinDeposit.getCoinType(idx).toString(),
                        CoinDeposit.getAmmount(idx)
                    )
                }

                'A' -> { idx = (idx - 1 + CoinDeposit.countCoin()) % CoinDeposit.countCoin()
                    browseMaintenance(
                        idx,
                        CoinDeposit.getCoinType(idx).toString(),
                        CoinDeposit.getAmmount(idx)
                    )
                }
                'B' -> { idx = (idx + 1) % CoinDeposit.countCoin()
                    browseMaintenance(
                        idx,
                        CoinDeposit.getCoinType(idx).toString(),
                        CoinDeposit.getAmmount(idx)
                    )
                }
                '#' -> idleMaintenance()
            }
        }
    }

    // 4º CASO - DAR RESET NOS CONTADORES
    /**
     * Pede confirmação e, se aceite, faz reset a todos os contadores; coloca  as stations e as coins a 0
     * (estações vendidas e moedas depositadas) e persiste o novo estado.
     * Sem parâmetros. Sem retorno — volta sempre a idleMaintenance().
     */
    fun resetcounters() {
        TUI.clear()
        TUI.writeCentered(0, " Reset Counters")
        TUI.writeSides(1, "*- Yes", "Other- No")
        while (true) {
            val key = TUI.readKey(5000)
            TUI.clear()
            if (key == TUI.NONE) return idleMaintenance()
            if (key == '*') {
                // Confirmado: reset + persistência dos dados em ficheiro
                TUI.clear()
                TUI.writeCentered(0, " Resetting Cont.")
                Time.sleep(2000)
                Stations.resetCounters()
                CoinDeposit.resetCounter()
                Stations.saveStations()
                CoinDeposit.saveCoins()
                return idleMaintenance()
            } else if (key in '1'..'9' || key in 'A'..'D') { idleMaintenance() } // qualquer outra tecla = "No"
        }
    }

    // 4º CASO (nota original) - DAR SHUTDOWN À MÁQUINA
    /**
     * Pede confirmação e, se aceite, desliga (bloqueia) a máquina.
     * Sem parâmetros. Sem retorno — em caso de confirmação entra num
     * loop infinito de "desligado" (bloqueia a execução).
     * simular o estado "desligado" e nunca mais sai .
     */
    fun shutdown() {
        TUI.clear()
        TUI.writeCentered(0, "Shutdown")
        TUI.writeSides(1, "*- Yes", "Other- No")
        while (true) {
            val key = TUI.readKey(5000)
            if (key == TUI.NONE) return idleMaintenance()
            if (key == '*') {
                TUI.clear()
                TUI.writeCentered(0, "Shutdowning")
                Time.sleep(2000)
                while (true) TUI.clear() // estado terminal: máquina "desligada"
            } else if (key in '1'..'9' || key in 'A'..'D') { idleMaintenance() }
        }
    }

    // 
    // FUNÇÕES DA APP 
    // 

    /**
     * Ecrã inicial (idle) da máquina: mostra título, data e hora, e
     * fica à espera de uma tecla para iniciar a navegação nas estações.
     * Também verifica continuamente se deve entrar em modo de manutenção.
     * Sem parâmetros. Sem retorno — despacha para browseStations()
     * assim que uma tecla é premida.
     */
    fun idleDisplay() {
        TUI.clear()
        TUI.writemenuHome("TICKET TO RIDE", getDate(), getTime())
        while (true) {
            Maintenance.startmaintenance() // verifica trigger de entrada em manutenção
            val key = TUI.getKey()                        // espera tecla (bloqueia até timeout)
            if (key != TUI.NONE) browseStations()                  // houve tecla -> deixa o browseStations correr
        }
    }

    /**
     * Permite navegar entre as estações disponíveis (setas A/B e dígitos
     * 1-9) e iniciar o processo de compra ao premir '#' (só se a estação
     * tiver preço definido, i.e. != 0).
     * Sem parâmetros. Sem retorno — volta a idleDisplay() por timeout.
     */
    fun browseStations() {
        var idx = 0
        while (true) {
            val key = TUI.readKey(5000)
            if (key == TUI.NONE) return idleDisplay()

            when (key) {
                in '1'..'9' -> {
                    // 1ª tecla numérica é "Lisboa"
                    if (idx == 0 && countS == 0) {
                        TUI.clear()
                        showStation(idx)
                        countS++
                    } else {
                        val digit = key!! - '0'
                        idx = if (idx == digit) {
                            val next = digit + 9
                            if (next < Stations.count()) next else digit
                        } else digit
                        TUI.clear()
                        showStation(idx)
                    }
                }
                'A' -> { idx = (idx - 1 + Stations.count()) % Stations.count()
                    TUI.clear()
                    showStation(idx) }
                'B' -> { idx = (idx + 1) % Stations.count()
                    TUI.clear()
                    showStation(idx) }
                '#' -> if (Stations.getPrice(idx) != 0) { // estação inválida/sem preço não pode ser comprada
                    TUI.clear()
                    purchase(idx)
                }
            }
        }
    }

    /**
     * Fluxo de compra: aceita moedas inseridas até perfazer o preço da
     * estação (simples ou ida-e-volta, alternável com '*'), atualizando
     * o LCD com o valor em falta. Cancela com '#'.
     * @param idx Índice da estação a comprar.
     * Sem retorno — avança para processTicket() quando o valor inserido
     * é suficiente, ou para vendingAborted()+idleDisplay() se cancelado.
     */
    fun purchase(idx: Int) {
        var roundTrip = false
        var lastRemaining = -1          // estado anterior (fora do loop)
        var lastRoundTrip = !roundTrip  // força desenhar a 1ª vez

        while (true) {
            val price = if (roundTrip) Stations.getPrice(idx) * 2 else Stations.getPrice(idx)

            // entrou moeda? -> lê e confirma o handshake com o hardware
            if (CoinAcceptor.checkCoin()) {
                CoinAcceptor.readCoin()
                CoinAcceptor.coinAccept()
            }

            val inserted = CoinAcceptor.inserted_coins.sum()
            val remaining = price - inserted

            // Se inserio o preço do bilhete, começa a precossar o bilhete
            if (inserted >= price) return processTicket(idx, roundTrip, inserted)

            // só reescreve o LCD se algo mudou (evita flicker no display)
            if (remaining != lastRemaining || roundTrip != lastRoundTrip) {
                val tipo = if (roundTrip) "$ARROW_UP$ARROW_DOWN" else "$ARROW_UP"
                TUI.clear()
                TUI.writemenuHome(Stations.getName(idx), tipo, "${formatPrice(remaining)}$EURO")
                lastRemaining = remaining
                lastRoundTrip = roundTrip
            }

            val key = TUI.readKey(100)
            when (key) {
                '*' -> roundTrip = !roundTrip // alterna entre bilhete simples / ida-e-volta
                '#' -> { vendingAborted(inserted); return idleDisplay() } // cancela e devolve moedas
            }
        }
    }

    /**
     * Cancela a compra em curso: ejeta as moedas inseridas, persiste o
     * estado dos depósitos e informa o utilizador do valor devolvido.
     * @param remaining Valor (em cêntimos) a devolver/mostrar ao utilizador.
     * Sem retorno; efeito lateral: ejeção física de moedas + pausa 5s.
     */

    fun vendingAborted(remaining: Int) {
        CoinAcceptor.eject()
        CoinDeposit.saveCoins()
        TUI.clear()
        TUI.writeCentered(0, "Vending Aborted")
        TUI.writeCentered(1, "returned  ${formatPrice(remaining)}$EURO")
        Time.sleep(5000)
    }

    /**
     * Finaliza uma compra bem sucedida: regista o dinheiro recebido,
     * imprime o bilhete, aguarda a sua recolha e atualiza os contadores
     * de vendas da estação, persistindo tudo em ficheiro.
     * @param idx Índice da estação comprada.
     * @param roundTrip true = ida e volta, false = só ida.
     * @param price Valor total (em cêntimos) efetivamente recebido/cobrado.
     * Sem retorno — no fim volta sempre a idleDisplay().
     */
    fun processTicket(idx: Int, roundTrip: Boolean, price: Int) {
        TUI.clear()
        TUI.writeCentered(0, Stations.getName(idx))
        TUI.writeCentered(1, "Processing...")

        // Regista o dinheiro: junta as moedas ao depósito e persiste
        CoinAcceptor.collect()
        CoinDeposit.addCoin(price)
        CoinDeposit.saveCoins()

        TicketDispenser.activatePrintingTicket(roundTrip, 6, idx)

        TUI.clear()
        TUI.writeCentered(0, Stations.getName(idx))
        TUI.writeCentered(1, "Collect Ticket")

        TicketDispenser.waitTicket() // bloqueia até o bilhete ser retirado

        // Atualiza e persiste as estatísticas de venda da estação
        Stations.sellTicket(idx)
        Stations.saveStations()

        TUI.writeCentered(0, "Thank you!")
        TUI.writeCentered(1, "Have a nice trip")
        Time.sleep(5000)
        idleDisplay()
    }
}

/**
 * Ponto de entrada da aplicação.
 * Inicializa os módulos e entra num loop infinito que alterna entre
 * o modo de manutenção e o modo normal de venda, consoante o estado
 * devolvido por Maintenance.stilMaintenance().
 */
fun main() {
    APP.init()
    while (true) {
            APP.idleDisplay()
            APP.browseStations()
        }
    }

