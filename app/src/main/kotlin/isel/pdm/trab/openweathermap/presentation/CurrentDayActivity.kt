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
import isel.pdm.trab.openweathermap.models.CurrentWeatherDto
import isel.pdm.trab.openweathermap.models.ForecastWeatherDto
import isel.pdm.trab.openweathermap.models.content.WeatherProvider
import isel.pdm.trab.openweathermap.models.content.toContentValues
import isel.pdm.trab.openweathermap.models.content.toCurrentWeatherDto
import isel.pdm.trab.openweathermap.utils.ConvertUtils
import isel.pdm.trab.openweathermap.services.RefreshCurrentDayService
import isel.pdm.trab.openweathermap.services.RefreshForecastService
import isel.pdm.trab.openweathermap.utils.UrlBuilder
import kotlinx.android.synthetic.main.activity_current_day.*
import kotlinx.android.synthetic.main.activity_current_day.view.*
import java.util.*

/**
 * Activity responsible for displaying the current day's weather information
 */
class CurrentDayActivity : BaseActivity(), TextView.OnEditorActionListener {

    override val layoutResId: Int = R.layout.activity_current_day
    override val actionBarId: Int? = R.id.toolbar
    override val actionBarMenuResId: Int? = R.menu.action_bar_activity_current_day

    /**
     * Method responsible for loading a previously saved state of this activity
     * @param outState Bundle containing information from a previously saved state
     */
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
        val parcel = intent.extras

        if(savedInstanceState == null) {
            val weather = parcel.getParcelable<CurrentWeatherDto>("WEATHER_DATA")
            onCurrentDayRequestFinished(weather)
        }else{ //restore previous state
            activity_current_day.curday_temperature.text = savedInstanceState.getString("activity_current_day.curday_temperature")
            activity_current_day.curday_country_textview.text = savedInstanceState.getString("activity_current_day.curday_country_textview")
            activity_current_day.curday_country_edittext.isEnabled = savedInstanceState.getBoolean("activity_current_day.curday_country_edittext")
            activity_current_day.curday_weather_desc.text = savedInstanceState.getString("activity_current_day.curday_weather_desc")
            activity_current_day.curday_other_info.text = savedInstanceState.getString("activity_current_day.curday_other_info")

            activity_current_day.curday_image.setImageBitmap(parcel.getParcelable("WeatherBitmap"))
        }
        activity_current_day.curday_country_edittext.setOnEditorActionListener(this)
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
        if(activity_current_day.curday_country_edittext.text.equals("")) return true // don't send request if empty string
        if (event?.action == KeyEvent.ACTION_DOWN || actionId == EditorInfo.IME_ACTION_DONE) {
            activity_current_day.curday_country_edittext.isEnabled = false

            val inputCityName: String = activity_current_day.curday_country_edittext.text.toString().trim()
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
            refreshWeatherInfo(activity_current_day.curday_country_textview.text.toString().trim())
            Toast.makeText(this, resources.getString(R.string.get_curday_updating_text),Toast.LENGTH_LONG).show()
            true
        }

