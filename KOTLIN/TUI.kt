object TUI {

    fun init() {
        // Inicializa todos os modulos usados pela interface com o utilizador.
        LCD.init()
        KBD.init()
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
    // retorna null se timeout
    fun readKey(timeout: Int): Char? {
        // Aguarda ate ao limite definido pelo timeout.
        val key = KBD.waitKey(timeout.toLong())
        return if (key == KBD.NONE.toChar()) null else key
    }

    // Lê uma tecla sem esperar
    fun getKey(): Char {
        return KBD.getKey()
    }

    // seleciona item da lista com A/B para cima/baixo, # confirma
    fun selectFromList(items: List<String>, timeout: Int): Int? {
        if (items.isEmpty()) return null

        var idx = 0
        while (true) {
            clear()
            writeCentered(0, items[idx])
            writeSides(1, "A", "B")

            val key = readKey(timeout)
            if (key == null) return null

            when (key) {
                'A' -> idx = (idx - 1 + items.size) % items.size
                'B' -> idx = (idx + 1) % items.size
                '#' -> return idx
                '*' -> return null
            }
        }
    }

    // formata centimos para "X.XXE"
    fun formatPrice(cents: Int): String {
        val euros = cents / 100
        val centimos = cents % 100
        return String.format("%d.%02d€", euros, centimos)
    }

}

fun main() {
    TUI.init()

    // testa selectFromList
    val stations = listOf("Lisboa", "Madrid", "Paris", "London")
    val idx = TUI.selectFromList(stations, 10000)
    println("Escolheu: $idx")

    // testa formatPrice
    println(TUI.formatPrice(225))
    println(TUI.formatPrice(50))
}
