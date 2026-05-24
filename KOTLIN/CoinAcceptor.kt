
import isel.leic.utils.Time

object CoinAcceptor {


    /*
    UsbPort.O4 -> ca.accept
    UsbPort.O6 -> ca.collect
    UsbPort.O5 -> ca.eject
    ca.coin -> UsbPort.I3
    ca.cid[0-2] -> UsbPort.I[0-2]
     */

    //saidas do controlo
    private val ACCEPT_MASK = 0x10
    private val COLLECT_MASK = 0x40
    private val EJECT_MASK = 0x20

    //Entradas no controlo
    //sinal coin
    private val COIN_MASK = 0x08
    // 3 bits [2:0]
    private val COIN_ID_MASK = 0x07



    val coinArray = arrayOf(5, 10, 20, 50, 100, 200)

    /*
    000 - 005 cent
    001 - 010
    010 - 020
    011 - 050
    100 - 100
    101 - 200
     */


    fun init(){
        HAL.clrBits(ACCEPT_MASK)
        HAL.clrBits(COLLECT_MASK)
        HAL.clrBits(EJECT_MASK)
    }

    //APP vai usar no polling e CoinAccept vai usar no
    fun checkCoin(): Boolean {
        if (HAL.isBit(COIN_MASK)) return true
        return false
    }

    fun readCoin(): Int{
        return coinArray[HAL.readBits(COIN_ID_MASK)]
    }



    fun coinAccept() {
        if (HAL.isBit(COIN_MASK)){
            HAL.setBits(ACCEPT_MASK)
            HAL.clrBits(ACCEPT_MASK)
        }
    }

    fun depositCoin () {
        val coin_value = readCoin()
        coinAccept()

        //CoinDeposit.depositCoin(coin_value)
    }

    //collect
    fun collect () {
        HAL.setBits(COLLECT_MASK)
        Time.sleep(2000)
        HAL.clrBits(COLLECT_MASK)
    }

    //eject
    fun eject () {
        HAL.setBits(EJECT_MASK)
        Time.sleep(2000)
        HAL.clrBits(EJECT_MASK)
    }









}


fun main() {
    HAL.init()
    CoinAcceptor.init()
    var atual = 0
    val total = 50
    //falta contar como tens no simul 0€ stored in 0 coins




    while(atual < total){
        // user depositou moeda (pode ainda não ter leccionado cidade)
        if(CoinAcceptor.checkCoin()){
            // guarda valor da coin (depositCoin manda accept
            val coin_value = CoinAcceptor.readCoin()
            atual += coin_value
            CoinAcceptor.depositCoin()
        }
    }
    while (true){
        CoinAcceptor.collect()
        CoinAcceptor.coinAccept()
        Time.sleep(2000)
    }


    /*
    // Teste manual: lê uma moeda quando aparecer
    println("À espera de moeda...")
    while (!CoinAcceptor.signalCoin()) { Time.sleep(50) }
    val value = CoinAcceptor.consumeCoin()
    println("Moeda inserida: ${value}c")

    // Devolver depois de 2s
    Time.sleep(2000)
    CoinAcceptor.eject()
    */
}