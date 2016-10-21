package isel.pdm.trab.openweathermap.models

import com.fasterxml.jackson.annotation.JsonProperty

class CurrentWeatherDto(
        @JsonProperty("name")      val location: String,
        @JsonProperty("id")      val locationId: Int,
        @JsonProperty("coord")   val coord: Coordinates,
        @JsonProperty("weather") val shortInfo: Collection<WeatherShortInfo>,
        @JsonProperty("main")    val info: WeatherInfo,
        @JsonProperty("wind")    val windDetail: WindDetail,
        @JsonProperty("clouds")  val cloudDetail: CloudDetail,
        @JsonProperty("rain")    val rainDetail: RainDetail?,
        @JsonProperty("snow")    val snowDetail: SnowDetail?,
        @JsonProperty("dt")      val utc: Int,
        @JsonProperty("sys")     val locDetail: LocationDetail
){}