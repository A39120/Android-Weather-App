package isel.pdm.trab.openweathermap.models

import android.os.Parcel
import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty

/**
 *  Class whose instances represent weather information for a specific day (the current day)
 *      obtained from the remote API
 *
 *  @property location:  The location of which the API request has been made
 *  @property locationId:    The location identifier
 *  @property coord: The coordinates of the location
 *  @property shortInfo: A short decription of the weather
 *  @property info: Information about temperature, pressure and humidity
 *  @property windDetail: Information about the wind
 *  @property cloudDetail: Information about cloudiness
 *  @property rainDetail: Information about rain
 *  @property snowDetail: Information about snow
 *  @property utc: The time of data calculation, UTC
 *  @property locDetail: Internal information
 */
class CurrentWeatherDto(
        @JsonProperty("name")    val location: String,
        @JsonProperty("id")      val locationId: Int,
        @JsonProperty("coord")   val coord: Coordinates,
        @JsonProperty("weather") val shortInfo: List<WeatherShortInfo>,
        @JsonProperty("main")    val info: WeatherInfo,
        @JsonProperty("wind")    val windDetail: WindDetail,
        @JsonProperty("clouds")  val cloudDetail: CloudDetail,
        @JsonProperty("rain")    val rainDetail: RainDetail?,
        @JsonProperty("snow")    val snowDetail: SnowDetail?,
        @JsonProperty("dt")      val utc: Long,
        @JsonProperty("sys")     val locDetail: LocationDetail
): Parcelable {
    companion object {
        /** Factory of CurrentWeatherDtos instances */
        @JvmField @Suppress("unused")
        val CREATOR = object : Parcelable.Creator<CurrentWeatherDto> {
            override fun createFromParcel(source: Parcel) = CurrentWeatherDto(source)
            override fun newArray(size: Int): Array<CurrentWeatherDto?> = arrayOfNulls(size)
        }
    }

    /**
     * Initiates an instance from the given parcel.
     * @param source The parcel from where the data is to be loaded from
     */
    constructor(source: Parcel) : this(
            location = source.readString(),
            locationId = source.readInt(),
            coord = source.readTypedObject(Coordinates.CREATOR),
            shortInfo = mutableListOf<WeatherShortInfo>().apply {source.readTypedList(this, WeatherShortInfo.CREATOR)},
            info = source.readTypedObject(WeatherInfo.CREATOR),
            windDetail = source.readTypedObject(WindDetail.CREATOR),
            cloudDetail= source.readTypedObject(CloudDetail.CREATOR),
            rainDetail = source.readTypedObject(RainDetail.CREATOR),
            snowDetail = source.readTypedObject(SnowDetail.CREATOR),
            utc = source.readLong(),
            locDetail = source.readTypedObject(LocationDetail.CREATOR)
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.apply {
            writeString(location)
            writeInt(locationId)
            writeTypedObject(coord,0)
            writeTypedList(shortInfo)
            writeTypedObject(info, 0)
            writeTypedObject(windDetail,0)
            writeTypedObject(cloudDetail,0)
            writeTypedObject(rainDetail,0)
            writeTypedObject(snowDetail,0)
            writeLong(utc)
            writeTypedObject(locDetail, 0)
        }
    }}