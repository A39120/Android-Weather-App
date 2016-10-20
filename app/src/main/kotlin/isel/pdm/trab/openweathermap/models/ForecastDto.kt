package isel.pdm.trab.openweathermap.models

import com.fasterxml.jackson.annotation.JsonProperty

//api.openweathermap.org/data/2.5/forecast?id={city ID}
//example: http://api.openweathermap.org/data/2.5/forecast?q=Lisbon&appid=83c7d0f16bdbdd8949896f1be1226258

data class ForecastDto(
        val city: City,
        @JsonProperty("list")val days : Array<DayDto>
){
    data class DayDto(val dt : Int,
                      @JsonProperty("main") val tempInfo: TemperatureInfo,
                      val weather: Array<Weather>,
                      val clouds: Clouds?,
                      val wind: Wind?,
                      val rain: Rain?,
                      val snow: Snow?,
                      @JsonProperty("dt_txt") val info: String
                      )

}