package isel.pdm.trab.openweathermap.models.content

import android.content.ContentValues
import android.database.AbstractCursor
import android.database.Cursor
import android.database.CursorIndexOutOfBoundsException
import android.net.Uri
import isel.pdm.trab.openweathermap.models.*
import java.util.*
import 	android.database.DatabaseUtils;

/**
 * Extension function that maps a given [CurrentWeatherDto] to the corresponding URI and
 * [ContentValues] pair.
 * @param [weather day] The instance bearing the weather day information
 * @return The newly created [ContentValues] instance
 */
fun CurrentWeatherDto.toContentValues(): ContentValues {
    val result = ContentValues()
    with (WeatherProvider) {
        result.put(COLUMN_ID, location + ", " + locDetail.countryCode)
        result.put(COLUMN_UTC, utc)
        result.put(COLUMN_LOCATION, location)
        result.put(COLUMN_LOCATION_ID, locationId)
        result.put(COLUMN_LONGITUDE, coord.longitude)
        result.put(COLUMN_LATITUDE, coord.latitude)
        result.put(COLUMN_INFO_MAIN, shortInfo[0].mainWeather)
        result.put(COLUMN_INFO_DESCRIPTION, shortInfo[0].description)
        result.put(COLUMN_INFO_ICON, shortInfo[0].icon)
        result.put(COLUMN_PRESSURE, info.pressure)
        result.put(COLUMN_HUMIDITY, info.humidity)
        result.put(COLUMN_MIN_TEMP, info.minTemp)
        result.put(COLUMN_MAX_TEMP, info.maxTemp)
        result.put(COLUMN_WIND_SPEED, windDetail.speed)
        result.put(COLUMN_WIND_DEG, windDetail.windDegrees)
        result.put(COLUMN_CLOUDS, cloudDetail.clouds)
        result.put(COLUMN_RAIN, rainDetail?.rainVolume)
        result.put(COLUMN_SNOW, snowDetail?.snowVolume)
        result.put(COLUMN_COUNTRY_CODE, locDetail.countryCode)
        result.put(COLUMN_CURRENT_TEMPERATURE, info.temperature)
        result.put(COLUMN_CURRENT_SHORT_INFO_ID, shortInfo[0].id)
        result.put(COLUMN_CURRENT_SUNRISE, locDetail.sunriseTime)
        result.put(COLUMN_CURRENT_SUNSET, locDetail.sunsetTime)
    }
    return result
}

/**
 * Function that builds a [CurrentWeatherDto] instance from the given [Cursor]
 * @param [cursor] The cursor pointing to the weather day item data
 * @return The newly created [CurrentWeatherDto]
 */
fun toCurrentWeatherDto(cursor: Cursor): CurrentWeatherDto {
    with (WeatherProvider.Companion) {
        val al = ArrayList<WeatherShortInfo>()

        al.add(WeatherShortInfo(
                cursor.getInt(COLUMN_CURRENT_SHORT_INFO_ID_IDX),
                cursor.getString(COLUMN_INFO_MAIN_IDX),
                cursor.getString(COLUMN_INFO_DESCRIPTION_IDX),
                cursor.getString(COLUMN_INFO_ICON_IDX)
        ))

        return CurrentWeatherDto(
                location = cursor.getString(COLUMN_LOCATION_IDX),
                locationId = cursor.getInt(COLUMN_LOCATION_ID_IDX),
                coord = Coordinates(
                        cursor.getDouble(COLUMN_LONGITUDE_IDX),
                        cursor.getDouble(COLUMN_LATITUDE_IDX)
                ),
                shortInfo = al,
                info = WeatherInfo(
                        cursor.getDouble(COLUMN_CURRENT_TEMPERATURE_IDX),
                        cursor.getDouble(COLUMN_PRESSURE_IDX),
                        cursor.getInt(COLUMN_HUMIDITY_IDX),
                        cursor.getDouble(COLUMN_MIN_TEMP_IDX),
                        cursor.getDouble(COLUMN_MAX_TEMP_IDX)
                ),
                windDetail = WindDetail(
                        cursor.getDouble(COLUMN_WIND_SPEED_IDX),
                        cursor.getDouble(COLUMN_WIND_DEG_IDX)
                ),
                cloudDetail =  CloudDetail(cursor.getInt(COLUMN_CLOUDS_IDX)),
                rainDetail =  RainDetail(cursor.getDouble(COLUMN_RAIN_IDX)),
                snowDetail = SnowDetail(cursor.getDouble(COLUMN_SNOW_IDX)),
                utc = cursor.getLong(COLUMN_UTC_IDX),
                locDetail =  LocationDetail(
                        cursor.getString(COLUMN_COUNTRY_CODE_IDX),
                        cursor.getLong(COLUMN_CURRENT_SUNRISE_IDX),
                        cursor.getLong(COLUMN_CURRENT_SUNSET_IDX)
                )
        )
    }
}

/**
 * Extension function that builds a list of [CurrentDayDto]
 * @param [cursor] The cursor bearing the set of weather day items
 * @return The newly created list of [CurrentWeatherDto]
 */
fun Cursor.toCurrentWeatherDtoList(): List<CurrentWeatherDto> {

    val cursorIterator = object : AbstractIterator<CurrentWeatherDto>() {
        override fun computeNext() {
            when (isAfterLast) {
                true -> done()
                false -> setNext(toCurrentWeatherDto(this@toCurrentWeatherDtoList))
            }
        }
    }

    return mutableListOf<CurrentWeatherDto>().let {
        it.addAll(Iterable { cursorIterator }); it
    }
}