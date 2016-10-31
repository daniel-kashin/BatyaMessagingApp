package com.example.batyamessagingapp.model;

import android.content.Context;
import android.os.AsyncTask;

import com.example.batyamessagingapp.SecurePreferences;
import com.example.batyamessagingapp.util.Message;
import com.example.batyamessagingapp.util.LoginData;
import com.example.batyamessagingapp.util.Token;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

//todo: изменить структуру NetworkService, убрать публичную переменную
//todo: rxjava, dagger2, service
//todo: add last name
//todo: scroll up

//http://softwareengineering.stackexchange.com/questions/148108/why-is-global-state-so-evil
//https://www.javacodegeeks.com/2015/10/alternatives-to-global-variables-and-passing-the-same-value-over-a-long-chain-of-calls.html
//передавать токен? service?
//http://ru-code-android.livejournal.com/4594.html
//https://dzone.com/articles/alternatives-to-global-variables
//http://softwareengineering.stackexchange.com/questions/40373/so-singletons-are-bad-then-what
//dependency injection
//sharedpreferences TOKEN

//сначала реализовать базовое, с глобальной переменной! затем круче

public class NetworkService {

    public static final String API_BASE_URL = "http://139.59.157.41:8080";
    private static final String APP_PREFERENCES = "auth";
    private static final String APP_PREFERENCES_USERNAME = "username";
    private static final String APP_PREFERENCES_TOKEN = "token";
    private static final String KEY = "onetwothreefoutfivesixseveneightnineten";
    private static SecurePreferences _sharedPreferences;

    private static APIService _apiService = null;

    static {
        Build();
    }

    public static boolean tryToConnect() {
        return (_sharedPreferences.containsKey(APP_PREFERENCES_TOKEN) &&
                (_sharedPreferences.getString(APP_PREFERENCES_TOKEN).length() == 32));
    }

    public static void initializeSharedPreferences(Context context) {
        _sharedPreferences = new SecurePreferences(context, APP_PREFERENCES, KEY, true);
    }

    public static boolean preferencesInitialized() {
        return _sharedPreferences != null;
    }

    public static String getUsername() {
        if (_sharedPreferences.containsKey(APP_PREFERENCES_USERNAME))
            return _sharedPreferences.getString(APP_PREFERENCES_USERNAME);

        return "";
    }

    public static boolean isLoggedIn() {
        return (_sharedPreferences != null && _sharedPreferences.containsKey(APP_PREFERENCES_TOKEN));
    }

    public static void cacheTokenAndUsername(Token token, String username){
        if (isLoggedIn()) clearSharedPreferences();
        _sharedPreferences.put(APP_PREFERENCES_TOKEN, token.getValue());
        _sharedPreferences.put(APP_PREFERENCES_USERNAME, username);
    }

    //public static boolean connectWithNewData(String username, String password) {
    //    login(username, password);
    //    if (isLoggedIn()) saveActivityPreferences(username, password);
    //    return isLoggedIn();
    //}

    //public boolean registerWithNewData(String username, String password) {
    //    register(username, password);
    //    if (isLoggedIn()) saveActivityPreferences(username, password);
    //    return isLoggedIn();
    //}

    static void Build() {
        _apiService = RetrofitGenerator.createService(APIService.class, API_BASE_URL);
    }

    public static Call<Token> getAuthCall(final String username, final String password) {
        //Build();

        LoginData loginData = new LoginData(username, password);
        return _apiService.login(loginData);
    }

    public static Call<Token> getRegisterCall(final String username, final String password) {
        //Build();

        LoginData loginData = new LoginData(username, password);
        return _apiService.register(loginData);
    }



    private static void clearSharedPreferences(){
        _sharedPreferences.clear();
    }

    public void getUsers() {
/*
        int offset = 0;

        Call<HashMap<String, Message>> call = _apiService.getUsers(_token, ("offset/" + offset));
        call.enqueue(new Callback<HashMap<String, Message>>() {
            @Override
            public void onResponse(Call<HashMap<String, Message>> call, Response<HashMap<String, Message>> response) {
                if (response.isSuccessful()) {


                } else {
                    // error response, no access to resource?
                }
            }

            @Override
            public void onFailure(Call<HashMap<String, Message>> call, Throwable t) {
                // something went completely south (like no internet connection)
            }
        });
*/
    }

    public void getMessages(Token token, String string) {

    }

    public static String getTokenValue(){
        if(isLoggedIn())
            return _sharedPreferences.getString(APP_PREFERENCES_TOKEN);
        else return "";
    }

    public static void deleteToken() {
        if (isLoggedIn())
            _sharedPreferences.removeValue(APP_PREFERENCES_TOKEN);
    }

    public void onLogout() {
        deleteToken();
    }
}