package isel.pdm.trab.openweathermap.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import isel.pdm.trab.openweathermap.MyWeatherApp

class BatteryStateReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        val percentage = level / scale.toFloat()
        val app = MyWeatherApp

        app.isBatterySavingMode = (percentage < app.batteryLevel) // has it reached battery saving levels ?
    }
}
