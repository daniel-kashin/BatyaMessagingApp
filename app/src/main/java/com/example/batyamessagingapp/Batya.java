package com.example.batyamessagingapp;

import android.app.Application;

import com.example.batyamessagingapp.model.NetworkService;

/**
 * Created by Кашин on 23.10.2016.
 */

//todo: кеширование
    //// TODO: сервисы 
    //// TODO: android lifecycle
    //// TODO: android mvp
    //// TODO: adapter

public class Batya extends Application {

    public void onCreate(){
        super.onCreate();
        NetworkService.initializeSharedPreferences(this);
    }

}