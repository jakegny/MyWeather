package com.madmanwithagreenhat.myweather;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.madmanwithagreenhat.myweather.data.WeatherContract;
import com.madmanwithagreenhat.myweather.data.WeatherContract.WeatherEntry;

/**
 * Created by jakegny on 1/28/2015.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    // private static final String LOG = DetailFragment.class.getSimpleName();
    static final String DETAIL_URI = "URI";

    private static final String FORECAST_SHARE_HASHTAG = " #MyWeather #MMWGH";

    private static final int DETAIL_LOADER = 0;

    private static final String[] DETAIL_COLS = {
            WeatherEntry.TABLE_NAME + "." + WeatherEntry._ID,
            WeatherEntry.COL_DATE,
            WeatherEntry.COL_SHORT_DESC,
            WeatherEntry.COL_MAX_TEMP,
            WeatherEntry.COL_MIN_TEMP,
            WeatherEntry.COL_HUMIDITY,
            WeatherEntry.COL_PRESSURE,
            WeatherEntry.COL_WIND_SPEED,
            WeatherEntry.COL_DEGREES,
            WeatherEntry.COL_WEATHER_ID,
            WeatherContract.LocationEntry.COL_LOCATION_SETTING
    };

    // openweatherapi.org
    public static final int COL_WEATHER_ID = 0;
    public static final int COL_WEATHER_DATE = 1;
    public static final int COL_WEATHER_DESC = 2;
    public static final int COL_WEATHER_MAX_TEMP = 3;
    public static final int COL_WEATHER_MIN_TEMP = 4;
    public static final int COL_WEATHER_HUMIDITY = 5;
    public static final int COL_WEATHER_PRESSURE = 6;
    public static final int COL_WEATHER_WIND_SPEED = 7;
    public static final int COL_WEATHER_DEGREES = 8;
    public static final int COL_WEATHER_CONDITION_ID = 9;


    private ShareActionProvider shareActionProvider;
    private String forecastDescription;
    private Uri forecastUri;
    private ImageView icon;
    private TextView dateText;
    private TextView fancyDateText;
    private TextView forecastText;
    private TextView highText;
    private TextView lowText;
    private TextView humidityText;
    private TextView windText;
    private TextView pressureText;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            forecastUri = arguments.getParcelable(DetailFragment.DETAIL_URI);
        }
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        icon = (ImageView) rootView.findViewById(R.id.detail_icon);
        dateText = (TextView) rootView.findViewById(R.id.detail_date_textview);
        fancyDateText = (TextView) rootView.findViewById(R.id.detail_day_textview);
        forecastText = (TextView) rootView.findViewById(R.id.detail_forecast_textview);
        highText = (TextView) rootView.findViewById(R.id.detail_high_textview);
        lowText = (TextView) rootView.findViewById(R.id.detail_low_textview);
        humidityText = (TextView) rootView.findViewById(R.id.detail_humidity_textview);
        windText = (TextView) rootView.findViewById(R.id.detail_wind_textview);
        pressureText = (TextView) rootView.findViewById(R.id.detail_pressure_textview);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detailfragment, menu);

        MenuItem menuItem = menu.findItem(R.id.action_share);

        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        if (forecastDescription != null) {
            shareActionProvider.setShareIntent(createShareForecastIntent());
        }
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, forecastDescription + FORECAST_SHARE_HASHTAG);
        return shareIntent;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    void onLocationChanged( String newLocation ) {
        Uri uri = forecastUri;
        if (null != uri) {
            long date = WeatherContract.WeatherEntry.getDateFromUri(uri);
            Uri updatedUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(newLocation, date);
            forecastUri = updatedUri;
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if ( null != forecastUri ) {
            return new CursorLoader(
                    getActivity(),
                    forecastUri,
                    DETAIL_COLS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            int weatherId = data.getInt(COL_WEATHER_CONDITION_ID);

            icon.setImageResource(Utility.getArtResourceForWeatherCondition(weatherId));

            // Set Date TextView
            long date = data.getLong(COL_WEATHER_DATE);
            String friendlyDateText = Utility.getDayOfWeek(getActivity(), date);
            String dateStr = Utility.getFormattedMonthDay(getActivity(), date);
            fancyDateText.setText(friendlyDateText);
            dateText.setText(dateStr);

            // Set Forecast TextView
            String description = data.getString(COL_WEATHER_DESC);
            forecastText.setText(description);
            icon.setContentDescription(description);

            boolean isMetric = Utility.isMetric(getActivity()); // TODO: isMetric

            // days High and Low TextView
            double high = data.getDouble(COL_WEATHER_MAX_TEMP);
            String highString = Utility.formatTemperature(getActivity(), high);
            highText.setText(highString);
            double low = data.getDouble(COL_WEATHER_MIN_TEMP);
            String lowString = Utility.formatTemperature(getActivity(), low);
            lowText.setText(lowString);
            
            float humidity = data.getFloat(COL_WEATHER_HUMIDITY);
            humidityText.setText(getActivity().getString(R.string.format_humidity, humidity));

            float windSpeedStr = data.getFloat(COL_WEATHER_WIND_SPEED);
            float windDirStr = data.getFloat(COL_WEATHER_DEGREES);
            windText.setText(Utility.getFormattedWind(getActivity(), windSpeedStr, windDirStr));

            float pressure = data.getFloat(COL_WEATHER_PRESSURE);
            pressureText.setText(getActivity().getString(R.string.format_pressure, pressure));

            forecastDescription = String.format("%s - %s - %s/%s", dateStr, description, high, low);

            if (shareActionProvider != null) {
                shareActionProvider.setShareIntent(createShareForecastIntent());
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }
}
