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

        const val CURRENT_CONTENT = "content://$AUTHORITY/$CURRENT_TABLE_PATH"
        val CURRENT_CONTENT_URI: Uri = Uri.parse(CURRENT_CONTENT)
        val CURRENT_ITEM_CONTENT_TYPE =  "${ContentResolver.CURSOR_ITEM_BASE_TYPE}/current"

        /** TODO: This would be more beautiful if the data from Current and Forecast used the same classes
          * and all the information not used was not in there
          */

        //Columns that are equal in all classes
        const val COLUMN_ID = "_ID"
        //Columns that are equal in Current and Forecast Detail
        const val COLUMN_UTC = "UTC"
        //Columns that are in Current Weather Dto
        const val COLUMN_CURRENT_LOCATION = "LOCATION"
        const val COLUMN_CURRENT_LOCATION_ID = "LOCATION_ID"
        const val COLUMN_CURRENT_LONGITUDE = "LONGITUDE"
        const val COLUMN_CURRENT_LATITUDE = "LATITUDE"
        // Short Weather Info
        const val COLUMN_CURRENT_SHORT_INFO_ID = "SHORT_INFO_MAIN"
        const val COLUMN_CURRENT_SHORT_INFO_MAIN = "SHORT_INFO_DESCRIPTION"
        const val COLUMN_CURRENT_SHORT_INFO_DESCRIPTION = "SHORT_INFO_ICON"
        const val COLUMN_CURRENT_SHORT_INFO_ICON = "TEMPERATURE"
        // Weather Info
        const val COLUMN_CURRENT_TEMPERATURE = "HUMIDITY"
        const val COLUMN_CURRENT_PRESSURE = "MIN_TEMP"
        const val COLUMN_CURRENT_HUMIDITY = "MAX_TEMP"
        const val COLUMN_CURRENT_MIN_TEMP = "WIND_SPEED"
        const val COLUMN_CURRENT_MAX_TEMP = "WIND DEGGREES"
        // Wind Detail
        const val COLUMN_CURRENT_WIND_SPEED = "CLOUDS"
        const val COLUMN_CURRENT_WIND_DEG = "RAIN"
        // Cloud , Rain and Snow Detail
        const val COLUMN_CURRENT_CLOUDS = "COUNTRY_CODE"
        const val COLUMN_CURRENT_RAIN = "SUNRISE"
        const val COLUMN_CURRENT_SNOW = "SUNSET"
        // Location Detail
        const val COLUMN_CURRENT_COUNTRY_CODE = "COUNTRY CODE"
        const val COLUMN_CURRENT_SUNRISE = "SUNRISE"
        const val COLUMN_CURRENT_SUNSET = "SUNSET"


        const val COLUMN_ID_IDX = 0
        const val COLUMN_UTC_IDX = 1
        //Column index for Current
        const val COLUMN_CURRENT_LOCATION_IDX = 2
        const val COLUMN_CURRENT_LOCATION_ID_IDX = 3
        const val COLUMN_CURRENT_LONGITUDE_IDX = 4
        const val COLUMN_CURRENT_LATITUDE_IDX = 5
        const val COLUMN_CURRENT_SHORT_INFO_ID_IDX = 6
        const val COLUMN_CURRENT_SHORT_INFO_MAIN_IDX = 7
        const val COLUMN_CURRENT_SHORT_INFO_DESCRIPTION_IDX = 8
        const val COLUMN_CURRENT_SHORT_INFO_ICON_IDX = 9
        const val COLUMN_CURRENT_TEMPERATURE_IDX = 10
        const val COLUMN_CURRENT_PRESSURE_IDX = 11
        const val COLUMN_CURRENT_HUMIDITY_IDX = 12
        const val COLUMN_CURRENT_MIN_TEMP_IDX = 13
        const val COLUMN_CURRENT_MAX_TEMP_IDX = 14
        const val COLUMN_CURRENT_WIND_SPEED_IDX = 15
        const val COLUMN_CURRENT_WIND_DEG_IDX = 16
        const val COLUMN_CURRENT_CLOUDS_IDX = 17
        const val COLUMN_CURRENT_RAIN_IDX = 18
        const val COLUMN_CURRENT_SNOW_IDX = 19
        const val COLUMN_CURRENT_COUNTRY_CODE_IDX = 20
        const val COLUMN_CURRENT_SUNRISE_IDX = 21
        const val COLUMN_CURRENT_SUNSET_IDX = 22

        // Private constants to be used by the implementation
        private const val CURRENT_TABLE_NAME = "Current"

        private const val CURRENT_LIST_CODE = 1010
        private const val CURRENT_ITEM_CODE = 1011
    }

    /**
     * The associated helper for DB accesses and migration.
     */
    private inner class WeatherDbHelper(version: Int = 1, dbName: String = "WEATHER_DB") :
            SQLiteOpenHelper(this@WeatherProvider.context, dbName, null, version) {

        private fun createCurrentTable(db: SQLiteDatabase?, tableName: String) {
            val CREATE_CMD = "CREATE TABLE CURRENT_$tableName ( " +
                    "$COLUMN_ID INTEGER PRIMARY KEY , "
                    "$COLUMN_UTC LONG," +
                    "$COLUMN_CURRENT_LOCATION VARCHAR(255), " +
                    "$COLUMN_CURRENT_LOCATION_ID INTEGER, " +
                    "$COLUMN_CURRENT_LONGITUDE DOUBLE, " +
                    "$COLUMN_CURRENT_LATITUDE  DOUBLE, " +
                    "$COLUMN_CURRENT_SHORT_INFO_ID INTEGER, " +
                    "$COLUMN_CURRENT_SHORT_INFO_MAIN VARCHAR(256), " +
                    "$COLUMN_CURRENT_SHORT_INFO_DESCRIPTION VARCHAR(1024), " +
                    "$COLUMN_CURRENT_SHORT_INFO_ICON VARCHAR(255), " +
                    "$COLUMN_CURRENT_TEMPERATURE DOUBLE, " +
                    "$COLUMN_CURRENT_PRESSURE DOUBLE, " +
                    "$COLUMN_CURRENT_HUMIDITY INTEGER, " +
                    "$COLUMN_CURRENT_MIN_TEMP DOUBLE, " +
                    "$COLUMN_CURRENT_MAX_TEMP DOUBLE, " +
                    "$COLUMN_CURRENT_WIND_SPEED DOUBLE, " +
                    "$COLUMN_CURRENT_WIND_DEG DOUBLE, " +
                    "$COLUMN_CURRENT_CLOUDS INTEGER, " +
                    "$COLUMN_CURRENT_RAIN DOUBLE, " +
                    "$COLUMN_CURRENT_SNOW DOUBLE, " +
                    "$COLUMN_CURRENT_COUNTRY_CODE VARCHAR(256), " +
                    "$COLUMN_CURRENT_SUNRISE LONG, " +
                    "$COLUMN_CURRENT_SUNSET LONG) "
            db?.execSQL(CREATE_CMD)
        }

        private fun dropCurrentTable(db: SQLiteDatabase?, tableName: String) {
            val DROP_CMD = "DROP TABLE IF EXISTS CURRENT_$tableName"
            db?.execSQL(DROP_CMD)
        }

        override fun onCreate(db: SQLiteDatabase?) {
            createCurrentTable(db, "DAY")
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            dropCurrentTable(db, "DAY")
            createCurrentTable(db, "DAY")
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

        val params = resolveTableAndSelectionInfoFromUri(uri, selection, selectionArgs)
        val db = dbHelper.readableDatabase
        return try {
            db.query(params.first, projection, params.second, params.third, null, null, sortOrder)
        }
        finally {
            db.close()
        }
    }

    @MainThread
    override fun onCreate(): Boolean {
        dbHelper = WeatherDbHelper()
        uriMatcher = UriMatcher(UriMatcher.NO_MATCH)
        with (uriMatcher) {
            addURI(AUTHORITY, CURRENT_TABLE_PATH, CURRENT_LIST_CODE)
            addURI(AUTHORITY, "$CURRENT_TABLE_PATH/#", CURRENT_ITEM_CODE)
        }
        return true
    }
    override fun update(uri: Uri?, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}
