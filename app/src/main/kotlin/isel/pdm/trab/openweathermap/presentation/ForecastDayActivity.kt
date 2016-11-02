package isel.pdm.trab.openweathermap.presentation

import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
import android.view.KeyEvent
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import com.android.volley.toolbox.Volley
import isel.pdm.trab.openweathermap.*
import isel.pdm.trab.openweathermap.comms.GetRequest
import isel.pdm.trab.openweathermap.models.CurrentWeatherDto
import isel.pdm.trab.openweathermap.models.ForecastWeatherDto
import kotlinx.android.synthetic.main.activity_forecast_day.*
import kotlinx.android.synthetic.main.activity_forecast_day.view.*
import kotlinx.android.synthetic.main.activity_forecast_day.*
import java.util.*

class ForecastDayActivity : BaseActivity(), TextView.OnEditorActionListener {

    val UPDATE_TIMEOUT: Long = 1000 * 60 * 60 // (1000 milis) * (60 seconds) * (60 minutes) = 1 hour

    override val layoutResId: Int = R.layout.activity_forecast_day
    override val actionBarId: Int? = R.id.toolbar
    override val actionBarMenuResId: Int? = R.menu.action_bar_activity_forecast

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        outState?.putString("activity_forecast_day.curday_temperature", activity_forecast_day.curday_temperature.text.toString())
        outState?.putString("activity_forecast_day.curday_country_textview", activity_forecast_day.curday_country_textview.text.toString())
        outState?.putBoolean("activity_forecast_day.curday_country_edittext", activity_forecast_day.curday_country_edittext.isEnabled)
        outState?.putString("activity_forecast_day.curday_weather_desc", activity_forecast_day.curday_weather_desc.text.toString())
        outState?.putString("activity_forecast_day.curday_other_info", activity_forecast_day.curday_other_info.text.toString())
        intent.putExtra("WeatherBitmap", (activity_forecast_day.curday_image.drawable as BitmapDrawable).bitmap)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val parcel = intent.extras

        if(savedInstanceState == null) {
            val weather = parcel.getParcelable<ForecastWeatherDto>("FORECAST_DATA")
            val pos = parcel.getInt("POSITION")
            onForecastDayRequestFinished(weather, pos)
        }else{ //restore previous state
            activity_forecast_day.curday_temperature.text = savedInstanceState.getString("activity_forecast_day.curday_temperature")
            activity_forecast_day.curday_country_textview.text = savedInstanceState.getString("activity_forecast_day.curday_country_textview")
            activity_forecast_day.curday_country_edittext.isEnabled = savedInstanceState.getBoolean("activity_forecast_day.curday_country_edittext")
            activity_forecast_day.curday_weather_desc.text = savedInstanceState.getString("activity_forecast_day.curday_weather_desc")
            activity_forecast_day.curday_other_info.text = savedInstanceState.getString("activity_forecast_day.curday_other_info")

            activity_forecast_day.curday_image.setImageBitmap(parcel.getParcelable("WeatherBitmap"))
        }
        activity_forecast_day.curday_country_edittext.setOnEditorActionListener(this)


        Handler(mainLooper).postDelayed({
            refreshWeatherInfo(activity_forecast_day.curday_country_textview.text.toString())
        }, UPDATE_TIMEOUT)
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if(activity_forecast_day.curday_country_edittext.text.equals("")) return true // don't send request if empty string
        if (event?.action == KeyEvent.ACTION_DOWN || actionId == EditorInfo.IME_ACTION_DONE) {
            activity_forecast_day.curday_country_edittext.isEnabled = false

            val inputCityName: String = activity_forecast_day.curday_country_edittext.text.toString().trim()
            refreshWeatherInfo(inputCityName)
            Toast.makeText(this, resources.getString(R.string.get_curday_text) + " $inputCityName...", Toast.LENGTH_LONG).show()
        }
        return true
    }

    private fun onForecastDayRequestFinished(weather: ForecastWeatherDto, pos: Int){
        activity_forecast_day.curday_temperature.text = weather.forecastDetail[pos].temp.day.toString() + "ºC"
        activity_forecast_day.curday_country_textview.text = weather.cityDetail.cityName + "," +
                weather.cityDetail.country
        activity_forecast_day.curday_country_edittext.isEnabled = true // re-enable choosing country
        activity_forecast_day.curday_weather_desc.text = weather.forecastDetail[pos].weather.first().weatherDesc

        val imgUrl = UrlBuilder().buildImgUrl(resources, weather.forecastDetail[pos].weather.first().icon)
        DownloadImageTask(activity_forecast_day.curday_image).execute(imgUrl)

        writeOtherWeatherInfo(weather.forecastDetail[pos])
    }

    private fun writeOtherWeatherInfo(weather: ForecastWeatherDto.ForecastDetail){
        var rain: String = ""
        var snow: String = ""
        if(weather.rain != 0.0)
            rain = "\n" + getString(R.string.precipitation) + ": " + weather.rain + "mm"
        if(weather.snow != 0.0)
            snow = "\n" + getString(R.string.snow) + ": " + weather.snow + "mm"

        activity_forecast_day.curday_other_info.text = String.format(getString(R.string.forday_other_info),
                weather.windSpeed, "km/h",
                ConvertUtils().convertDegreesToTextDescription(weather.windDegrees),
                weather.clouds,
                rain,
                snow,
                weather.humidity,
                weather.pressure,
                weather.temp.max, "ºC",
                weather.temp.min, "ºC",
                weather.temp.night, "ºC",
                weather.temp.eve, "ºC",
                weather.temp.morn, "ºC",
                ConvertUtils().convertUnixToDateTime(weather.utc))
    }

    private fun refreshWeatherInfo(currentCity: String){
        val aIntent = Intent(this, ForecastActivity::class.java)
        Volley.newRequestQueue(this).add(
                GetRequest(
                        UrlBuilder().buildForecastByCityUrl(resources, currentCity),
                        { weather ->
                            run {
                                    aIntent.putExtra("FORECAST_DATA", weather)
                                    startActivity(aIntent)
                                }
                        },
                        { error -> System.out.println("Error in response?")},
                        ForecastWeatherDto::class.java)
        )

    }


}
