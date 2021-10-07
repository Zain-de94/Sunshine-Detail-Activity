package com.example.android.sunshine;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.sunshine.utilities.SunshineDateUtils;
import com.example.android.sunshine.utilities.SunshineWeatherUtils;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder> {

    private final Context mContext;

    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_FUTURE_DAY = 1;

    /*
     * An on-click handler that we've defined to make it easy for an Activity to interface with
     * our RecyclerView
     */
    private final ForecastAdapterOnClickHandler mClickHandler;

    /**
     * The interface that receives onClick messages.
     */
    public interface ForecastAdapterOnClickHandler {
        void onClick(long date);
    }

    private boolean mUseTodayLayout;

    private Cursor mCursor;


    public ForecastAdapter(@NonNull Context context, ForecastAdapterOnClickHandler clickHandler) {

        mContext = context;
        mClickHandler = clickHandler;

        mUseTodayLayout = mContext.getResources().getBoolean(R.bool.use_today_layout);
    }


    //This gets called when each new ViewHolder is created. This happens when the RecyclerView
    //is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.

    @Override
    public ForecastAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        int layoutId;

        switch (viewType) {

            case VIEW_TYPE_TODAY: {
                layoutId = R.layout.list_item_forecast_today;
                break;
            }

            case VIEW_TYPE_FUTURE_DAY: {
                layoutId = R.layout.forecast_list_item;
                break;
            }
            default:
                throw new IllegalArgumentException("Invalid view Type , value of" + viewType);

        }


        View view = LayoutInflater.from(mContext).inflate(layoutId, viewGroup, false);

        view.setFocusable(true);

        return new ForecastAdapterViewHolder(view);

    }

    // Override onBindViewHolder
    // Set the text of the TextView to the weather for this list item's position

    @Override
    public void onBindViewHolder(ForecastAdapterViewHolder forecastAdapterViewHolder, int position) {

        mCursor.moveToPosition(position);

        /****************
         * Weather Icon *
         ****************/
        int weatherId = mCursor.getInt(MainActivity.INDEX_WEATHER_CONDITION_ID);
        int weatherImageId;
        int viewType = getItemViewType(position);

        switch (viewType) {

            case VIEW_TYPE_TODAY:
                weatherImageId = SunshineWeatherUtils.
                        getLargeArtResourceIdForWeatherCondition(weatherId);
                break;

            case VIEW_TYPE_FUTURE_DAY:
                weatherImageId = SunshineWeatherUtils.
                        getSmallArtResourceIdForWeatherCondition(weatherId);
                break;

            default:
                throw new IllegalArgumentException("Invalid View Type , value of" + viewType);


        }


        forecastAdapterViewHolder.iconView.setImageResource(weatherImageId);


        /****************
         * Weather Date *
         ****************/
        long dataInMillis = mCursor.getLong(MainActivity.INDEX_WEATHER_DATE);
        //human readable date conversion
        String dateString = SunshineDateUtils.getFriendlyDateString(mContext, dataInMillis, false);
        forecastAdapterViewHolder.dateView.setText(dateString);


        /***********************
         * Weather Description *
         ***********************/

        String description = SunshineWeatherUtils.getStringForWeatherCondition(mContext, weatherId);
        String descriptionAlly = mContext.getString(R.string.a11y_forecast, description);

        forecastAdapterViewHolder.descriptionView.setText(description);
        forecastAdapterViewHolder.descriptionView.setContentDescription(descriptionAlly);

        /**************************
         * High (max) temperature *
         **************************/
        double highInCelsius = mCursor.getDouble(MainActivity.INDEX_WEATHER_MAX_TEMP);
        String highString = SunshineWeatherUtils.formatTemperature(mContext, highInCelsius);
        String highAlly = mContext.getString(R.string.a11y_high_temp, highString);

        forecastAdapterViewHolder.highTempView.setText(highString);
        forecastAdapterViewHolder.highTempView.setContentDescription(highAlly);


        /*************************
         * Low (min) temperature *
         *************************/
        double lowInCelsius = mCursor.getDouble(MainActivity.INDEX_WEATHER_MIN_TEMP);
        String lowString = SunshineWeatherUtils.formatTemperature(mContext, lowInCelsius);
        String lowAlly = mContext.getString(R.string.a11y_low_temp, lowString);

        forecastAdapterViewHolder.lowTempView.setText(lowString);
        forecastAdapterViewHolder.lowTempView.setContentDescription(lowAlly);

    }

    // This method simply returns the number of items to display. It is used behind the scenes
    //to help layout our Views and for animations.

    @Override
    public int getItemCount() {

        if (null == mCursor) return 0;

        return mCursor.getCount();
    }

    @Override
    public int getItemViewType(int position) {

        if (mUseTodayLayout && position == 0) {
            return VIEW_TYPE_TODAY;
        } else {
            return VIEW_TYPE_FUTURE_DAY;
        }

    }

    void swapCursor(Cursor newCursor) {
        //updating newCursor to notify change
        mCursor = newCursor;

        notifyDataSetChanged();


    }


    /**
     * Cache of the children views for a forecast list item.
     */
    public class ForecastAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView dateView;
        final TextView descriptionView;
        final TextView highTempView;
        final TextView lowTempView;

        final ImageView iconView;

        public ForecastAdapterViewHolder(View view) {
            super(view);

            iconView = (ImageView) view.findViewById(R.id.weather_icon);
            dateView = (TextView) view.findViewById(R.id.date);
            descriptionView = (TextView) view.findViewById(R.id.weather_description);
            highTempView = (TextView) view.findViewById(R.id.high_temperature);
            lowTempView = (TextView) view.findViewById(R.id.low_temperature);

            view.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

            int adapterPosition = getAdapterPosition();

            mCursor.moveToPosition(adapterPosition);
            long dataInMillis = mCursor.getLong(MainActivity.INDEX_WEATHER_DATE);
            mClickHandler.onClick(dataInMillis);

        }

    }


}
