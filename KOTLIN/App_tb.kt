// App_tb.kt
fun tAPP(s: String, ok: Boolean) = println("${if (ok) "ok  " else "FAIL"} $s")

fun main() {
    // formatPrice
    tAPP("225 -> 2.25", APP.formatPrice(225) == "2.25")
    tAPP("100 -> 1.00", APP.formatPrice(100) == "1.00")
    tAPP("5   -> 0.05", APP.formatPrice(5)   == "0.05")
    tAPP("0   -> 0.00", APP.formatPrice(0)   == "0.00")

    // enum Station
    tAPP("Lisboa = 225", APP.Station.LISBOA.price == 225)
    tAPP("Berlin = 100", APP.Station.BERLIN.price == 100)
    tAPP("Lisboa.name", APP.Station.LISBOA.stationName == "Lisboa")
    tAPP("16 estacoes", APP.Station.entries.size == 16)

    // ida vs ida/volta
    val s = APP.Station.LONDON
    tAPP("London ida = 150", s.price == 150)
    tAPP("London i/v = 300", s.price * 2 == 300)

    // wrap A/B
    val n = APP.Station.entries.size
    tAPP("A de 0 -> ${n-1}", ((0 - 1 + n) % n) == n - 1)
    tAPP("B de ${n-1} -> 0", ((n - 1 + 1) % n) == 0)
}