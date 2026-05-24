// HAL_tb.kt
fun tHal(s: String, ok: Boolean) = println("${if (ok) "ok  " else "FAIL"} $s")

fun main() {
    HAL.init()
    tHal("init -> 0", HAL.lastOutput == 0)

    HAL.setBits(0x0F);             tHal("setBits 0x0F", HAL.lastOutput == 0x0F)
    HAL.setBits(0xF0);             tHal("setBits 0xF0 acumula", HAL.lastOutput == 0xFF)
    HAL.clrBits(0x0F);             tHal("clrBits 0x0F", HAL.lastOutput == 0xF0)
    HAL.clrBits(0xFF);             tHal("clrBits tudo", HAL.lastOutput == 0)

    // writeBits so toca os bits da mask
    HAL.writeBits(0x0F, 0xFF);     tHal("writeBits respeita mask", HAL.lastOutput == 0x0F)
    HAL.writeBits(0xF0, 0xA0);     tHal("writeBits mantem bits 0-3", HAL.lastOutput == 0xAF)
    HAL.writeBits(0x0F, 0x00);     tHal("writeBits a 0 limpa mask", HAL.lastOutput == 0xA0)

    // isBit nao testado isoladamente: read e write tocam pinos diferentes (I vs O)
}