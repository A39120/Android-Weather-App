package isel.pdm.trab.openweathermap.services

import android.app.Application
import android.content.ContentResolver
import android.database.DatabaseUtils
import com.android.volley.toolbox.Volley
import isel.pdm.trab.openweathermap.MyWeatherApp
import isel.pdm.trab.openweathermap.comms.GetRequest
import isel.pdm.trab.openweathermap.models.ForecastWeatherDto
import isel.pdm.trab.openweathermap.models.content.WeatherProvider
import isel.pdm.trab.openweathermap.models.content.toContentValues
import isel.pdm.trab.openweathermap.models.content.toForecastWeatherDto
import isel.pdm.trab.openweathermap.utils.ConvertUtils
import isel.pdm.trab.openweathermap.utils.UrlBuilder

class ForecastInfoGetter(val application: Application, val contentResolver: ContentResolver) {

    fun getForecastInfo(city: String): ForecastWeatherDto? {
        val location = city.capitalize()
        val url = UrlBuilder().buildForecastByCityUrl(application.resources, location)

        var weather = getForecastFromCache(url)
        if(weather == null)
            weather =  getForecastFromProvider(location)

        return weather
    }

    fun forceUpdateForecastInfoInProvider(city: String){
        val url = UrlBuilder().buildForecastByCityUrl(MyWeatherApp@this.application.resources, city)

        Volley.newRequestQueue(MyWeatherApp@this.application).add(
            GetRequest(
                url,
                { weather ->
                    val tableUri = WeatherProvider.FORECAST_CONTENT_URI

                    val location = weather.cityDetail.cityName
                    val country = weather.cityDetail.country
                    val selection = "${WeatherProvider.COLUMN_LOCATION}=? AND " +
                            "${WeatherProvider.COLUMN_COUNTRY_CODE}=?"

                    val selectionArgs = arrayOf(location, country)
                    val projection = arrayOf(WeatherProvider.COLUMN_ID ,WeatherProvider.COLUMN_UTC)
                    // check if day-month-year is already in db
                    val cursor = contentResolver.query(
                            tableUri,
                            projection,
                            selection,
                            selectionArgs,
                            "${WeatherProvider.COLUMN_UTC} ASC"
                    )
                    val wcv = weather.toContentValues()
                    var count = 0

                    if(cursor.count == 0){
                        for(element in wcv)
                            contentResolver.insert(tableUri, element)
                    }
                    else{
                        cursor.moveToNext()
                        //check if data of the day is already there
                        val current_utc = weather.forecastDetail[count].utc
                        val current_date = ConvertUtils.convertUnixToDate(current_utc)
                        val content_utc = cursor.getLong(1)     // cursor only has 2 columns
                        val content_date = ConvertUtils.convertUnixToDate(content_utc * 1000)

                        if(content_utc < current_utc){
                            contentResolver.delete(tableUri,
                                    selection,
                                    selectionArgs)
                        }
                        cursor.moveToFirst()

                        //Insert the others that didn't exist
                        while(count < weather.forecastDetail.size){
                            contentResolver.insert(tableUri, wcv[count])
                            count++
                        }
                    }
                    cursor.close()
                },
                { error -> System.out.println("Error in refresh forecast service?")},
                ForecastWeatherDto::class.java)
        )
    }

    private fun getForecastFromCache(url: String): ForecastWeatherDto?{
        val apl = (application as MyWeatherApp)

        if(apl.lruDtoCache.contains(url))
            return apl.lruDtoCache[url] as ForecastWeatherDto
        return null
    }

    private fun getForecastFromProvider(city: String): ForecastWeatherDto? {
        val tableUri = WeatherProvider.FORECAST_CONTENT_URI
        val selection = "${WeatherProvider.COLUMN_LOCATION}=?"

        val selectionArgs = arrayOf(city)

        val cursor = contentResolver.query(
                tableUri,
                null,
                selection,
                selectionArgs,
                "${WeatherProvider.COLUMN_UTC} ASC"
        )
        val a2 = DatabaseUtils.dumpCursorToString(cursor)
        if (cursor.count == 0) return null
        else {
            cursor.moveToFirst()
            val fwd = toForecastWeatherDto(cursor)
            cursor.close()
            return fwd
        }
    }
}
