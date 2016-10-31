package isel.pdm.trab.openweathermap

import java.text.SimpleDateFormat
import java.util.*

class ConvertUtils(){

    fun convertCToF(celsius: Double): Double{
        val fahrenheit = celsius * (9/5) + 32
        return fahrenheit
    }

    fun convertFToC(fahrenheit: Double): Double{
        val celsius = (fahrenheit - 32) * (5/9)
        return celsius
    }

    fun convertUnixToTime(unixTime: Long): String{
        val dateTime: Date = Date(unixTime*1000)
        val localDateFormat = SimpleDateFormat("HH:mm")
        return localDateFormat.format(dateTime)
    }
}
