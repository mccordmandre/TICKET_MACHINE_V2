
// Escreve no LCD usando a interface a 10 bits.
object LCD {

    // Dimensao do display
    const val LINES = 2
    const val COLS  = 16
    // Comandos
    const val CMD_CLEAR = 0x01
    const val CMD_FUNCTION_SET = 0x38 // 8 bits, 2 linhas, 5x8
    const val CMD_DISPLAY_OFF = 0x08 //novo
    const val CMD_DISPLAY_ON = 0x0C // display on, cursor off
    const val CMD_ENTRY_MODE = 0x06

    // Trama de 10 bits: bit 0 = RS, bits 1..8 = D0..D7, bit 9 = E
    // Escreve um byte de comando/dados no LCD em serie
    private fun writeByteSerial(rs: Boolean, data: Int) {
        val rsBit = if (rs) 1 else 0
        val base = (data shl 1) or rsBit
        SerialEmitter.send(SerialEmitter.Peripheral.LCD, base or SerialEmitter.E)
        SerialEmitter.send(SerialEmitter.Peripheral.LCD, base)
    }
    private fun writeByte(rs: Boolean, data: Int) = writeByteSerial(rs, data)

    private fun writeCMD(data: Int)  = writeByte(false, data)

    private fun writeDATA(data: Int) = writeByte(true, data)

    fun init() {
        SerialEmitter.init()
        Thread.sleep(15)
        writeCMD(CMD_FUNCTION_SET)
        Thread.sleep(5)
        writeCMD(CMD_FUNCTION_SET)
        Thread.sleep(1)
        writeCMD(CMD_FUNCTION_SET)
        writeCMD(CMD_FUNCTION_SET)
        writeCMD(CMD_DISPLAY_OFF)
        writeCMD(CMD_CLEAR)
        Thread.sleep(2)
        writeCMD(CMD_ENTRY_MODE)
        writeCMD(CMD_DISPLAY_ON)
    }

    fun writeC(c: Char) = writeDATA(c.code)

    fun write(text: String) { for (c in text) writeC(c) }

    fun cursor(line: Int, column: Int) {
        val address = when (line) {
            0    -> 0x00 + column
            1    -> 0x40 + column
            else -> 0x00 + column
        }
        writeCMD( 0x80 or address)
    }

    fun clear() {
        writeCMD(CMD_CLEAR)
        Thread.sleep(2)
    }
}


fun main() {


    LCD.init()

    Thread.sleep(500)

    LCD.clear()
    LCD.cursor(0, 0);
    LCD.write("Ola Mundinho")

    Thread.sleep(1000)

    LCD.clear()
    LCD.cursor(0, 0); LCD.write("0123456789ABCDEF")
    LCD.cursor(1, 0); LCD.write("GHIJKLMNOPQRSTUV")


}