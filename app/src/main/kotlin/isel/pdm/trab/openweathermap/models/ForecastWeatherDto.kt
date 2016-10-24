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
            nForecasts = source.readInt()
            //TODO: forecastDetail
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.apply {
            writeTypedObject(cityDetail, 0)
            writeInt(nForecasts)
            writeTypedList(forecastDetail)
        }
    }

    data class ForecastDetail(
            @JsonProperty("dt")      val utc:         Int,
            @JsonProperty("main")    val info:        WeatherInfo,
            @JsonProperty("weather") val shortInfo:   List<WeatherShortInfo>,
            @JsonProperty("wind")    val windDetail:  WindDetail,
            @JsonProperty("clouds")  val cloudDetail: CloudDetail,
            @JsonProperty("rain")    val rainDetail:  RainDetail?,
            @JsonProperty("snow")    val snowDetail:  SnowDetail?,
            @JsonProperty("dt_txt")  val dateText:    String
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
            utc = source.readInt(),
            info = source.readTypedObject(WeatherInfo.CREATOR),
            //TODO: shortInfo = mutableListtOf<WeatherShortInfo>().apply { source.readTypedList(this, WeatherShortInfo.CREATOR) },
            windDetail = source.readTypedObject(WindDetail.CREATOR),
            cloudDetail = source.readTypedObject(CloudDetail.CREATOR),
            rainDetail = source.readTypedObject(RainDetail.CREATOR),
            snowDetail = source.readTypedObject(SnowDetail.CREATOR),
            dateText = source.readString()
        )

        override fun describeContents() = 0

        override fun writeToParcel(dest: Parcel, flags: Int) {
            dest.apply {
                writeInt(utc)
                writeTypedObject(info, 0)
                writeTypedObject(windDetail, 0)
                writeTypedObject(cloudDetail, 0)
                writeTypedObject(rainDetail, 0)
                writeTypedObject(snowDetail, 0)
                writeString(dateText)
            }
        }
    }

    data class CityDetail(
            @JsonProperty("id")      val id:              Int,
            @JsonProperty("name")    val cityName:        String,
            @JsonProperty("coord")   val cityCoordinates: Coordinates,
            @JsonProperty("country") val country:         String
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