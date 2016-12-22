package com.example.batyamessagingapp.activity.splash;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.batyamessagingapp.activity.authentication.view.AuthenticationActivity;
import com.example.batyamessagingapp.activity.main.view.MainActivity;
import com.example.batyamessagingapp.model.PreferencesService;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startNextActivity();
    }

    private void startNextActivity(){
        Intent intent;

        if (PreferencesService.isTokenAvailableInPreferences()) {
            intent = new Intent(this, MainActivity.class);
        } else {
            intent = new Intent(this, AuthenticationActivity.class);
        }

        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }



}
