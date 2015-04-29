package com.madmanwithagreenhat.myweather;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.format.Time;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by jakegny on 1/28/2015.
 */

public class Utility {

    public static String getPreferredLocation(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_location_key),
                context.getString(R.string.pref_location_default));
    }

    public static boolean isMetric(Context context) { //TODO
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_units_key),
                context.getString(R.string.pref_units_metric))
                .equals(context.getString(R.string.pref_units_metric));
    }

    public static String formatTemperature(Context context, double temperature) {
        // Data stored in Celsius by default.  If user prefers to see in Fahrenheit, convert
        // the values here.
        String suffix = "\u00B0";
        if (!isMetric(context)) {
            temperature = (temperature * 1.8) + 32; // TODO
        }
        return String.format(context.getString(R.string.format_temperature), temperature);
    }

    static String formatDate(long date) {
        Date formattedDate = new Date(date);
        return DateFormat.getDateInstance().format(formattedDate);
    }

    // Database format for openweathermap.org
    public static final String DATE_FORMAT = "yyyyMMdd";

    /**
     * Format openweathermap.org database date.
     *
     * @param context - Android context you are working with
     * @param date - LONG time in milliseconds from  from 1970-01-01T00:00:00Z
     * @return - STRING Today, Tomorrow, Day of week, or actual date after 7 days
     */
    public static String getDayString(Context context, long date) {
        Time time = new Time();
        time.setToNow();
        long currentTime = System.currentTimeMillis();
        int julianDate = Time.getJulianDay(date, time.gmtoff);
        int currentJulianDate = Time.getJulianDay(currentTime, time.gmtoff);

        if (julianDate == currentJulianDate) {  // is "Today, June 24"
            String today = context.getString(R.string.today);
            int formatId = R.string.format_full_friendly_date;
            return String.format(context.getString(
                    formatId,
                    today,
                    getFormattedMonthDay(context, date)));
        } else if (julianDate < currentJulianDate + 7) { // Day of week
            return getDayOfWeek(context, date);
        } else { // Normal American Date format
            SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
            return shortenedDateFormat.format(date);
        }
    }

    /**
     * Helper to display Today, Tomorrow, Day of week. Nicety
     *
     * @param context - Android context you are working with
     * @param date - LONG time in milliseconds from  from 1970-01-01T00:00:00Z
     * @return - STRING Today, Tomorrow, or actual date
     */
    public static String getDayOfWeek(Context context, long date) {
        Time t = new Time();
        t.setToNow();
        int julianDate = Time.getJulianDay(date, t.gmtoff);
        int currentJulianDate = Time.getJulianDay(System.currentTimeMillis(), t.gmtoff);

        if (julianDate == currentJulianDate) {  // Today
            return context.getString(R.string.today);
        } else if (julianDate == currentJulianDate + 1) { // Tomorrow
            return context.getString(R.string.tomorrow);
        } else {    // Day of week
            Time time = new Time();
            time.setToNow();
            // Otherwise, the format is just the day of the week (e.g "Wednesday".
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
            return dayFormat.format(date);
        }
    }

    /**
     * Set up the display string for date info
     *
     * @param context - Android context you are working with
     * @param date - LONG time in milliseconds from  from 1970-01-01T00:00:00Z
     * @return - STRING The day in the form of a string formatted, Month Day, "January 1"
     */
    public static String getFormattedMonthDay(Context context, long date) {
        Time time = new Time();
        time.setToNow();
        SimpleDateFormat dbDateFormat = new SimpleDateFormat(Utility.DATE_FORMAT);
        SimpleDateFormat monthDayFormat = new SimpleDateFormat("MMMM dd");
        String monthDayString = monthDayFormat.format(date);
        return monthDayString;
    }

    /**
     * Set up the display string for wind conditions
     *
     * @param context - Android context you are working with
     * @param windSpeed - FLOAT self explanatory
     * @param degrees - FLOAT self explanatory
     * @return - STRING for displaying wind conditions
     */
    public static String getFormattedWind(Context context, float windSpeed, float degrees) {
        int windFormat;
        if (Utility.isMetric(context)) {
            windFormat = R.string.format_wind_kmh;
        } else {
            windFormat = R.string.format_wind_mph;
            windSpeed = .621371192237334f * windSpeed;
        }

        String direction = "unknown";
        if (degrees >= 337.5 || degrees < 22.5) {
            direction = "N";
        } else if (degrees >= 22.5 && degrees < 67.5) {
            direction = "NE";
        } else if (degrees >= 67.5 && degrees < 112.5) {
            direction = "E";
        } else if (degrees >= 112.5 && degrees < 157.5) {
            direction = "SE";
        } else if (degrees >= 157.5 && degrees < 202.5) {
            direction = "S";
        } else if (degrees >= 202.5 && degrees < 247.5) {
            direction = "SW";
        } else if (degrees >= 247.5 && degrees < 292.5) {
            direction = "W";
        } else if (degrees >= 292.5 && degrees < 337.5) {
            direction = "NW";
        }
        return String.format(context.getString(windFormat), windSpeed, direction);
    }

    /**
     * Weather code to icon conversion. Credit to openweathermap.org
     *
     * @param weatherCode - INT corresponding to type of weather
     * @return - INT icon id for type of weather.
     */
    public static int getIconResourceForWeatherCondition(int weatherCode) {
        // TODO: get openweatherapi.org icons working
        return R.drawable.placeholder;
    }

    /**
     * Weather code to drawable conversion. Credit to openweathermap.org
     *
     * @param weatherCode - INT corresponding to type of weather
     * @return - INT drawable id for weather image
     */
    public static int getArtResourceForWeatherCondition(int weatherCode) {
        // TODO: get openweatherapi.org icons working
        return R.drawable.placeholder;
    }
}

