package isel.pdm.trab.openweathermap

import java.text.SimpleDateFormat
import java.util.*

class SimpleDate {
    companion object{

        private val EU_TIME_FORMAT = "dd/MM/yyyy"
        private val ONE_DAY_IN_MILLISECONDS = 86400000

        /**
         * Return date in specified format.
         * @param milliSeconds SimpleDate in milliseconds
         * @param dateFormat SimpleDate format
         * @return String representing date in specified format
         */
        fun getDate(milliSeconds: Long): String {
            // Create a DateFormatter object for displaying date in specified format.
            val formatter = SimpleDateFormat(EU_TIME_FORMAT);

            // Create a calendar object that will convert the date and time value in milliseconds to date.
            val calendar = Calendar.getInstance();
            calendar.setTimeInMillis(milliSeconds);
            return formatter.format(calendar.getTime());
        }


        fun getCurrentDate(): String {
            // Gets current time in millis
            val milliseconds = System.currentTimeMillis()
            return getDate(milliseconds)
        }

        fun getDaysAfterCurrent(days: Int): String {
            val milliseconds = System.currentTimeMillis()
            return getDate(days * ONE_DAY_IN_MILLISECONDS + milliseconds)
        }

    }
}
