package isel.pdm.trab.openweathermap.models

import com.fasterxml.jackson.annotation.JsonProperty

open class WeatherDto(val location: String){}

data class Coordinates(
        @JsonProperty("lon") val longitude: Double,
        @JsonProperty("lat") val latitude:  Double
){}

data class WeatherShortInfo(
        @JsonProperty("id")          val id:          Int,
        @JsonProperty("main")        val mainWeather: String,
        @JsonProperty("description") val description: String,
        @JsonProperty("icon")        val icon:        String
){}

data class WeatherInfo(
        @JsonProperty("temp")     val temperature: Double,
        @JsonProperty("pressure") val pressure:    Double,
        @JsonProperty("humidity") val humidity:    Int,
        @JsonProperty("temp_min") val minTemp:     Double,
        @JsonProperty("temp_max") val maxTemp:     Double
){}

data class WindDetail(
        @JsonProperty("speed") val speed:       Double,
        @JsonProperty("deg")   val windDegrees: Double
){}

data class CloudDetail(
        @JsonProperty("all") val clouds: Int
){}

data class RainDetail(
        @JsonProperty("3h") val rainVolume: Int
){}

data class SnowDetail(
        @JsonProperty("3h") val snowVolume: Int
){}

data class LocationDetail(
        @JsonProperty("country") val countryCode: String,
        @JsonProperty("sunrise") val sunriseTime: Int,
        @JsonProperty("sunset")  val sunsetTime:  Int
){}