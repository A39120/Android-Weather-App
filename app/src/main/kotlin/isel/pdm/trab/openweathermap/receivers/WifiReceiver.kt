package isel.pdm.trab.openweathermap.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import isel.pdm.trab.openweathermap.MyWeatherApp

class WifiReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val connManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        //val net: Network = connManager.activeNetwork // API 23 and above...
        val wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val mobile = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
        val isWifiConnected = wifi != null && wifi.isConnectedOrConnecting
        val isMobileDataConnected = mobile != null && mobile.isConnectedOrConnecting

        if(isWifiConnected || (MyWeatherApp.canUseMobileData && isMobileDataConnected)){
            // TODO update with wifi code goes here
            // update only if last update timestamp "tells" the info can be outdated
            // TODO currentday timestamp check goes here
            (MyWeatherApp.instance).currentInfoGetter?.forceUpdateCurrentDayInfoInProvider(MyWeatherApp.favouriteLoc)
            // TODO forecast timestamp check goes here
            (MyWeatherApp.instance).forecastInfoGetter?.forceUpdateForecastInfoInProvider(MyWeatherApp.favouriteLoc)
        }
    }
}
