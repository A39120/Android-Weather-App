package isel.pdm.trab.openweathermap.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import isel.pdm.trab.openweathermap.R
import isel.pdm.trab.openweathermap.models.ForecastWeatherDto
import android.app.NotificationManager
import android.content.Context.NOTIFICATION_SERVICE



class FavouriteReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val parcel = intent.extras
        val forecastList = parcel.getParcelable<ForecastWeatherDto>("FORECAST_FOR_NOTIFY")

        val weather = forecastList.forecastDetail[0]

        val minTempInfo = getMinTempInfo(weather.temp)
        val maxTempInfo = getMaxTempInfo(weather.temp)
        val windInfo = getWindInfo(weather.windSpeed)
        val rainInfo = getRainInfo(weather.rain)

        val finalMessage = minTempInfo.plus(maxTempInfo).plus(windInfo).plus(rainInfo)

        val mBuilder = NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.forecast)
                .setContentTitle(R.string.app_name.toString())
                .setContentText(finalMessage)

        val mNotificationId = 1

        val mNotifyMgr: NotificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        mNotifyMgr.notify(mNotificationId, mBuilder.build())
    }

    private fun getMinTempInfo(temp: ForecastWeatherDto.TempDetail): String {
        val minTemp: String = "Min Temp: "
        when {
            temp.min < 0.0 -> return minTemp.plus("It's freezing, get some really warm clothes, some gloves and a bonnet.")
            temp.min in 0.0..10.9 -> return minTemp.plus("It's really cold, wear some warm clothes.")
            temp.min in 11.0..19.9 -> return minTemp.plus("It might be fresh outside, don't go with a t-shirt only.")
            else -> return minTemp.plus("It's a good temperature.")
        }
    }

    private fun getMaxTempInfo(temp: ForecastWeatherDto.TempDetail): String {
        val maxTemp: String = "Max Temp: "
        when {
            temp.max in 30.0..35.9 -> return maxTemp.plus("It's hot outside, bring some sunglasses.")
            temp.max >= 36.0 -> return maxTemp.plus("It's realy hot outside, wear sun screen and some sunglasses.")
            else -> return maxTemp.plus("It's a good temperature.")
        }

    }

    private fun getWindInfo(speed: Double): String {
        when {
            speed in 10.8..17.1 -> return "It's a bit windy, you might wear an extra layer of clothes."
            speed in 17.2..24.4 -> return "The wind is blowing hard, be careful when going outside."
            speed in 24.5..32.6 -> return "Storm winds, go outside only if you must."
            speed > 32.7 -> return "Hurricane warning, stay indoors and protect yourself."
            else -> return "Light or no wind."
        }
    }

    private fun getRainInfo(rain: Double): String {
        when {
            rain in 1.2..2.4 -> return "It's raining lightly, you might wanna bring an umbrella with you."
            rain in 2.5..7.4 -> return "Moderate rain, don't forget the umbrella!"
            rain in 7.5..29.9 -> return "Heavy rain, be careful when going outside."
            rain >= 30 -> return "Torrential rain, stay indoors."
            else -> return "No rain."
        }
    }
}
