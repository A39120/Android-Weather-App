package isel.pdm.trab.openweathermap.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import isel.pdm.trab.openweathermap.MyWeatherApp
import isel.pdm.trab.openweathermap.models.content.WeatherProvider
import isel.pdm.trab.openweathermap.models.content.toForecastWeatherDto
import isel.pdm.trab.openweathermap.utils.UrlBuilder

class FavNotificationService : Service() {

    val BROADCAST_ACTION = "isel.pdm.trab.openweathermap.FORECAST_SERVICE"

    override fun onBind(intent: Intent): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val location: String = intent?.getStringExtra("FORECAST_CITY_NOTIFY") as String
        val country: String = intent?.getStringExtra("FORECAST_COUNTRY_NOTIFY") as String

        val tableUri = WeatherProvider.FORECAST_CONTENT_URI
        val selection = "${WeatherProvider.COLUMN_LOCATION}=? AND " +
                "${WeatherProvider.COLUMN_COUNTRY_CODE}=?"

        val selectionArgs = arrayOf(location, country)

        val cursor = contentResolver.query(
                tableUri,
                null,
                selection,
                selectionArgs,
                "${WeatherProvider.COLUMN_UTC} ASC"
        )

        if(cursor.count != 0){
            cursor.moveToFirst()
            val weather = toForecastWeatherDto(cursor)

            val timestamp = (application as MyWeatherApp)
                    .forecastTimestampMap[UrlBuilder().buildForecastByCityUrl(resources, location)] as Long
            val currentTime = System.currentTimeMillis()
            val days = ((currentTime-timestamp)  / (1000*60*60*24)).toInt()

            if(days < 6){

                val myIntent = Intent()
                myIntent.action = BROADCAST_ACTION
                myIntent.putExtra("WEATHER_FOR_NOTIFY", weather.forecastDetail[days+1])
                myIntent.putExtra("LOCATION_FOR_NOTIFY", "$location, $country.")
                sendBroadcast(myIntent)
            }
        }
        return START_REDELIVER_INTENT
    }
}
