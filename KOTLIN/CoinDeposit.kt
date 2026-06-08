import java.io.File


object CoinDeposit {

    //start empty coin array before loading coins from deposit txt
    val array_coin = mutableListOf<coin>()

    init{
        load("CoinDeposit.txt")
    }


    // inicuamos uma class coin que obriga o seu valor a estar num dos valores do CoinAcceptor.coinArray
    data class coin (val coinvalue: Int, var ammount: Int) {

        init{
            require(coinvalue in CoinAcceptor.coinArray)
        }

    }



    // loada o txt e vai chamando o parser até
    fun load(filename: String){
        //lista de linhas
        val file = File(filename).readLines()

        for (line in file){
            if (line != null) {
                val coin = parser(line)
                array_coin.add(coin)
            }
        }

        //lê linha a linha

        //usa parser() para converter para coin

        //enche array_coin com o deposit inteiro


    }


    fun save(filename: String){
        var text = mutableListOf<String>()
        for (coin in array_coin){
            text.add(writeToLine(coin))
        }
        //joinToString recebe uma lista devolve uma string
        File(filename).writeText(text.joinToString("\n"))
    }





    fun addCoin(coinvalue: Int) {
        for (coin in array_coin){
            if (coinvalue == coin.coinvalue){
                coin.ammount += 1
            }
        }
    }

    /*
    //buffer temporário com as coins inseridas
    // devia estar no coin acceptor???????????????
    class bufferedCoins(val coinType: Int, ){

    }
     */



    // passa string de coin tipo "5;0" -> coin
    fun parser(string: String): coin{
        val valueammount_string_List = string.split(";")
        return coin(valueammount_string_List[0].toInt(), valueammount_string_List[1].toInt())
    }

    // passa a coin para linha
    //faz o contrário do parser
    fun writeToLine(coin: coin): String{
        val coin_string = "${coin.coinvalue};${coin.ammount}"
        return coin_string
    }

    // chamado pelo coin acceptor deposita a lista inteira
    fun depositCoins (inserted_coins: List<Int>) {
        //agora vou transformar a lista de que vweio de acceptor em coins,e depois guardo-as no array_coin
        for (i in inserted_coins){
            addCoin(i)
        }
    }







}

fun main(){
    CoinDeposit.depositCoins(listOf(5, 10, 100))
    CoinDeposit.save("CoinDeposit.txt")
}