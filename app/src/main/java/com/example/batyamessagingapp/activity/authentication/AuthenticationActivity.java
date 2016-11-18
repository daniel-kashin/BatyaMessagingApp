package com.example.batyamessagingapp.activity.authentication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.batyamessagingapp.activity.dialog.DialogActivity;
import com.example.batyamessagingapp.model.PreferencesService;
import com.example.batyamessagingapp.R;

import java.util.regex.Pattern;


public class AuthenticationActivity extends AppCompatActivity implements AuthenticationView {

    private Button registrationButton;
    private Button authButton;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private View activityRootView;
    private ScrollView scrollView;
    private ProgressDialog progressDialog;
    private TextInputLayout usernameTextInputLayout;
    private TextInputLayout passwordTextInputLayout;
    private TextView mainIcon;
    private int mainIconPaddingTop;
    private int mainIconPaddingButtom;

    private AuthenticationPresenter presenter;

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
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setTitle(null);
        usernameTextInputLayout = (TextInputLayout)findViewById(R.id.usernameTextInputLayout);
        passwordTextInputLayout = (TextInputLayout)findViewById(R.id.passwordTextInputLayout);
        mainIcon = (TextView)findViewById(R.id.mainIcon);
        mainIconPaddingTop = mainIcon.getPaddingTop();
        mainIconPaddingButtom = mainIcon.getPaddingBottom();
    }

    private void setListeners() {


        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                DisplayMetrics metrics = AuthenticationActivity.this.getResources().getDisplayMetrics();
                final float dp200 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, metrics);

                final int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
                if (heightDiff > dp200) { // if more than 200 dp, it's probably a keyboard...
                    mainIcon.setPadding(mainIcon.getPaddingLeft(), mainIconPaddingButtom/2 ,mainIcon.getPaddingRight(), mainIconPaddingButtom/2);

                    scrollView.post(new Runnable() {
                        public void run() {
                            scrollView.scrollTo(0, 1000);
                        }
                    });
                } else {
                    mainIcon.setPadding(mainIcon.getPaddingLeft(), mainIconPaddingTop, mainIcon.getPaddingRight(), mainIconPaddingButtom);
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
                        presenter.onAuthButtonClick();
                        break;
                    case (R.id.registrationButton):
                        presenter.onRegistrationButtonClick();
                        break;
                    default:
                        break;
                }
            }
        };

        authButton.setOnClickListener(onButtonClick);
        registrationButton.setOnClickListener(onButtonClick);

        passwordEditText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    authButton.callOnClick();
                }
                return false;
            }
        });
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
        setUsernameEditText(PreferencesService.getUsernameFromPreferences());

        presenter = new AuthenticationService(this);
    }

    @Override
    public boolean checkInputs() {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String usernameError = null;
        String passwordError = null;

        Pattern p = Pattern.compile("[A-Za-z0-9_-]+");
        if (password.length() < 10) passwordError = "at least 10 characters";

        if (!p.matcher(username).matches())
            usernameError = "latin letters, numbers, _ and - are allowded";
        if (username.length() < 2) usernameError = "at least 2 characters";

        usernameTextInputLayout.setError(usernameError);
        passwordTextInputLayout.setError(passwordError);

        if (username.isEmpty() || password.isEmpty()) return false;
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
    public void showAlert(String message, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setCancelable(true);
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void setUsernameEditText(String newText){
        usernameEditText.setText(newText);
    }

    @Override
    public void openContactsActivity() {
        Intent intent = new Intent(this, DialogActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

}
