object TUI {

    fun init() {
        // Inicializa todos os modulos usados pela interface com o utilizador.
        HAL.init()
        SerialEmitter.init()
        LCD.init()
        KBD.init()
        TicketDispenser.init()
    }

 fun writeInCursor(line : Int , column : Int, sentence : String ) {
        LCD.cursor(line, column)
        LCD.write(sentence)
    }

    // Limpa o ecrã
    fun clear() {
        LCD.clear()
    }

    // Lê uma tecla com timeout (5 segundos por defeito conforme o enunciado)
    fun readKey(): Char {
        // Aguarda ate ao limite definido pelo timeout.
        return KBD.waitKey(5000)
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
