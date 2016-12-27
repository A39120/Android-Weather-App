package isel.pdm.trab.openweathermap.models.content

import android.content.ContentValues
import android.database.Cursor
import android.database.AbstractCursor
import android.database.CursorIndexOutOfBoundsException
import android.net.Uri
import isel.pdm.trab.openweathermap.models.ForecastWeatherDto
import isel.pdm.trab.openweathermap.models.*
import isel.pdm.trab.openweathermap.models.ForecastWeatherDto.*
import isel.pdm.trab.openweathermap.presentation.ForecastActivity
import isel.pdm.trab.openweathermap.utils.ConvertUtils
import java.text.SimpleDateFormat
import java.util.*

/**
 * Extension function that maps a given [CurrentWeatherDto] to the corresponding URI and
 * [ContentValues] pair.
 * @param [weather day] The instance bearing the weather day information
 * @return The newly created [ContentValues] instance
 */
fun ForecastWeatherDto.toContentValues(): List<ContentValues> {
    val results = ArrayList<ContentValues>()

    for(element: ForecastDetail in this.forecastDetail){
        val result = ContentValues()
        with (WeatherProvider) {
            // Primary key is a combination of location, country and day
        result.put(COLUMN_ID, "${cityDetail.cityName}, " +
                    "${cityDetail.country} " +
                    ConvertUtils.convertUnixToDate(element.utc*1000))
            result.put(COLUMN_LOCATION_ID, this@toContentValues.cityDetail.id)
            result.put(COLUMN_LOCATION, this@toContentValues.cityDetail.cityName)
            result.put(COLUMN_LONGITUDE, this@toContentValues.cityDetail.cityCoordinates.longitude)
            result.put(COLUMN_LATITUDE, this@toContentValues.cityDetail.cityCoordinates.latitude)
            result.put(COLUMN_COUNTRY_CODE, this@toContentValues.cityDetail.country)
            result.put(COLUMN_FORECAST_FORECASTSNUMBER, this@toContentValues.nForecasts)
            result.put(COLUMN_UTC, element.utc)
            result.put(COLUMN_INFO_MAIN, element.weather[0].weather)
            result.put(COLUMN_INFO_DESCRIPTION, element.weather[0].weatherDesc)
            result.put(COLUMN_INFO_ICON, element.weather[0].icon)
            result.put(COLUMN_PRESSURE, element.pressure)
            result.put(COLUMN_HUMIDITY, element.humidity)
            result.put(COLUMN_FORECAST_DAY, element.temp.day)
            result.put(COLUMN_MIN_TEMP, element.temp.min)
            result.put(COLUMN_MAX_TEMP, element.temp.max)
            result.put(COLUMN_FORECAST_NIGHT, element.temp.night)
            result.put(COLUMN_FORECAST_EVE, element.temp.eve)
            result.put(COLUMN_FORECAST_MORN, element.temp.morn)
            result.put(COLUMN_WIND_DEG, element.windDegrees)
            result.put(COLUMN_WIND_SPEED, element.windSpeed)
            result.put(COLUMN_CLOUDS, element.clouds)
            result.put(COLUMN_RAIN, element.rain)
            result.put(COLUMN_SNOW, element.snow)
        }
        results.add(result)
    }
    return results
}

/**
 * Function that builds a [ForecastWeatherDto] instance from the given [Cursor]
 * @param [cursor] The cursor pointing to the weather day item data
 * @return The newly created [ForecastWeatherDto]
 */
fun toForecastWeatherDto(cursor: Cursor): ForecastWeatherDto {
    with (WeatherProvider.Companion) {
        val list = ArrayList<ForecastDetail>()

        val fwDto = ForecastWeatherDto(
                CityDetail(
                        cursor.getInt(COLUMN_LOCATION_ID_IDX),
                        cursor.getString(COLUMN_LOCATION_IDX),
                        Coordinates(
                                cursor.getDouble(COLUMN_LONGITUDE_IDX),
                                cursor.getDouble(COLUMN_LATITUDE_IDX)
                        ),
                        cursor.getString(COLUMN_COUNTRY_CODE_IDX)
                ),
                cursor.getInt(COLUMN_FORECAST_FORECASTSNUMBER_IDX),
                list
        )
        val weather = ArrayList<ForecastWeatherInfo>()
        do{
            weather.add(ForecastWeatherInfo(
                    cursor.getString(COLUMN_INFO_MAIN_IDX),
                    cursor.getString(COLUMN_INFO_DESCRIPTION_IDX),
                    cursor.getString(COLUMN_INFO_ICON_IDX)
            ))
            list.add(
                    ForecastDetail(
                            cursor.getLong(COLUMN_UTC_IDX),
                            TempDetail(
                                    cursor.getDouble(COLUMN_FORECAST_DAY_IDX),
                                    cursor.getDouble(COLUMN_MIN_TEMP_IDX),
                                    cursor.getDouble(COLUMN_MAX_TEMP_IDX),
                                    cursor.getDouble(COLUMN_FORECAST_NIGHT_IDX),
                                    cursor.getDouble(COLUMN_FORECAST_EVE_IDX),
                                    cursor.getDouble(COLUMN_FORECAST_MORN_IDX)
                            ),
                            cursor.getDouble(COLUMN_PRESSURE_IDX),
                            cursor.getInt(COLUMN_HUMIDITY_IDX),
                            weather,
                            cursor.getDouble(COLUMN_WIND_DEG_IDX),
                            cursor.getDouble(COLUMN_WIND_SPEED_IDX),
                            cursor.getInt(COLUMN_CLOUDS_IDX),
                            cursor.getDouble(COLUMN_RAIN_IDX),
                            cursor.getDouble(COLUMN_SNOW_IDX)
                    )
            )
        }while(cursor.moveToNext())

        /*
        val al = ArrayList<WeatherShortInfo>()
        al.add(WeatherShortInfo(
                cursor.getInt(COLUMN_CURRENT_SHORT_INFO_ID_IDX),
                cursor.getString(COLUMN_INFO_MAIN_IDX),
                cursor.getString(COLUMN_INFO_DESCRIPTION_IDX),
                cursor.getString(COLUMN_INFO_ICON_IDX)
        ))
        */
        return fwDto
    }
}