package isel.pdm.trab.openweathermap.presentation

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import isel.pdm.trab.openweathermap.MyWeatherApp
import isel.pdm.trab.openweathermap.R
import kotlinx.android.synthetic.main.activity_preference.*
import kotlinx.android.synthetic.main.activity_preference.view.*
import java.util.*
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Spinner

class PreferencesActivity : BaseActivity() {
    override var layoutResId: Int = R.layout.activity_preference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // load from shared preferences
        //if(MyWeatherApp.prefs == null) MyWeatherApp.prefs = this.getSharedPreferences(MyWeatherApp.SHARED_PREFERENCES_URI, Context.MODE_PRIVATE)
        val prefs = MyWeatherApp.prefs
        val app = MyWeatherApp
        val editor = prefs!!.edit()

        // create adapters for each Spinner
        // locations we've subscribed from update
        app.locationsSubbedSpinnerAdapter = ArrayAdapter<String>(PreferencesActivity@this, android.R.layout.simple_spinner_item, app.subscribedLocs)
        app.locationsSubbedSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // our favourite location -> we'll get notified of stuff about this location
        app.favouriteLocSpinnerAdapter = ArrayAdapter<String>(PreferencesActivity@this, android.R.layout.simple_spinner_item, app.subscribedLocs)
        app.favouriteLocSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        (activity_preference.unsubscribeSpinner as Spinner).adapter = app.locationsSubbedSpinnerAdapter
        (activity_preference.favouriteLocationSpinner).adapter = app.favouriteLocSpinnerAdapter

        (activity_preference.unsubscribeSpinner).setSelection(0)
        app.locationsSubbedSpinnerAdapter.notifyDataSetChanged()


        var idx = -1
        for (i in app.subscribedLocs.indices) {
            if(app.subscribedLocs[i].equals(app.favouriteLoc)){
                idx = i
                break
            }
        }
        if(idx >= 0)(activity_preference.favouriteLocationSpinner).setSelection(idx) // select fav location if it was found
        app.favouriteLocSpinnerAdapter.notifyDataSetChanged()

        if(idx < 0) // user doesn't have favourite city, then disable timePicker
            app.enabledTimeForNotifications = false

        // "are we receiving notifications ?"
        (activity_preference.notificationSwitch).isChecked = app.enabledTimeForNotifications

        (activity_preference.timePicker).isEnabled = app.enabledTimeForNotifications
        (activity_preference.timePicker).currentHour = app.timeForNotifications.get(Calendar.HOUR_OF_DAY)
        (activity_preference.timePicker).currentMinute = app.timeForNotifications.get(Calendar.MINUTE)

        (activity_preference.refreshIntervalSpinner).setSelection(app.refreshTime)
        (activity_preference.batteryIntervalSpinner).setSelection(app.batteryLevel)
        (activity_preference.batterySwitch).isChecked = app.enabledBatteryLevel

        (activity_preference.subscribeButton).setOnClickListener {
            val location: String = activity_preference.subscribeText.text.toString()
            if(!app.subscribedLocs.contains(location) && !location.equals("")) {
                activity_preference.subscribeText.text.clear()
                app.subscribedLocs.add(location)
                app.locationsSubbedSpinnerAdapter.notifyDataSetChanged()
                app.favouriteLocSpinnerAdapter.notifyDataSetChanged()
                editor.putStringSet(app.SUBSCRIBED_LOCS_KEY, app.subscribedLocs.toSet())
                editor.apply()
            }
        }

        (activity_preference.unsubscribeButton).setOnClickListener {
            val location: String = activity_preference.unsubscribeSpinner.selectedItem as String
            if(!location.equals("")){
                app.subscribedLocs.remove(location)
                app.locationsSubbedSpinnerAdapter.notifyDataSetChanged()
                app.favouriteLocSpinnerAdapter.notifyDataSetChanged()
                activity_preference.unsubscribeSpinner.setSelection(0) // select first one
                editor.putStringSet(app.SUBSCRIBED_LOCS_KEY, app.subscribedLocs.toSet())
                editor.apply()
            }
        }

        (activity_preference.favouriteLocationSpinner).onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View?, position: Int, id: Long) {
                MyWeatherApp.favouriteLoc = activity_preference.favouriteLocationSpinner.selectedItem as String
                editor.putString(MyWeatherApp.FAVOURITE_LOC_KEY, MyWeatherApp.favouriteLoc)
                editor.apply()
            }
            override fun onNothingSelected(parentView: AdapterView<*>) {}
        }

        (activity_preference.timePicker).setOnTimeChangedListener {
            timePicker, hour, minutes ->
                app.timeForNotifications.set(Calendar.HOUR_OF_DAY, hour)
                app.timeForNotifications.set(Calendar.MINUTE, minutes)
                app.timeForNotificationsUnix = app.timeForNotifications.timeInMillis
                editor.putLong(app.TIME_FOR_NOTIFICATIONS_UNIX_KEY,app.timeForNotificationsUnix)
                editor.apply()
        }

        (activity_preference.notificationSwitch).setOnCheckedChangeListener {
            compoundButton, checked ->
                app.enabledTimeForNotifications = checked
                (activity_preference.timePicker).isEnabled = checked
                editor.putBoolean(app.ENABLED_TIME_FOR_NOTIFICATIONS_KEY, checked)
                editor.apply()
        }

        (activity_preference.batterySwitch).setOnCheckedChangeListener {
            compoundButton, checked ->
            app.enabledBatteryLevel = checked
            (activity_preference.batteryIntervalSpinner).isEnabled = checked
            editor.putBoolean(app.ENABLED_BATTERY_LEVEL_KEY, checked)
            editor.apply()
        }

        (activity_preference.refreshIntervalSpinner).onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View?, position: Int, id: Long) {
                /*val refreshInterval: String = activity_preference.refreshIntervalSpinner.selectedItem as String
                val refreshIntervalTime = refreshInterval.split(" ")[0]
                val refreshIntervalUnit = refreshInterval.split(" ")[1]*/
                // TODO will need some kinda conversion when someone accesses this field later on
                // TODO 12h 1day 2days to 12h 24h 48h
                MyWeatherApp.refreshTime = position
                editor.putInt(MyWeatherApp.REFRESH_TIME_KEY, position)
                editor.apply()
            }
            override fun onNothingSelected(parentView: AdapterView<*>) {}
        }

        (activity_preference.batteryIntervalSpinner).onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                /*
                val selectedItem: String = activity_preference.batteryIntervalSpinner.selectedItem as String
                val batteryPercentage = selectedItem.split(" ")[0]
                */
                MyWeatherApp.batteryLevel = position
                editor.putInt(MyWeatherApp.BATTERY_LEVEL_KEY, position)
                editor.apply()
            }
            override fun onNothingSelected(parentView: AdapterView<*>) {}
        }
    }
}
