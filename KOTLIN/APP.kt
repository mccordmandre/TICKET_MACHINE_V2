
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
    const val M = 0x40 // i6
    val ARROW_UP   = "\u0000"
    val ARROW_DOWN = "\u0001"
    val EURO       = "\u0002"      // euro

    // FUNÇÕES GENERICAS DA APP
    fun getTime(): String {
        val cal = java.util.Calendar.getInstance()
        val h = cal.get(java.util.Calendar.HOUR_OF_DAY).toString().padStart(2, '0')
        val m = cal.get(java.util.Calendar.MINUTE).toString().padStart(2, '0')
        return "$h:$m"
    }

    fun getDate(): String {
        val cal = java.util.Calendar.getInstance()
        val d = cal.get(java.util.Calendar.DAY_OF_MONTH).toString().padStart(2, '0')
        val mo = (cal.get(java.util.Calendar.MONTH) + 1).toString().padStart(2, '0')
        val year = (cal.get(java.util.Calendar.YEAR) ).toString().padStart(2, '0')
        return "$d/$mo/$year"
    }
    fun formatPrice(cents: Int): String { //155 -> 1.55
        return "${cents / 100}.${(cents % 100).toString().padStart(2, '0')}"
    }
    fun showStation(idx: Int) {
        TUI.writemenuHome(Stations.getName(idx), "$idx$ARROW_UP$ARROW_DOWN", "${formatPrice(Stations.getPrice(idx))}$EURO")
    }

    fun browseMaintenance(idx: Int, nome : String, sold : Int ) {
        TUI.clear()
        TUI.writemenuHome(nome, "$idx$ARROW_UP$ARROW_DOWN", sold.toString())
    }
    // MANUTENÇÃO
    fun idleMaintenance() {
        val slides = listOf("# - Print Ticket", "A - Station Cnt.", "B - Coins Cnt.", "C - Reset Cnt.", "D - Shutdown")
        var slideIdx = -1
        var lastSlideTime = Time.getTimeInMillis()
        val SLIDE_INTERVAL = 2000

        slideIdx = 0
        TUI.clear()
        TUI.writeCentered(0, "Maintenance")
        TUI.writeLeftSide(1, slides[slideIdx])
        while(true) {
            if (Maintenance.stilMaintenance()) {
                if (Time.getTimeInMillis() - lastSlideTime >= SLIDE_INTERVAL) {
                    slideIdx = (slideIdx + 1) % slides.size
                    lastSlideTime = Time.getTimeInMillis()
                    TUI.clear()
                    TUI.writeCentered(0, "Maintenance")
                    TUI.writeLeftSide(1, slides[slideIdx])
                }

                val key = TUI.getKey()
                when (key) {
                    '#' -> {
                        showStation(0); browseTest(); return
                    }

                    'A' -> {
                        browseMaintenance(0, Stations.getName(0), Stations.getSold(0)); browseStationsM(); return
                    }

                    'B' -> {
                        browseMaintenance(
                            0,
                            CoinDeposit.getCoinType(0).toString(),
                            CoinDeposit.getAmmount(0)
                        ); browsecoinsM(); return
                    }

                    'C' -> {
                        resetcounters(); return
                    }

                    'D' -> {
                        shutdown(); return
                    }
                }
            } else idleDisplay()
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
                    val digit = key!! - '0'
                    idx = if (idx == digit) {
                        val next = digit + 9
                        if (next < Stations.count()) next else digit
                    } else digit
                    TUI.clear()
                    showStation(idx)
                }
                'A' -> { idx = (idx - 1 + Stations.count()) % Stations.count()
                    TUI.clear()
                    showStation(idx) }
                'B' -> { idx = (idx + 1) % Stations.count()
                    TUI.clear()
                    showStation(idx) }
                '#' -> purchaseTest(idx)
            }
        }
    }

    fun purchaseTest(idx: Int) {
        while (true) {
            TUI.writeCentered(0, Stations.getName(idx))
            TUI.writeLeftSide(1,"$ARROW_UP *- to Print")
            val key = TUI.readKey(100)
            when (key) {
                '*' -> {processTicketTest(idx,roundTrip = true)}
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
                    val digit = key!! - '0'
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
                    val digit = key!! - '0'
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
        TUI.clear()
        TUI.writeCentered(0," Reset Counters")
        TUI.writeSides(1,"*- Yes", "Other- No")
        while (true) {
            val key = TUI.readKey(5000)
            TUI.clear()
            if (key == TUI.NONE) return idleMaintenance()
            if (key == '*') {
                TUI.clear()
                TUI.writeCentered(0," Resetting Cont.")
                Time.sleep(2000)
                Stations.resetCounters()
                CoinDeposit.resetCounter()
                Stations.saveStations()
                CoinDeposit.saveCoins()
                return idleMaintenance()
            } else if (key in '1'..'9'|| key in 'A'..'D') {idleMaintenance()}
        }
    }
    // 4º CASO - DAR SHUTDOWN NA MACHINE
    fun shutdown() {
        TUI.clear()
        TUI.writeCentered(0,"Shutdown")
        TUI.writeSides(1,"*- Yes", "Other- No")
        while (true) {
            val key = TUI.readKey(5000)
            if (key == TUI.NONE) return idleMaintenance()
            if (key == '*') {
                TUI.clear()
                TUI.writeCentered(0,"Shutdowning")
                Time.sleep(2000)
                while (true) TUI.clear()
            } else if (key in '1'..'9'|| key in 'A'..'D') {idleMaintenance()}
        }

    }


    // FUNÇÕES NA APP
    fun idleDisplay() {
        TUI.clear()
        TUI.writemenuHome("TICKET TO RIDE", getDate(), getTime())   // escreve UMA vez
        while (true) {
            Maintenance.startmaintenance()
            val key = TUI.getKey()                        // espera tecla (bloqueia até timeout)
            if (key != TUI.NONE) browseStations()                  // houve tecla -> deixa o browseStations correr
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
                        TUI.clear()
                        showStation(idx)
                        countS++
                    } else {
                        val digit = key!! - '0'
                        idx = if (idx == digit) {
                            val next = digit + 9
                            if (next < Stations.count()) next else digit
                        } else digit
                        TUI.clear()
                        showStation(idx)
                    }
                }
                'A' -> { idx = (idx - 1 + Stations.count()) % Stations.count()
                    TUI.clear()
                    showStation(idx) }
                'B' -> { idx = (idx + 1) % Stations.count()
                    TUI.clear()
                    showStation(idx) }
                '#' -> if ( Stations.getPrice(idx) != 0 ){
                    TUI.clear()
                    purchase(idx)
                }
            }
        }
    }

    fun purchase(idx: Int) {
        var roundTrip = false
        var lastRemaining = -1          // estado anterior (fora do loop)
        var lastRoundTrip = !roundTrip  // força desenhar a 1ª vez

        while (true) {
            val price = if (roundTrip) Stations.getPrice(idx) * 2 else Stations.getPrice(idx)

            // entrou moeda? -> lê e confirma o handshake
            if (CoinAcceptor.checkCoin()) {
                CoinAcceptor.readCoin()
                CoinAcceptor.coinAccept()
            }

            val inserted  = CoinAcceptor.inserted_coins.sum()
            val remaining = price - inserted

            if (inserted >= price) return processTicket(idx, roundTrip, inserted)

            // só reescreve o LCD se algo mudou (evita flicker)
            if (remaining != lastRemaining || roundTrip != lastRoundTrip) {
                val tipo = if (roundTrip) "$ARROW_UP$ARROW_DOWN" else "$ARROW_UP"
                TUI.clear()
                TUI.writemenuHome(Stations.getName(idx), tipo, "${formatPrice(remaining)}$EURO")
                lastRemaining = remaining
                lastRoundTrip = roundTrip
            }

            val key = TUI.readKey(100)
            when (key) {
                '*' -> roundTrip = !roundTrip
                '#' -> { vendingAborted(inserted); return idleDisplay() }
            }
        }
    }

    fun vendingAborted(remaining : Int) {
        CoinAcceptor.eject()
        CoinDeposit.saveCoins()
        TUI.clear()
        TUI.writeCentered(0, "Vending Aborted")
        TUI.writeCentered(1, "returned  ${formatPrice(remaining)}$EURO")
        Time.sleep(5000)
    }
    fun processTicket(idx: Int, roundTrip: Boolean, price : Int) {
        TUI.clear()
        TUI.writeCentered(0, Stations.getName(idx))
        TUI.writeCentered(1, "Processing...")

        CoinAcceptor.collect()
        CoinDeposit.addCoin(price)
        CoinDeposit.saveCoins()

        TicketDispenser.activatePrintingTicket(roundTrip, 6, idx)

        TUI.clear()
        TUI.writeCentered(0, Stations.getName(idx))
        TUI.writeCentered(1, "Collect Ticket")

        TicketDispenser.waitTicket()
        Stations.sellTicket(idx)
        Stations.saveStations()

        TUI.writeCentered(0, "Thank you!")
        TUI.writeCentered(1, "Have a nice trip")
        Time.sleep(5000)
        idleDisplay()
    }

}

fun main() {
    APP.init()
    while (true) {
        if (Maintenance.stilMaintenance())
            APP.idleMaintenance()
        else {
            APP.idleDisplay()
            APP.browseStations()
        }
    }
}
