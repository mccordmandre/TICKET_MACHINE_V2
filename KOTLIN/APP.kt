import isel.leic.utils.Time
object APP {
    fun init() {
        HAL.init()
        SerialEmitter.init()
        LCD.init()
        KBD.init()
        TicketDispenser.init()

    }
    /*
    // no projeto se tiver no idle pode ativar M for ativo entra em fase de manutenção
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

     */


    // contadores e variavais de escrita
   var countS =0
    const val M = 0x06 // i6
  val ARROW_UP   = "\u0000"
  val ARROW_DOWN = "\u0001"
  val EURO       = "\u0002"      // euro

    
    fun processTicket(idx: Int, roundTrip: Boolean) {
        TUI.clear()
        TUI.writeCentered(0, Stations.getName(idx))
        TUI.writeCentered(1, "Processing...")

        CoinAcceptor.collect()
        TUI.printTicket(roundTrip, 6, idx)

        TUI.clear()
        TUI.writeCentered(0, Stations.getName(idx))
        TUI.writeCentered(1, "Collect Ticket")

        TUI.ticketcolected()  // espera clicar

        TUI.writeCentered(0, "Thank you!")
        TUI.writeCentered(1, "Have a nice trip")
        Thread.sleep(2000)
    }
    fun showStation(idx: Int) {
        TUI.clear()
        TUI.writemenuHome(Stations.getName(idx), "$idx$ARROW_UP$ARROW_DOWN", "${formatPrice(Stations.getPrice(idx))}$EURO")
    }

// para chamar fora if TUI.readKey() == '0' && countS == 0 - chama isto

    fun idleDisplay(){

        while(true) {
            val key = TUI.getKey()
            TUI.clear()
            TUI.writemenuHome("TICKET TO RIDE", 1234.toString(), 567.toString())
            countS = 0
            //if (key == M.toChar()) maintence()

        }

    }

    fun browseStations() {
        var idx = 0
        while (true) {
            val key = TUI.readKey(5000)
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
                            if (next < Stations.count()) next else digit
                        } else digit
                        showStation(idx)
                    }
                }
                'A' -> { idx = (idx - 1 + Stations.count()) % Stations.count()
                     showStation(idx) }
                'B' -> { idx = (idx + 1) % Stations.count()
                     showStation(idx) }
                '#' -> purchase(idx)
            }
        }
    }

    fun vendingAborted(remaining : Int) {
        CoinAcceptor.eject()
        TUI.clear()
        TUI.writeCentered(0, "Vending Aborted")
        TUI.writeCentered(1, "returned  ${formatPrice(remaining)}$EURO")
        Time.sleep(5000)
    }

    fun formatPrice(cents: Int): String {
        return "${cents / 100}.${(cents % 100).toString().padStart(2, '0')}"
    }
    fun purchase(idx: Int) {
        var roundTrip = false
        var inserted = 0

        while (true) {
            val price = if (roundTrip) Stations.getPrice(idx) * 2 else Stations.getPrice(idx)
            val tipo = if (roundTrip) "$ARROW_UP$ARROW_DOWN" else "$ARROW_UP"
            val remaining = price - inserted

            if (CoinAcceptor.checkCoin()) {
                val coin = CoinAcceptor.readCoin()
                if (coin != null) inserted += coin
                CoinAcceptor.coinAccept()
            }

            if (inserted >= price) {
                return processTicket(idx, roundTrip)
            }

            TUI.clear()
            TUI.writemenuHome(Stations.getName(idx), tipo, "${formatPrice(remaining)}$EURO")


            val key = TUI.readKey(100)
            when (key) {
                '*' -> roundTrip = !roundTrip
                '#' -> { vendingAborted(inserted)
                     return idleDisplay() }
                }

        }
    }

}


fun main(args : Array<String>) {
    APP.init()
    Stations.init(args[1])
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
