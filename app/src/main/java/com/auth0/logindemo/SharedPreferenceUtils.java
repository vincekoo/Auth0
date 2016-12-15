package com.auth0.logindemo;

import android.content.Context;
import android.preference.PreferenceManager;

public class SharedPreferenceUtils {
    public static void setIdToken(Context context, String token) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(Constants.SHARED_PREFS_ID_TOKEN_KEY, token).apply();
    }

    public static String getIdToken(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(Constants.SHARED_PREFS_ID_TOKEN_KEY, null);
    }

    public static void setRefreshToken(Context context, String token) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(Constants.SHARED_PREFS_REFRESH_TOKEN_KEY, token).apply();
    }

    public static String getRefreshToken(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(Constants.SHARED_PREFS_REFRESH_TOKEN_KEY, null);
    }

    public static void clearSharedPreferences(Context context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().clear().apply();
    }
}
