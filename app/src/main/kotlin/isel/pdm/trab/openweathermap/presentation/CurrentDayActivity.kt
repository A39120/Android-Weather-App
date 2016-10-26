package isel.pdm.trab.openweathermap.presentation

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import com.android.volley.toolbox.Volley
import isel.pdm.trab.openweathermap.R
import isel.pdm.trab.openweathermap.comms.GetRequest
import isel.pdm.trab.openweathermap.models.CurrentWeatherDto
import kotlinx.android.synthetic.main.activity_current_day.*
import kotlinx.android.synthetic.main.activity_current_day.view.*

class CurrentDayActivity : BaseActivity(), TextView.OnEditorActionListener {

    override val layoutResId: Int = R.layout.activity_current_day

    override val actionBarId: Int? = R.id.toolbar

    override val actionBarMenuResId: Int? = R.menu.action_bar_activity_current_day

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(savedInstanceState == null) {
            val parcel = intent.extras
            val weather = parcel.getParcelable<CurrentWeatherDto>("WEATHER_DATA")
            onCurrentDayRequestFinished(weather)
        }
    }

    var listenerSet = false
    private fun onCurrentDayRequestFinished(weather: CurrentWeatherDto){

        activity_current_day.curday_temperature.setText(weather.info.temperature.toString() + "ºC")
        activity_current_day.curday_country_textview.setText(weather.location)
        activity_current_day.curday_country_edittext.isEnabled = true // re-enable choosing country
        activity_current_day.curday_weather_desc.setText(weather.shortInfo.first().description + "\n")
        activity_current_day.curday_other_info.setText(
                // TODO: change to translatable strings, check what units are used for pressure and sunrise and sunset
                "Humidity: " + weather.info.humidity + "%" +
                "\nMax Temp: " + weather.info.minTemp + "ºC" +
                "\nMin Temp: " + weather.info.maxTemp + "ºC" +
                "\nPressure: " + weather.info.pressure +
                "\nSunrise: " + weather.locDetail.sunriseTime +
                "\nSunset: " + weather.locDetail.sunsetTime
        )

        // TODO: url is to be put in a configuration file;
        val imgUrl = "http://openweathermap.org/img/w/" + weather.shortInfo[0].icon + ".png"

        if(!listenerSet) {
            activity_current_day.curday_country_edittext.setOnEditorActionListener(this)
            listenerSet = true
        }
    }


    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (event?.action == KeyEvent.ACTION_DOWN || actionId == EditorInfo.IME_ACTION_DONE) {
            activity_current_day.curday_country_edittext.isEnabled = false

            //TODO build url correctly
            val url = "http://api.openweathermap.org/data/2.5/weather?q=" + activity_current_day.curday_country_edittext.text + "&units=metric&lang=pt&appid=c7a548f3e5399a3cebb9b39ad5645120"
            Volley.newRequestQueue(this).add(
                    GetRequest(
                            url,
                            { weather ->
                                onCurrentDayRequestFinished(weather)
                            },
                            { error -> System.out.println("Error in response?")},
                            CurrentWeatherDto::class.java)
            )
            Toast.makeText(this,"Getting current day's weather for " + activity_current_day.curday_country_edittext.text + "...", Toast.LENGTH_LONG).show()
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
