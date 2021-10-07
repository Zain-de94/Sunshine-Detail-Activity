package com.example.android.sunshine;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.databinding.DataBindingUtil;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.android.sunshine.data.WeatherContract;
import com.example.android.sunshine.databinding.ActivityDetailBinding;
import com.example.android.sunshine.utilities.SunshineDateUtils;
import com.example.android.sunshine.utilities.SunshineWeatherUtils;

public class DetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String FORECAST_SHARE_HASHTAG = "#SunShineApp";

    public static final String[] WEATHER_DETAIL_PROJECTION = {
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID
    };

    public static final int INDEX_WEATHER_DATE = 0;
    public static final int INDEX_WEATHER_MAX_TEMP = 1;
    public static final int INDEX_WEATHER_MIN_TEMP = 2;
    public static final int INDEX_WEATHER_HUMIDITY = 3;
    public static final int INDEX_WEATHER_PRESSURE = 4;
    public static final int INDEX_WEATHER_WIND_SPEED = 5;
    public static final int INDEX_WEATHER_DEGREES = 6;
    public static final int INDEX_WEATHER_WEATHER_ID =7;


    private static final int ID_DETAIL_LOADER = 353;


    private String mForeCastSummary;

    private Uri mUri;

    private ActivityDetailBinding mDetailBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDetailBinding = DataBindingUtil.setContentView(this , R.layout.activity_detail);

        mUri = getIntent().getData();

        if(mUri == null) throw new NullPointerException("URI for Detail activity can not be null");


        getSupportLoaderManager().initLoader(ID_DETAIL_LOADER , null , this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.detail , menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if( id == R.id.action_settings)
        {
            Intent startSettingsActivity = new Intent(this , SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }

        if(id == R.id.action_share)
        {
            Intent shareIntent = createShareForecastIntent();
            startActivity(shareIntent);
            return true;

        }


        return super.onOptionsItemSelected(item);
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setText(mForeCastSummary + FORECAST_SHARE_HASHTAG)
                .getIntent();
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        return shareIntent;
    }



    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {

        switch (loaderId)
        {

            case ID_DETAIL_LOADER:

                return new CursorLoader(this,
                        mUri,
                        WEATHER_DETAIL_PROJECTION,
                        null,
                        null,
                        null);

            default:
                throw  new RuntimeException("Loader not implemented" + loaderId);


        }


    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        boolean cursorHasValidData = false;

        if(data !=null && data.moveToFirst())
        {
            cursorHasValidData = true;
        }

        // no data available
        if(!cursorHasValidData)
        {
            return;

        }

        /****************
         * Weather Icon *
         ****************/

        int weatherId = data.getInt(INDEX_WEATHER_WEATHER_ID);
        int weatherImageId = SunshineWeatherUtils.
                getLargeArtResourceIdForWeatherCondition(weatherId);
        mDetailBinding.primaryInfo.weatherIcon.setImageResource(weatherImageId);


        /****************
         * Weather Date *
         ****************/

        long localDateMidnightGmt = data.getLong(INDEX_WEATHER_DATE);
        String dateText = SunshineDateUtils.getFriendlyDateString(this
                , localDateMidnightGmt , true);
        mDetailBinding.primaryInfo.date.setText(dateText);

        /***********************
         * Weather Description *
         ***********************/

        String description = SunshineWeatherUtils.
                getStringForWeatherCondition(this,weatherId);

        String descriptionAlly = getString(R.string.a11y_forecast , description);

        mDetailBinding.primaryInfo.weatherDescription.setText(description);
        mDetailBinding.primaryInfo.weatherDescription.setContentDescription(descriptionAlly);

        mDetailBinding.primaryInfo.weatherIcon.setContentDescription(descriptionAlly);


        /**************************
         * High (max) temperature *
         **************************/

        double highInCelsius = data.getDouble(INDEX_WEATHER_MAX_TEMP);
        String highString = SunshineWeatherUtils.formatTemperature(this ,highInCelsius);

        String highAlly = getString(R.string.a11y_high_temp , highString);

        mDetailBinding.primaryInfo.highTemperature.setText(highString);
        mDetailBinding.primaryInfo.highTemperature.setContentDescription(highAlly);


        /*************************
         * Low (min) temperature *
         *************************/
        double lowInCelsius = data.getDouble(INDEX_WEATHER_MIN_TEMP);
        String lowString = SunshineWeatherUtils.formatTemperature(this , lowInCelsius);

        String lowAlly = getString(R.string.a11y_low_temp , lowString);

        mDetailBinding.primaryInfo.lowTemperature.setText(lowString);
        mDetailBinding.primaryInfo.lowTemperature.setContentDescription(lowAlly);



        /************
         * Humidity *
         ************/
        float humidity = data.getFloat(INDEX_WEATHER_HUMIDITY);
        String humidityString = getString(R.string.format_humidity, humidity);

        String humidityAlly = getString(R.string.a11y_humidity , humidityString);

        mDetailBinding.extraDetails.humidity.setText(humidityString);
        mDetailBinding.extraDetails.humidity.setContentDescription(humidityAlly);
        mDetailBinding.extraDetails.humidityLabel.setContentDescription(humidityAlly);


        /****************************
         * Wind speed and direction *
         ****************************/

        float windSpeed = data.getFloat(INDEX_WEATHER_WIND_SPEED);
        float windDirection = data.getFloat(INDEX_WEATHER_DEGREES);
        String windString = SunshineWeatherUtils.getFormattedWind(this ,
                windSpeed , windDirection);

        String windAlly = getString(R.string.a11y_wind , windString);

        mDetailBinding.extraDetails.windMeasurement.setText(windString);
        mDetailBinding.extraDetails.windMeasurement.setContentDescription(windAlly);
        mDetailBinding.extraDetails.windLabel.setContentDescription(windAlly);

        /************
         * Pressure *
         ************/
        float pressure = data.getFloat(INDEX_WEATHER_PRESSURE);
        String pressureString = getString(R.string.format_pressure , pressure);

        String pressureAlly = getString(R.string.a11y_pressure , pressureString);

        mDetailBinding.extraDetails.pressure.setText(pressureString);
        mDetailBinding.extraDetails.pressure.setContentDescription(pressureAlly);
        mDetailBinding.extraDetails.pressureLabel.setContentDescription(pressureAlly);


        mForeCastSummary = String.format("%s - %s -%s%s",
                dateText , description , highString ,lowString);


    }

    @Override
    public void onLoaderReset( Loader<Cursor> loader) {

    }
}