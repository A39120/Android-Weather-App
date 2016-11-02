package isel.pdm.trab.openweathermap.models

import android.os.Parcel
import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty

import java.util.*


class ForecastWeatherDto(
        @JsonProperty("city") val cityDetail: CityDetail,
        @JsonProperty("cnt")  val nForecasts: Int,
        @JsonProperty("list") val forecastDetail: List<ForecastDetail>
) : Parcelable{
    companion object {
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


    data class ForecastWeatherInfo(
            @JsonProperty("main") val weather: String,
            @JsonProperty("description") val weatherDesc: String,
            val icon: String
    ): Parcelable{

        companion object {
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


    data class ForecastDetail(
            @JsonProperty("dt")      val utc:         Long,
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

    data class TempDetail(
            val day: Double,
            val min: Double,
            val max: Double,
            val night: Double,
            val eve: Double,
            val morn: Double
    ) : Parcelable {
        companion object {
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

    data class CityDetail(
            val id:              Int,
            @JsonProperty("name")    val cityName:        String,
            @JsonProperty("coord")   val cityCoordinates: Coordinates,
            val country:         String
    ) : Parcelable {
        companion object {
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