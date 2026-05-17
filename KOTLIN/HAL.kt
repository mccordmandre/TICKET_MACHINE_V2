import isel.leic.UsbPort


object HAL {


    var lastOutput = 0

    // Inicia o objeto
    fun init() {
        clrBits(0xFF)
    }

    // Retorna 'true' se o bit definido pela mask esta com o valor logico '1' no UsbPort
    fun isBit(mask: Int): Boolean {
        return (readBits(mask) == mask)
    }

    // Retorna os valores dos bits representados por mask presentes no UsbPort
    fun readBits(mask: Int): Int {
        return mask and UsbPort.read()
    }


    // Escreve nos bits representados por mask os valores dos bits correspondentes em value
    fun writeBits(mask: Int, value: Int) {
        val output = (lastOutput and mask.inv()) or (mask and value)
        UsbPort.write(output)
        lastOutput = output
    }

    // Coloca os bits representados por mask no valor logico '1'
    fun setBits(mask: Int) {
        val output = lastOutput or mask
        UsbPort.write(output)
        lastOutput = output
    }

    // Coloca os bits representados por mask no valor logico '0'
    fun clrBits(mask: Int) {
        val output = mask.inv() and lastOutput
        UsbPort.write(output)
        lastOutput = output
    }

}

    fun main() {
        //
        //HAL.writeBits(0x0F, 0x0F)
        //HAL.clrBits(0x01)
        //HAL.setBits(0x01)
        //HAL.isBit(0x01)
        val tecla = HAL.readBits(0x0F)
        while(true) {
            val teclaNew = HAL.readBits(0x0F)
            while (tecla != teclaNew){
                println(teclaNew)
            }

        }
    }


