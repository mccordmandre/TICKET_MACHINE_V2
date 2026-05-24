
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
    val ARROW_UP   = "\u0000"
    val ARROW_DOWN = "\u0001"
    val EURO       = "\u0002"      // euro

    fun processTicket(idx: Int, roundTrip: Boolean) {
        val station = Station.entries[idx]
        TUI.writeCentered(1, station.stationName)
        CoinAcceptor.collect()
        val ticket = Ticket(roundTrip, 0, idx)
        TicketDispenser.activatePrintingTicket(ticket.roundTrip, ticket.origin, ticket.destination)
    }


    fun showStation(idx: Int) {
        val station = Station.entries[idx]
        TUI.clear()
        TUI.writemenuHome(station.stationName,"$idx$ARROW_UP$ARROW_DOWN","${formatPrice(station.price)}$EURO")
    }

// para chamar fora if TUI.readKey() == '0' && countS == 0 - chama isto

    fun idleDisplay(){
            TUI.clear()// nenhuma tecla carregada mas o get.key ta sempre a ser usada
            TUI.writemenuHome("TICKET TO RIDE",1234.toString(),567.toString())
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
                            if (next < Station.entries.size) next else digit
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

    fun formatPrice(cents: Int): String {
        return "${cents / 100}.${(cents % 100).toString().padStart(2, '0')}"
    }
    fun purchase(idx: Int) {
        val station = Station.entries[idx]
        var roundTrip = false  //
        var inserted=0// começa em ida

        while (true) {
            val price = if (roundTrip) station.price * 2 else station.price
            val remaining = price - inserted
            val tipo = if (roundTrip) "$ARROW_UP$ARROW_DOWN" else "$ARROW_UP"

            TUI.clear()
            TUI.writemenuHome(station.stationName, tipo, "${formatPrice(price)}$EURO")

            if (CoinAcceptor.checkCoin()) {
                inserted += CoinAcceptor.readCoin()
                CoinAcceptor.coinAccept()
            }
            
            if (inserted >= price) {
                processTicket(idx, roundTrip)
                return
            }
            val key = TUI.readKey()
            when (key) {
                '*' -> roundTrip = !roundTrip
                '#' -> { TUI.clear()
                    TUI.writeCentered(0, "Vending Aborted")
                }
                KBD.NONE.toChar() -> return
            }
        }
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
