package isel.pdm.trab.openweathermap.models.content

import android.content.ContentProvider
import android.content.ContentResolver
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri
import android.support.annotation.MainThread

class WeatherProvider: ContentProvider() {
    companion object{
        // Identifier
        const val AUTHORITY = "isel.pdm.trab.openweathermap"
        const val CURRENT_TABLE_PATH = "current"
        const val FORECAST_TABLE_PATH = "forecast"

        const val CURRENT_CONTENT = "content://$AUTHORITY/$CURRENT_TABLE_PATH"
        const val FORECAST_CONTENT = "content://$AUTHORITY/$FORECAST_TABLE_PATH"
        val CURRENT_CONTENT_URI: Uri = Uri.parse(CURRENT_CONTENT)
        val FORECAST_CONTENT_URI: Uri = Uri.parse(FORECAST_CONTENT)

        val CURRENT_ITEM_CONTENT_TYPE =  "${ContentResolver.CURSOR_ITEM_BASE_TYPE}/current"
        val FORECAST_ITEM_CONTENT_TYPE =  "${ContentResolver.CURSOR_ITEM_BASE_TYPE}/forecast"

        /** TODO: This would be more beautiful if the data from Current and Forecast used the same classes
          * and all the information not used was not in there
          */


        const val COLUMN_ID = "_ID"
        const val COLUMN_UTC = "UTC"
        const val COLUMN_LOCATION = "LOCATION"
        const val COLUMN_LOCATION_ID = "LOCATION_ID"
        const val COLUMN_LONGITUDE = "LONGITUDE"
        const val COLUMN_LATITUDE = "LATITUDE"
        const val COLUMN_COUNTRY_CODE = "COUNTRY_CODE" //TODO: REPLACE IDX
        /**
         * For current columns,
         */
        // Short Weather Info
        const val COLUMN_INFO_MAIN = "SHORT_INFO_MAIN"
        const val COLUMN_INFO_DESCRIPTION = "SHORT_INFO_DESCRIPTION"
        const val COLUMN_INFO_ICON = "SHORT_INFO_ICON"
        // Weather Info
        const val COLUMN_PRESSURE = "PRESSURE"
        const val COLUMN_HUMIDITY = "HUMIDITY"
        const val COLUMN_MIN_TEMP = "MIN_TEMP"
        const val COLUMN_MAX_TEMP = "MAX_TEMP"
        // Wind Detail
        const val COLUMN_WIND_SPEED = "WIND_SPEED"
        const val COLUMN_WIND_DEG = "WIND_DEG"
        // Cloud , Rain and Snow Detail
        const val COLUMN_CLOUDS = "CLOUDS"
        const val COLUMN_RAIN = "RAIN"
        const val COLUMN_SNOW = "SNOW"
        // Location Detail
        const val COLUMN_CURRENT_TEMPERATURE = "TEMPERATURE"
        const val COLUMN_CURRENT_SHORT_INFO_ID = "SHORT_INFO_ID"
        const val COLUMN_CURRENT_SUNRISE = "SUNRISE"
        const val COLUMN_CURRENT_SUNSET = "SUNSET"


        const val COLUMN_FORECAST_FORECASTSNUMBER = "FORECASTS_NUMBER"
        const val COLUMN_FORECAST_DAY = "TEMPERATURE_DAY"
        const val COLUMN_FORECAST_NIGHT = "TEMPERATURE_NIGHT"
        const val COLUMN_FORECAST_EVE = "TEMPERATURE_EVE"
        const val COLUMN_FORECAST_MORN = "TEMPERATURE_MORN"


        const val COLUMN_ID_IDX = 0
        const val COLUMN_UTC_IDX = 1
        const val COLUMN_LOCATION_IDX = 2
        const val COLUMN_LOCATION_ID_IDX = 3
        const val COLUMN_LONGITUDE_IDX = 4
        const val COLUMN_LATITUDE_IDX = 5
        const val COLUMN_INFO_MAIN_IDX = 6
        const val COLUMN_INFO_DESCRIPTION_IDX = 7
        const val COLUMN_INFO_ICON_IDX = 8
        const val COLUMN_PRESSURE_IDX = 9
        const val COLUMN_HUMIDITY_IDX = 10
        const val COLUMN_MIN_TEMP_IDX = 11
        const val COLUMN_MAX_TEMP_IDX = 12
        const val COLUMN_WIND_SPEED_IDX = 13
        const val COLUMN_WIND_DEG_IDX = 14
        const val COLUMN_CLOUDS_IDX = 15
        const val COLUMN_RAIN_IDX = 16
        const val COLUMN_SNOW_IDX = 17
        const val COLUMN_COUNTRY_CODE_IDX = 18

        // Columns for the current dto
        const val COLUMN_CURRENT_TEMPERATURE_IDX = 19
        const val COLUMN_CURRENT_SHORT_INFO_ID_IDX = 20
        const val COLUMN_CURRENT_SUNRISE_IDX = 21
        const val COLUMN_CURRENT_SUNSET_IDX = 22

        // Columns for the forecast weather dto
        const val COLUMN_FORECAST_FORECASTSNUMBER_IDX = 19
        const val COLUMN_FORECAST_DAY_IDX = 20
        const val COLUMN_FORECAST_NIGHT_IDX = 21
        const val COLUMN_FORECAST_EVE_IDX = 22
        const val COLUMN_FORECAST_MORN_IDX = 23

        // Private constants to be used by the implementation
        private const val CURRENT_TABLE_NAME = "Current"
        private const val FORECAST_TABLE_NAME = "Forecast"

        private const val CURRENT_LIST_CODE = 1010
        private const val FORECAST_LIST_CODE = 1020
        private const val CURRENT_ITEM_CODE = 1011
        private const val FORECAST_ITEM_CODE = 1021
    }

