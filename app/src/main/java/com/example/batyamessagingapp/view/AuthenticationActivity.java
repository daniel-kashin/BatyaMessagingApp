package com.example.batyamessagingapp.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.batyamessagingapp.Batya;
import com.example.batyamessagingapp.SecurePreferences;
import com.example.batyamessagingapp.model.NetworkService;
import com.example.batyamessagingapp.presenter.AuthenticationPresenter;
import com.example.batyamessagingapp.presenter.AuthenticationService;
import com.example.batyamessagingapp.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AuthenticationActivity extends AppCompatActivity implements AuthenticationView {

    private Button registrationButton;
    private Button authButton;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private View activityRootView;
    private ScrollView scrollView;
    private View mainLayout;
    private ProgressDialog progressDialog;

    private AuthenticationPresenter authenticationPresenter;

    public ProgressDialog getProgressDialog(){
        return progressDialog;
    }

    private void initializeViews() {
        registrationButton = (Button) findViewById(R.id.registrationButton);
        authButton = (Button) findViewById(R.id.authButton);
        usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        activityRootView = findViewById(R.id.activityRoot);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        mainLayout = findViewById(R.id.mainLayout);
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setTitle("Please, wait");
    }

    private void setListeners() {
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                DisplayMetrics metrics = AuthenticationActivity.this.getResources().getDisplayMetrics();
                final float dp200 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, metrics);

                final int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
                if (heightDiff > dp200) { // if more than 200 dp, it's probably a keyboard...
                    scrollView.post(new Runnable() {
                        public void run() {
                            scrollView.scrollTo(0, (int) (dp200 * 0.6f));
                        }
                    });
                } else if (heightDiff < dp200) { // if more than 200 dp, it's probably a keyboard...
                    scrollView.post(new Runnable() {
                        public void run() {
                            scrollView.scrollTo(0, 0);
                            activityRootView.refreshDrawableState();
                        }
                    });
                }
            }
        });

        View.OnClickListener onButtonClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case (R.id.authButton):
                        authenticationPresenter.onAuthButtonClick();
                        break;
                    case (R.id.registrationButton):
                        authenticationPresenter.onRegistrationButtonClick();
                        break;
                    default:
                        break;
                }
            }
        };

        authButton.setOnClickListener(onButtonClick);
        registrationButton.setOnClickListener(onButtonClick);
    }

    public void startProgressDialog(String message){
        progressDialog.setMessage(message);
        if (!progressDialog.isShowing())
        progressDialog.show();
    }

    public void stopProgressDialog(){
        if (progressDialog.isShowing())
        progressDialog.dismiss();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        initializeViews();
        setListeners();
        setUsernameEditText(NetworkService.getUsername());

        authenticationPresenter = new AuthenticationService(this);
    }

    @Override
    public boolean checkInputs() {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (username.isEmpty() || password.isEmpty()) return false;

        String usernameError = null;
        String passwordError = null;

        Pattern p = Pattern.compile("[A-Za-z0-9_-]+");
        if (password.length() < 10) passwordError = "at least 10 characters";
        if (username.length() < 2) usernameError = "at least 2 characters";
        if (!p.matcher(username).matches())
            usernameError = "latin letters, numbers, _ and - are allowded";

        usernameEditText.setError(usernameError);
        passwordEditText.setError(passwordError);
        return usernameError == null && passwordError == null;
    }

    @Override
    public String getUsername() {
        return usernameEditText.getText().toString();
    }

    @Override
    public String getPassword() {
        return passwordEditText.getText().toString();
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public void setUsernameEditText(String newText){
        usernameEditText.setText(newText);
    }

    @Override
    public void openContactsActivity() {
        Intent intent = new Intent(this, ContactsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

}
