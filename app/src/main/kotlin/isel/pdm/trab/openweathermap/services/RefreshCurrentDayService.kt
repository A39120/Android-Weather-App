package isel.pdm.trab.openweathermap.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.android.volley.toolbox.Volley
import isel.pdm.trab.openweathermap.MyWeatherApp
import isel.pdm.trab.openweathermap.comms.GetRequest
import isel.pdm.trab.openweathermap.models.CurrentWeatherDto
import isel.pdm.trab.openweathermap.models.content.WeatherProvider
import isel.pdm.trab.openweathermap.models.content.toContentValues

class RefreshCurrentDayService : Service() {

    override fun onBind(intent: Intent): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val currentCity: String = intent?.getStringExtra("CURRENT_CITY") as String
        val url = UrlBuilder().buildWeatherByCityUrl(resources, currentCity)

        Volley.newRequestQueue(this).add(
                GetRequest(
                        url,
                        { weather ->
                            val tableUri = WeatherProvider.CURRENT_CONTENT_URI
                            contentResolver.insert(tableUri, weather.toContentValues())

                            val projection = arrayOf(WeatherProvider.COLUMN_CURRENT_LOCATION)
                            val selectionArgs = arrayOf("")
                            val cursor = contentResolver.query(
                                    WeatherProvider.CURRENT_CONTENT_URI,
                                    projection,
                                    null,
                                    selectionArgs,
                                    "COLUMN_CURRENT_LOCATION ASC"
                            )
                            println(cursor.count)
                            // TODO: put weather in ContentProvider
                        },
                        { error -> System.out.println("Error in refresh currentday service?")},
                        CurrentWeatherDto::class.java)
        )

        return START_REDELIVER_INTENT
    }
}
