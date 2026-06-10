import Stations.resetCounters
import isel.leic.utils.Time
object APP {
    fun init() {
        HAL.init()
        SerialEmitter.init()
        LCD.init()
        KBD.init()
        TicketDispenser.init()

    }
   var countS =0
    const val M = 0x06 // i6
  val ARROW_UP   = "\u0000"
  val ARROW_DOWN = "\u0001"
  val EURO       = "\u0002"      // euro

// FUNÇÕES GENERICAS DA APP
    fun formatPrice(cents: Int): String { //155 -> 1.55
        return "${cents / 100}.${(cents % 100).toString().padStart(2, '0')}"
    }
    fun showStation(idx: Int) {
        TUI.clear()
        TUI.writemenuHome(Stations.getName(idx), "$idx$ARROW_UP$ARROW_DOWN", "${formatPrice(Stations.getPrice(idx))}$EURO")
    }
    fun browseMaintenance(idx: Int, nome : String, sold : Int ) {
        TUI.clear()
        TUI.writemenuHome(nome, "$idx$ARROW_UP$ARROW_DOWN", sold.toString())
    }
// MANUTENÇÃO
 fun carroselMaintenance() {
        while (true){
            TUI.writeCentered(0, "Maintenance")
            TUI.writeLeftSide(1, "# - Print Ticket")
            Time.sleep(2000)
            TUI.writeCentered(0, "Maintenance")
            TUI.writeLeftSide(1, "A - Station Cnt.")
            Time.sleep(2000)
            TUI.writeCentered(0, "Maintenance")
            TUI.writeLeftSide(1, "B - Coins Cnt.")
            Time.sleep(2000)
            TUI.writeCentered(0, "Maintenance")
            TUI.writeLeftSide(1, "C - Reset Cnt.")
            Time.sleep(2000)
            TUI.writeCentered(0, "Maintenance")
            TUI.writeLeftSide(1, "D - Shutdown")
            Time.sleep(2000)
        }
    }

    fun idleMaintenance(){
        TUI.clear()
        while(true) {
            val key = TUI.getKey()
            carroselMaintenance()
            when (key) {
                '#' -> browseTest()
                'A' -> browseStationsM()
                'B' -> browsecoinsM()
                'C' -> resetcounters()
                'D' -> shutdown()

            }
        }
    }
    // 1º CASO - PRINT TICKET TEST
    fun browseTest() {
        var idx = 0
        while (true) {
            val key = TUI.readKey(5000)
            if (key == TUI.NONE ) return idleMaintenance()

            when (key) {
                in '1'..'9' -> {
                    val digit = key - '0'
                    idx = if (idx == digit) {
                        val next = digit + 9
                        if (next < Stations.count()) next else digit
                    } else digit
                    showStation(idx)
                }
                'A' -> { idx = (idx - 1 + Stations.count()) % Stations.count()
                    showStation(idx) }
                'B' -> { idx = (idx + 1) % Stations.count()
                    showStation(idx) }
                '#' -> purchaseTest(idx)
            }
        }
    }

