
object APP {
    fun init() {
        HAL.init()
        SerialEmitter.init()
        LCD.init()
        KBD.init()
        TicketDispenser.init()
    }
    enum class Station(val stationName: String, val price: Int) {
        LISBOA("Lisboa", 225),
        MADRID("Madrid", 200),
        PARIS("Paris", 175),
        LONDON("London", 150),
        ROMA("Roma", 125),
        BERLIN("Berlin", 100),
        MOSKVA("Moskva", 0),
        BRUXELLES("Bruxelles", 100),
        AMSTERDAM("Amsterdam", 125),
        KYIV("Kyiv", 150),
        ATHINA("Athina", 175),
        WIEN("Wien", 200),
        WARSZAWA("Warszawa", 225),
        KOBENHAVN("Kobenhavn", 250),
        STOCKHOLM("Stockholm", 275),
        CONSTANTINOPLE("Constantinople", 300);
    }
var countS =0

    // ver se não faz mais sentido isto estar na no ticket dispenser e não na app
    class ticket (val roundTrip: Boolean, val origin: Int, val destination: Int) {

    }

    fun printTicket(ticket: ticket){
        TODO()
       // TicketDispenser.activatePrintingTicket()
    }


    fun showStation(idx: Int) {
        val station = Station.entries[idx]
        TUI.clear()
        TUI.writeCentered(0, station.stationName)
        TUI.writeSides(1,idx.toString(), station.price.toString() )
    }

// para chamar fora if TUI.readKey() == '0' && countS == 0 - chama isto

    fun idleDisplay(){
            TUI.clear()// nenhuma tecla carregada mas o get.key ta sempre a ser usada
            TUI.writeCentered(0,"TICKET TO RIDE")
            TUI.writeSides(1,1254.toString(), 123.toString()) // descobrir como colocar data e hora
            countS = 0

    }

    fun browseStations() {
        var idx = 0
        while (true) {
            val key = TUI.readKey()
            if (key == KBD.NONE.toChar() ) return idleDisplay()

            when (key) {
                in '1'..'9' -> {
                    if (idx== 0 && countS==0) {
                        showStation(idx)
                        countS++
                    } else {
                        val digit = key - '0'
                        idx = if (idx == digit) {
                            val next = digit + 9
                            if (next < Station.entries.size) next else digit // se nao existe fica no atual
                        } else digit
                        showStation(idx)
                    }
                }
                'A' -> { idx = (idx - 1 + Station.entries.size) % Station.entries.size; showStation(idx) }
                'B' -> { idx = (idx + 1) % Station.entries.size; showStation(idx) }
                '#' -> purchase(idx)
            }
        }
    }

    fun purchase(idx: Int) {
        TODO()
    }


}


fun main() {
    APP.init()
    while (true) {
        APP.idleDisplay()
        APP.browseStations()
    }
}











/*
object app{
    // funcao geral de ler coins
    fun coinTotal(precoBilhete: Int): Int {
        var total = 0
        while (total < precoBilhete){
            total += CoinAcceptor.coinAccept()
        }
        return total
    }
}
*/
