package isel.pdm.trab.openweathermap.presentation

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.*
import com.android.volley.VolleyError
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.Volley
import isel.pdm.trab.openweathermap.MyWeatherApp
import isel.pdm.trab.openweathermap.R
import isel.pdm.trab.openweathermap.UrlBuilder
import isel.pdm.trab.openweathermap.comms.GetRequest
import isel.pdm.trab.openweathermap.models.ForecastWeatherDto
import kotlinx.android.synthetic.main.activity_forecast.*
import kotlinx.android.synthetic.main.activity_forecast.view.*
import java.util.*
import kotlin.collections.List

class ForecastActivity : BaseActivity(), TextView.OnEditorActionListener {

    override val layoutResId: Int = R.layout.activity_forecast
    override val actionBarId: Int? = R.id.toolbar
    override val actionBarMenuResId: Int? = R.menu.action_bar_activity_forecast

    private var adapterBackup : ForecastAdapter? = null

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        outState?.putString("activity_forecast_city", activity_forecast.forecast_country_textview.text.toString())

        if(adapterBackup != null) {
            var i: Int = 0
            outState?.putInt("activity_forecast_list_count", adapterBackup?.count as Int)
            while (i < adapterBackup?.count as Int) {
                outState?.putParcelable("activity_forecast_list_item" + i, adapterBackup?.getItem(i))
                i++
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(savedInstanceState == null) {
            val parcel = intent.extras
            val weather = parcel.getParcelable<ForecastWeatherDto>("FORECAST_DATA")
            fillListView(weather)
        }else{
            activity_forecast.forecast_country_textview.text = savedInstanceState.getString("activity_forecast_city")

            var list : ArrayList<ForecastWeatherDto.ForecastDetail> = ArrayList<ForecastWeatherDto.ForecastDetail>()
            var i: Int = savedInstanceState.getInt("activity_forecast_list_count")
            var j: Int = 0
            while(j < i){
                //se não for usado um tmp, ele queixa-se de lista de 'ForecastDetail?'
                val tmp : ForecastWeatherDto.ForecastDetail = savedInstanceState?.getParcelable("activity_forecast_list_item" + j)
                list.add(tmp)
                j++
            }

            var adapter : ForecastAdapter = ForecastAdapter(ForecastActivity@this, list, null,resources)

            (activity_forecast.forecastList_weather_list as ListView).adapter = adapter
            activity_forecast.forecast_country_edittext.isEnabled = true // re-enable choosing country

            adapterBackup = adapter
        }

        activity_forecast.forecast_country_edittext.setOnEditorActionListener(this)
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        val name = activity_forecast.forecast_country_edittext.text
        if(name.equals("")) return true // don't send request if empty string
        if(event?.action == KeyEvent.ACTION_DOWN || actionId == EditorInfo.IME_ACTION_DONE
                || actionId == EditorInfo.IME_ACTION_NEXT) {
            activity_forecast.forecast_country_edittext.isEnabled = false
            val inputCityName: String = activity_forecast.forecast_country_edittext.text.toString()
            val url : String = UrlBuilder().buildForecastByCityUrl(resources, inputCityName)
            Volley.newRequestQueue(this).add(
                    GetRequest(
                            url,
                            {
                                weather ->
                                run {
                                    intent.putExtra("FORECAST_DATA", weather)
                                    fillListView(weather)
                                }
                            },
                            {
                                error -> System.out.println(error.cause.toString())
                            },
                            ForecastWeatherDto::class.java)
            )
            Toast.makeText(this, "Getting forecast for the next 5 days for $inputCityName...", Toast.LENGTH_LONG).show()
        }
        return true
    }


    fun fillListView(fwDto: ForecastWeatherDto){

        activity_forecast.forecast_country_textview.text = fwDto.cityDetail.cityName+","+fwDto.cityDetail.country
        val adapter = ForecastAdapter(ForecastActivity@this,fwDto.forecastDetail, fwDto, resources)
        (activity_forecast.forecastList_weather_list as ListView).adapter = adapter
        activity_forecast.forecast_country_edittext.isEnabled = true // re-enable choosing country

        adapterBackup = adapter
    }

    private class ForecastAdapter(context : Context,
                                  list: List<ForecastWeatherDto.ForecastDetail>,
                                  val source : ForecastWeatherDto?,
                                  val resources : Resources):
            ArrayAdapter<ForecastWeatherDto.ForecastDetail>(
                    context,
                    R.layout.forecast_item,
                    list) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            // Make sure we have a view to work with (may have been given null)
            var itemView: View? = convertView
            if (itemView == null) {
                itemView = LayoutInflater.from(context).inflate(R.layout.forecast_item, parent, false)
            }

            val current = this.getItem(position)

            if(position == 0){
                (itemView?.findViewById(R.id.forecast_short_info) as TextView).text =  current.temp.day.toString() + "ºC, " + itemView!!.context.getString(R.string.today)
            }else{
                var str : String = current.temp.day.toString() + "ºC, " + String.format(itemView!!.context.getString(R.string.in_x_day), position)//"ºC, in "+position+" day"
                if(position > 1) str += "s"
                (itemView?.findViewById(R.id.forecast_short_info) as TextView).text = str
            }

            val imgUrl = UrlBuilder().buildImgUrl(resources, current.weather[0].icon)
            var imgView = (itemView!!.findViewById(R.id.forecast_icon) as ImageView)

            MyWeatherApp.instance.imageLoader.get(imgUrl, object : ImageLoader.ImageListener {
                override fun onResponse(response: ImageLoader.ImageContainer, isImmediate: Boolean) {
                    if (response == null) {
                        setErrorImg(imgView)
                        return
                    }
                    val bitmap = response.bitmap
                    if (bitmap != null) {
                        imgView.setImageBitmap(bitmap)
                    } else {
                        setErrorImg(imgView)
                        return
                    }
                }

                override fun onErrorResponse(error: VolleyError) {
                    setErrorImg(imgView)
                }

                private fun setErrorImg(imgView: ImageView){
                    imgView.setImageBitmap(BitmapFactory.decodeResource(imgView.context.getResources(), R.drawable.error_icon))
                    Toast.makeText(imgView.context , R.string.could_not_download_icon_for_weather,Toast.LENGTH_SHORT)
                }
            })

            itemView.setOnClickListener(object: View.OnClickListener {
                override fun onClick(view: View): Unit{
                    val anIntent = Intent(context, ForecastDayActivity::class.java)
                    anIntent.putExtra("POSITION", position)
                    anIntent.putExtra("FORECAST_DATA", source)
                    context.startActivity(anIntent)
                }})

                return itemView
            }
    }
    override fun onOptionsItemSelected(item: MenuItem?): Boolean = when (item?.itemId) {
        R.id.action_refresh -> {
            refreshWeatherInfo(activity_forecast.forecast_country_textview.text.toString().trim())
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

            activity_forecast.forecast_country_edittext.hint = getString(R.string.insert_country_edit_text)
            refreshWeatherInfo(activity_forecast.forecast_country_textview.text.toString().trim())

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

    private fun onForecastRequestFinished(weather: ForecastWeatherDto){
        fillListView(weather)
    }

    private fun refreshWeatherInfo(currentCity: String){
        Volley.newRequestQueue(this).add(
                GetRequest(
                        UrlBuilder().buildForecastByCityUrl(resources, currentCity),
                        { weather ->
                            onForecastRequestFinished(weather)
                        },
                        { error -> System.out.println("Error in response?")},
                        ForecastWeatherDto::class.java)
        )
    }
}
