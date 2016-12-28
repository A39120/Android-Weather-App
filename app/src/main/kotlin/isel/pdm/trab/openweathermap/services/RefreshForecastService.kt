package isel.pdm.trab.openweathermap.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import isel.pdm.trab.openweathermap.MyWeatherApp

class RefreshForecastService : Service() {

    companion object {
        val FORECAST_CITY = "FORECAST_CITY"
    }

    override fun onBind(intent: Intent): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if(!MyWeatherApp.isBatterySavingMode) {
            val currentCity: String = intent?.getStringExtra(FORECAST_CITY) as String
            (application as MyWeatherApp).forecastInfoGetter?.forceUpdateForecastInfoInProvider(currentCity)
        }

        return START_REDELIVER_INTENT
    }
}
