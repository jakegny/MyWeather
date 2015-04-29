package com.madmanwithagreenhat.myweather;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by jakegny on 1/28/2015.
 */
public class ForecastAdapter extends CursorAdapter {

    private static final int VIEW_TYPE_COUNT = 2;
    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_FUTURE_DAY = 1;

    // Seperate View for "today" in the list view. TODO FINAL CHANGE BACK TO: true
    // Note. Different view for today broke build:
    // Results: TODO: Add fix it info
    private boolean todayLayoutActive = true;

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        public final ImageView icon;
        public final TextView dateText;
        public final TextView forecastText;
        public final TextView highText;
        public final TextView lowText;

        public ViewHolder(View view) {
            icon = (ImageView) view.findViewById(R.id.list_item_icon); // TODO: Images from openweathermapapi.org
            dateText = (TextView) view.findViewById(R.id.list_item_date_textview);
            forecastText = (TextView) view.findViewById(R.id.list_item_forecast_textview);
            highText = (TextView) view.findViewById(R.id.list_item_high_textview);
            lowText = (TextView) view.findViewById(R.id.list_item_low_textview);
        }
    }

    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Choose the layout type
        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;
        switch (viewType) {
            case VIEW_TYPE_TODAY: {
                layoutId = R.layout.list_item_forecast_today;
                break;
            }
            case VIEW_TYPE_FUTURE_DAY: {
                layoutId = R.layout.list_item_forecast;
                break;
            }
        }

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        int viewType = getItemViewType(cursor.getPosition());
        switch (viewType) {
            case VIEW_TYPE_TODAY: {
                // Get weather icon
                viewHolder.icon.setImageResource(Utility.getArtResourceForWeatherCondition(
                        cursor.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID)));
                break;
            }
            case VIEW_TYPE_FUTURE_DAY: {
                // Get weather icon
                viewHolder.icon.setImageResource(Utility.getIconResourceForWeatherCondition(
                        cursor.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID)));
                break;
            }
        }

        long date = cursor.getLong(ForecastFragment.COL_WEATHER_DATE);
        viewHolder.dateText.setText(Utility.getDayString(context, date));

        String forecast = cursor.getString(ForecastFragment.COL_WEATHER_DESC);
        viewHolder.forecastText.setText(forecast);

        viewHolder.icon.setContentDescription(forecast);

        boolean isMetric = Utility.isMetric(context);

        double high = cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP);
        viewHolder.highText.setText(Utility.formatTemperature(context, high));

        double low = cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP);
        viewHolder.lowText.setText(Utility.formatTemperature(context, low));
    }

    public void setUseTodayLayout(boolean useTodayLayout) {
        todayLayoutActive = useTodayLayout;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0 && todayLayoutActive) ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

}
