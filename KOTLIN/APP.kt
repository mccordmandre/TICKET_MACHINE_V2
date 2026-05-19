


object APP {
    fun init() {
        HAL.init()
        SerialEmitter.init()
        LCD.init()
        KBD.init()
        TicketDispenser.init()
    }

    // Escreve uma mensagem no LCD (linha 0 e linha 1)
    fun writeCountryT(country: String, idx: Char, price: String) {
        TUI.writeInCursor(0 ,5,country)
        TUI.writeInCursor(1 ,0,idx.toString())
        TUI.writeInCursor(1 ,11,price)
    }

    fun idleDisplay(){
        if (TUI.readKey() == '0'){ // nenhuma decla carregada mas o get.key ta sempre a ser usada
            TUI.writeInCursor(0 ,1," TICKET TO RIDE")
            TUI.writeInCursor(1 ,0,1023.toString())
            TUI.writeInCursor(1 ,11,1030.toString())
        }
    }

    // a primeira tecla ira ser sempre lisboa
/*
    fun isLISBON(){
        if ()

    }
*/
    // se clicamso no #, inicia o processo de compra onde seleciona a ida e volta e se ele receber moedas diminui no cursos
   /* fun compra(){
        if (TUI.readKey() == '#'){
        }
    }
    // se antes de colocr o dinheiro ou seja depois de dar o colectt, se primir em # cancela e diz "vending aborted , return ( o dinheiro que esta no mialheiro )
// depois de colocar todo o dinheiro, passa para processing e colectar o ticket
    */
}


fun main() {

    APP.init()

    when (TUI.getKey()) {
        '1' -> APP.writeCountryT(stations[1], TUI.getKey(),"2.00€")
        '2' -> APP.writeCountryT(stations[2], TUI.getKey(),"2.00€")
        '3' -> APP.writeCountryT(stations[3], TUI.getKey(),"2.00€")


    }
    LCD.clear()



}