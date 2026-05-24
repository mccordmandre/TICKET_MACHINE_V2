// LCD_tb.kt
import isel.leic.utils.Time

fun main() {
    LCD.init()
    Time.sleep(500)

    // cantos
    LCD.cursor(0, 0);  LCD.write("1")
    LCD.cursor(0, 15); LCD.write("2")
    LCD.cursor(1, 0);  LCD.write("3")
    LCD.cursor(1, 15); LCD.write("4")
    println("visual: cantos 1 2 / 3 4")
    Time.sleep(2000)

    // linhas cheias
    LCD.clear()
    LCD.cursor(0, 0); LCD.write("ABCDEFGHIJKLMNOP")
    LCD.cursor(1, 0); LCD.write("0123456789ABCDEF")
    println("visual: linhas cheias")
    Time.sleep(2000)

    // custom chars (setas + euro)
    LCD.clear()
    LCD.cursor(0, 0); LCD.write("\u0000\u0001\u0002")
    println("visual: seta cima, seta baixo, euro")
    Time.sleep(2000)

    // ecra tipico da app
    LCD.clear()
    LCD.cursor(0, 1);  LCD.write("TICKET TO RIDE")
    LCD.cursor(1, 0);  LCD.write("0\u0000\u0001")
    LCD.cursor(1, 12); LCD.write("2.25\u0002")
    println("visual: homescreen")
    Time.sleep(3000)

    LCD.clear()
}