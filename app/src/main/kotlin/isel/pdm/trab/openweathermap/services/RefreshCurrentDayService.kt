package isel.pdm.trab.openweathermap.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import isel.pdm.trab.openweathermap.MyWeatherApp

class RefreshCurrentDayService : Service() {

    companion object {
        val CURRENT_CITY = "CURRENT_CITY"
    }

    override fun onBind(intent: Intent): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if(!MyWeatherApp.isBatterySavingMode) {
            val currentCity: String = intent?.getStringExtra(CURRENT_CITY) as String
            (application as MyWeatherApp).currentInfoGetter?.forceUpdateCurrentDayInfoInProvider(currentCity)
        }

        return START_REDELIVER_INTENT
    }
}