package isel.pdm.trab.openweathermap.services

import android.app.Application
import android.content.ContentResolver
import isel.pdm.trab.openweathermap.MyWeatherApp
import isel.pdm.trab.openweathermap.models.CurrentWeatherDto
import isel.pdm.trab.openweathermap.models.content.WeatherProvider
import isel.pdm.trab.openweathermap.models.content.toCurrentWeatherDto
import isel.pdm.trab.openweathermap.utils.UrlBuilder


class CurrentInfoGetter(val application: Application, val contentResolver: ContentResolver) {

    fun getCurrentDayInfo(city: String): CurrentWeatherDto? {
        val url = UrlBuilder().buildWeatherByCityUrl(application.resources, city)

        var weather = getCurrentFromCache(url)
        if(weather == null){
            weather = getCurrentFromProvider(city)
        }
        return weather
    }

    private fun getCurrentFromCache(url: String): CurrentWeatherDto?{
        val apl = (application as MyWeatherApp)

        if(apl.lruDtoCache.contains(url))
            return apl.lruDtoCache[url] as CurrentWeatherDto
        return null
    }

    private fun getCurrentFromProvider(city: String): CurrentWeatherDto? {
        val tableUri = WeatherProvider.CURRENT_CONTENT_URI
        val selectionArgs = arrayOf(city)
        val projection = arrayOf(WeatherProvider.COLUMN_LOCATION)
        val cursor = contentResolver.query(
                tableUri,
                projection,
                WeatherProvider.COLUMN_LOCATION + "=?",
                selectionArgs,
                WeatherProvider.COLUMN_LOCATION + " ASC"
        )

        if(cursor.count == 0) return null
        else{
            cursor.moveToFirst()
            return toCurrentWeatherDto(cursor)
        }
    }
}