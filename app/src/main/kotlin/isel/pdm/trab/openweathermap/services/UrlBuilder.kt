package isel.pdm.trab.openweathermap.services

import android.content.res.Resources
import isel.pdm.trab.openweathermap.MyWeatherApp
import isel.pdm.trab.openweathermap.R
import java.util.*

class UrlBuilder(){

    fun buildWeatherByCityUrl(
            resources: Resources,
            city: String = MyWeatherApp.city,
            lang: String = MyWeatherApp.language,
            units: String = "metric"): String
    {
        val baseUrl = resources.getString(R.string.api_base_url)
        val configPath = resources.getString(R.string.api_config_path)
        val city_param = "${resources.getString(R.string.api_city_name)}=" + city
        val lang_param = "${resources.getString(R.string.api_lang_name)}=" + lang
        val metric_param = "${resources.getString(R.string.api_units_name)}=" + units
        val api_key = "${resources.getString(R.string.api_key_name)}=${resources.getString(R.string.api_key_value)}"
        return "$baseUrl$configPath$city_param$lang_param$metric_param$api_key"
    }

    fun buildForecastByCityUrl(
            resources: Resources,
            city: String = MyWeatherApp.city,
            lang: String = MyWeatherApp.language,
            units: String = "metric"): String
    {
        val baseUrl = resources.getString(R.string.api_base_url)
        val configPath = resources.getString(R.string.api_config_path)
        val city_param = "${resources.getString(R.string.api_forecast_city_name)}=" + city
        val lang_param = "${resources.getString(R.string.api_lang_name)}=" + lang
        val metric_param = "${resources.getString(R.string.api_units_name)}=" + units
        val api_key = "${resources.getString(R.string.api_key_name)}=${resources.getString(R.string.api_key_value)}"
        return "$baseUrl$configPath$city_param$lang_param$metric_param$api_key"
    }

    fun buildImgUrl(resources: Resources, imgId: String): String {
        val baseUrl = resources.getString(R.string.api_base_url)
        val imgPath = resources.getString(R.string.api_img_path)
        return "$baseUrl$imgPath" + imgId + ".png"
    }
}

