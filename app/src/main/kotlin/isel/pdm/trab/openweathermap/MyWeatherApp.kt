package isel.pdm.trab.openweathermap

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.widget.ArrayAdapter
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.Volley
import isel.pdm.trab.openweathermap.services.LruDtoCache
import isel.pdm.trab.openweathermap.services.LruImageCache
import java.util.*


class MyWeatherApp : Application(){

    lateinit var requestQueue: RequestQueue

    lateinit var imageLoader: ImageLoader

    val DTO_CACHE_SIZE = 20
    lateinit var lruDtoCache: MutableMap<String, Any>

    val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
    val cacheSize = maxMemory / 8

    var forecastRefreshTimestamp: Long = 0


    companion object {
        lateinit var instance: MyWeatherApp

        /**
         *  The language for the app UI
         *  English by default
         */
        var language: String = "en"

        /**
         *  The city for which the weather requests will be made
         *  Is set by default to the device's country
         */
        var city: String = Locale.getDefault().displayCountry

        /**
         * Shared preferences stuff, so it can be accessed when needed
         */
        var prefs: SharedPreferences? = null
        val SHARED_PREFERENCES_URI = "isel.pdm.trab.openweathermap"
        /* keys to get the variables */
        val FAVOURITE_LOC_KEY = "isel.pdm.trab.openweathermap.favloc"
        val SUBSCRIBED_LOCS_KEY = "isel.pdm.trab.openweathermap.sublocs"
        val ENABLED_TIME_FOR_NOTIFICATIONS_KEY = "isel.pdm.trab.openweathermap.enabledtimefornotifications"
        val ENABLED_BATTERY_LEVEL_KEY = "isel.pdm.trab.openweathermap.enabledbatterylevel"
        val TIME_FOR_NOTIFICATIONS_UNIX_KEY = "isel.pdm.trab.openweathermap.timefornotificationsunix"
        val REFRESH_TIME_KEY = "isel.pdm.trab.openweathermap.timefornotifications"
        val BATTERY_LEVEL_KEY = "isel.pdm.trab.openweathermap.batterylevel"
        val USE_MOBILE_DATA_KEY = "isel.pdm.trab.openweathermap.usemobiledata"
        /* variables */
        lateinit var favouriteLoc: String
        lateinit var subscribedLocs: ArrayList<String>
        var enabledTimeForNotifications: Boolean = false
        var enabledBatteryLevel: Boolean = false
        var timeForNotificationsUnix: Long = -1
        var timeForNotifications: Calendar = Calendar.getInstance()
        var refreshTime: Int = -1 // TODO change to some default ? maybe 12h ?
        var batteryLevel: Int = -1
        var isBatterySavingMode: Boolean = false /** used by BatteryStateReceiver as a flag, other actions should consult this flag */
        var canUseMobileData: Boolean = false
        // val refreshIntervalValues: Array<Int> = arrayOf(12, 24, 48) // 12h, 1day, 2days
        // we can use strings.xml -> see <string-array name="pref_refresh_interval_values">
        // static values can be set there
        lateinit var locationsSubbedSpinnerAdapter: ArrayAdapter<String>
        lateinit var favouriteLocSpinnerAdapter: ArrayAdapter<String>
    }

    override fun onCreate(){
        super.onCreate()
        instance = this
        loadPreferences() // load preferences
        city = favouriteLoc // set favourite location as "main" location to search info for
        requestQueue = Volley.newRequestQueue(this)
        //imageLoader = ImageLoader(requestQueue, NullImageCache())
        imageLoader = ImageLoader(requestQueue, LruImageCache(cacheSize))
        lruDtoCache = Collections.synchronizedMap(LruDtoCache<String, Any>(DTO_CACHE_SIZE))
    }

    private fun loadPreferences(){
        // load from shared preferences
        prefs = this.getSharedPreferences(MyWeatherApp.SHARED_PREFERENCES_URI, Context.MODE_PRIVATE)
        val prefs = MyWeatherApp.prefs!!
        // favourite location
        favouriteLoc = prefs.getString(FAVOURITE_LOC_KEY, Locale.getDefault().displayCountry) // default will be device's country
        // list of subscribed locations
        subscribedLocs = ArrayList<String>(prefs.getStringSet(SUBSCRIBED_LOCS_KEY, LinkedHashSet<String>()))
        // check to see if a time for notification is set
        enabledTimeForNotifications = prefs.getBoolean(ENABLED_TIME_FOR_NOTIFICATIONS_KEY, false)
        // check to see if a battery level is set
        enabledBatteryLevel = prefs.getBoolean(ENABLED_BATTERY_LEVEL_KEY, false)
        // time for notifications (8 in da mornin')
        timeForNotificationsUnix = prefs.getLong(TIME_FOR_NOTIFICATIONS_UNIX_KEY, -1)
        timeForNotifications.timeInMillis = timeForNotificationsUnix
        // time interval for database refresh (human scale)
        refreshTime = prefs.getInt(REFRESH_TIME_KEY, -1)
        // battery level
        batteryLevel = prefs.getInt(BATTERY_LEVEL_KEY, -1)
        // if we can use mobile data for the updates
        canUseMobileData = prefs.getBoolean(USE_MOBILE_DATA_KEY, false)
    }
}