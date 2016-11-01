package isel.pdm.trab.openweathermap

import android.app.Application
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import java.util.*


class MyWeatherApp : Application(){

    lateinit var requestQueue: RequestQueue

    companion object {
        var language: String = "en" // default
        var city: String = Locale.getDefault().displayCountry
        //TODO add last url inserted with timestamp to check for future requests ???
    }

    override fun onCreate(){
        super.onCreate()
        requestQueue = Volley.newRequestQueue(this)
    }
}