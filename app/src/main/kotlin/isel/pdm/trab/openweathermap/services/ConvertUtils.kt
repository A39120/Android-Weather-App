package isel.pdm.trab.openweathermap.services

import java.text.SimpleDateFormat
import java.util.*

class ConvertUtils(){

    fun convertTempCToF(celsius: Double): Double{
        val fahrenheit = celsius * (9/5) + 32
        return fahrenheit
    }

    fun convertTempFToC(fahrenheit: Double): Double{
        val celsius = (fahrenheit - 32) * (5/9)
        return celsius
    }

    fun convertUnixToTime(unixTime: Long): String{
        val dateTime: Date = Date(unixTime*1000)
        val localDateFormat = SimpleDateFormat("HH:mm")
        return localDateFormat.format(dateTime)
    }

    fun convertUnixToDateTime(unixTime: Long): String{
        val dateTime: Date = Date(unixTime*1000)
        val localDateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
        return localDateFormat.format(dateTime)
    }

    fun convertDegreesToTextDescription(degree: Double): String {
        val directions = arrayOf("N", "NE", "E", "SE", "S", "SW", "W", "NW", "N")
        return directions[Math.round(degree % 360 / 45).toInt()]
    }
}
