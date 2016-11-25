package com.example.batyamessagingapp.model;

import android.content.Context;

import com.example.batyamessagingapp.lib.SecurePreferences;
import com.example.batyamessagingapp.model.pojo.Token;

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

    private static SecurePreferences sSharedPreferences;

    private static final String APP_PREFERENCES = "auth";
    private static final String APP_PREFERENCES_USERNAME = "username";
    private static final String APP_PREFERENCES_TOKEN = "token";
    private static final String KEY = "onetwothreefoutfivesixseveneightnineten";


    public static void initializeSharedPreferences(Context context) {
        sSharedPreferences = new SecurePreferences(context, APP_PREFERENCES, KEY, true);
    }


    public static boolean tryToConnect() {
        return (sSharedPreferences.containsKey(APP_PREFERENCES_TOKEN) &&
                (sSharedPreferences.getString(APP_PREFERENCES_TOKEN).length() == 32));
    }

    public static boolean preferencesInitialized() {
        return sSharedPreferences != null;
    }

    public static String getUsernameFromPreferences() {
        if (sSharedPreferences.containsKey(APP_PREFERENCES_USERNAME)) {
            return sSharedPreferences.getString(APP_PREFERENCES_USERNAME);
        }else {
            return "";
        }
    }

    public static boolean isTokenAvailableInPreferences() {
        return (sSharedPreferences != null && sSharedPreferences.containsKey(APP_PREFERENCES_TOKEN));
    }

    //TODO: make private
    public static String getTokenValueFromPreferences() {
        if (isTokenAvailableInPreferences()) {
            return sSharedPreferences.getString(APP_PREFERENCES_TOKEN);
        } else {
            return "";
        }
    }

    public static void deleteTokenFromPreferences() {
        if (isTokenAvailableInPreferences()) {
            sSharedPreferences.removeValue(APP_PREFERENCES_TOKEN);
        }
    }

    public static void saveTokenAndUsernameToPreferences(Token token, String username) {
        if (isTokenAvailableInPreferences()) {
            clearSharedPreferences();
        }

        sSharedPreferences.put(APP_PREFERENCES_TOKEN, token.getValue());
        sSharedPreferences.put(APP_PREFERENCES_USERNAME, username);
    }

    private static void clearSharedPreferences() {
        sSharedPreferences.clear();
    }
}