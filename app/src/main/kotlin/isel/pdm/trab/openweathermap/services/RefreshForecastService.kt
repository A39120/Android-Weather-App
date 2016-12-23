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

    override fun onBind(intent: Intent): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val currentCity: String = intent?.getStringExtra("FORECAST_CITY") as String
        val url = UrlBuilder().buildForecastByCityUrl(resources, currentCity)

        Volley.newRequestQueue(this).add(
                GetRequest(
                        url,
                        { weather ->
                            (application as MyWeatherApp).forecastRefreshTimestamp = System.currentTimeMillis()
                            //Forecast Weather Test
                            val tableUri = WeatherProvider.FORECAST_CONTENT_URI

                            val location = weather.cityDetail.cityName
                            val country = weather.cityDetail.country
                            val selection = "${WeatherProvider.COLUMN_LOCATION}=? AND " +
                                    "${WeatherProvider.COLUMN_COUNTRY_CODE}=?"

                            val selectionArgs = arrayOf(location, country)
                            val projection = arrayOf(WeatherProvider.COLUMN_ID ,WeatherProvider.COLUMN_UTC)
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
                                while(cursor.moveToNext()){
                                    //check if data of the day is already there

                                    val current_utc = weather.forecastDetail[count].utc
                                    val current_date = ConvertUtils.convertUnixToDate(current_utc)
                                    val content_utc = cursor.getLong(1)     // cursor only has 2 columns
                                    val content_date = ConvertUtils.convertUnixToDate(content_utc * 1000)

                                    //go to first day that has the same date as first day on ContentProvider
                                    while(content_utc < current_utc && !current_date.equals(content_date)){
                                        //the day is not the same
                                        if(!cursor.moveToNext())
                                            break;
                                    }
                                    val updateArgs = arrayOf("$location, $country $current_date")

                                    //There is a newer version for the weather for that day
                                    contentResolver.update(tableUri,
                                            wcv[count],
                                            "${WeatherProvider.COLUMN_ID}=? ",
                                            updateArgs
                                    )
                                    count+=1
                                }

                                //Insert the others that didn't exist
                                while(count < weather.forecastDetail.size){
                                    contentResolver.insert(tableUri, wcv[count])
                                    count++
                                }
                            }
                            cursor.close()

                            // Debug code to check if insertion/update was successful
                            val after = contentResolver.query(
                                    tableUri,
                                    projection,
                                    null,
                                    null,
                                    null
                            )
                            println("debug on refreshforecastdayservice (count):" + after.count)
                            cursor.close()
                            // end of debug code

                            // TODO: this is only to see if the service works, to be DELETED
                            val myIntent = Intent(this, FavNotificationService::class.java)
                            myIntent.putExtra("FORECAST_CITY_NOTIFY", weather.cityDetail.cityName)
                            myIntent.putExtra("FORECAST_COUNTRY_NOTIFY", weather.cityDetail.country)
                            startService(myIntent)
                            //
                        },
                        { error -> System.out.println("Error in refresh forecast service?")},
                        ForecastWeatherDto::class.java)
        )

        return START_REDELIVER_INTENT
    }
}