    /**
     * The associated helper for DB accesses and migration.
     */
    private inner class WeatherDbHelper(version: Int = 1, dbName: String = "WEATHER_DB") :
            SQLiteOpenHelper(this@WeatherProvider.context, dbName, null, version) {

        private fun createCurrentTable(db: SQLiteDatabase?) {
            val CREATE_CMD = "CREATE TABLE $CURRENT_TABLE_NAME ( " +
                    "$COLUMN_ID VARCHAR(255) PRIMARY KEY, " +
                    "$COLUMN_UTC LONG," +
                    "$COLUMN_LOCATION VARCHAR(255), " +
                    "$COLUMN_LOCATION_ID INTEGER, " +
                    "$COLUMN_LONGITUDE DOUBLE, " +
                    "$COLUMN_LATITUDE  DOUBLE, " +
                    "$COLUMN_CURRENT_SHORT_INFO_ID INTEGER, " +
                    "$COLUMN_INFO_MAIN VARCHAR(256), " +
                    "$COLUMN_INFO_DESCRIPTION VARCHAR(1024), " +
                    "$COLUMN_INFO_ICON VARCHAR(255), " +
                    "$COLUMN_CURRENT_TEMPERATURE DOUBLE, " +
                    "$COLUMN_PRESSURE DOUBLE, " +
                    "$COLUMN_HUMIDITY INTEGER, " +
                    "$COLUMN_MIN_TEMP DOUBLE, " +
                    "$COLUMN_MAX_TEMP DOUBLE, " +
                    "$COLUMN_WIND_SPEED DOUBLE, " +
                    "$COLUMN_WIND_DEG DOUBLE, " +
                    "$COLUMN_CLOUDS INTEGER, " +
                    "$COLUMN_RAIN DOUBLE, " +
                    "$COLUMN_SNOW DOUBLE, " +
                    "$COLUMN_COUNTRY_CODE VARCHAR(256), " +
                    "$COLUMN_CURRENT_SUNRISE LONG, " +
                    "$COLUMN_CURRENT_SUNSET LONG ) "
            db?.execSQL(CREATE_CMD)
        }

        private fun createForecastTable(db: SQLiteDatabase?) {
            val CREATE_CMD = "CREATE TABLE $FORECAST_TABLE_NAME ( " +
                    "$COLUMN_ID VARCHAR(255) PRIMARY KEY, " +
                    "$COLUMN_UTC LONG," +
                    "$COLUMN_LOCATION VARCHAR(255), " +
                    "$COLUMN_LOCATION_ID INTEGER, " +
                    "$COLUMN_LONGITUDE DOUBLE, " +
                    "$COLUMN_LATITUDE  DOUBLE, " +
                    "$COLUMN_INFO_MAIN VARCHAR(256), " +
                    "$COLUMN_INFO_DESCRIPTION VARCHAR(1024), " +
                    "$COLUMN_INFO_ICON VARCHAR(255), " +
                    "$COLUMN_PRESSURE DOUBLE, " +
                    "$COLUMN_HUMIDITY INTEGER, " +
                    "$COLUMN_MIN_TEMP DOUBLE, " +
                    "$COLUMN_MAX_TEMP DOUBLE, " +
                    "$COLUMN_WIND_SPEED DOUBLE, " +
                    "$COLUMN_WIND_DEG DOUBLE, " +
                    "$COLUMN_CLOUDS INTEGER, " +
                    "$COLUMN_RAIN DOUBLE, " +
                    "$COLUMN_SNOW DOUBLE, " +
                    "$COLUMN_COUNTRY_CODE VARCHAR(256), " +
                    "$COLUMN_FORECAST_FORECASTSNUMBER INT," +
                    "$COLUMN_FORECAST_DAY DOUBLE, " +
                    "$COLUMN_FORECAST_NIGHT DOUBLE, " +
                    "$COLUMN_FORECAST_EVE DOUBLE," +
                    "$COLUMN_FORECAST_MORN DOUBLE) "
            db?.execSQL(CREATE_CMD)
        }

        private fun dropTable(db: SQLiteDatabase?, tableName: String) {
            val DROP_CMD = "DROP TABLE IF EXISTS $tableName"
            db?.execSQL(DROP_CMD)
        }

        override fun onCreate(db: SQLiteDatabase?) {
            createCurrentTable(db)
            createForecastTable(db)
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            dropTable(db, CURRENT_TABLE_NAME)
            dropTable(db, FORECAST_TABLE_NAME)
            createCurrentTable(db)
            createForecastTable(db)
        }
    }


