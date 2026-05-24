// TicketDispenserTest.kt
// Testa a codificação da trama enviada ao PETD.
// Verifica os bits de RT, origem, destino e PRT sem precisar do hardware.
// A lógica de codificação é extraída para uma função pura testável.

fun checkTD(label: String, expected: Int, actual: Int) {
    if (expected == actual)
        println("PASS  $label")
    else
        println("FAIL  $label  esperado=0b${expected.toString(2).padStart(10,'0')}  obtido=0b${actual.toString(2).padStart(10,'0')}")
}

// Replica a lógica de activatePrintingTicket para testar sem hardware
fun buildTicketData(roundTrip: Boolean, origin: Int, destination: Int): Int {
    val rt = if (roundTrip) 1 else 0
    return (1 shl 9) or (destination shl 1) or (origin shl 5) or rt
    // bit 9 = PRT, bits 5-8 = origin, bits 1-4 = destination, bit 0 = RT
}

fun main() {
    println("=== TicketDispenser tests ===")

    // Bilhete simples: Lisboa (0) → Madrid (1), ida
    // Esperado: PRT=1(bit9), origin=0(bits5-8), dest=1(bits1-4), RT=0(bit0)
    // = 0b1000000000_10_0 = 0x202
    val t1 = buildTicketData(false, 0, 1)
    checkTD("ida, origin=0 dest=1 -> bit9=1 bits1-4=1 bit0=0",
        (1 shl 9) or (1 shl 1) or 0, t1)

    // Ida/volta: Lisboa (0) → Paris (2), RT=1
    val t2 = buildTicketData(true, 0, 2)
    checkTD("ida/volta, origin=0 dest=2 -> bit0=1 bits1-4=2",
        (1 shl 9) or (2 shl 1) or 1, t2)

    // Origem não-zero: Berlin (5) → London (3), ida
    val t3 = buildTicketData(false, 5, 3)
    checkTD("ida, origin=5 dest=3",
        (1 shl 9) or (5 shl 5) or (3 shl 1) or 0, t3)

    // Verificar bit RT isolado
    val t4 = buildTicketData(true, 0, 0)
    checkTD("RT=1 quando roundTrip=true", 1, t4 and 0x01)

    val t5 = buildTicketData(false, 0, 0)
    checkTD("RT=0 quando roundTrip=false", 0, t5 and 0x01)

    // Verificar bit PRT sempre a 1
    checkTD("PRT sempre 1", 1 shl 9, t1 and (1 shl 9))
    checkTD("PRT sempre 1 (t2)", 1 shl 9, t2 and (1 shl 9))

    // Teste de integração — enviar um bilhete real e esperar Fn
    println("\n--- Teste de integração (requer emulador) ---")
    println("Vai imprimir um bilhete Lisboa -> Madrid, ida")
    TicketDispenser.init()
    TicketDispenser.activatePrintingTicket(false, 0, 1)
    println("Bilhete enviado. Verifica o SimDig.")
    println("A aguardar Fn...")
    TicketDispenser.waitTicket()
    println("Fn recebido — impressão concluída.")

    println("=== fim TicketDispenser tests ===")
}