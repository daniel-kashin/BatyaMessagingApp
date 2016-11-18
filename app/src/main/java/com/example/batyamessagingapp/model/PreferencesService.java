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

    private static final String APP_PREFERENCES = "auth";
    private static final String APP_PREFERENCES_USERNAME = "username";
    private static final String APP_PREFERENCES_TOKEN = "token";
    private static final String KEY = "onetwothreefoutfivesixseveneightnineten";
    private static SecurePreferences _sharedPreferences;

    public static void initializeSharedPreferences(Context context) {
        _sharedPreferences = new SecurePreferences(context, APP_PREFERENCES, KEY, true);
    }


    public static boolean tryToConnect() {
        return (_sharedPreferences.containsKey(APP_PREFERENCES_TOKEN) &&
                (_sharedPreferences.getString(APP_PREFERENCES_TOKEN).length() == 32));
    }

    public static boolean preferencesInitialized() {
        return _sharedPreferences != null;
    }

    public static String getUsernameFromPreferences() {
        if (_sharedPreferences.containsKey(APP_PREFERENCES_USERNAME)) {
            return _sharedPreferences.getString(APP_PREFERENCES_USERNAME);
        }

        return "";
    }

    public static boolean isTokenAvailableInPreferences() {
        return (_sharedPreferences != null && _sharedPreferences.containsKey(APP_PREFERENCES_TOKEN));
    }

    //TODO: make private
    public static String getTokenValueFromPreferences() {
        if (isTokenAvailableInPreferences()) {
            String token = _sharedPreferences.getString(APP_PREFERENCES_TOKEN);
            return token;
        } else {
            return "";
        }
    }

    public static void deleteTokenFromPreferences() {
        if (isTokenAvailableInPreferences()) {
            _sharedPreferences.removeValue(APP_PREFERENCES_TOKEN);
        }
    }

    public static void saveTokenAndUsernameToPreferences(Token token, String username) {
        if (isTokenAvailableInPreferences()) {
            clearSharedPreferences();
        }

        _sharedPreferences.put(APP_PREFERENCES_TOKEN, token.getValue());
        _sharedPreferences.put(APP_PREFERENCES_USERNAME, username);
    }

    private static void clearSharedPreferences() {
        _sharedPreferences.clear();
    }
}