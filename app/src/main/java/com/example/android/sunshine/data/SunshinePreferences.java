package com.example.android.sunshine.data;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.example.android.sunshine.R;

public class SunshinePreferences {

    /*
     * In order to uniquely pinpoint the location on the map when we launch the
     * map intent, we store the latitude and longitude.
     */
    public static final String PREF_COORD_LAT = "coord_lat";
    public static final String PREF_COORD_LONG = "coord_long";


    ///Helper method to handle setting location details in Preferences

    static public void setLocationDetails(Context context, double lat, double lon) {

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        editor.putLong(PREF_COORD_LAT, Double.doubleToRawLongBits(lat));
        editor.putLong(PREF_COORD_LONG, Double.doubleToRawLongBits(lon));
        editor.apply();
    }

    //Resets the stored location coordinates.
    static public void resetLocationCoordinates(Context context) {

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        editor.remove(PREF_COORD_LAT);
        editor.remove(PREF_COORD_LONG);
        editor.apply();
    }

    //Returns the location currently set in Preferences.

    public static String getPreferredWeatherLocation(Context context) {

        SharedPreferences prefs = PreferenceManager.
                getDefaultSharedPreferences(context);
        String keyForLocation = context.getString(R.string.pref_location_key);
        String defaultLocation = context.getString(R.string.pref_location_default);
        return prefs.getString(keyForLocation, defaultLocation);

    }


    //Returns true if the user has selected metric temperature display.

    public static boolean isMetric(Context context) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        String keyForUnits = context.getString(R.string.pref_units_key);
        String defaultUnits = context.getString(R.string.pref_units_metric);
        String preferredUnits = prefs.getString(keyForUnits, defaultUnits);
        String metric = context.getString(R.string.pref_units_metric);
        boolean userPreferenceMetric;

        if (metric.equals(preferredUnits)) {
            userPreferenceMetric = true;

        } else {
            userPreferenceMetric = false;
        }
        return userPreferenceMetric;


    }


    // Returns the location coordinates associated with the location.

    public static double[] getLocationCoordinates(Context context) {

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        double[] preferredCoordinates = new double[2];

        preferredCoordinates[0] = Double.
                longBitsToDouble(sp.getLong(PREF_COORD_LAT, Double.doubleToRawLongBits(0.0)));
        preferredCoordinates[1] = Double
                .longBitsToDouble(sp.getLong(PREF_COORD_LONG, Double.doubleToRawLongBits(0.0)));

        return preferredCoordinates;

    }

    // Returns true if the latitude and longitude values are available.

    public static boolean isLocationLatLonAvailable(Context context) {

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        boolean spContainLatitude = sp.contains(PREF_COORD_LAT);
        boolean spContainLongitude = sp.contains(PREF_COORD_LONG);

        boolean spContainBothLatitudeAndLongitude = false;

        if (spContainLatitude && spContainLongitude) {
            spContainBothLatitudeAndLongitude = true;

        }
        return spContainBothLatitudeAndLongitude;


    }

    public static boolean areNotificationEnabled(Context context) {

        String displayNotificationKey = context.getString(R.string
                .pref_enable_notifications_key);

        boolean shouldDisplayNotificationByDefault = context.getResources()
                .getBoolean(R.bool.show_notification_by_default);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        boolean shouldDisplayNotification = sp
                .getBoolean(displayNotificationKey, shouldDisplayNotificationByDefault);

        return shouldDisplayNotification;
    }

    public static long getLastNotificationTimeInMillis(Context context) {


        String lastNotificationKey = context.getString(R.string.pref_last_notification);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        long lastNotificationTime = sp.getLong(lastNotificationKey, 0);

        return lastNotificationTime;
    }

    public static long getEllapsedTimeSinceLastNotification(Context context) {

        long lastNotificationTimeMillis =
                SunshinePreferences.getLastNotificationTimeInMillis(context);

        long timeSinceLastNotification = System.currentTimeMillis() -
                lastNotificationTimeMillis;

        return timeSinceLastNotification;
    }

    public static void saveLastNotificationTime(Context context, long timeOfNotification) {

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        String lastNotificationKey = context.getString(R.string.pref_last_notification);
        editor.putLong(lastNotificationKey, timeOfNotification);
        editor.apply();
    }


}