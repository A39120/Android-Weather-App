package isel.pdm.trab.openweathermap.services

import android.app.Application
import android.content.ContentResolver
import com.android.volley.toolbox.Volley
import isel.pdm.trab.openweathermap.MyWeatherApp
import isel.pdm.trab.openweathermap.comms.GetRequest
import isel.pdm.trab.openweathermap.models.CurrentWeatherDto
import isel.pdm.trab.openweathermap.models.content.WeatherProvider
import isel.pdm.trab.openweathermap.models.content.toContentValues
import isel.pdm.trab.openweathermap.models.content.toCurrentWeatherDto
import isel.pdm.trab.openweathermap.utils.UrlBuilder

class CurrentInfoGetter(val application: Application, val contentResolver: ContentResolver) {

    fun getCurrentDayInfo(city: String): CurrentWeatherDto? {
        val url = UrlBuilder().buildWeatherByCityUrl(application.resources, city)

        var weather = getCurrentFromCache(url)
        if(weather == null){
            weather = getCurrentFromProvider(city)
            if(weather != null)
                (application as MyWeatherApp).lruDtoCache.put(url, weather)
        }
        return weather
    }

    fun forceUpdateCurrentDayInfoInProvider(city: String){
        val url = UrlBuilder().buildWeatherByCityUrl(MyWeatherApp@this.application.resources, city)

        Volley.newRequestQueue(MyWeatherApp@this.application).add(
            GetRequest(
                url,
                { weather ->
                    val tableUri = WeatherProvider.CURRENT_CONTENT_URI
                    val contentResolver = contentResolver
                    val location = weather.location
                    val selectionArgs = arrayOf(location)
                    val projection = arrayOf(WeatherProvider.COLUMN_LOCATION)
                    val cursor = contentResolver.query(
                            tableUri,
                            projection,
                            WeatherProvider.COLUMN_LOCATION + "=?",
                            selectionArgs,
                            WeatherProvider.COLUMN_LOCATION + " ASC"
                    )

                    if(cursor.count == 0){
                        val wcv = weather.toContentValues()
                        contentResolver.insert(tableUri, wcv)
                    }
                    else{   // It already contains data, update instead
                        contentResolver.update(
                                tableUri,
                                weather.toContentValues(),
                                WeatherProvider.COLUMN_LOCATION + "=?",
                                selectionArgs
                        )
                    }
                    cursor.close()
                },
                { error -> System.out.println("Error refreshing currentday info?")},
                CurrentWeatherDto::class.java)
        )
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
        val cursor = contentResolver.query(
                tableUri,
                null,
                WeatherProvider.COLUMN_LOCATION + "=?",
                selectionArgs,
                WeatherProvider.COLUMN_LOCATION + " ASC"
        )

        if(cursor.count == 0) return null
        else{
            cursor.moveToFirst()
            val cwd = toCurrentWeatherDto(cursor)
            cursor.close()
            return cwd
        }
    }
}