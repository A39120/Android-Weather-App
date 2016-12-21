package isel.pdm.trab.openweathermap.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.android.volley.toolbox.Volley
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

                            // Debug code to check if insertion/update was successful
                            val after = contentResolver.query(
                                    tableUri,
                                    projection,
                                    WeatherProvider.COLUMN_LOCATION + "=?",
                                    selectionArgs,
                                    WeatherProvider.COLUMN_LOCATION + " ASC"
                            )
                            println("debug on refreshcurrentdayservice (count):" + after.count)
                            // end of debug code
                        },
                        { error -> System.out.println("Error in refresh currentday service?")},
                        CurrentWeatherDto::class.java)
        )

        return START_REDELIVER_INTENT
    }
}