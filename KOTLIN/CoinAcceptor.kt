
import isel.leic.utils.Time

object CoinAcceptor {


    /*
    UsbPort.O4 -> ca.accept --      0001_0000 OUTPUT    ACCEPT_MASK = 0x10
    UsbPort.O6 -> ca.collect --     0100_0000 OUTPUT    COLLECT_MASK = 0x40
    UsbPort.O5 -> ca.eject --       0010_0000 OUTPUT    EJECT_MASK = 0x20
    ca.coin -> UsbPort.I3 --        0000_1000 INPUT     COIN_MASK = 0x08
    ca.cid[0-2] -> UsbPort.I[0-2] --0000_0111 INPUT     COIN_ID_MASK = 0x07
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
    arr || cents
    000 - 005
    001 - 010
    010 - 020
    011 - 050
    100 - 100
    101 - 200
     */


    val inserted_coins = mutableListOf<Int>()

    fun init(){
        HAL.clrBits(ACCEPT_MASK)
        HAL.clrBits(COLLECT_MASK)
        HAL.clrBits(EJECT_MASK)
    }

    // APP vai usar no polling e CoinAccept vai usar no
    fun checkCoin(): Boolean {
        if (HAL.isBit(COIN_MASK)) return true
        return false
    }


    // Adiciona coin à lista inserted_coins
    fun readCoin(): Int?{
        if (checkCoin()){
            val coinvalue = coinArray[HAL.readBits(COIN_ID_MASK)]
            inserted_coins.add(coinvalue)
            return coinvalue
        }
        return null
    }


    // para ser usada pela funcao depositCoin - quando deposita envia no CoinDeposit então manda accept para o vhdl
    fun coinAccept() {
        //não sei se este if é necessário
        //está bloqueante?
        if (checkCoin()){
            HAL.setBits(ACCEPT_MASK)
            while(checkCoin() == true)
            HAL.clrBits(ACCEPT_MASK)
        }
    }

    //collect
    fun collect () {
        HAL.setBits(COLLECT_MASK)
        Time.sleep(2000)
        HAL.clrBits(COLLECT_MASK)
        CoinDeposit.depositCoins(inserted_coins)
        inserted_coins.clear()
    }

    //eject
    fun eject () {
        HAL.setBits(EJECT_MASK)
        Time.sleep(2000)
        HAL.clrBits(EJECT_MASK)
        inserted_coins.clear()
    }








}


fun main() {

}
