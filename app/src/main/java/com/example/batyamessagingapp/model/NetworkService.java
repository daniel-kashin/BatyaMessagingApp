package com.example.batyamessagingapp.model;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Interpolator;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.example.batyamessagingapp.SecurePreferences;
import com.example.batyamessagingapp.util.Message;
import com.example.batyamessagingapp.util.LoginData;
import com.example.batyamessagingapp.util.Token;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

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

    private static final String APP_PREFERENCES = "auth";
    private static final String APP_PREFERENCES_USERNAME = "username";
    private static final String APP_PREFERENCES_PASSWORD = "password";
    private static final String KEY = "onetwothreefoutfivesixseveneightnineten";
    private SecurePreferences sharedPreferences;

    private APIService _apiService = null;
    private Token _token = null;
    private boolean _isBuilt = false;
    public static final String API_BASE_URL = "http://139.59.157.41:8080";
    public String THIS_BASE_URL;

    public boolean tryToConnect() {
        if (sharedPreferences.containsKey(APP_PREFERENCES_USERNAME) && sharedPreferences.containsKey(APP_PREFERENCES_PASSWORD)) {
            final String username = sharedPreferences.getString(APP_PREFERENCES_USERNAME);
            final String password = sharedPreferences.getString(APP_PREFERENCES_PASSWORD);
            sharedPreferences.clear();

            connectWithNewData(username, password);
        }

        return isLoggedIn();
    }

    public String getUsername(){
        if (sharedPreferences.containsKey(APP_PREFERENCES_USERNAME))
            return sharedPreferences.getString(APP_PREFERENCES_USERNAME);

        return null;
    }

    public String getToken() {
        return _token.getToken();
    }

    private void saveActivityPreferences(String username, String password) {
        sharedPreferences.put(APP_PREFERENCES_USERNAME, username);
        sharedPreferences.put(APP_PREFERENCES_PASSWORD, password);
    }

    private void deletePreferences() {
        sharedPreferences.clear();
    }

    public boolean connectWithNewData(String username, String password) {
        login(username, password);
        if (isLoggedIn()) saveActivityPreferences(username, password);
        return isLoggedIn();
    }

    public boolean registerWithNewData(String username, String password) {
        register(username, password);
        if (isLoggedIn()) saveActivityPreferences(username, password);
        return isLoggedIn();
    }

    public NetworkService(Context context) {
        this(API_BASE_URL, context);
    }

    public NetworkService(String baseUrl, Context context) {
        this.THIS_BASE_URL = baseUrl;
        Build(baseUrl);
        sharedPreferences = new SecurePreferences(context, APP_PREFERENCES, KEY, true);
    }

    public boolean isLoggedIn() {
        return _token != null;
    }


    void Build(String baseUrl) {
        _apiService = RetrofitGenerator.createService(APIService.class, baseUrl);
        _isBuilt = true;
    }

    public Token login(final String name, final String password) {

        class Login extends AsyncTask<Void,Void,Token> {
            @Override
            protected Token doInBackground(Void... voids) {

                LoginData loginData = new LoginData(name, password);

                Call<Token> call = _apiService.login(loginData);
                try {
                    Token token = call.execute().body();
                    return token;
                } catch (Exception e){
                    e.printStackTrace();
                    return null;
                }
            }

            protected void onPostExecute(Token token){
               // _token = token;
            }
        }

        try {
            Login login = new Login();
            _token = login.execute().get();
        }catch(Exception ex){
        }
        return _token;
    }


    public Token register(final String name, final String password) {

        class Register extends AsyncTask<Void,Void,Token> {
            @Override
            protected Token doInBackground(Void... voids) {
                LoginData loginData = new LoginData(name, password);

                Call<Token> call = _apiService.register(loginData);
                try {
                    Token token = call.execute().body();
                    return token;
                } catch (Exception e){
                    e.printStackTrace();
                    return null;
                }
            }

            protected void onPostExecute(Token token){
                // _token = token;
            }
        }

        try {
            Register register = new Register();
            _token = register.execute().get();
        }catch(Exception ex){
        }
        return _token;
    }

    public void getUsers() {

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

    }

    public void getMessages(Token token, String string) {

    }

    private void deletePassword(){
        sharedPreferences.removeValue(APP_PREFERENCES_PASSWORD);
    }

    public void logout() {
        deletePassword();
        _token = null;
    }
}