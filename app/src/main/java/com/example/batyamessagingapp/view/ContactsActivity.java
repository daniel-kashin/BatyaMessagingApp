package com.example.batyamessagingapp.view;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.batyamessagingapp.Batya;
import com.example.batyamessagingapp.R;
import com.example.batyamessagingapp.model.NetworkService;
import com.example.batyamessagingapp.presenter.AuthenticationPresenter;
import com.example.batyamessagingapp.presenter.AuthenticationService;
import com.example.batyamessagingapp.presenter.ContactsPresenter;
import com.example.batyamessagingapp.presenter.ContactsService;

public class ContactsActivity extends AppCompatActivity implements ContactsView {
    Button logoutButton;
    TextView textView;

    private ContactsPresenter contactsPresenter;

    private void initializeViews() {
        logoutButton = (Button)findViewById(R.id.logoutButton);
        textView = (TextView)findViewById(R.id.textView);
    }

    private void setOnClickListeners(){
        logoutButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                contactsPresenter.onLogoutButtonClick();
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
        textView.setText(NetworkService.getTokenValue());
    }

    @Override
    public void openAuthenticationActivity(){
        Intent intent = new Intent(this, AuthenticationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }
}
