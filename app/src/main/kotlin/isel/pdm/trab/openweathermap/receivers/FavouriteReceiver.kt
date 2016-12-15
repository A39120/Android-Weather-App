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

        val tempInfo = getTemperatureInfo(weather.temp)
        val windInfo = getWindInfo(weather.windSpeed)
        val rainInfo = getRainInfo(weather.rain)

        val finalMessage = tempInfo.plus(windInfo).plus(rainInfo)

        val mBuilder = NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.forecast)
                .setContentTitle(R.string.app_name.toString())
                .setContentText(finalMessage)

        val mNotificationId = 1

        val mNotifyMgr: NotificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        mNotifyMgr.notify(mNotificationId, mBuilder.build())
    }

    private fun getTemperatureInfo(temp: ForecastWeatherDto.TempDetail): String {
        if(temp.min <= 0)
            return "It's freezing, get some really warm clothes, some gloves and a bonnet."
        if(temp.min > 0 && temp.min <= 10)
            return "It's really cold, wear some warm clothes."
        if(temp.min >10 && temp.min <= 20){
            if(temp.max > 30){
                if(temp.max >= 35)
                    return "It's realy hot outside, wear sun screen and some sunglasses."
                else
                    return "It's hot outside, bring some sunglasses."
            }
            else
                return "It might be fresh outside, don't go with a t-shirt only."
        }
        return "It's a good temperature."
    }

    private fun getWindInfo(speed: Double): String {
        if(speed > 10.8 && speed <= 17.1)
            return "It's a bit windy, you might wear an extra layer of clothes."
        if(speed > 17.2 && speed <= 24.4)
            return "The wind is blowing hard, be careful when going outside."
        if(speed > 24.5){
            if(speed > 32.7)
                return "Hurricane warning, stay indoors and protect yourself."
            else
                return "Storm winds, go outside only if you must."
        }
        return "Light or no wind."
    }

    private fun getRainInfo(rain: Double): String {
        if(rain > 1.2 && rain <= 2.4)
            return "It's raining lightly, you might wanna bring an umbrella with you."
        if(rain > 2.5 && rain <= 7.4)
            return "Moderate rain, don't forget the umbrella!"
        if(rain > 7.5){
            if(rain >= 30)
                return "Torrential rain, stay indoors."
            else
                return "Heavy rain, be careful when going outside."
        }
        return "No rain."
    }
}
