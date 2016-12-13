package isel.pdm.trab.openweathermap.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.android.volley.toolbox.Volley
import isel.pdm.trab.openweathermap.MyWeatherApp
import isel.pdm.trab.openweathermap.comms.GetRequest
import isel.pdm.trab.openweathermap.models.ForecastWeatherDto

class RefreshForecastService : Service() {

    override fun onBind(intent: Intent): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val currentCity: String = intent?.getStringExtra("FORECAST_CITY") as String
        val url = UrlBuilder().buildWeatherByCityUrl(resources, currentCity)
        val apl = (application as MyWeatherApp)

        Volley.newRequestQueue(this).add(
                GetRequest(
                        url,
                        { weather ->
                            // TODO: put weather in ContentProvider
                        },
                        { error -> System.out.println("Error in response?")},
                        ForecastWeatherDto::class.java)
        )

        return START_REDELIVER_INTENT
    }
}
