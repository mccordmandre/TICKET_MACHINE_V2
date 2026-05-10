object TUI {

    fun init() {
        HAL.init()
        SerialEmitter.init()
        LCD.init()
        KBD.init()
    }

    // Escreve uma mensagem no LCD (linha 0 e linha 1)
    fun writeLine( text: String) {

        LCD.cursor(LCD.LINES, 0)
        LCD.write(text.padEnd(LCD.COLS))
    }

    // Limpa o ecrã
    fun clear() {
        LCD.clear()
    }

    // Lê uma tecla com timeout (5 segundos por defeito conforme o enunciado)
    fun readKey(timeout: Long = 5000): Char {
        return KBD.waitKey(timeout)
    }

    // Lê uma tecla sem esperar
    fun getKey(): Char {
        return KBD.getKey()
    }
}

// TUI
fun main() {

    TUI.init()

    Thread.sleep(500)
    TUI.writeLine( "Hello World!")
    Thread.sleep(1000)

    TUI.writeLine("Teste TUI")
    println("writeLine 1 - esperado: 'Teste TUI' na linha 1, resto preenchido com espacos")
    Thread.sleep(1000)

    TUI.writeLine("Texto longo demais para caber no display LCD 16 cols")
    println("writeLine texto lonngo")
    Thread.sleep(1000)

    TUI.writeLine("")
    println(" linha 0 preenchida com 16 espacos")
    Thread.sleep(1000)

    TUI.clear()
    println(" limpo ")
    Thread.sleep(1000)

    println(" carrega uma tecla ")
    val key = TUI.readKey()
    println(" $key")
    val none = TUI.getKey()
    println(" $none")

}