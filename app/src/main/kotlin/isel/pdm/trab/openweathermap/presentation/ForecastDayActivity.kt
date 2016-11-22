package isel.pdm.trab.openweathermap.presentation

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import com.android.volley.VolleyError
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.Volley
import isel.pdm.trab.openweathermap.*
import isel.pdm.trab.openweathermap.comms.GetRequest
import isel.pdm.trab.openweathermap.models.ForecastWeatherDto
import isel.pdm.trab.openweathermap.services.ConvertUtils
import isel.pdm.trab.openweathermap.services.UrlBuilder
import kotlinx.android.synthetic.main.activity_forecast_day.*
import kotlinx.android.synthetic.main.activity_forecast_day.view.*
import java.util.*

/**
 * Activity responsible for displaying specific information from a day in the forecast
 */
class ForecastDayActivity : BaseActivity(), TextView.OnEditorActionListener {

    val UPDATE_TIMEOUT: Long = 1000 * 60 * 60 // (1000 milis) * (60 seconds) * (60 minutes) = 1 hour

    override val layoutResId: Int = R.layout.activity_forecast_day
    override val actionBarId: Int? = R.id.toolbar
    override val actionBarMenuResId: Int? = R.menu.action_bar_activity_forecast

    /**
     * Method responsible for loading a previously saved state of this activity
     * @param outState Bundle containing information from a previously saved state
     */
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

    /**
     * Callback method from the interface TextView.OnEditorActionListener
     * to be invoked when an action is performed on the editor
     * @param v TextView in which the action occurred
     * @param actionId Int that identifies the action
     * @param event If triggered by an enter key, this is the event, otherwise this is null.
     * @return Returns true if action was consumed
     */
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

    /**
     * Callback method invoked when an item from the options menu is selected
     * @param item MenuItem that was selected
     * @returns Returns true when the menu item is successfully handled
     */
    override fun onOptionsItemSelected(item: MenuItem?): Boolean = when (item?.itemId) {
        R.id.action_refresh -> {
            refreshWeatherInfo(activity_forecast_day.curday_country_textview.text.toString().trim())
            Toast.makeText(this, resources.getString(R.string.get_curday_updating_text),Toast.LENGTH_LONG).show()
            true
        }

        R.id.action_language -> {
            if(MyWeatherApp.language.equals("pt")){
                MyWeatherApp.language = "en"
                item?.setIcon(R.drawable.en_flag)
            }else{ // en
                MyWeatherApp.language = "pt"
                item?.setIcon(R.drawable.pt_flag)
            }

            val displayMetrics = resources.displayMetrics
            val configuration = resources.configuration
            configuration.setLocale(Locale(MyWeatherApp.language))
            resources.updateConfiguration(configuration, displayMetrics)

            activity_forecast_day.curday_country_edittext.hint = getString(R.string.insert_country_edit_text)
            refreshWeatherInfo(activity_forecast_day.curday_country_textview.text.toString().trim())

            Toast.makeText(this,
                    resources.getString(R.string.language_set_to) + " " + MyWeatherApp.language.toUpperCase(),
                    Toast.LENGTH_SHORT
            ).show()
            true
        }

        R.id.action_credits -> {
            startActivity(Intent(this, CreditsActivity::class.java))
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    /**
     * Method called when the ForecastDayActivity is to be updated with new information
     * from a new ForecastWeatherDto
     * @param weather ForecastWeatherDto with the new information
     */
    private fun onForecastDayRequestFinished(weather: ForecastWeatherDto, pos: Int){
        activity_forecast_day.curday_temperature.text = weather.forecastDetail[pos].temp.day.toString() + "ºC"
        activity_forecast_day.curday_country_textview.text = weather.cityDetail.cityName + "," +
                weather.cityDetail.country
        activity_forecast_day.curday_country_edittext.isEnabled = true // re-enable choosing country
        activity_forecast_day.curday_weather_desc.text = weather.forecastDetail[pos].weather.first().weatherDesc

        val imgUrl = UrlBuilder().buildImgUrl(resources, weather.forecastDetail[pos].weather.first().icon)

        (application as MyWeatherApp).imageLoader.get(imgUrl, object : ImageLoader.ImageListener {
            override fun onResponse(response: ImageLoader.ImageContainer, isImmediate: Boolean) {
                val bitmap = response.bitmap
                if (bitmap != null) {
                    activity_forecast_day.curday_image.setImageBitmap(bitmap)
                }else{
                    setLoadingImg(activity_forecast_day.curday_image)
                    return
                }
            }
            override fun onErrorResponse(error: VolleyError) {
                setErrorImg(activity_forecast_day.curday_image)
            }
        })

        writeOtherWeatherInfo(weather.forecastDetail[pos])
    }

    /**
     * Method used to fill the "other info" textview with information from a ForecastWeatherDto.ForecastDetail
     * @param weather a ForecastWeatherDto.ForecastDetail with information to fill the textview
     */
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

    /**
     * Method used to get weather information for a specific city
     * @param currentCity City to get weather information about
     */
    private fun refreshWeatherInfo(currentCity: String){
        val aIntent = Intent(this, ForecastActivity::class.java)
        val currCity = currentCity.toLowerCase()

        val apl = (application as MyWeatherApp)

        if(apl.lruDtoCache.contains(currCity)){
            aIntent.putExtra("FORECAST_DATA", apl.lruDtoCache[currCity] as ForecastWeatherDto)
            startActivity(aIntent)
        }
        else
            Volley.newRequestQueue(this).add(
                    GetRequest(
                            UrlBuilder().buildForecastByCityUrl(resources, currCity),
                            { weather ->
                                run {
                                        apl.lruDtoCache.put(weather.cityDetail.cityName.toLowerCase(), weather)
                                        aIntent.putExtra("FORECAST_DATA", weather)
                                        startActivity(aIntent)
                                    }
                            },
                            { error -> System.out.println("Error in response?")},
                            ForecastWeatherDto::class.java)
            )
    }
}
