import KBD.NONE
import KBD.getKey
import isel.leic.utils.Time


// Envia tramas para os diferentes modulos Serial Receiver.
object SerialEmitter {
    enum class Peripheral { LCD, TICKET }

    const val TD_DONE = 0x01

    const val RS = 0x200 // mensagem de controlo ou dados
    const val E = 0x01

    const val LCD_SEL = 0x1FE // mensagem pro lcd
    const val TD_SEL_O = 0x1E0
    const val TD_SEL_D = 0x01E
    const val SDX = 0x01

    const val sclkmask = 0x02
    const val SS = 0x04


    // Inicia a classe
    fun init() {
        HAL.init()
        HAL.setBits(SS)
    }

    //Envia uma trama para o Serial Receiver identificando o periferico de destino em "addr", os bits de dados em 'data' e em 'size' o numero de bits a enviar
    // ele pode enviar mais do que os 9 bits de data que usamos para comunicar com o PELCD
    fun send(addr: Peripheral, data: Int) {

        val newAddr = if (addr == Peripheral.LCD) RS and LCD_SEL and E
        else RS and TD_SEL_D and TD_SEL_O and TD_DONE

        val sizedata = 9

        HAL.clrBits(SS)

        for (n in 0 until sizedata) {
            HAL.clrBits(sclkmask)
            val bit = (data shr n) and 1
            HAL.writeBits(SDX, bit)
            HAL.setBits(sclkmask)
        }
        HAL.clrBits(sclkmask)
        HAL.setBits(SS)


    }
    //o bit de FN tÃªm de estar a 0
    // Retorna informação se o periferico está ocupado
    fun isBusy(): Boolean = !HAL.isBit(TD_DONE)

}






fun main() {
    var a = 0x101
    while (true) {

        SerialEmitter.send(SerialEmitter.Peripheral.LCD, a)
        a += a
        Thread.sleep(10000 )
    }

}


// o bit de FN têm de estar a 0
///fun isBusy(): Boolean = !HAL.isBit(TD_DONE)

/*

import isel.leic.utils.*

// Envia tramas para os diferentes modulos Serial Receiver.
object SerialEmitter {

    enum class Peripheral {LCD, TICKET}
    // vou ter que ter um mandar um SCLK, e o SDX é
    //dentro da class LCD vou ter que colocar as masks
    //[,,,,,,,"inv.in/hr.clk","sr.clk","sr.in"]
/*
    # PE LCD
    # Port Expander
    UsbPort.O0 -> sr.in
    UsbPort.O1-> sr.clk
    UsbPort.O2-> inv.in, hr.clk
    inv.out -> sr.en
    sr.out[0-9] -> hr.in[0-9]
    hr.out[1-8] -> lcd.D[0-7]
    hr.out0 -> lcd.rs
    hr.out9-> lcd.e

 */

    //const val serialmask
    val lcdselmask = 0x04 //SS
    // (LDCsel -> enable  SR a 0)
    val sclkmask = 0x02 //SCLK: UsbPort.O1-> sr.clk
    val sdxmask = 0x01 //SDX: UsbPort.O0 -> sr.in



    // Inicia a classe
    fun init() {
        //provavelmente quero dar reset ao PELCD e ao PETD
        //HAL.clrBits(serialMask)



    }

    // Envia uma trama para o SerialReceiver identificado o periferico de destino em 'addr', os bits de dados em 'data' e em 'size' o numero de bits a enviar.
    fun send(addr: Peripheral, data: Int, size: Int) {
            //addr escolhes se queres enviar para LCD ou para Ticket Dispenser.
            //para isso tens o enum class Preipheral

        var sdx = 0
        //lcdsel = 0


        for (i in 0 .. 9){
            var bitmask = 0x01
            val bit = data and bitmask
            HAL.writeBits(sdxmask, bit)
            bitmask = shl i

        }

    }

    // Retorna informacao se o periferico esta ocupado
    fun isBusy() : Boolean {}
}


*/
