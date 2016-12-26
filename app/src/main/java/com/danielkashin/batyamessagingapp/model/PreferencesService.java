package com.danielkashin.batyamessagingapp.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.danielkashin.batyamessagingapp.model.pojo.Token;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Кашин on 23.10.2016.
 */

public class PreferencesService {

    private static SharedPreferences sSharedPreferences;

    private static final String APP_PREFERENCES = "auth";
    private static final String APP_PREFERENCES_USERNAME = "id";
    private static final String APP_PREFERENCES_TOKEN = "token";
    private static final String KEY = "onetwothreefoutfivesixseveneightnineten";


    public static void initializeSharedPreferences(Context context) {
        sSharedPreferences = context.getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
    }

    public static String getIdFromPreferences() {
        return sSharedPreferences.getString(APP_PREFERENCES_USERNAME, "");
    }

    public static boolean isTokenAvailableInPreferences() {
        return (sSharedPreferences != null && sSharedPreferences.contains(APP_PREFERENCES_TOKEN));
    }

    //TODO: make private
    public static String getTokenFromPreferences() {
        return sSharedPreferences.getString(APP_PREFERENCES_TOKEN, "");
    }

    public static void deleteTokenFromPreferences() {
        if (isTokenAvailableInPreferences()) {
            SharedPreferences.Editor editor = sSharedPreferences.edit();
            editor.remove(APP_PREFERENCES_TOKEN)
                    .apply();
        }
    }

    public static void saveTokenAndUsernameToPreferences(Token token, String username) {
        if (isTokenAvailableInPreferences()) {
            clearSharedPreferences();
        }

        SharedPreferences.Editor editor = sSharedPreferences.edit();
        editor.putString(APP_PREFERENCES_TOKEN, token.getValue())
                .putString(APP_PREFERENCES_USERNAME, username)
                .apply();
    }

    private static void clearSharedPreferences() {
        SharedPreferences.Editor editor = sSharedPreferences.edit();
        editor.clear()
                .apply();
    }
}