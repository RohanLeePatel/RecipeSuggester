package com.example.recipesuggester.util;

import android.content.Context;
import android.content.SharedPreferences;

// the app's settings, saved with SharedPreferences (a small key/value file on the phone).
// I didn't put these in SQLite because it's only two values, a whole table would be overkill
public class Settings {

    private static final String PREFS_NAME = "settings";
    private static final String KEY_EXPIRY_ALERTS = "expiry_alerts";
    private static final String KEY_WARNING_DAYS = "warning_days";

    // the choices for "warn me ... before"
    public static final int[] WARNING_DAY_OPTIONS = {1, 3, 7};

    private final SharedPreferences prefs;

    public Settings(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // on by default so new users see the feature straight away
    public boolean isExpiryAlertsOn() {
        return prefs.getBoolean(KEY_EXPIRY_ALERTS, true);
    }

    public void setExpiryAlertsOn(boolean on) {
        // apply() saves in the background, commit() would block the screen while it writes
        prefs.edit().putBoolean(KEY_EXPIRY_ALERTS, on).apply();
    }

    public int getWarningDays() {
        return prefs.getInt(KEY_WARNING_DAYS, 3);
    }

    public void setWarningDays(int days) {
        prefs.edit().putInt(KEY_WARNING_DAYS, days).apply();
    }
}
