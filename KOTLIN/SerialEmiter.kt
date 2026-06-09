// Envia tramas para os diferentes modulos Serial Receiver.
object SerialEmitter {

    enum class Peripheral {LCD, TICKET}

    // entreadas no controlo:
    // entrea no controlo dps de sair do Ticket Dispenser como Fn e indica que acabou de imprimir ticket já podemos imprimir um novo
    const val TDdone_MASK = 0x10

    // saidas do controlo:
    // saida para LCD exclusiva:
    const val nLCDsel_MASK = 0x04
    //saida para Ticked Dispenser exclusiva:
    const val nTDsel_MASK  = 0x08
    // saidas do controlo para ambos:
    const val SCLK_MASK = 0x02
    const val SDX_MASK = 0x01

    // Protocolo série são sempre 10 bits para LCD e TD
    const val SIZE_DATA = 10


    // Inicia a classe
    fun init() {
        HAL.setBits(nLCDsel_MASK or nTDsel_MASK)
    }

    // Envia uma trama para o SerialReceiver identificado o periferico de destino em 'addr', os bits de dados em 'data' e em 'size' o numero de bits a enviar.
    fun send(addr: Peripheral, data: Int) {

        // primeira parte do protocolo de com em sserie
        val SEL_MASK = if (addr == Peripheral.LCD) nLCDsel_MASK else nTDsel_MASK
        HAL.clrBits(SEL_MASK)

        // enviar dados em serie
        for (n in 0 until SIZE_DATA) {
            HAL.clrBits(SCLK_MASK)
            //envia pelo SDX (como SEL_MASK ja está a enviar LCD ou TD os dados chegam ao sitio certo)
            val bit = (data shr n) and 1
            HAL.writeBits(SDX_MASK, bit)

            HAL.setBits(SCLK_MASK)
        }
        // ultima parte do protocolo, primeiro SCLK a 0 depois SEL a 1
        HAL.clrBits(SCLK_MASK)
        HAL.setBits(SEL_MASK)
    }

    //Retorna informação se o periferico (TD) esta ocupado
    //Ticket Dispenser "fn == 1 quando concluida a dispensa do bilhete"
    //isBusy() usado pelo TicketDispenser.kt!!!
    fun isBusy(): Boolean = !HAL.isBit(TDdone_MASK)
}

fun main() {
    HAL.init()
    SerialEmitter.init()
}
