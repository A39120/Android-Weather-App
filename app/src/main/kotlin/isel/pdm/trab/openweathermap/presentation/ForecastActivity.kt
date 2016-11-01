package isel.pdm.trab.openweathermap.presentation

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import com.android.volley.toolbox.Volley
import isel.pdm.trab.openweathermap.DownloadImageTask
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
            //TODO recebe dados por intent.extra ?
            //if(savedInstanceState == null) {
            //    val parcel = intent.extras
            //    val weather = parcel.getParcelable<ForecastWeatherDto>("FORECAST_DATA")
            //    fillListView(weather)
            //}
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

            var adapter : ForecastAdapter = ForecastAdapter(ForecastActivity@this, list, resources)

            (activity_forecast.forecastList_weather_list as ListView).adapter = adapter
            activity_forecast.forecastList_country_edittext.isEnabled = true // re-enable choosing country

            adapterBackup = adapter
        }

        activity_forecast.forecastList_country_edittext.setOnEditorActionListener(this)
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if(activity_forecast.forecastList_country_edittext.text.equals("")) return true // don't send request if empty string
        if(event?.action == KeyEvent.ACTION_DOWN || actionId == EditorInfo.IME_ACTION_DONE) {
            activity_forecast.forecastList_country_edittext.isEnabled = false
            val inputCityName: String = activity_forecast.forecastList_country_edittext.text.toString()
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
        val adapter = ForecastAdapter(ForecastActivity@this,fwDto.forecastDetail, resources)
        (activity_forecast.forecastList_weather_list as ListView).adapter = adapter
        activity_forecast.forecastList_country_edittext.isEnabled = true // re-enable choosing country

        adapterBackup = adapter
    }

    private class ForecastAdapter(context : Context,
                                  list: List<ForecastWeatherDto.ForecastDetail>,
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
                (itemView?.findViewById(R.id.forecast_short_info) as TextView).text =  current.temp.day.toString() + "ºC, Today"
            }else{
                var str : String = current.temp.day.toString() + "ºC, in "+position+" day"
                if(position > 1) str += "s"
                (itemView?.findViewById(R.id.forecast_short_info) as TextView).text = str
            }

            val imgUrl = UrlBuilder().buildImgUrl(resources, current.weather[0].icon)
            DownloadImageTask((itemView!!.findViewById(R.id.forecast_icon) as ImageView)).execute(imgUrl)

            itemView.setOnClickListener {
                val anIntent = Intent(context, CurrentDayActivity::class.java)
                anIntent.putExtra("WEATHER_DATA", current)
                context.startActivity(anIntent)
            }

            return itemView
        }
    }
}
