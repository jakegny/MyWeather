package com.madmanwithagreenhat.myweather.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by jakegny on 1/28/2015.
 */
public class WeatherDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;

    static final String DATABASE_NAME = "weather.db";

    public WeatherDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_LOCATION_TABLE = "CREATE TABLE " + WeatherContract.LocationEntry.TABLE_NAME + " (" +
                WeatherContract.LocationEntry._ID + " INTEGER PRIMARY KEY," +
                WeatherContract.LocationEntry.COL_LOCATION_SETTING + " TEXT UNIQUE NOT NULL, " +
                WeatherContract.LocationEntry.COL_CITY_NAME + " TEXT NOT NULL, " +
                WeatherContract.LocationEntry.COL_COORD_LAT + " REAL NOT NULL, " +
                WeatherContract.LocationEntry.COL_COORD_LONG + " REAL NOT NULL " +
                " );";

        final String SQL_CREATE_WEATHER_TABLE = "CREATE TABLE " + WeatherContract.WeatherEntry.TABLE_NAME + " (" +
                WeatherContract.LocationEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                // the ID of the location entry associated with this weather data
                WeatherContract.WeatherEntry.COL_LOC_KEY + " INTEGER NOT NULL, " +
                WeatherContract.WeatherEntry.COL_DATE + " INTEGER NOT NULL, " +
                WeatherContract.WeatherEntry.COL_SHORT_DESC + " TEXT NOT NULL, " +
                WeatherContract.WeatherEntry.COL_WEATHER_ID + " INTEGER NOT NULL," +

                WeatherContract.WeatherEntry.COL_MIN_TEMP + " REAL NOT NULL, " +
                WeatherContract.WeatherEntry.COL_MAX_TEMP + " REAL NOT NULL, " +

                WeatherContract.WeatherEntry.COL_HUMIDITY + " REAL NOT NULL, " +
                WeatherContract.WeatherEntry.COL_PRESSURE + " REAL NOT NULL, " +
                WeatherContract.WeatherEntry.COL_WIND_SPEED + " REAL NOT NULL, " +
                WeatherContract.WeatherEntry.COL_DEGREES + " REAL NOT NULL, " +

                " FOREIGN KEY (" + WeatherContract.WeatherEntry.COL_LOC_KEY + ") REFERENCES " +
                WeatherContract.LocationEntry.TABLE_NAME + " (" + WeatherContract.LocationEntry._ID + "), " +

                " UNIQUE (" + WeatherContract.WeatherEntry.COL_DATE + ", " +
                WeatherContract.WeatherEntry.COL_LOC_KEY + ") ON CONFLICT REPLACE);";

        db.execSQL(SQL_CREATE_LOCATION_TABLE);
        db.execSQL(SQL_CREATE_WEATHER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + WeatherContract.LocationEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + WeatherContract.WeatherEntry.TABLE_NAME);
        onCreate(db);
    }
}

