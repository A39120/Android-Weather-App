package isel.pdm.trab.openweathermap.utils

import android.content.res.Resources
import isel.pdm.trab.openweathermap.MyWeatherApp
import isel.pdm.trab.openweathermap.R
import java.util.*

/**
 *  A class that builds the URLs for the HTTP requests
 */
class UrlBuilder(){

    /**
     *  Builds an URL to request the current weather of a city to the API
     *  @param resources: The application resources
     *  @param city: The city for the search
     *  @param lang: The language in which the response should arrive
     *  @param units: The scale of the values of the response: metric or imperial
     */
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
        return "$baseUrl$configPath$city_param$lang_param$metric_param$api_key".toLowerCase()
    }

    /**
     *  Builds an URL to request the weather forecast for 5 days of a city to the API
     *  @param resources: The application resources
     *  @param city: The city for the search
     *  @param lang: The language in which the response should arrive
     *  @param units: The scale of the values of the response: metric or imperial
     */
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

    /**
     *  Builds an URL to request an image icon to the API
     *  @param resources: The application resources
     *  @param imgId: The id of the image to be requested
     */
    fun buildImgUrl(resources: Resources, imgId: String): String {
        val baseUrl = resources.getString(R.string.api_base_url)
        val imgPath = resources.getString(R.string.api_img_path)
        return "$baseUrl$imgPath" + imgId + ".png"
    }
}

