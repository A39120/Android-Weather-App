package isel.pdm.trab.openweathermap.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.widget.Toast
import com.android.volley.toolbox.Volley
import isel.pdm.trab.openweathermap.MyWeatherApp
import isel.pdm.trab.openweathermap.comms.GetRequest
import isel.pdm.trab.openweathermap.models.ForecastWeatherDto
import isel.pdm.trab.openweathermap.models.content.WeatherProvider
import isel.pdm.trab.openweathermap.models.content.*
class RefreshForecastService : Service() {

    val BROADCAST_ACTION = "isel.pdm.trab.openweathermap.FORECAST_SERVICE"

    override fun onBind(intent: Intent): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val currentCity: String = intent?.getStringExtra("FORECAST_CITY") as String
        val url = UrlBuilder().buildForecastByCityUrl(resources, currentCity)

        Volley.newRequestQueue(this).add(
                GetRequest(
                        url,
                        { weather ->
                            //Forecast Weather Test
                            val tableUri = WeatherProvider.FORECAST_CONTENT_URI

                            val location = weather.cityDetail.cityName
                            val country = weather.cityDetail.country
                            val selection = "${WeatherProvider.COLUMN_LOCATION}=? AND " +
                                    "${WeatherProvider.COLUMN_COUNTRY_CODE}=?"

                            val selectionArgs = arrayOf(location, country)
                            val projection = arrayOf(WeatherProvider.COLUMN_UTC)
                            // check if day-month-year is already in db
                            val cursor = contentResolver.query(
                                    tableUri,
                                    projection,
                                    selection,
                                    selectionArgs,
                                    "${WeatherProvider.COLUMN_UTC} ASC"
                            )
                            val wcv = weather.toContentValues()
                            var count = 0

                            if(cursor.count == 0){
                                for(element in wcv)
                                    contentResolver.insert(tableUri, element)
                            }
                            else{
                                do{
                                    //check if data of the day is already there
                                    val current_utc = weather.forecastDetail[count].utc
                                    val current_date = ConvertUtils.convertUnixToDate(current_utc*1000)

                                    val content_utc = cursor.getLong(WeatherProvider.COLUMN_UTC_IDX)
                                    val content_date = ConvertUtils.convertUnixToDate(content_utc*1000)

                                    while(content_utc < current_utc && !current_date.equals(content_date)){
                                        //the day is not the same
                                        if(!cursor.moveToNext())
                                            break;
                                    }

                                    val updateArgs = arrayOf("$location, $country $current_date")

                                    //TODO: DELETE VALUES OLDER THAN TODAY
                                    //There is a newer version for the weather for that day
                                    contentResolver.update(tableUri,
                                            wcv[count],
                                            "${WeatherProvider.COLUMN_ID}=? ",
                                            updateArgs
                                    )
                                    count+=1
                                }while(cursor.moveToNext())
                                //Insert the others that didn't exist
                                while(count < weather.forecastDetail.size){
                                    contentResolver.insert(tableUri, wcv[count])
                                    count++
                                }
                            }
                            cursor.close()

                            val myIntent = Intent()
                            myIntent.action = BROADCAST_ACTION
                            myIntent.putExtra("WEATHER_FOR_NOTIFY", weather.forecastDetail[1])
                            sendBroadcast(myIntent)
                        },
                        { error -> System.out.println("Error in refresh current service?")},
                        ForecastWeatherDto::class.java)
        )

        return START_REDELIVER_INTENT
    }
}
