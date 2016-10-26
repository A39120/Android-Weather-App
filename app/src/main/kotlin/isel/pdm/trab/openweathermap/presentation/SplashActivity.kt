package isel.pdm.trab.openweathermap.presentation

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import isel.pdm.trab.openweathermap.MyWeatherApp
import isel.pdm.trab.openweathermap.R
import isel.pdm.trab.openweathermap.comms.GetRequest
import isel.pdm.trab.openweathermap.models.CurrentWeatherDto

class SplashActivity : BaseActivity() {

    override val layoutResId: Int = R.layout.activity_splash

    private fun buildConfigUrl(): String {
        val baseUrl = resources.getString(R.string.api_base_url)
        val configPath = resources.getString(R.string.api_config_path)
        // Implementation note: All requests contain the api key. We will implement a general solution
        val api_key = "${resources.getString(R.string.api_key_name)}=${resources.getString(R.string.api_key_value)}"
        val metric_param = resources.getString(R.string.metric_units_param)
        return "$baseUrl$configPath?$metric_param$api_key"
    }

    //TODO add proper handling to notify user is he has network disabled (took me some time to figure why it wasn't working)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val aIntent = Intent(this, CurrentDayActivity::class.java)

        (application as MyWeatherApp).requestQueue.add(
            GetRequest(buildConfigUrl(),
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
                            //System.out.println(error.networkResponse.statusCode)
                            Toast.makeText(this, R.string.splash_api_unreachable, Toast.LENGTH_LONG).show()
                            Handler(mainLooper).postDelayed({ finish() }, 3000)
                        }
                    },
                    CurrentWeatherDto::class.java
            )
        )
    }
}
