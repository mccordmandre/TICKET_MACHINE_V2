// Ler teclas. Funcoes retornam '0'..'9','A'..'D','#','*' ou NONE.
import isel.leic.utils.*

object KBD {
    const val NONE = 0;

    const val keymask = 0x0F
    const val kackmask = 0x80
    const val kvalmask = 0x10


    val teclado = charArrayOf(
        '1', '4', '7', '*',
        '2', '5', '8', '0',
        '3', '6', '9', '#',
        'A', 'B', 'C', 'D'
    )

    // Inicia a classe
    fun init() {
        HAL.clrBits(kackmask)
    }


    // Retorna de imediato a tecla premida ou NONE se nao ha tecla premida.
    fun getKey(): Char {
        var character = NONE.toChar()
        // enquanto kval lê o indice se for i != 0
        if (HAL.isBit(kvalmask)) {
            val indice = HAL.readBits(keymask)
            character = teclado[indice]
            HAL.setBits(kackmask)
            while (HAL.isBit(kvalmask)) {
            }
            HAL.clrBits(kackmask)
        }
        return character
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















/*
    // Retorna a tecla premida, caso ocorra antes do 'timeout' (em milissegundos),
    // ou NONE caso contrario.
    fun waitKey(timeout: Long): Char {
        val start = System.nanoTime()
        while (((timeout * 10_000_000 - start) < 0) and (getKey() == NONE.toChar())){
            return getKey()
        }
        return NONE.toChar()
    }

 */









/*

object KBD {
    const val NONE = 0
    const val MASK  = 0x0f

    fun init (){}

    val teclado = charArrayOf(
        '1', '2', '3', 'A',
        '4', '5', '6', 'B',
        '7', '8', '9', 'C',
        '*', '0', '#', 'D')

    fun getKey(): Char {
        val rcv = HAL.readBits(MASK)
        if (rcv == 0) return NONE.toChar()
        return teclado[rcv]
    }

    fun waitKey(timeout: Long): Char {
        while (Time.getTimeInMillis()  < timeout) {
            val key = getKey()
            if (key != NONE.toChar()) return key
        }
        return NONE.toChar()
    }
}
*/