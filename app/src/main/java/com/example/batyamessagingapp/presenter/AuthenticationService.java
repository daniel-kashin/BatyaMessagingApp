package com.example.batyamessagingapp.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.TextView;

import com.example.batyamessagingapp.Batya;
import com.example.batyamessagingapp.SecurePreferences;
import com.example.batyamessagingapp.model.NetworkService;
import com.example.batyamessagingapp.util.LoginData;
import com.example.batyamessagingapp.view.AuthenticationView;

/**
 * Created by Кашин on 29.10.2016.
 */

public class AuthenticationService implements AuthenticationPresenter {
    private AuthenticationView view;

    public AuthenticationService(AuthenticationView view){
        this.view = view;
    }

    @Override
    public void tryToConnectWithPreviousData(){
        if (Batya.networkService.tryToConnect())
            view.openContactsActivity();
        else if ((Batya.networkService).getUsername() != null)
            view.setUsernameEditText((Batya.networkService).getUsername());
    }

    @Override
    public void onAuthButtonClick(){
        //TODO: add regular expressions

        if (view.checkInputs() && Batya.networkService.connectWithNewData(view.getUsername(),view.getPassword()))
            view.openContactsActivity();
    }

    @Override
    public void onRegistrationButtonClick(){
        if (view.checkInputs() && Batya.networkService.registerWithNewData(view.getUsername(),view.getPassword()))
            view.openContactsActivity();
    }

}