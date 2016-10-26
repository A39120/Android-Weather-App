package isel.pdm.trab.openweathermap.presentation

import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.KeyEvent
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import com.android.volley.toolbox.Volley
import isel.pdm.trab.openweathermap.DownloadImageTask
import isel.pdm.trab.openweathermap.R
import isel.pdm.trab.openweathermap.UrlBuilder
import isel.pdm.trab.openweathermap.comms.GetRequest
import isel.pdm.trab.openweathermap.models.CurrentWeatherDto
import kotlinx.android.synthetic.main.activity_current_day.*
import kotlinx.android.synthetic.main.activity_current_day.view.*

class CurrentDayActivity : BaseActivity(), TextView.OnEditorActionListener {

    override val layoutResId: Int = R.layout.activity_current_day

    override val actionBarId: Int? = R.id.toolbar

    override val actionBarMenuResId: Int? = R.menu.action_bar_activity_current_day

    var listenerSet = false

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        outState?.putString("activity_current_day.curday_temperature", activity_current_day.curday_temperature.text.toString())
        outState?.putString("activity_current_day.curday_country_textview", activity_current_day.curday_country_textview.text.toString())
        outState?.putBoolean("activity_current_day.curday_country_edittext", activity_current_day.curday_country_edittext.isEnabled)
        outState?.putString("activity_current_day.curday_weather_desc", activity_current_day.curday_weather_desc.text.toString())
        outState?.putString("activity_current_day.curday_other_info", activity_current_day.curday_other_info.text.toString())
        intent.putExtra("WeatherBitmap", (activity_current_day.curday_image.drawable as BitmapDrawable).bitmap)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(savedInstanceState == null) {
            val parcel = intent.extras
            val weather = parcel.getParcelable<CurrentWeatherDto>("WEATHER_DATA")
            onCurrentDayRequestFinished(weather)
        }else{ //restore previous state
            activity_current_day.curday_temperature.text = savedInstanceState.getString("activity_current_day.curday_temperature")
            activity_current_day.curday_country_textview.text = savedInstanceState.getString("activity_current_day.curday_country_textview")
            activity_current_day.curday_country_edittext.isEnabled = savedInstanceState.getBoolean("activity_current_day.curday_country_edittext")!!
            activity_current_day.curday_weather_desc.text = savedInstanceState.getString("activity_current_day.curday_weather_desc")
            activity_current_day.curday_other_info.text = savedInstanceState.getString("activity_current_day.curday_other_info")
            val parcel = intent.extras
            activity_current_day.curday_image.setImageBitmap(parcel.getParcelable("WeatherBitmap"))
        }
    }

    // TODO: create utility class to convert values between metric and imperial scale
    private fun onCurrentDayRequestFinished(weather: CurrentWeatherDto){
        activity_current_day.curday_temperature.text = weather.info.temperature.toString() + "ºC"
        activity_current_day.curday_country_textview.text = weather.location
        activity_current_day.curday_country_edittext.isEnabled = true // re-enable choosing country
        activity_current_day.curday_weather_desc.text = weather.shortInfo.first().description + "\n"
        activity_current_day.curday_other_info.text =
                "${resources.getString(R.string.humidity_text)}: ${weather.info.humidity}%\n" +
                "${resources.getString(R.string.max_temp_text)}: ${weather.info.maxTemp}ºC\n" +
                "${resources.getString(R.string.min_temp_text)}: ${weather.info.minTemp}ºC\n" +
                "${resources.getString(R.string.pressure_text)}: ${weather.info.pressure} hpa\n" +
                "${resources.getString(R.string.sunrise_text)}: ${weather.locDetail.sunriseTime}\n" +
                "${resources.getString(R.string.sunset_text)}: ${weather.locDetail.sunsetTime}"

        val imgUrl = UrlBuilder().buildImgUrl(resources, weather.shortInfo[0].icon)

        if(!listenerSet) {
            activity_current_day.curday_country_edittext.setOnEditorActionListener(this)
            listenerSet = true
        }

        DownloadImageTask(activity_current_day.curday_image).execute(imgUrl)
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if(activity_current_day.curday_country_edittext.text.equals("")) return true // don't send request if empty string
        if (event?.action == KeyEvent.ACTION_DOWN || actionId == EditorInfo.IME_ACTION_DONE) {
            activity_current_day.curday_country_edittext.isEnabled = false

            val inputCityName: String = activity_current_day.curday_country_edittext.text.toString()
            Volley.newRequestQueue(this).add(
                    GetRequest(
                            UrlBuilder().buildWeatherByCityUrl(resources, inputCityName),
                            { weather ->
                                onCurrentDayRequestFinished(weather)
                            },
                            { error -> System.out.println("Error in response?")},
                            CurrentWeatherDto::class.java)
            )
            Toast.makeText(this, "Getting current day's weather for $inputCityName...", Toast.LENGTH_LONG).show()
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean = when (item?.itemId) {
        R.id.action_credits -> {
            startActivity(Intent(this, CreditsActivity::class.java))
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}
