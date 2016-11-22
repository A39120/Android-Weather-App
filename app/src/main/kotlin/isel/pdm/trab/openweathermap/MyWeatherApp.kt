package isel.pdm.trab.openweathermap

import android.app.Application
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.Volley
import isel.pdm.trab.openweathermap.services.LruDtoCache
import isel.pdm.trab.openweathermap.services.LruImageCache
import isel.pdm.trab.openweathermap.services.NullImageCache
import java.util.*


class MyWeatherApp : Application(){

    lateinit var requestQueue: RequestQueue

    lateinit var imageLoader: ImageLoader

    val DTO_CACHE_SIZE = 20
    lateinit var lruDtoCache: MutableMap<String, Any>

    val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
    val cacheSize = maxMemory / 8


    companion object {
        lateinit var instance: MyWeatherApp

        /**
         *  The language for the app UI
         *  English by default
         */
        var language: String = "en"

        /**
         *  The city for which the weather requests will be made
         *  Is set by default to the device's country
         */
        var city: String = Locale.getDefault().displayCountry

    }

    override fun onCreate(){
        super.onCreate()
        instance = this
        requestQueue = Volley.newRequestQueue(this)
        //imageLoader = ImageLoader(requestQueue, NullImageCache())
        imageLoader = ImageLoader(requestQueue, LruImageCache(cacheSize))
        lruDtoCache = Collections.synchronizedMap(LruDtoCache<String, Any>(DTO_CACHE_SIZE))
    }
}