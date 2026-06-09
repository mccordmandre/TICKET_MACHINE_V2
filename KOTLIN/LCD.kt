import isel.leic.utils.Time
// Escreve no LCD usando a interface a 8 bits.
object LCD {

    // Dimensao do display
    const val LINES = 2
    const val COLS = 16

    // Comandos LCD
    const val CMD_CLEAR = 0x01
    const val CMD_FUNCTION_SET = 0x38 // 8 bits, 2 linhas, 5x8
    // Desliga/liga display
    const val CMD_DISPLAY_OFF = 0x08
    const val CMD_DISPLAY_ON = 0x0C // cursor fica off


    const val CMD_ENTRY_MODE = 0x06
    const val SERIAL_INTERFACE = false

    // Trama de 10 bits:
    /*
    #sinais internos do hardware!!!
    pelcd.D0 -> lcd.rs          (RS indica se mensagem é comando == 0 (ex CLEAR) ou dados == 1 (ex 'A')
    pelcd.D[1-8] -> lcd.D[0-7]  (D0 a D7)
    pelcd.D9-> lcd.e            (E enable registar info no LCD no falling edge)
     */

    //bit 9 da trama
    const val E = 0x200

    // Escreve um byte de comando/dados no LCD em serie
    private fun writeByteSerial(rs: Boolean, data: Int) {
        // rs == false é comando || rs == true é dados
        val rsBit = if (rs) 1 else 0
        val base = (data shl 1) or rsBit
        //por ex: 0b(E=1)01000001(RS=1)
        SerialEmitter.send(SerialEmitter.Peripheral.LCD, base or E)
        //por ex: 0b(E=0)001000001(RS=1)
        SerialEmitter.send(SerialEmitter.Peripheral.LCD, base)
        // LCD só vê E = 1 -> E=0 entre estas duas emissões de dados, e entende que é para registar informação no falling edge de E
    }

    // Escreve um byte de comando/dados no LCD
    private fun writeByte(rs: Boolean, data: Int) = writeByteSerial(rs, data)

    // Escreve um comando no LCD rs=false
    private fun writeCMD(data: Int)  = writeByte(false, data)

    // Escreve um dado no LCD rs=dados
    private fun writeDATA(data: Int) = writeByte(true, data)


    // para criar char customizado tipo setinha
    private fun createChar(slot: Int, pattern: IntArray) {
        //0x40 = Set CGRAM Address/pointer para CGRAM
        // slot << 3 = slot × 8 (cada slot tem 8 bytes)
        writeCMD(0x40 or (slot shl 3))
        // writeDATA não write
        // Escreve 8 bytes (8 linhas de 5 pixels cada)
        for (row in pattern) writeDATA(row)
        //0x80 = Set DDRAM Address/pointer na DDRAM)
        writeCMD(0x80)
    }

    fun initCustomChars() {
        val arrowUp   = intArrayOf(0x04, 0x0E, 0x1F, 0x04, 0x04, 0x04, 0x04, 0x00)
        val arrowDown = intArrayOf(0x04, 0x04, 0x04, 0x04, 0x1F, 0x0E, 0x04, 0x00)
        val euro      = intArrayOf(0x07, 0x09, 0x1C, 0x08, 0x1C, 0x09, 0x07, 0x00)
        //slot 0 = ↑ (escreve em '\u0000')
        createChar(0, arrowUp)
        //slot 1 = ↓ (escreve em '\u0001')
        createChar(1, arrowDown)
        //Slot 2 = € (escreve em '\u0002')
        createChar(2, euro)
    }

    // Envia a squencia de iniciacao para comicacao a 8 bits.
    fun init() {
        SerialEmitter.init()
    // espera até LCD estar pronto
        Time.sleep(15)

        // 3x FUNCTION_SET para sync com LCD
        writeCMD(CMD_FUNCTION_SET)
        Time.sleep(5)
        writeCMD(CMD_FUNCTION_SET)
        Time.sleep(1)
        writeCMD(CMD_FUNCTION_SET)
        writeCMD(CMD_FUNCTION_SET)


        writeCMD(CMD_DISPLAY_OFF)
        writeCMD(CMD_CLEAR)
        Time.sleep(2)
        // Modo: cursor avança após cada caracter
        writeCMD(CMD_ENTRY_MODE)
        writeCMD(CMD_DISPLAY_ON) // Liga display

        // Cria setas e euro
        initCustomChars()
    }

    //funcoes que APP e TUI usam

    // Escreve um caracter na posicao corrente
    fun write(c: Char) = writeDATA(c.code)

    // Escreve uma string na posicao corrente
    fun write(text: String) { for (c in text) write(c) }

    // Envia comando para posicionar cursor ('line':0..LINES-1 , 'column':0..COLS-1)
    fun cursor(line: Int, column: Int) {
        // Endereços DDRAM:
        // Linha 0: 0x00-0x0F (16 posições)
        // Linha 1: 0x40-0x4F (16 posições)
        val address = when (line) {
            0    -> 0x00 + column
            1    -> 0x40 + column
            else -> 0x00 + column
        }
        //0x80 = "Set DDRAM Address"
        writeCMD( 0x80 or address)
    }

    // Envia comando para limpar ecra e posicionar cursor em (0,0)
    fun clear() {
        writeCMD(CMD_CLEAR)
        Time.sleep(2)
    }
}

// LCD
fun main() {
    LCD.init()
    Time.sleep(500)

    // Cantos. verifica posicionamento cursor
    LCD.cursor(0, 0);  LCD.write("A")
    LCD.cursor(0, 15); LCD.write("B")
    LCD.cursor(1, 0);  LCD.write("C")
    LCD.cursor(1, 15); LCD.write("D")
    Time.sleep(2000)

    // preencher as 2 linhas completas
    LCD.clear()
    LCD.cursor(0, 0); LCD.write("ABCDEFGHIJKLMNOP")
    LCD.cursor(1, 0); LCD.write("0123456789ABCDEF")
    Time.sleep(2000)

    // clear
    LCD.clear()
    Time.sleep(1000)

    // exemplod e uso da app
    LCD.cursor(0, 1); LCD.write("TICKET MACHINE")
    LCD.cursor(1, 3); LCD.write("Lisboa  2.00")
    Time.sleep(2000)

    // actualizar só uma linha
    LCD.cursor(1, 3); LCD.write("Madrid  3.50")
    Time.sleep(2000)

    LCD.clear()
}
