package com.example.android.sunshine.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.android.sunshine.utilities.SunshineDateUtils;

public class WeatherProvider extends ContentProvider {

    /*
     * These constant will be used to match URIs with the data they are looking for. We will take
     * advantage of the UriMatcher class to make that matching MUCH easier than doing something
     * ourselves, such as using regular expressions.
     */
    public static final int CODE_WEATHER = 100;
    public static final int CODE_WEATHER_WITH_DATE = 101;


    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private WeatherDbHelper mOpenHelper;

    public static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        final String authority = WeatherContract.CONTENT_AUTHORITY;

        /* This URI is content://com.example.android.sunshine/weather/ */
        matcher.addURI(authority, WeatherContract.PATH_WEATHER, CODE_WEATHER);

        /*
         * This URI would look something like content://com.example.android.sunshine/weather/1472214172
         * The "/#" signifies to the UriMatcher that if PATH_WEATHER is followed by ANY number,
         * that it should return the CODE_WEATHER_WITH_DATE code
         */
        matcher.addURI(authority, WeatherContract.PATH_WEATHER + "/#", CODE_WEATHER_WITH_DATE);

        return matcher;


    }


    @Override
    public boolean onCreate() {

        mOpenHelper = new WeatherDbHelper(getContext());

        return true;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] Values) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)) {

            case CODE_WEATHER:
                db.beginTransaction();
                int rowInserted = 0;

                try {
                    for (ContentValues value : Values) {

                        long weatherDate =
                                value.getAsLong(WeatherContract.WeatherEntry.COLUMN_DATE);

                        if (!SunshineDateUtils.isDateNormalized(weatherDate)) {
                            throw new IllegalArgumentException("This date must be Normalized");
                        }

                        long _id = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, value);

                        if (_id != -1) {

                            rowInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if (rowInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowInserted;


            //If the URI does match match CODE_WEATHER, return the super implementation of bulkInsert
            default:
                return super.bulkInsert(uri, Values);

        }

    }


    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {


        Cursor cursor;

        switch (sUriMatcher.match(uri)) {

            // content://com.example.android.sunshine/weather/1472214172(sec)

            case CODE_WEATHER_WITH_DATE: {

                String normalizedUtcDateString = uri.getLastPathSegment();

                String[] selectionArguments = new String[]{normalizedUtcDateString};

                cursor = mOpenHelper.getReadableDatabase().query(

                        WeatherContract.WeatherEntry.TABLE_NAME,

                        projection,

                        WeatherContract.WeatherEntry.COLUMN_DATE + " =?",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);


                break;
            }

            //  content://com.example.android.sunshine/weather/
            case CODE_WEATHER: {
                cursor = mOpenHelper.getReadableDatabase().query(
                        WeatherContract.WeatherEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;


            }

            default:
                throw new UnsupportedOperationException("Unknown Uri" + uri);


        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }


    @Override
    public String getType(@NonNull Uri uri) {
        throw new RuntimeException(" we are not implementing get type in sunshine");
    }


    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        throw new RuntimeException(" Later we will implement in future classes");
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        int numRowsDeleted;

        if(null == selection) selection = "1" ;

        switch (sUriMatcher.match(uri))
        {

            case CODE_WEATHER:
                numRowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        WeatherContract.WeatherEntry.TABLE_NAME
                        ,selection
                        ,selectionArgs);

            break;

            default:
                throw new UnsupportedOperationException("Uknown Uri" + uri);
        }

        if(numRowsDeleted!=0)
        {
            getContext().getContentResolver().notifyChange(uri,null);
        }

        return numRowsDeleted;

    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new RuntimeException(" we are not implementing update in sunshine");
    }
}
