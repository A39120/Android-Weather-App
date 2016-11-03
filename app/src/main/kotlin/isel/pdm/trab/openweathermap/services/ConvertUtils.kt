package isel.pdm.trab.openweathermap.services

import java.text.SimpleDateFormat
import java.util.*

/**
 *  Util class for conversions
 */
class ConvertUtils(){

    /**
     *  Converts a temperature value in celsius to fahrenheit
     *  @param celsius: The temperature in celsius
     *  @returns The temperature in fahrenheit
     */
    fun convertTempCToF(celsius: Double): Double{
        val fahrenheit = celsius * (9/5) + 32
        return fahrenheit
    }

    /**
     *  Converts a temperature value in fahrenheit to celsius
     *  @param fahrenheit: The temperature in fahrenheit
     *  @returns The temperature in celsius
     */
    fun convertTempFToC(fahrenheit: Double): Double{
        val celsius = (fahrenheit - 32) * (5/9)
        return celsius
    }

    /**
     *  Converts a value in unix to an "hour and minutes" format
     *  @param unixtTime: The time in unix, UTC
     *  @returns The hours and minutes (HH:mm)
     */
    fun convertUnixToTime(unixTime: Long): String{
        val dateTime: Date = Date(unixTime*1000)
        val localDateFormat = SimpleDateFormat("HH:mm")
        return localDateFormat.format(dateTime)
    }

    /**
     *  Converts a value in unix to an "date and time" format
     *  @param unixtTime: The time in unix, UTC
     *  @returns The date and time (dd-MM-yyyy HH:mm:ss)
     */
    fun convertUnixToDateTime(unixTime: Long): String{
        val dateTime: Date = Date(unixTime*1000)
        val localDateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
        return localDateFormat.format(dateTime)
    }

    /**
     *  Converts a value in degrees to cardinal description
     *  @param unixtTime: The value in degrees
     *  @returns The cardinal description
     */
    fun convertDegreesToTextDescription(degree: Double): String {
        val directions = arrayOf("N", "NE", "E", "SE", "S", "SW", "W", "NW", "N")
        return directions[Math.round(degree % 360 / 45).toInt()]
    }
}
