package com.danielkashin.batyamessagingapp;

import android.app.Application;

import com.danielkashin.batyamessagingapp.model.PreferencesService;

/**
 * Created by Кашин on 23.10.2016.
 */

public class Batya extends Application {

  public void onCreate() {
    super.onCreate();
    PreferencesService.initializeSharedPreferences(this);
  }

}