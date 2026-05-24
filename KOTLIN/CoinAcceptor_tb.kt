// CoinAcceptor_tb.kt
import isel.leic.utils.Time

fun tCA(s: String, ok: Boolean) = println("${if (ok) "ok  " else "FAIL"} $s")

fun main() {
    // tabela de valores
    val a = CoinAcceptor.coinArray
    tCA("cid=0 -> 5c",   a[0] == 5)
    tCA("cid=1 -> 10c",  a[1] == 10)
    tCA("cid=2 -> 20c",  a[2] == 20)
    tCA("cid=3 -> 50c",  a[3] == 50)
    tCA("cid=4 -> 100c", a[4] == 100)
    tCA("cid=5 -> 200c", a[5] == 200)
    tCA("array tem 6", a.size == 6)

    // apos init sinais a 0
    HAL.init(); CoinAcceptor.init()
    tCA("accept=0",  (HAL.lastOutput and 0x10) == 0)
    tCA("collect=0", (HAL.lastOutput and 0x40) == 0)
    tCA("eject=0",   (HAL.lastOutput and 0x20) == 0)

    // collect liga 2s e desliga
    println("-- collect 2s --")
    CoinAcceptor.collect()
    tCA("collect desligou", (HAL.lastOutput and 0x40) == 0)

    println("-- eject 2s --")
    CoinAcceptor.eject()
    tCA("eject desligou", (HAL.lastOutput and 0x20) == 0)

    // manual: insere moeda no SimDig
    println("\n-- insere uma moeda de 50c (cid=011) --")
    val limit = Time.getTimeInMillis() + 10000
    while (!CoinAcceptor.checkCoin()) {
        if (Time.getTimeInMillis() > limit) { println("skip timeout"); return }
    }
    tCA("moeda lida = 50", CoinAcceptor.readCoin() == 50)
    CoinAcceptor.coinAccept()
}