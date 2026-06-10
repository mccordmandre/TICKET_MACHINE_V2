import java.io.File

/*
funções:
ler os dois files e a cada um vai retornar de uma maneira diferente
  - stations- vai retornar o nome e o preco e os bilhetes vendidos e o idx
  - coins : retornar o preco e quantas venderam apenas

para a main:
- stations funcao que separa apenas oq preciasamos, ou seja nome, idx e preço, podemos ter algo que assim que clica na tecla vai buscar o valor aqui, guardado numa base de dados
- coins: ler as coins dos ficheiro

para a manutencao
- stations : A- nome idx e sold o mecanismo igual p a persquisa mas envez do preco os bilhetes ja vendidos
- coins : B- o mesmo que o a , os coins que temos no ficheiro o idx e quantas ja venderam
- os dois: mecanismo do mesmo dor purchase no entanto nao precisa de inserir moedas, para dar print do ticket aquelas clilcar no *
- afetar os ficheiros- D- ao cliclar no D vai dar reset do contador nos ficheiros
 */
data class CoinData(val valueCents: Int, var count: Int)
data class StationData(val priceCents: Int, var sold: Int, val name: String)

object FileAccess {

    fun init() {}
    fun loadCoins(path: String): MutableList<CoinData> {
        val result = mutableListOf<CoinData>()
        File(path).forEachLine { line ->
            val parts = line.trim().split(";")
            if (parts.size == 2) {
                val value = parts[0].trim().toIntOrNull()
                val count = parts[1].trim().toIntOrNull()
                if (value != null && count != null)
                    result.add(CoinData(value, count))
            }
        }
        return result
    }

    fun loadStations(path: String): MutableList<StationData> {
        val result = mutableListOf<StationData>()
        File(path).forEachLine { line ->
            val parts = line.trim().split(";")
            if (parts.size == 3) {
                val price = parts[0].trim().toIntOrNull()
                val sold  = parts[1].trim().toIntOrNull()
                val name  = parts[2].trim()
                if (price != null && sold != null)
                    result.add(StationData(price, sold, name))
            }
        }
        return result
    }

    fun saveCoins(path: String, list: MutableList<CoinData>) {
        File(path).writeText(
            list.joinToString("\n") { "${it.valueCents};${it.count}" }
        )
    }

    fun saveStations(path: String, list: MutableList<StationData>) {
        File(path).writeText(
            list.joinToString("\n") { "${it.priceCents};${it.sold};${it.name}" }
        )
    }

    fun resetCoins(path: String, list: MutableList<CoinData>) {
        list.forEach { it.count = 0 }
        saveCoins(path, list)
    }

    fun resetStations(path: String, list: MutableList<StationData>) {
        list.forEach { it.sold = 0 }
        saveStations(path, list)
    }

}