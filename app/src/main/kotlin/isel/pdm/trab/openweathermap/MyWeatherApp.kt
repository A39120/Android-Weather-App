package isel.pdm.trab.openweathermap

import android.app.Application
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.Volley
import isel.pdm.trab.openweathermap.services.NullImageCache
import java.util.*


class MyWeatherApp : Application(){

    lateinit var requestQueue: RequestQueue

    lateinit var imageLoader: ImageLoader

    companion object {
        lateinit var instance: MyWeatherApp

        var language: String = "en" // default
        var city: String = Locale.getDefault().displayCountry
        //TODO add last url inserted with timestamp to check for future requests ???
    }

    override fun onCreate(){
        super.onCreate()
        instance = this
        requestQueue = Volley.newRequestQueue(this)
        imageLoader = ImageLoader(requestQueue, NullImageCache())
    }
}