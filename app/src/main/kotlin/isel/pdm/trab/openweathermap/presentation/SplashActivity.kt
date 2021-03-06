package isel.pdm.trab.openweathermap.presentation

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import isel.pdm.trab.openweathermap.MyWeatherApp
import isel.pdm.trab.openweathermap.R
import isel.pdm.trab.openweathermap.utils.UrlBuilder
import isel.pdm.trab.openweathermap.comms.GetRequest
import isel.pdm.trab.openweathermap.models.CurrentWeatherDto
import isel.pdm.trab.openweathermap.services.CurrentInfoGetter
import isel.pdm.trab.openweathermap.services.ForecastInfoGetter
import isel.pdm.trab.openweathermap.services.RefreshCurrentDayService
import java.util.*
import android.provider.*

/**
 * Activity responsible for displaying a splash screen while the app is launching
 */
class SplashActivity : BaseActivity() {

    override var layoutResId: Int = R.layout.activity_splash

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = (application as MyWeatherApp)

        app.currentInfoGetter = CurrentInfoGetter(application, contentResolver)
        app.forecastInfoGetter = ForecastInfoGetter(application, contentResolver)

        // To force the Locale to the one set in @MyWeatherApp.language
        val displayMetrics = resources.displayMetrics
        val configuration = resources.configuration
        configuration.setLocale(Locale(MyWeatherApp.language))
        resources.updateConfiguration(configuration, displayMetrics)

        val url = UrlBuilder().buildWeatherByCityUrl(resources)
        val weatherInfo = app.currentInfoGetter?.getCurrentDayInfo(MyWeatherApp.city)

        if(weatherInfo != null)
            launchCurrentDayActivity(url, weatherInfo)
        else{
            val myIntent = Intent(this, RefreshCurrentDayService::class.java)
            myIntent.putExtra("CURRENT_CITY", MyWeatherApp.city)
            startService(myIntent)
            //TODO ask to turn wifi on, right about here

            app.requestQueue.add(
                    GetRequest(url,
                            {
                                weather ->
                                app.currentTimestampMap.put(url, System.currentTimeMillis())
                                launchCurrentDayActivity(url, weather)
                            },
                            {
                                error ->
                                run {
                                    val connManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                                    //val net: Network = connManager.activeNetwork // API 23 and above...
                                    val wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                                    val mobile = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                                    val isWifiConnected = wifi != null && wifi.isConnectedOrConnecting
                                    val isMobileDataConnected = mobile != null && mobile.isConnectedOrConnecting
                                    if(isWifiConnected || (MyWeatherApp.canUseMobileData && isMobileDataConnected)) {
                                        Toast.makeText(this, R.string.splash_api_unreachable, Toast.LENGTH_LONG).show()
                                    }else{
                                        Toast.makeText(this, R.string.connection_problem_wifi_off, Toast.LENGTH_LONG).show()
                                    }

                                    Handler(mainLooper).postDelayed({ finish() }, 3000)
                                }
                            },
                            CurrentWeatherDto::class.java
                    )
            )
        }
    }

    private fun  launchCurrentDayActivity(url: String, weather: CurrentWeatherDto) {
        (application as MyWeatherApp).lruDtoCache.put(url, weather)
        val aIntent = Intent(this, CurrentDayActivity::class.java)
        aIntent.putExtra("WEATHER_DATA", weather)
        startActivity(aIntent)
    }
}
