package isel.pdm.trab.openweathermap

import android.app.Application
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley


class MyWeatherApp : Application(){

    lateinit var requestQueue: RequestQueue

    companion object {
        lateinit var language: String
        //TODO add last url inserted with timestamp to check for future requests ???
    }

    override fun onCreate(){
        super.onCreate()
        language = "en" // TODO carregar de um ficheiro de configuração
        requestQueue = Volley.newRequestQueue(this)
    }
}