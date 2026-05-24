// TicketDispenser_tb.kt
fun tTD(s: String, ok: Boolean) = println("${if (ok) "ok  " else "FAIL"} $s")

// replica a logica do activatePrintingTicket para testar sem hardware
fun buildTicketDataTd(rt: Boolean, o: Int, d: Int): Int {
    val r = if (rt) 1 else 0
    return (1 shl 9) or (o shl 5) or (d shl 1) or r
}

fun main() {
    // bits: PRT(9) | origin(5-8) | dest(1-4) | RT(0)
    val t1 = buildTicketDataTd(false, 0, 1)
    tTD("ida Lisboa->Madrid", t1 == ((1 shl 9) or (1 shl 1)))

    val t2 = buildTicketDataTd(true, 0, 2)
    tTD("ida/volta Lisboa->Paris", t2 == ((1 shl 9) or (2 shl 1) or 1))

    val t3 = buildTicketDataTd(false, 5, 3)
    tTD("ida Berlin->London", t3 == ((1 shl 9) or (5 shl 5) or (3 shl 1)))

    tTD("RT=1 se roundTrip", (buildTicketDataTd(true,  0, 0) and 1) == 1)
    tTD("RT=0 se ida",       (buildTicketDataTd(false, 0, 0) and 1) == 0)
    tTD("PRT sempre 1",      (t1 and (1 shl 9)) != 0 && (t2 and (1 shl 9)) != 0)

    // integracao - imprime bilhete real (precisa do emulador)
    println("\n-- integracao --")
    TicketDispenser.init()
    TicketDispenser.activatePrintingTicket(false, 0, 1)
    println("Bilhete enviado. A aguardar Fn...")
    TicketDispenser.waitTicket()
    println("ok  Fn recebido")
}