object Stations {
    private var path: String = ""
    private var list: MutableList<StationData> = mutableListOf()

    fun init(stationsPath: String) {
        path = stationsPath
        list = FileAccess.loadStations(path)
    }

    fun count(): Int = list.size

    fun getName(idx: Int): String = list[idx].name

    fun getPrice(idx: Int): Int = list[idx].priceCents

    fun getSold(idx: Int): Int = list[idx].sold

    fun addSold(idx: Int) {
        list[idx].sold++
    }

    fun reset() = FileAccess.resetStations(path, list)

    fun save() = FileAccess.saveStations(path, list)
}