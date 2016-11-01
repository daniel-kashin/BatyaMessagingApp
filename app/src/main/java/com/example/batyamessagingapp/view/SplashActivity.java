package com.example.batyamessagingapp.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.example.batyamessagingapp.model.NetworkService;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startNextActivity();
    }

    private void startNextActivity(){
        try { Thread.sleep(500); } catch (Throwable t){ }

        Intent intent;
        if (NetworkService.isLoggedIn())
            intent = new Intent(this,ContactsActivity.class);
        else
            intent = new Intent(this,AuthenticationActivity.class);

        startActivity(intent);
        overridePendingTransition(0, 0);

        finish();
    }
}
