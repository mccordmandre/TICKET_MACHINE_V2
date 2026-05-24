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

    //tipo (0, "Porto")
    fun writeCentered(line: Int, text: String){
        writeInCursor(line,(LCD.COLS - text.count()) / 2, text)
    }

    fun writeSides(line: Int ,textL: String, textR:String){
        writeInCursor(line,0, textL)
        writeInCursor(line,(LCD.COLS - textR.count()) , textR)
    }

    fun writemenuHome(apptipe: String,left: String, right : String){
        writeCentered(0, apptipe)
        writeSides(1,left,right)

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


    TUI.clear()
    println(" limpo ")
    Thread.sleep(1000)

    println(" carrega uma tecla ")
    val key = TUI.readKey()
    println(" $key")
    val none = TUI.getKey()
    println(" $none")

}
