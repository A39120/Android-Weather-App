package isel.pdm.trab.openweathermap.models

import android.os.Parcel
import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty

/**
 *  Class whose instances represent coordinates of a weather request from the remote API.
 *
 *  @property longitude: The longitude
 *  @property latitude: The latitude
 */
data class Coordinates(
        @JsonProperty("lon") val longitude: Double,
        @JsonProperty("lat") val latitude:  Double
): Parcelable{
    companion object {
        /** Factory of Coordinates instances */
        @JvmField @Suppress("unused")
        val CREATOR = object : Parcelable.Creator<Coordinates> {
            override fun createFromParcel(source: Parcel) = Coordinates(source)
            override fun newArray(size: Int): Array<Coordinates?> = arrayOfNulls(size)
        }
    }

    /**
     * Initiates an instance from the given parcel.
     * @param source The parcel from where the data is to be loaded from
     */
    constructor(source: Parcel) : this(
            longitude= source.readDouble(),
            latitude = source.readDouble()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.apply {
            writeDouble(longitude)
            writeDouble(latitude)
        }
    }
}

/**
 *  Class whose instances represent a short description of the weather request from the remote API.
 *
 *  @property id: The weather condition id
 *  @property mainWeather: Group of weather parameters (Rain, Snow, Extreme etc.)
 *  @property description: A short description of the weather
 *  @property icon: The weather icon identifier
 */
data class WeatherShortInfo(
        @JsonProperty("id")          val id:          Int,
        @JsonProperty("main")        val mainWeather: String,
        @JsonProperty("description") val description: String,
        @JsonProperty("icon")        val icon:        String
) : Parcelable{
    companion object {
        /** Factory of WeatherShortInfo instances */
        @JvmField @Suppress("unused")
        val CREATOR = object : Parcelable.Creator<WeatherShortInfo> {
            override fun createFromParcel(source: Parcel) = WeatherShortInfo(source)
            override fun newArray(size: Int): Array<WeatherShortInfo?> = arrayOfNulls(size)
        }
    }

    /**
     * Initiates an instance from the given parcel.
     * @param source The parcel from where the data is to be loaded from
     */
    constructor(source: Parcel) : this(
            id = source.readInt(),
            mainWeather = source.readString(),
            description = source.readString(),
            icon = source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.apply {
            writeInt(id)
            writeString(mainWeather)
            writeString(description)
            writeString(icon)
        }
    }
}

/**
 * Class whose instances represent information about temperature, pressure and humidity from the
 *      weather request of the remote API.
 *
 * @property temperature: The location's temperature
 * @property pressure: The location's atmospheric pressure in hPa
 * @property humidity: The location's humidity in %
 * @property minTemp: The location's minimum temperature at the moment
 * @property maxTemp: The location's maximum temperature at the moment
 */
data class WeatherInfo(
        @JsonProperty("temp")     val temperature: Double,
        @JsonProperty("pressure") val pressure:    Double,
        @JsonProperty("humidity") val humidity:    Int,
        @JsonProperty("temp_min") val minTemp:     Double,
        @JsonProperty("temp_max") val maxTemp:     Double
): Parcelable{

    companion object {
        /** Factory of WeatherInfo instances */
        @JvmField @Suppress("unused")
        val CREATOR = object : Parcelable.Creator<WeatherInfo> {
            override fun createFromParcel(source: Parcel) = WeatherInfo(source)
            override fun newArray(size: Int): Array<WeatherInfo?> = arrayOfNulls(size)
        }
    }

    /**
     * Initiates an instance from the given parcel.
     * @param source The parcel from where the data is to be loaded from
     */
    constructor(source: Parcel) : this(
            temperature = source.readDouble(),
            pressure  = source.readDouble(),
            humidity = source.readInt(),
            minTemp = source.readDouble(),
            maxTemp = source.readDouble()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.apply {
            writeDouble(temperature)
            writeDouble(pressure)
            writeInt(humidity)
            writeDouble(minTemp)
            writeDouble(maxTemp)
        }
    }
}

/**
 * Class whose instances represent information about the wind conditions
 *
 * @property speed: The wind's speed
 * @property windDegrees: The wind direction, in degrees
 */
