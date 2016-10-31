package com.example.batyamessagingapp.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.TextView;

import com.example.batyamessagingapp.SecurePreferences;
import com.example.batyamessagingapp.util.LoginData;
import com.example.batyamessagingapp.view.AuthenticationView;

/**
 * Created by Кашин on 29.10.2016.
 */

public class AuthenticationService implements AuthenticationPresenter {
    private static final String APP_PREFERENCES = "auth";
    private static final String APP_PREFERENCES_USERNAME= "username";
    private static final String APP_PREFERENCES_PASSWORD = "password";
    private static final String KEY = "onetwothreefoutfivesixseveneightnineten";
    private SecurePreferences sharedPreferences;
    private AuthenticationView view;
    private boolean isConnected;

    public AuthenticationService(AuthenticationView view){
        this.view = view;
    }

    @Override
    public void tryToConnectWithPreviousData(){
        sharedPreferences = new SecurePreferences((Context)view, APP_PREFERENCES, KEY, true);
        String login;
        String password;

        if(sharedPreferences.containsKey(APP_PREFERENCES_USERNAME) && sharedPreferences.containsKey(APP_PREFERENCES_PASSWORD)){
            login = sharedPreferences.getString(APP_PREFERENCES_USERNAME);
            password = sharedPreferences.getString(APP_PREFERENCES_PASSWORD);
            isConnected = true;
            view.openContactsActivity();
        }
    }

    @Override
    public void onAuthButtonClick(){
        //TODO: add regular expressions



        saveActivityPreferences(view.getUsername(),view.getPassword());
        isConnected = true;
        view.openContactsActivity();
    }

    @Override
    public void onRegistrationButtonClick(){

    }

    private void saveActivityPreferences(String username, String password) {

        sharedPreferences.put(APP_PREFERENCES_USERNAME, username);
        sharedPreferences.put(APP_PREFERENCES_PASSWORD, password);
    }


}