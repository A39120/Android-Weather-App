package isel.pdm.trab.openweathermap.models

import com.fasterxml.jackson.annotation.JsonProperty

class FiveDaysWeatherDto(
        @JsonProperty("city") val cityDetail: CityDetail,
        @JsonProperty("cnt")  val nForecasts: Int,
        @JsonProperty("list") val forecastDetail: Collection<ForecastDetail>
){

    data class ForecastDetail(
            @JsonProperty("dt")      val utc:         Int,
            @JsonProperty("main")    val info:        WeatherInfo,
            @JsonProperty("weather") val shortInfo:   Collection<WeatherShortInfo>,
            @JsonProperty("wind")    val windDetail:  WindDetail,
            @JsonProperty("clouds")  val cloudDetail: CloudDetail,
            @JsonProperty("rain")    val rainDetail:  RainDetail?,
            @JsonProperty("snow")    val snowDetail:  SnowDetail?,
            @JsonProperty("dt_txt")  val dateText:    String
    ){}

    data class CityDetail(
            @JsonProperty("id")      val id:              Int,
            @JsonProperty("name")    val cityName:        String,
            @JsonProperty("coord")   val cityCoordinates: Coordinates,
            @JsonProperty("country") val country:         String
    ){}
}