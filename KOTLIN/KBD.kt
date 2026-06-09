import isel.leic.utils.*

// Ler teclas. Funcoes retornam '0'..'9','A'..'D','#','*' ou NONE.
object KBD {
    const val NONE = 0
    const val serial = true
    //para parallel
    const val keymask = 0x0F
    const val kackmask = 0x80
    const val kvalmask = 0x10
    //para serial
    const val TXCLKMASK: Int = 0x80
    const val RXDMASK:   Int = 0x80

    val teclado = charArrayOf(
        '1', '4', '7', '*',
        '2', '5', '8', '0',
        '3', '6', '9', '#',
        'A', 'B', 'C', 'D'
    )

    // Inicia a classe
    fun init() {
        keymask
    }

    private fun getKeyParallel() : Char{
        var character = NONE.toChar()
        // enquanto kval = 1 lê o indice se for i != 0
        if (HAL.isBit(kvalmask)) {
            val indice = HAL.readBits(keymask)
            character = teclado[indice]
            HAL.setBits(kackmask)
            // enquanto kval = true, ou seja até keydecode receber kack bloqueia
            while (HAL.isBit(kvalmask)) {
            }
            HAL.clrBits(kackmask)
        }
        return character
    }


    private fun getKeySerial(): Char {
        var character = NONE.toChar()
        var bits = 0
        // TXD desceu há dados para ler
        if (!HAL.isBit(RXDMASK)) {
            for (i in 0 until 7) {
                HAL.setBits(TXCLKMASK)
                if (i in 1..4) {
                    if (HAL.isBit(RXDMASK)) {
                        bits = bits or (1 shl (i - 1))
                    }
                }
                HAL.clrBits(TXCLKMASK)
            }
            character = teclado[bits]
        }
        return character
    }



    // Retorna de imediato a tecla premida ou NONE se nao ha tecla premida
    fun getKey(): Char {
        if (serial != true){
            return getKeyParallel()

        }
        else {
            return getKeySerial()
        }
    }


    // Retorna a tecla premida, caso ocorra antes do 'timeout' (em milissegundos),
    // ou NONE caso contrario.
    fun waitKey(timeout: Long): Char {
        val start = Time.getTimeInMillis()
        while (Time.getTimeInMillis() - start < timeout) {
            val key = getKey()
            if (key != NONE.toChar()) {
                return key
            }
        }
        return NONE.toChar()
    }
}



fun main() {
    KBD.init()
    println("A aguardar teclas...")
    while (true) {
        val key = KBD.getKey()
        if (key != KBD.NONE.toChar()) {
            println("Tecla: $key")
        }
    }
}
