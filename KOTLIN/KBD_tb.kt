// KBD_tb.kt
import isel.leic.utils.Time

fun tKBD(s: String, ok: Boolean) = println("${if (ok) "ok  " else "FAIL"} $s")

fun main() {
    // mapping
    val k = KBD.teclado
    tKBD("idx 0 -> '1'",  k[0] == '1')
    tKBD("idx 3 -> '*'",  k[3] == '*')
    tKBD("idx 7 -> '0'",  k[7] == '0')
    tKBD("idx 11 -> '#'", k[11] == '#')
    tKBD("idx 12 -> 'A'", k[12] == 'A')
    tKBD("idx 15 -> 'D'", k[15] == 'D')

    // NONE
    tKBD("NONE != '0'",   KBD.NONE.toChar() != '0')
    tKBD("NONE == \\u0", KBD.NONE.toChar() == '\u0000')

    // getKey sem tecla
    HAL.init(); KBD.init()
    tKBD("getKey vazio = NONE", KBD.getKey() == KBD.NONE.toChar())

    // waitKey com timeout
    val t0 = Time.getTimeInMillis()
    val r = KBD.waitKey(500)
    val dt = Time.getTimeInMillis() - t0
    tKBD("waitKey 500 = NONE", r == KBD.NONE.toChar())
    tKBD("waitKey 500 ~500ms", dt in 400..700)

    // manual
    println("\n-- carrega tecla 5 --")
    val key = KBD.waitKey(10000)
    if (key == KBD.NONE.toChar()) println("skip timeout")
    else tKBD("tecla = '5'", key == '5')
}