    /**
     * @property dbHelper The DB helper instance to be used for DB accesses.
     */
    @Volatile private lateinit var dbHelper: WeatherDbHelper

    /**
     * @property uriMatcher The instance used to match an URI to its corresponding content type
     */
    @Volatile private lateinit var uriMatcher: UriMatcher

    override fun getType(uri: Uri?) = when (uriMatcher.match(uri)) {
        CURRENT_LIST_CODE, CURRENT_ITEM_CODE -> CURRENT_ITEM_CONTENT_TYPE
        FORECAST_LIST_CODE, FORECAST_ITEM_CODE -> FORECAST_ITEM_CONTENT_TYPE
        else -> throw IllegalArgumentException("Uri $uri not supported")
    }

    /**
     * Helper function used to obtain the table information (i.e. table name and path) based on the
     * given [uri]
     * @param [uri] The table URI
     * @return A [Pair] instance bearing the table name (the pair's first) and the table path
     * part (the pair's second).
     * @throws IllegalArgumentException if the received [uri] does not refer to an existing table
     */
    private fun resolveTableInfoFromUri(uri: Uri): Pair<String, String> = when (uriMatcher.match(uri)) {
        CURRENT_LIST_CODE -> Pair(CURRENT_TABLE_NAME, CURRENT_TABLE_PATH)
        FORECAST_LIST_CODE -> Pair(FORECAST_TABLE_NAME, FORECAST_TABLE_PATH)
        else -> null
    } ?: throw IllegalArgumentException("Uri $uri not supported")

    /**
     * Helper function used to obtain the table name and selection arguments based on the
     * given [uri]
     * @param [uri] The received URI, which may refer to a table or to an individual entry
     * @return A [Triple] instance bearing the table name (the triple's first), the selection
     * string (the triple's second) and the selection string parameters (the triple's third).
     * @throws IllegalArgumentException if the received [uri] does not refer to a valid data set
     */
    private fun resolveTableAndSelectionInfoFromUri(uri: Uri, selection: String?, selectionArgs: Array<String>?)
            : Triple<String, String?, Array<String>?> {
        val itemSelection = "$COLUMN_ID = ${uri.pathSegments.last()}"
        return when (uriMatcher.match(uri)) {
            CURRENT_ITEM_CODE -> Triple(CURRENT_TABLE_NAME, itemSelection, null)
            FORECAST_ITEM_CODE -> Triple(FORECAST_TABLE_NAME, itemSelection, null)
            else -> resolveTableInfoFromUri(uri).let { Triple(it.first, selection, selectionArgs) }
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {

        val tableInfo = resolveTableInfoFromUri(uri)
        val db = dbHelper.writableDatabase
        return try {
            val id = db.insert(tableInfo.first, null, values)
            if (id < 0) null else {
                context.contentResolver.notifyChange(uri, null)
                Uri.parse("content://$AUTHORITY/${tableInfo.second}/$id")
            }
        }
        finally {
            db.close()
        }
    }


    /** @see ContentProvider.delete */
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val params = resolveTableAndSelectionInfoFromUri(uri, selection, selectionArgs)
        val db = dbHelper.writableDatabase
        try {
            val deletedCount = db.delete(params.first, params.second, params.third)
            if (deletedCount != 0)
                context.contentResolver.notifyChange(uri, null)
            return deletedCount
        }
        finally {
            db.close()
        }
    }

    /** @see ContentProvider.query */
    override fun query(uri: Uri, projection: Array<String>?, selection: String?,
                       selectionArgs: Array<String>?, sortOrder: String?): Cursor? {

        val params = resolveTableInfoFromUri(uri)
        val db = dbHelper.readableDatabase
        var selectionAux: String? = selection
        if(selection != null) selectionAux = selection + "=?"
        return db.query(params.first, projection, selectionAux, selectionArgs, null, null, sortOrder)
    }

    @MainThread
    override fun onCreate(): Boolean {
        dbHelper = WeatherDbHelper()
        uriMatcher = UriMatcher(UriMatcher.NO_MATCH)
        with (uriMatcher) {
            addURI(AUTHORITY, CURRENT_TABLE_PATH, CURRENT_LIST_CODE)
            addURI(AUTHORITY, "$CURRENT_TABLE_PATH/#", CURRENT_ITEM_CODE)
            addURI(AUTHORITY, FORECAST_TABLE_PATH, FORECAST_ITEM_CODE)
            addURI(AUTHORITY, "$FORECAST_TABLE_PATH/#", FORECAST_ITEM_CODE)
        }
        return true
    }
    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        val params = resolveTableAndSelectionInfoFromUri(uri, selection, selectionArgs)
        val db = dbHelper.writableDatabase
        try {
            val updatedCount = db.update(params.first, values, params.second, params.third)
            if (updatedCount != 0)
                context.contentResolver.notifyChange(uri, null)
            return updatedCount
        }
        finally {
            db.close()
        }
    }


}
