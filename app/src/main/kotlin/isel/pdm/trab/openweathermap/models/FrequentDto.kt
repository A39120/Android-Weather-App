package isel.pdm.trab.openweathermap.models

import com.fasterxml.jackson.annotation.JsonProperty

data class City(val id: Int,
                val name: String,
                val country: String)

data class TemperatureInfo(val temp: Float,         // Temperature. Unit Default: Kelvin, Metric: Celsius, Imperial: Fahrenheit.
                           val temp_min: Float,     // Minimum temperature at the moment.
                           val temp_max: Float,     // Maximum temperature at the moment.
                           val pressure: Float,     // Atmospheric pressure (on the sea level, if there is no sea_level or grnd_level data)
                           val sea_level: Float,    // Atmospheric pressure on the sea level
                           val grnd_level: Float,   // Atmospheric pressure on the ground level
                           val humidity: Float)     // Humidity, %

data class Weather(val id: Int,             // Weather condition id
                   val main: String,        // Group of weather parameters (Rain, Snow, Extreme etc.)
                   val description: String, // Weather condition within the group
                   val icon: String)        // Weather icon id

data class Clouds(val all: Float)                 //Cloudiness, %
data class Rain(@JsonProperty("3h")val h3: Float) //Rain volume for the last 3 hours
data class Snow(@JsonProperty("3h")val h3: Float) //Snow volume for the last 3 hours
data class Wind(val speed: Float, val deg: Float) // Wind speed. Unit Default: meter/sec