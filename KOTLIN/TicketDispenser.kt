
object TicketDispenser {
    const val PRT = 0x200
    fun init() {
        HAL.init()
        SerialEmitter.init()
    }


    fun activatePrintingTicket(roundTrip: Boolean, origin: Int, destination: Int) {
        // se for 1 é ida e volta se for 0 é so ida
        val rt  = if (roundTrip) 1 else 0
        val data = PRT or (origin  shl 1) or (destination  shl 5) or rt

        if (!SerialEmitter.isBusy()) {
            SerialEmitter.send(SerialEmitter.Peripheral.TICKET, data)
        }
    }
}

fun main (args: Array<String>) {
    TicketDispenser.init()
    TicketDispenser.activatePrintingTicket(true, 1, 0)
}