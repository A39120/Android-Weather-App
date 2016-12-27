package isel.pdm.trab.openweathermap.services

import android.app.Application
import android.content.ContentResolver
import isel.pdm.trab.openweathermap.MyWeatherApp
import isel.pdm.trab.openweathermap.models.ForecastWeatherDto
import isel.pdm.trab.openweathermap.models.content.WeatherProvider
import isel.pdm.trab.openweathermap.models.content.toForecastWeatherDto
import isel.pdm.trab.openweathermap.utils.UrlBuilder

class ForecastInfoGetter(val application: Application, val contentResolver: ContentResolver) {

    fun getForecastInfo(city: String): ForecastWeatherDto? {
        val url = UrlBuilder().buildForecastByCityUrl(application.resources, city)

        var weather = getForecastFromCache(url)
        if(weather == null){
            weather =  getForecastFromProvider(city)
        }
        return weather
    }

    private fun getForecastFromCache(url: String): ForecastWeatherDto?{
        val apl = (application as MyWeatherApp)

        if(apl.lruDtoCache.contains(url))
            return apl.lruDtoCache[url] as ForecastWeatherDto
        return null
    }

    private fun getForecastFromProvider(city: String): ForecastWeatherDto? {
        val tableUri = WeatherProvider.FORECAST_CONTENT_URI
        val selection = "${WeatherProvider.COLUMN_LOCATION}=? AND " +
                "${WeatherProvider.COLUMN_COUNTRY_CODE}=?"

        val selectionArgs = arrayOf(city)

        val cursor = contentResolver.query(
                tableUri,
                null,
                selection,
                selectionArgs,
                "${WeatherProvider.COLUMN_UTC} ASC"
        )

        if (cursor.count != 0) {
            cursor.moveToFirst()
            return toForecastWeatherDto(cursor)
        }
        return null
    }
}
