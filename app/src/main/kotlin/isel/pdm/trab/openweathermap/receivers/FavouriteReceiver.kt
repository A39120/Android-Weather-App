package isel.pdm.trab.openweathermap.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import isel.pdm.trab.openweathermap.R
import isel.pdm.trab.openweathermap.models.ForecastWeatherDto
import android.app.NotificationManager
import android.content.Context.NOTIFICATION_SERVICE
import android.content.res.Resources
import isel.pdm.trab.openweathermap.R.string.*


class FavouriteReceiver : BroadcastReceiver() {

    /**
     *  Receives a ForecastDetail instance from intent to produce a notification
     */

    var resources: Resources = Resources.getSystem()

    override fun onReceive(context: Context, intent: Intent) {

        resources = context.resources

        val parcel = intent.extras
        val weatherDetail = parcel.getParcelable<ForecastWeatherDto.ForecastDetail>("WEATHER_FOR_NOTIFY")
        val location = parcel.getString("LOCATION_FOR_NOTIFY")

        val minTempInfo = getMinTempInfo(weatherDetail.temp)
        val maxTempInfo = getMaxTempInfo(weatherDetail.temp)
        val windInfo = getWindInfo(weatherDetail.windSpeed)
        val rainInfo = getRainInfo(weatherDetail.rain)

        val intro = resources.getString(weather_for) + location
        val finalMessage = intro.plus(minTempInfo.plus(maxTempInfo).plus(windInfo).plus(rainInfo))

        val mBuilder = NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.notification_template_icon_bg)
                .setContentTitle(context.resources.getString(app_name))
                .setContentText(finalMessage)
                .setStyle( NotificationCompat.BigTextStyle().bigText(finalMessage))

        val mNotificationId = 1
        val mNotifyMgr: NotificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        mNotifyMgr.notify(mNotificationId, mBuilder.build())
    }

    private fun getMinTempInfo(temp: ForecastWeatherDto.TempDetail): String {
        val minTemp: String = resources.getString(min_temp)
        when {
            temp.min < 0.0 -> return minTemp.plus(resources.getString(min_temp_text1))
            temp.min in 0.0..10.9 -> return minTemp.plus(resources.getString(min_temp_text2))
            temp.min in 11.0..19.9 -> return minTemp.plus(resources.getString(min_temp_text3))
            else -> return minTemp.plus(resources.getString(default_temp_msg))
        }
    }

    private fun getMaxTempInfo(temp: ForecastWeatherDto.TempDetail): String {
        val maxTemp: String = resources.getString(max_temp)
        when {
            temp.max in 30.0..35.9 -> return maxTemp.plus(resources.getString(max_temp_text1))
            temp.max >= 36.0 -> return maxTemp.plus(resources.getString(max_temp_text2))
            else -> return maxTemp.plus(resources.getString(default_temp_msg))
        }

    }

    private fun getWindInfo(speed: Double): String {
        when {
            speed in 10.8..17.1 -> return resources.getString(wind_text1)
            speed in 17.2..24.4 -> return resources.getString(wind_text2)
            speed in 24.5..32.6 -> return resources.getString(wind_text3)
            speed > 32.7 -> return resources.getString(wind_text4)
            else -> return resources.getString(default_wind_msg)
        }
    }

    private fun getRainInfo(rain: Double): String {
        when {
            rain in 1.2..2.4 -> return resources.getString(rain_text1)
            rain in 2.5..7.4 -> return resources.getString(rain_text2)
            rain in 7.5..29.9 -> return resources.getString(rain_text3)
            rain >= 30 -> return resources.getString(rain_text4)
            else -> return resources.getString(default_rain_msg)
        }
    }
}
