package isel.pdm.trab.openweathermap.presentation

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import isel.pdm.trab.openweathermap.MyWeatherApp
import isel.pdm.trab.openweathermap.R
import isel.pdm.trab.openweathermap.services.UrlBuilder
import isel.pdm.trab.openweathermap.comms.GetRequest
import isel.pdm.trab.openweathermap.models.CurrentWeatherDto
import java.util.*

/**
 * Activity responsible for displaying a splash screen while the app is launching
 */
class SplashActivity : BaseActivity() {

    override var layoutResId: Int = R.layout.activity_splash

    //TODO add proper handling to notify user is he has network disabled (took me some time to figure why it wasn't working)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val aIntent = Intent(this, CurrentDayActivity::class.java)

        // To force the Locale to the one set in @MyWeatherApp.language
        val displayMetrics = resources.displayMetrics
        val configuration = resources.configuration
        configuration.setLocale(Locale(MyWeatherApp.language))
        resources.updateConfiguration(configuration, displayMetrics)

        //TODO ask to turn wifi on, right about here
        (application as MyWeatherApp).requestQueue.add(
                GetRequest(UrlBuilder().buildWeatherByCityUrl(resources),
                        {
                            weather ->
                            run {
                                aIntent.putExtra("WEATHER_DATA", weather)
                                startActivity(aIntent)
                            }
                        },
                        {
                            error ->
                            run {
                                //check if it was caused because wifi was turned off (stackoverflow)
                                val connManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                                if (connManager.activeNetworkInfo != null) {
                                    if(connManager.activeNetworkInfo.isConnected) // problem in the web api
                                        Toast.makeText(this, R.string.splash_api_unreachable, Toast.LENGTH_LONG).show()
                                }else{ // user problem (wifi turned off)
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