data class WindDetail(
        @JsonProperty("speed") val speed:       Double,
        @JsonProperty("deg")   val windDegrees: Double
) : Parcelable{
    companion object {
        /** Factory of WindDetail instances */
        @JvmField @Suppress("unused")
        val CREATOR = object : Parcelable.Creator<WindDetail> {
            override fun createFromParcel(source: Parcel) = WindDetail(source)
            override fun newArray(size: Int): Array<WindDetail?> = arrayOfNulls(size)
        }
    }

    /**
     * Initiates an instance from the given parcel.
     * @param source The parcel from where the data is to be loaded from
     */
    constructor(source: Parcel) : this(
            speed = source.readDouble(),
            windDegrees = source.readDouble()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.apply {
            writeDouble(speed)
            writeDouble(windDegrees)
        }
    }
}

/**
 * Class whose instances represent information about cloudiness
 *
 * @property clouds: Cloudiness in %
 */
data class CloudDetail(
        @JsonProperty("all") val clouds: Int
): Parcelable{
    companion object {
        /** Factory of CloudDetail instances */
        @JvmField @Suppress("unused")
        val CREATOR = object : Parcelable.Creator<CloudDetail> {
            override fun createFromParcel(source: Parcel) = CloudDetail(source)
            override fun newArray(size: Int): Array<CloudDetail?> = arrayOfNulls(size)
        }
    }

    /**
     * Initiates an instance from the given parcel.
     * @param source The parcel from where the data is to be loaded from
     */
    constructor(source: Parcel) : this(
            clouds = source.readInt()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.apply {
            writeInt(clouds)
        }
    }
}

/**
 * Class whose instances represent information about rain
 *
 * @property rainVolume: Rain volume for the last 3 hours, in mm
 */
data class RainDetail(
        @JsonProperty("3h") val rainVolume: Double
): Parcelable{
    companion object {
        /** Factory of RainDetail instances */
        @JvmField @Suppress("unused")
        val CREATOR = object : Parcelable.Creator<RainDetail> {
            override fun createFromParcel(source: Parcel) = RainDetail(source)
            override fun newArray(size: Int): Array<RainDetail?> = arrayOfNulls(size)
        }
    }

    /**
     * Initiates an instance from the given parcel.
     * @param source The parcel from where the data is to be loaded from
     */
    constructor(source: Parcel) : this(
            rainVolume = source.readDouble()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.apply {
            writeDouble(rainVolume)
        }
    }
}

/**
 * Class whose instances represent information about snow
 *
 * @property snowVolume: Snow volume for the last 3 hours, in mm
 */
data class SnowDetail(
        @JsonProperty("3h") val snowVolume: Double
): Parcelable{
    companion object {
        /** Factory of SnowDetail instances */
        @JvmField @Suppress("unused")
        val CREATOR = object : Parcelable.Creator<SnowDetail> {
            override fun createFromParcel(source: Parcel) = SnowDetail(source)
            override fun newArray(size: Int): Array<SnowDetail?> = arrayOfNulls(size)
        }
    }

    /**
     * Initiates an instance from the given parcel.
     * @param source The parcel from where the data is to be loaded from
     */
    constructor(source: Parcel) : this(
            snowVolume = source.readDouble()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.apply {
            writeDouble(snowVolume)
        }
    }
}

/**
 * Class whose instances represent information about the country and the sun
 *
 * @property countryCode: The country code
 * @property sunriseTime: The sunsire time, unix, UTC
 * @property sunsetTime: The sunset time, unix, UTC
 */
data class LocationDetail(
        @JsonProperty("country") val countryCode: String,
        @JsonProperty("sunrise") val sunriseTime: Long,
        @JsonProperty("sunset")  val sunsetTime:  Long
): Parcelable{
    companion object {
        /** Factory of LocationDetail instances */
        @JvmField @Suppress("unused")
        val CREATOR = object : Parcelable.Creator<LocationDetail> {
            override fun createFromParcel(source: Parcel) = LocationDetail(source)
            override fun newArray(size: Int): Array<LocationDetail?> = arrayOfNulls(size)
        }
    }

    /**
     * Initiates an instance from the given parcel.
     * @param source The parcel from where the data is to be loaded from
     */
    constructor(source: Parcel) : this(
            countryCode = source.readString(),
            sunriseTime = source.readLong(),
            sunsetTime = source.readLong()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.apply {
            writeString(countryCode)
            writeLong(sunriseTime)
            writeLong(sunsetTime)
        }
    }
}