package com.danielkashin.batyamessagingapp.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.danielkashin.batyamessagingapp.model.pojo.Token;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Кашин on 23.10.2016.
 */

//todo: extends Service
//http://startandroid.ru/ru/uroki/vse-uroki-spiskom/157-urok-92-service-prostoj-primer.html
//todo: создать классы:
//http://stackoverflow.com/questions/31112124/getting-simple-json-object-response-using-retrofit-library
//http://stackoverflow.com/questions/32942661/how-can-retrofit-2-0-parse-nested-json-object
//todo: adapter
//todo: добавить выборочное кеширование
//todo: rxjava, dagger2, service

//http://softwareengineering.stackexchange.com/questions/148108/why-is-global-state-so-evil
//https://www.javacodegeeks.com/2015/10/alternatives-to-global-variables-and-passing-the-same-value-over-a-long-chain-of-calls.html
//передавать токен? service?
//http://ru-code-android.livejournal.com/4594.html
//https://dzone.com/articles/alternatives-to-global-variables
//http://softwareengineering.stackexchange.com/questions/40373/so-singletons-are-bad-then-what
//dependency injection

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