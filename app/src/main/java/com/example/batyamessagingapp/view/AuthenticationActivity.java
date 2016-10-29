package com.example.batyamessagingapp.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.batyamessagingapp.presenter.AuthenticationPresenter;
import com.example.batyamessagingapp.presenter.AuthenticationService;
import com.example.batyamessagingapp.R;

import butterknife.BindView;

public class AuthenticationActivity extends AppCompatActivity implements AuthenticationView {
    @BindView(R.id.registrationButton) Button registrationButton;
    @BindView(R.id.authButton) Button authButton;
    @BindView(R.id.usernameEditText) EditText usernameEditText;
    @BindView(R.id.passwordEditText) EditText passwordEditText;
    AuthenticationPresenter authenticationPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        authenticationPresenter = new AuthenticationService(this);

        authenticationPresenter.tryToConnectWithPreviousData();

        authButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authenticationPresenter.onAuthButtonClick();
            }
        });

    }

    @Override
    public String getUsername(){
        return usernameEditText.getText().toString();
    }

    @Override
    public String getPassword(){
        return passwordEditText.getText().toString();
    }

    @Override
    public void showToast(String message){
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
    }

}
