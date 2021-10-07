package com.example.android.sunshine.utilities;

import android.content.ContentValues;
import android.content.Context;

import com.example.android.sunshine.data.WeatherContract.WeatherEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class FakeDataUtils {

    private static int[] weather_IDS = { 200 , 300 , 500 , 711 , 900 , 962};

    private static ContentValues createTestWeatherContentValues(long date) {

        ContentValues testWeatherValues = new ContentValues();

        testWeatherValues.put(WeatherEntry.COLUMN_DATE , date);
        testWeatherValues.put(WeatherEntry.COLUMN_DEGREES , Math.random()*2 );
        testWeatherValues.put(WeatherEntry.COLUMN_HUMIDITY , Math.random()*100);
        testWeatherValues.put(WeatherEntry.COLUMN_PRESSURE , 875 + Math.random()*100);

        int MaxTemp = (int) (Math.random()*100);
        testWeatherValues.put(WeatherEntry.COLUMN_MAX_TEMP ,MaxTemp);
        testWeatherValues.put(WeatherEntry.COLUMN_MIN_TEMP  , MaxTemp - (int)(Math.random()*100));
        testWeatherValues.put(WeatherEntry.COLUMN_WIND_SPEED , Math.random()*10);
        testWeatherValues.put(WeatherEntry.COLUMN_WEATHER_ID ,weather_IDS[(int) (Math.random()*10)%5]);

        return testWeatherValues;
    }

    public static void insertFakeData(Context context)
    {
        long today = SunshineDateUtils.normalizeDate(System.currentTimeMillis());

        List<ContentValues> fakeValues = new ArrayList<ContentValues>();

        for(int i =0 ; i<7 ;i++)
        {
            fakeValues.add(FakeDataUtils.createTestWeatherContentValues(today + TimeUnit.DAYS.
                    toMillis(i)));

        }

        context.getContentResolver().bulkInsert(
                WeatherEntry.CONTENT_URI , fakeValues.toArray(new ContentValues[7]));


    }

}