    fun purchaseTest(idx: Int) {
        while (true) {

            TUI.clear()
            TUI.writeCentered(0, Stations.getName(idx))
            TUI.writeLeftSide(1,"$ARROW_UP *- to Print")
            val key = TUI.readKey(100)
            when (key) {
                '*' -> processTicketTest(idx,roundTrip = true)
                '#' -> {
                    vendingAbortedTest()
                    return idleDisplay() }
            }

        }
    }
    fun vendingAbortedTest() {
        TUI.clear()
        TUI.writeCentered(0, "Vending Aborted")
        Time.sleep(5000)
    }
    fun processTicketTest(idx: Int, roundTrip: Boolean) {
        TicketDispenser.activatePrintingTicket(roundTrip, 6, idx)
        TUI.clear()
        TUI.writeCentered(0, Stations.getName(idx))
        TUI.writeCentered(1, "Collect Ticket")

        TicketDispenser.waitTicket()

        TUI.writeCentered(0, "Thank you!")
        TUI.writeCentered(1, "Have a nice trip")
        Time.sleep(2000)

        idleMaintenance()
    }
    // 2º CASO - VER A QUANTIDADE DE BILHETES VENDIDOS
    fun browseStationsM() {
        var idx = 0
        while (true) {
            val key = TUI.readKey(5000)
            if (key == TUI.NONE ) return idleMaintenance()

            when (key) {
                in '1'..'9' -> {
                    val digit = key - '0'
                    idx = if (idx == digit) {
                        val next = digit + 9
                        if (next < Stations.count()) next else digit
                    } else digit
                  browseMaintenance(idx, Stations.getName(idx), Stations.getSold(idx))

                }
                'A' -> { idx = (idx - 1 + Stations.count()) % Stations.count()
                    browseMaintenance(idx, Stations.getName(idx), Stations.getSold(idx)) }
                'B' -> { idx = (idx + 1) % Stations.count()
                    browseMaintenance(idx, Stations.getName(idx), Stations.getSold(idx)) }
                '#' -> idleMaintenance()
            }
        }
    }
    // 3º CASO - VER A QUANTIDADE DE MOEDAS VENDIDOS
    fun browsecoinsM() {
        var idx = 0
        while (true) {
            val key = TUI.readKey(5000)
            if (key == TUI.NONE ) return idleMaintenance()

            when (key) {
                in '1'..'9' -> {
                    val digit = key - '0'
                    idx = if (idx == digit) {
                        val next = digit + 9
                        if (next < CoinDeposit.countCoin()) next else digit
                    } else digit
                    browseMaintenance(
                        idx,
                        CoinDeposit.getCoinType(idx).toString(),
                        CoinDeposit.getAmmount(idx)
                    )

                }
                'A' -> { idx = (idx - 1 + CoinDeposit.countCoin()) % CoinDeposit.countCoin()
                    browseMaintenance(
                        idx,
                        CoinDeposit.getCoinType(idx).toString(),
                        CoinDeposit.getAmmount(idx)
                    )
                }
                'B' -> { idx = (idx + 1) % CoinDeposit.countCoin()
                    browseMaintenance(
                        idx,
                        CoinDeposit.getCoinType(idx).toString(),
                        CoinDeposit.getAmmount(idx)
                    )
                }
                '#' -> idleMaintenance()
            }
        }
    }
    // 4º CASO - DAR RESET NOS CONTADORES
    fun resetcounters() {
        while (true) {
            val key = TUI.readKey(5000)
            if (key == TUI.NONE) return idleMaintenance()
            TUI.clear()
            TUI.writeCentered(0," Reset Counters")
            TUI.writeSides(1,"*- Yes", "Other- No")

            if (key == '*') {
                Stations.resetCounters()
                CoinDeposit.resetCounter()
                return idleMaintenance()
            } else if (key in '1'..'9'|| key in 'A'..'D') {idleMaintenance()}
        }
    }
    // 4º CASO - DAR SHUTDOWN NA MACHINE
    fun shutdown() {
        while (true) {
            val key = TUI.readKey(5000)
            if (key == TUI.NONE) return idleMaintenance()
            TUI.writeCentered(0,"Shutdown")
            TUI.writeSides(1,"*- Yes", "Other- No")
            if (key == '*') {
                while (true) TUI.clear()
            } else if (key in '1'..'9'|| key in 'A'..'D') {idleMaintenance()}
        }

    }


    // FUNÇÕES NA APP
    fun idleDisplay(){
        TUI.clear()
        while(true) {
            val key = TUI.getKey()
            TUI.writemenuHome("TICKET TO RIDE", 1234.toString(), 567.toString())
            countS = 0
            while(key == M.toChar()) idleMaintenance()
        }

    }

    fun browseStations() {
        var idx = 0
        while (true) {
            val key = TUI.readKey(5000)
            if (key == TUI.NONE ) return idleDisplay()

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

    fun purchase(idx: Int) {
        var roundTrip = false

        while (true) {
            val price = if (roundTrip) Stations.getPrice(idx) * 2 else Stations.getPrice(idx)
            val tipo = if (roundTrip) "$ARROW_UP$ARROW_DOWN" else "$ARROW_UP"
            val remaining = price - CoinAcceptor.inserted_coins.sum()

            CoinAcceptor.readCoin()

            if (CoinAcceptor.inserted_coins.sum() >= price) {
                return processTicket(idx, roundTrip)
            }

            TUI.clear()
            TUI.writemenuHome(Stations.getName(idx), tipo, "${formatPrice(remaining)}$EURO")

            val key = TUI.readKey(100)
            when (key) {
                '*' -> roundTrip = !roundTrip
                '#' -> { vendingAborted(CoinAcceptor.inserted_coins.sum())
                     return idleDisplay() }
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
    fun processTicket(idx: Int, roundTrip: Boolean) {
        TUI.clear()
        TUI.writeCentered(0, Stations.getName(idx))
        TUI.writeCentered(1, "Processing...")

        CoinAcceptor.collect()
        TicketDispenser.activatePrintingTicket(roundTrip, 6, idx)

        TUI.clear()
        TUI.writeCentered(0, Stations.getName(idx))
        TUI.writeCentered(1, "Collect Ticket")

        TicketDispenser.waitTicket()

        TUI.writeCentered(0, "Thank you!")
        TUI.writeCentered(1, "Have a nice trip")
        Time.sleep(2000)
    }

}




fun main(args : Array<String>) {
    APP.init()
    while (true) {
        APP.idleDisplay()
        APP.browseStations()
    }
}



