package isel.pdm.trab.openweathermap

import android.app.Application
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley


class MyWeatherApp : Application(){

    lateinit var requestQueue: RequestQueue

    override fun onCreate(){
        super.onCreate()
        requestQueue = Volley.newRequestQueue(this)
    }
}