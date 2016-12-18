package isel.pdm.trab.openweathermap.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.widget.Toast
import com.android.volley.toolbox.Volley
import isel.pdm.trab.openweathermap.MyWeatherApp
import isel.pdm.trab.openweathermap.comms.GetRequest
import isel.pdm.trab.openweathermap.models.ForecastWeatherDto

class RefreshForecastService : Service() {

    val BROADCAST_ACTION = "isel.pdm.trab.openweathermap.FORECAST_SERVICE"

    override fun onBind(intent: Intent): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val currentCity: String = intent?.getStringExtra("FORECAST_CITY") as String
        val url = UrlBuilder().buildForecastByCityUrl(resources, currentCity)

        Volley.newRequestQueue(this).add(
                GetRequest(
                        url,
                        { weather ->
                            // TODO: weather is to be stored in content provider, this is just a test
                            val myIntent = Intent()
                            myIntent.action = BROADCAST_ACTION
                            myIntent.putExtra("WEATHER_FOR_NOTIFY", weather.forecastDetail[1])
                            sendBroadcast(myIntent)
                        },
                        { error -> System.out.println("Error in refresh current service?")},
                        ForecastWeatherDto::class.java)
        )

        return START_REDELIVER_INTENT
    }
}
