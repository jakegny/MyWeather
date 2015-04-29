package com.madmanwithagreenhat.myweather.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;

/**
 * Created by jakegny on 1/28/2015.
 */
public class WeatherContract {

    public static final String CONTENT_AUTHORITY = "com.madmanwithagreenhat.myweather";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    // EX: content://com.madmanwithagreenhat.myweather/weather/
    public static final String PATH_WEATHER = "weather";
    public static final String PATH_LOCATION = "location";

    // Normalize from Google's Android class
    public static long normalizeDate(long startDate) {
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }

    // Location info for openweathermap.org database
    public static final class LocationEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;

        public static final String TABLE_NAME = "location";

        // location query for openweathermap.org
        public static final String COL_LOCATION_SETTING = "location_setting";

        // openweathermap API for human readable location
        public static final String COL_CITY_NAME = "city_name";
        public static final String COL_COORD_LAT = "coord_lat";
        public static final String COL_COORD_LONG = "coord_long";

        public static Uri buildLocationUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    // Weather info for openweathermap.org database
    public static final class WeatherEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_WEATHER).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;

        public static final String TABLE_NAME = "weather";

        // openweatherapi.org Table column names
        public static final String COL_LOC_KEY = "location_id";
        public static final String COL_DATE = "date";
        public static final String COL_WEATHER_ID = "weather_id";
        public static final String COL_SHORT_DESC = "short_desc";
        public static final String COL_MIN_TEMP = "min";
        public static final String COL_MAX_TEMP = "max";
        public static final String COL_HUMIDITY = "humidity";
        public static final String COL_PRESSURE = "pressure";
        public static final String COL_WIND_SPEED = "wind";
        public static final String COL_DEGREES = "degrees";

        public static Uri buildWeatherUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildWeatherLocation(String locationSetting) {
            return CONTENT_URI.buildUpon().appendPath(locationSetting).build();
        }

        public static Uri buildWeatherLocationWithStartDate(
                String locationSetting, long startDate) {
            long normalizedDate = normalizeDate(startDate);
            return CONTENT_URI.buildUpon().appendPath(locationSetting)
                    .appendQueryParameter(COL_DATE, Long.toString(normalizedDate)).build();
        }

        public static Uri buildWeatherLocationWithDate(String locationSetting, long date) {
            return CONTENT_URI.buildUpon().appendPath(locationSetting)
                    .appendPath(Long.toString(normalizeDate(date))).build();
        }

        public static String getLocationSettingFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static long getDateFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(2));
        }

        public static long getStartDateFromUri(Uri uri) {
            String dateString = uri.getQueryParameter(COL_DATE);
            if (null != dateString && dateString.length() > 0) {
                return Long.parseLong(dateString);
            } else {
                return 0;
            }
        }
    }
}
