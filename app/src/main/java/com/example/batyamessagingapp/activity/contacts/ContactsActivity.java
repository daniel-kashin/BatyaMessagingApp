package com.example.batyamessagingapp.activity.contacts;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.batyamessagingapp.R;
import com.example.batyamessagingapp.activity.authentication.AuthenticationActivity;
import com.example.batyamessagingapp.model.PreferencesService;

public class ContactsActivity extends AppCompatActivity implements ContactsView {
    private ProgressDialog progressDialog;
    Button logoutButton;
    Button fullLogoutButton;
    TextView textView;

    private ContactsPresenter contactsPresenter;

    private void initializeViews() {
        logoutButton = (Button)findViewById(R.id.logoutButton);
        fullLogoutButton = (Button)findViewById(R.id.fullLogoutButton);
        textView = (TextView)findViewById(R.id.textView);
    }

    @Override
    public void showAlert(String message, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setCancelable(true);
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void setOnClickListeners(){
        logoutButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                contactsPresenter.onLogoutButtonClick();
            }
        });
        fullLogoutButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                contactsPresenter.onFullLogoutButtonClick();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        initializeViews();
        setOnClickListeners();

        contactsPresenter = new ContactsService((ContactsView)this, (Context) this);
        textView.setText(PreferencesService.getTokenValueFromPreferences());
    }

    @Override
    public void openAuthenticationActivity(){
        Intent intent = new Intent(this, AuthenticationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }
}
