package isel.pdm.trab.openweathermap.models

import android.os.Parcel
import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty

/**
 *  Class whose instances represent forecast weather information for a period of time obtained from
 *      the remote API.
 *
 *  @property cityDetail: Details about the location
 *  @property nForecasts: The number of forecasts
 *  @property forecastDetail: A list of forecasts
 */
class ForecastWeatherDto(
        @JsonProperty("city") val cityDetail: CityDetail,
        @JsonProperty("cnt")  val nForecasts: Int,
        @JsonProperty("list") val forecastDetail: List<ForecastDetail>
) : Parcelable{
    companion object {
        /** Factory of ForecastWeatherDto instances */
        @JvmField @Suppress("unused")
        val CREATOR = object : Parcelable.Creator<ForecastWeatherDto> {
            override fun createFromParcel(source: Parcel) = ForecastWeatherDto(source)
            override fun newArray(size: Int): Array<ForecastWeatherDto?> = arrayOfNulls(size)
        }
    }

    /**
     * Initiates an instance from the given parcel.
     * @param source The parcel from where the data is to be loaded from
     */
    constructor(source: Parcel) : this(
            cityDetail = source.readTypedObject(CityDetail.CREATOR),
            nForecasts = source.readInt(),
            forecastDetail = mutableListOf<ForecastDetail>().apply {source.readTypedList(this, ForecastDetail.CREATOR)}
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.apply {
            writeTypedObject(cityDetail,0)
            writeInt(nForecasts)
            writeTypedList(forecastDetail)
        }
    }

    /**
     *  Class whose instances represent short forecast weather information obtained from the remote API
     *
     *  @property weather: Group of weather parameters (Rain, Snow, Extreme etc.)
     *  @property weatherDesc: Short description about the weather
     */
    data class ForecastWeatherInfo(
            @JsonProperty("main") val weather: String,
            @JsonProperty("description") val weatherDesc: String,
            val icon: String
    ): Parcelable{

        companion object {
            /** Factory of ForecastWeatherInfo instances */
            @JvmField @Suppress("unused")
            val CREATOR = object : Parcelable.Creator<ForecastWeatherInfo> {
                override fun createFromParcel(source: Parcel) = ForecastWeatherInfo(source)
                override fun newArray(size: Int): Array<ForecastWeatherInfo?> = arrayOfNulls(size)
            }
        }

        /**
         * Initiates an instance from the given parcel.
         * @param source The parcel from where the data is to be loaded from
         */
        constructor(source: Parcel) : this(
                weather = source.readString(),
                weatherDesc = source.readString(),
                icon = source.readString()
        )

        override fun describeContents() = 0

        override fun writeToParcel(dest: Parcel, flags: Int) {
            dest.apply {
                writeString(weather)
                writeString(weatherDesc)
                writeString(icon)
            }
        }
    }


    /**
     *  Class whose instances represent forecast weather information obtained from the remote API
     *
     *  @property utc: Time of data forecasted, unix, UTC
     *  @property temp: Temperature detail of the forecast
     *  @property pressure: Atmospheric pressure on the sea level by default, hPa
     *  @property humidity: Humidity, %
     *  @property weather: A short description of the forecasted weather
     *  @property windDegrees: The wind direction
     *  @property windSpeed: The wind speed
     *  @property clouds: Cloudiness, %
     *  @property rain: Rain volume for last 3 hours, mm
     *  @property snow: Snow volume for last 3 hours, mm
     */
    data class ForecastDetail(
            @JsonProperty("dt") val utc: Long,
            val temp: TempDetail,
            val pressure: Double,
            val humidity: Int,
            val weather: List<ForecastWeatherInfo>,
            @JsonProperty("deg") val windDegrees: Double,
            @JsonProperty("speed") val windSpeed: Double,
            val clouds: Int,
            val rain: Double = 0.0,
            val snow: Double = 0.0

    ): Parcelable{
        companion object {
            /** Factory of ForecastDetail instances */
            @JvmField @Suppress("unused")
            val CREATOR = object : Parcelable.Creator<ForecastDetail> {
                override fun createFromParcel(source: Parcel) = ForecastDetail(source)
                override fun newArray(size: Int): Array<ForecastDetail?> = arrayOfNulls(size)
            }
        }

        /**
         * Initiates an instance from the given parcel.
         * @param source The parcel from where the data is to be loaded from
         */
        constructor(source: Parcel) : this(
                utc = source.readLong(),
                temp = source.readTypedObject(TempDetail.CREATOR),
                pressure = source.readDouble(),
                humidity = source.readInt(),
                weather = mutableListOf<ForecastWeatherInfo>().apply {
                    source.readTypedList(this, ForecastWeatherInfo.CREATOR)
                },
                windDegrees = source.readDouble(),
                windSpeed = source.readDouble(),
                clouds = source.readInt(),
                rain = source.readDouble(),
                snow = source.readDouble()
        )

        override fun describeContents() = 0

        override fun writeToParcel(dest: Parcel, flags: Int) {
            dest.apply {
                writeLong(utc)
                writeTypedObject(temp, 0)
                writeDouble(pressure)
                writeInt(humidity)
                writeTypedList(weather)
                writeDouble(windDegrees)
                writeDouble(windSpeed)
                writeInt(clouds)
                writeDouble(rain)
                writeDouble(snow)
            }
        }
    }

    /**
     *  Class whose instances represent forecast weather temperature information obtained from the
     *      remote API.
     *
     *  @property day: The temperature at daytime
     *  @property min: The minimum temperature
     *  @property max: The maximum temperature
     *  @property night: The temperature at night
     *  @property eve: The temperature in the evening
     *  @property morn: The temperature in the morning
     */
    data class TempDetail(
            val day: Double,
            val min: Double,
            val max: Double,
            val night: Double,
            val eve: Double,
            val morn: Double
    ) : Parcelable {
        companion object {
            /** Factory of TempDetail instances */
            @JvmField @Suppress("unused")
            val CREATOR = object : Parcelable.Creator<TempDetail> {
                override fun createFromParcel(source: Parcel) = TempDetail(source)
                override fun newArray(size: Int): Array<TempDetail?> = arrayOfNulls(size)
            }
        }

        /**
         * Initiates an instance from the given parcel.
         * @param source The parcel from where the data is to be loaded from
         */
        constructor(source: Parcel) : this(
                day = source.readDouble(),
                min = source.readDouble(),
                max = source.readDouble(),
                night = source.readDouble(),
                eve = source.readDouble(),
                morn = source.readDouble()
        )

        override fun describeContents() = 0

        override fun writeToParcel(dest: Parcel, flags: Int) {
            dest.apply {
                writeDouble(day)
                writeDouble(min)
                writeDouble(max)
                writeDouble(night)
                writeDouble(eve)
                writeDouble(morn)
            }
        }
    }

    /**
     *  Class whose instances represent the locatiomn information of the forecast obtained from the
     *      remote API.
     *
     *  @property id: The location identifier
     *  @property cityName: The location's name
     *  @property cityCoordinates: The location's coordinates
     *  @property country: The location's country code
     */
    data class CityDetail(
            val id: Int,
            @JsonProperty("name")    val cityName: String,
            @JsonProperty("coord")   val cityCoordinates: Coordinates,
            val country: String
    ) : Parcelable {
        companion object {
            /** Factory of CityDetail instances */
            @JvmField @Suppress("Unused")
            val CREATOR = object : Parcelable.Creator<CityDetail> {
                override fun createFromParcel(source: Parcel) = CityDetail(source)
                override fun newArray(size: Int): Array<CityDetail?> = arrayOfNulls(size)
            }
        }

        constructor(dest: Parcel) : this(
                id = dest.readInt(),
                cityName = dest.readString(),
                cityCoordinates = dest.readTypedObject(Coordinates.CREATOR),
                country = dest.readString()
        )

        override fun describeContents() = 0

        override fun writeToParcel(dest: Parcel, flags: Int) {
            dest.apply {
                dest.writeInt(id)
                dest.writeString(cityName)
                dest.writeTypedObject(cityCoordinates, 0)
                dest.writeString(country)
            }
        }
    }
}