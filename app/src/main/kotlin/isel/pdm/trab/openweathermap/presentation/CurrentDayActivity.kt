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
import isel.pdm.trab.openweathermap.services.ConvertUtils
import isel.pdm.trab.openweathermap.services.UrlBuilder
import kotlinx.android.synthetic.main.activity_current_day.*
import kotlinx.android.synthetic.main.activity_current_day.view.*
import java.util.*

//Actually Current Day we're viewing at the moment
class CurrentDayActivity : BaseActivity(), TextView.OnEditorActionListener {
    val UPDATE_TIMEOUT: Long = 1000 * 60 * 60 // (1000 milis) * (60 seconds) * (60 minutes) = 1 hour

    override val layoutResId: Int = R.layout.activity_current_day
    override val actionBarId: Int? = R.id.toolbar
    override val actionBarMenuResId: Int? = R.menu.action_bar_activity_current_day

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


        Handler(mainLooper).postDelayed({
            refreshWeatherInfo(activity_current_day.curday_country_textview.text.toString())
        }, UPDATE_TIMEOUT)
    }

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

    override fun onOptionsItemSelected(item: MenuItem?): Boolean = when (item?.itemId) {
        R.id.action_refresh -> {
            refreshWeatherInfo(activity_current_day.curday_country_textview.text.toString().trim())
            Toast.makeText(this, resources.getString(R.string.get_curday_updating_text),Toast.LENGTH_LONG).show()
            true
        }

        R.id.action_forecast -> {
            val anIntent = Intent(CurrentDayActivity@this, ForecastActivity::class.java)
            val city = (activity_current_day.curday_country_textview.text as String).split(",")[0]
            Volley.newRequestQueue(this).add(
                    GetRequest(
                            UrlBuilder().buildForecastByCityUrl(resources, city),
                            { weather ->
                                run {
                                    anIntent.putExtra("FORECAST_DATA", weather)
                                    startActivity(anIntent)
                                }
                            },
                            { error -> System.out.println("Error in response?")},
                            ForecastWeatherDto::class.java)
            )
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

        R.id.action_credits -> {
            startActivity(Intent(this, CreditsActivity::class.java))
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun onCurrentDayRequestFinished(weather: CurrentWeatherDto){
        activity_current_day.curday_temperature.text = weather.info.temperature.toString() + "ºC"
        activity_current_day.curday_country_textview.text = weather.location + "," + weather.locDetail.countryCode
        activity_current_day.curday_country_edittext.isEnabled = true // re-enable choosing country
        activity_current_day.curday_weather_desc.text = weather.shortInfo.first().description
        writeOtherWeatherInfo(weather)

        val imgUrl = UrlBuilder().buildImgUrl(resources, weather.shortInfo[0].icon)

        (application as MyWeatherApp).imageLoader.get(imgUrl, object : ImageLoader.ImageListener {
            override fun onResponse(response: ImageLoader.ImageContainer, isImmediate: Boolean) {
                if (response == null) {
                    setErrorImg()
                    return
                }
                val bitmap = response.bitmap
                if (bitmap != null) {
                    activity_current_day.curday_image.setImageBitmap(bitmap)
                } else {
                    setErrorImg()
                    return
                }
            }
            override fun onErrorResponse(error: VolleyError) {
                setErrorImg()
            }
        })

    }

    fun setErrorImg(){
        activity_current_day.curday_image.setImageBitmap(BitmapFactory.decodeResource(this.resources, R.drawable.error_icon))
        Toast.makeText(this , R.string.could_not_download_icon_for_weather,Toast.LENGTH_SHORT).show()
    }


    private fun writeOtherWeatherInfo(weather: CurrentWeatherDto){
        var rain: String = ""
        var snow: String = ""
        if(weather.rainDetail != null)
            rain = "\n" + getString(R.string.precipitation) + ": " + weather.rainDetail.rainVolume + "mm"
        if(weather.snowDetail != null)
            snow = "\n" + getString(R.string.snow) + ": " + weather.snowDetail.snowVolume + "mm"
        activity_current_day.curday_other_info.text = String.format(getString(R.string.curday_other_info),
                weather.windDetail.speed, "km/h",
                ConvertUtils().convertDegreesToTextDescription(weather.windDetail.windDegrees),
                weather.cloudDetail.clouds,
                rain,
                snow,
                weather.info.humidity,
                weather.info.maxTemp, "ºC",
                weather.info.minTemp, "ºC",
                weather.info.pressure,
                ConvertUtils().convertUnixToTime(weather.locDetail.sunriseTime),
                ConvertUtils().convertUnixToTime(weather.locDetail.sunsetTime),
                ConvertUtils().convertUnixToDateTime(weather.utc))
    }

    private fun refreshWeatherInfo(currentCity: String){
        Volley.newRequestQueue(this).add(
                GetRequest(
                        UrlBuilder().buildWeatherByCityUrl(resources, currentCity),
                        { weather ->
                            onCurrentDayRequestFinished(weather)
                        },
                        { error -> System.out.println("Error in response?")},
                        CurrentWeatherDto::class.java)
        )

    }
}