        R.id.action_forecast -> {
            val anIntent = Intent(CurrentDayActivity@this, ForecastActivity::class.java)
            val city = (activity_current_day.curday_country_textview.text as String).split(",")[0]

            val url = UrlBuilder().buildForecastByCityUrl(resources, city.toLowerCase())
            val apl = (application as MyWeatherApp)

            apl.forecastTimestampMap.put(url, System.currentTimeMillis())

            val weatherInfo = (application as MyWeatherApp).forecastInfoGetter?.getForecastInfo(city)
            if(weatherInfo != null){
                anIntent.putExtra("FORECAST_DATA", weatherInfo)
                startActivity(anIntent)
            }
            else{
                val myIntent = Intent(this, RefreshForecastService::class.java)
                myIntent.putExtra("FORECAST_CITY", city)
                startService(myIntent)

                Volley.newRequestQueue(this).add(
                        GetRequest(
                                url,
                                { weather ->
                                    run {
                                        apl.lruDtoCache.put(url, weather)
                                        anIntent.putExtra("FORECAST_DATA", weather)
                                        startActivity(anIntent)
                                    }
                                },
                                { error -> System.out.println("Error in response?")},
                                ForecastWeatherDto::class.java)
                )
            }

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

            activity_current_day.curday_country_edittext.hint = getString(R.string.insert_country_edit_text)
            refreshWeatherInfo(activity_current_day.curday_country_textview.text.toString().trim())

            Toast.makeText(this,
                    resources.getString(R.string.language_set_to) + " " + MyWeatherApp.language.toUpperCase(),
                    Toast.LENGTH_SHORT
            ).show()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    /**
     * Method called when the CurrentDayActivity is to be updated with new information
     * from a new CurrentWeatherDto
     * @param weather CurrentWeatherDto with the new information
     */
    private fun onCurrentDayRequestFinished(weather: CurrentWeatherDto){
        activity_current_day.curday_temperature.text = weather.info.temperature.toString() + "ºC"
        activity_current_day.curday_country_textview.text = weather.location + "," + weather.locDetail.countryCode
        activity_current_day.curday_country_edittext.isEnabled = true // re-enable choosing country
        activity_current_day.curday_weather_desc.text = weather.shortInfo.first().description
        writeOtherWeatherInfo(weather)

        val imgUrl = UrlBuilder().buildImgUrl(resources, weather.shortInfo[0].icon)

        (application as MyWeatherApp).imageLoader.get(imgUrl, object : ImageLoader.ImageListener {
            override fun onResponse(response: ImageLoader.ImageContainer, isImmediate: Boolean) {
                val bitmap = response.bitmap
                if (bitmap != null) {
                    activity_current_day.curday_image.setImageBitmap(bitmap)
                } else {
                    setLoadingImg(activity_current_day.curday_image)
                    return
                }
            }
            override fun onErrorResponse(error: VolleyError) {
                setErrorImg(activity_current_day.curday_image)
            }
        })

    }

    /**
     * Method used to fill the "other info" textview with information from a CurrentWeatherDto
     * @param weather a CurrentWeatherDto with information to fill the textview
     */
    private fun writeOtherWeatherInfo(weather: CurrentWeatherDto){
        var rain: String = ""
        var snow: String = ""
        if(weather.rainDetail != null)
            rain = "\n" + getString(R.string.precipitation) + ": " + weather.rainDetail.rainVolume + "mm"
        if(weather.snowDetail != null)
            snow = "\n" + getString(R.string.snow) + ": " + weather.snowDetail.snowVolume + "mm"
        activity_current_day.curday_other_info.text = String.format(getString(R.string.curday_other_info),
                weather.windDetail.speed, "km/h",
                ConvertUtils.convertDegreesToTextDescription(weather.windDetail.windDegrees),
                weather.cloudDetail.clouds,
                rain,
                snow,
                weather.info.humidity,
                weather.info.maxTemp, "ºC",
                weather.info.minTemp, "ºC",
                weather.info.pressure,
                ConvertUtils.convertUnixToTime(weather.locDetail.sunriseTime),
                ConvertUtils.convertUnixToTime(weather.locDetail.sunsetTime),
                ConvertUtils.convertUnixToDateTime(weather.utc))
    }

    /**
     * Method used to get weather information for a specific city
     * @param currentCity City to get weather information about
     */
    private fun refreshWeatherInfo(currentCity: String){
        val url = UrlBuilder().buildWeatherByCityUrl(resources, currentCity)
        val apl = (application as MyWeatherApp)

        val weatherInfo = (application as MyWeatherApp).currentInfoGetter?.getCurrentDayInfo(currentCity)
        if(weatherInfo != null)
            onCurrentDayRequestFinished(weatherInfo)
        else{
            val myIntent = Intent(this, RefreshCurrentDayService::class.java)
            myIntent.putExtra("CURRENT_CITY", currentCity)
            startService(myIntent)

            Volley.newRequestQueue(this).add(
                    GetRequest(
                            url,
                            { weather ->
                                apl.lruDtoCache.put(url, weather)
                                apl.currentTimestampMap.put(url, System.currentTimeMillis())
                                onCurrentDayRequestFinished(weather)
                            },
                            { error -> System.out.println("Error in response?")},
                            CurrentWeatherDto::class.java)
            )
        }
    }
}
