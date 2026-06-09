import isel.leic.utils.Time

// controla o mecanismo de dispensa de bilhetes.
object TicketDispenser{

    // recebe TDdone (vem do Fn do Ticket Dispenser que indica fim de impressão/pode iniciar nova impressão
    const val TDdone_MASK = 0x10

    // para enviar a sequencia que sai do serial emitter
    const val PRT = 0x200

    // Inicia a classe, estabelecendo os valores iniciais.
    fun init() {
        HAL.init()
        SerialEmitter.init()
    }

    // para ficar a esperar o TDdone/fn
    fun waitTicket() {
        // espera Fn subir (bilhete pronto)
        while (!HAL.isBit(TDdone_MASK)) { Time.sleep(100) }
        // espera Fn descer (Collect clicado)
        while (HAL.isBit(TDdone_MASK))  { Time.sleep(100) }
    }

    // envia comando para dispensar um bilhete
    fun activatePrintingTicket(roundTrip: Boolean, origin: Int, destination: Int) {

        // se roundtrip == true rt = 1
        // se toundtrip(só ida) == false rt = 0
        val rt  = if (roundTrip) 1 else 0

        val data = PRT or (destination shl 1) or (origin shl 5) or rt
        /*
        petd.D0 -> tp.rt              # bit 0
        petd.D[1-4] -> tp.DId[0-3]    # bits 1-4
        petd.D[5-8] -> tp.OId[0-3]    # bits 5-8
        petd.D9 -> tp.prt             # bit 9  (PRT!!)
        */

        // envia a trama toda [0:9] pelo serial emitter
        //isBusy verifica se podemos imprimir novo bilhete
        if (!SerialEmitter.isBusy()) {
            SerialEmitter.send(SerialEmitter.Peripheral.TICKET, data)
        }
    }
}

fun main (args: Array<String>) {
    TicketDispenser.init()
    TicketDispenser.activatePrintingTicket(true, 10, 11)
}
