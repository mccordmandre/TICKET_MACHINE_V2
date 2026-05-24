import isel.leic.utils.Time
// Escreve no LCD usando a interface a 10 bits.
object LCD {

    // Dimensao do display
    const val LINES = 2
    const val COLS = 16



    // Comandos
    const val CMD_CLEAR = 0x01
    const val CMD_FUNCTION_SET = 0x38 // 8 bits, 2 linhas, 5x8
    const val CMD_DISPLAY_OFF = 0x08 //novo
    const val CMD_DISPLAY_ON = 0x0C // display on, cursor off
    const val CMD_ENTRY_MODE = 0x06
    const val SERIAL_INTERFACE = false

    // Trama de 10 bits:
        // bit 0 = RS (indica se mensagem é controlo ou dados)
        // bits 1 a 8 = D0 a D7
        // bit 9 = E (regista info no LCD)

    // Escreve um byte de comando/dados no LCD em serie
    private fun writeByteSerial(rs: Boolean, data: Int) {
        val rsBit = if (rs) 1 else 0
        val base = (data shl 1) or rsBit
        SerialEmitter.send(SerialEmitter.Peripheral.LCD, base or SerialEmitter.E)
        SerialEmitter.send(SerialEmitter.Peripheral.LCD, base)
    }

    // Escreve um byte de comando/dados no LCD
    private fun writeByte(rs: Boolean, data: Int) = writeByteSerial(rs, data)

    // Escreve um comando no LCD
    private fun writeCMD(data: Int)  = writeByte(false, data)

    // Escreve um dado no LCD
    private fun writeDATA(data: Int) = writeByte(true, data)

    private fun createChar(slot: Int, pattern: IntArray) {
        writeCMD(0x40 or (slot shl 3))
        for (row in pattern) writeDATA(row)  // writeDATA não write
        writeCMD(0x80)
    }

    fun initCustomChars() {
        val arrowUp   = intArrayOf(0x04, 0x0E, 0x1F, 0x04, 0x04, 0x04, 0x04, 0x00)
        val arrowDown = intArrayOf(0x04, 0x04, 0x04, 0x04, 0x1F, 0x0E, 0x04, 0x00)
        val euro      = intArrayOf(0x07, 0x09, 0x1C, 0x08, 0x1C, 0x09, 0x07, 0x00) // novo padrão
        createChar(0, arrowUp)
        createChar(1, arrowDown)
        createChar(2, euro)
    }

    // Envia a squencia de iniciacao para comicacao a 8 bits
    fun init() {
        SerialEmitter.init()
        Time.sleep(15)
        writeCMD(CMD_FUNCTION_SET)
        Time.sleep(5)
        writeCMD(CMD_FUNCTION_SET)
        Time.sleep(1)
        writeCMD(CMD_FUNCTION_SET)
        writeCMD(CMD_FUNCTION_SET)
        writeCMD(CMD_DISPLAY_OFF)
        writeCMD(CMD_CLEAR)
        Time.sleep(2)
        writeCMD(CMD_ENTRY_MODE)
        writeCMD(CMD_DISPLAY_ON)
        initCustomChars()
    }

    // Escreve um caracter na posicao corrente
    fun write(c: Char) = writeDATA(c.code)

    // Escreve uma string na posicao corrente
    fun write(text: String) { for (c in text) write(c) }

    // Envia comando para posicionar cursor ('line':0..LINES-1 , 'column':0..COLS-1)
    fun cursor(line: Int, column: Int) {
        val address = when (line) {
            0    -> 0x00 + column
            1    -> 0x40 + column
            else -> 0x00 + column
        }
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
