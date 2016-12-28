package isel.pdm.trab.openweathermap.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import isel.pdm.trab.openweathermap.MyWeatherApp
import isel.pdm.trab.openweathermap.utils.UrlBuilder

class WifiReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val connManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        //val net: Network = connManager.activeNetwork // API 23 and above...
        val wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val mobile = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
        val isWifiConnected = wifi != null && wifi.isConnectedOrConnecting
        val isMobileDataConnected = mobile != null && mobile.isConnectedOrConnecting

        if(isWifiConnected || (MyWeatherApp.canUseMobileData && isMobileDataConnected)){
            if(!MyWeatherApp.isBatterySavingMode) {
                val app = (MyWeatherApp.instance)
                var currentTimestamp: Long = 0
                if(app.currentTimestampMap.contains(UrlBuilder().buildWeatherByCityUrl(app.resources, MyWeatherApp.favouriteLoc)))
                    currentTimestamp = app.currentTimestampMap[UrlBuilder().buildWeatherByCityUrl(app.resources, MyWeatherApp.favouriteLoc)] as Long
                var forecastTimestamp: Long = 0
                if(app.forecastTimestampMap.contains(UrlBuilder().buildForecastByCityUrl(app.resources, MyWeatherApp.favouriteLoc)))
                    forecastTimestamp = app.forecastTimestampMap[UrlBuilder().buildForecastByCityUrl(app.resources, MyWeatherApp.favouriteLoc)] as Long
                val currentTime = System.currentTimeMillis()

                // TODO this is just ugly...   >.<
                var interval: Long = -1
                if(MyWeatherApp.refreshTime == 0) interval = 12
                if(MyWeatherApp.refreshTime == 1) interval = 24
                if(MyWeatherApp.refreshTime == 2) interval = 48

                val currentDayDifferenceInIntervals = ((currentTime - currentTimestamp)  / (1000*60*60*interval)).toInt()
                val forecastDifferenceInIntervals = ((currentTime - forecastTimestamp)  / (1000*60*60*interval)).toInt()

                if(currentDayDifferenceInIntervals > 0) // current day is outdated
                    (MyWeatherApp.instance).currentInfoGetter?.forceUpdateCurrentDayInfoInProvider(MyWeatherApp.favouriteLoc)

                if(forecastDifferenceInIntervals > 0) // forecast is outdated
                    (MyWeatherApp.instance).forecastInfoGetter?.forceUpdateForecastInfoInProvider(MyWeatherApp.favouriteLoc)
            }
        }
    }
}
