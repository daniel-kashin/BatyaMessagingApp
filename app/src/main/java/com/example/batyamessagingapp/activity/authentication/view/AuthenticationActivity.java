package com.example.batyamessagingapp.activity.authentication.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.batyamessagingapp.activity.authentication.presenter.AuthenticationPresenter;
import com.example.batyamessagingapp.activity.authentication.presenter.AuthenticationService;
import com.example.batyamessagingapp.activity.main.view.MainActivity;
import com.example.batyamessagingapp.model.PreferencesService;
import com.example.batyamessagingapp.R;

import java.util.regex.Pattern;


public class AuthenticationActivity extends AppCompatActivity implements AuthenticationView {

    private Button mRegistrationButton;
    private Button mAuthButton;
    private EditText mUsernameEditText;
    private EditText mPasswordEditText;
    private View mActivityRootView;
    private ScrollView mScrollView;
    private ProgressDialog mProgressDialog;
    private TextInputLayout mUsernameLayout;
    private TextInputLayout mPasswordLayout;
    private TextView mMainIcon;

    private int mMainIconPaddingTop;
    private int mMainIconPaddingButtom;

    private AuthenticationPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        initializeViews();
        setListeners();

        mPresenter = new AuthenticationService(this);
    }

    @Override
    public void startProgressDialog(String message) {
        mProgressDialog.setMessage(message);
        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    @Override
    public void stopProgressDialog() {
        if (mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public boolean checkInputs() {
        String username = mUsernameEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();
        String usernameError = null;
        String passwordError = null;
        Pattern usernamePattern = Pattern.compile("[A-Za-z0-9_-]+");

        if (password.length() < 10 || username.length() > 256) {
            passwordError = "10..256 characters";
        }

        if (!usernamePattern.matcher(username).matches()) {
            usernameError = "only letters, numbers and _-";
        }

        if (username.length() < 2 || username.length() > 256) {
            usernameError = "2..256 characters";
        }

        mUsernameLayout.setError(usernameError);
        mPasswordLayout.setError(passwordError);

        if (username.isEmpty() || password.isEmpty()) {
            return false;
        } else {
            return usernameError == null && passwordError == null;
        }
    }

    @Override
    public String getUsername() {
        return mUsernameEditText.getText().toString();
    }

    @Override
    public String getPassword() {
        return mPasswordEditText.getText().toString();
    }

    @Override
    public void showAlert(String message, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                new ContextThemeWrapper(this, android.R.style.Theme_DeviceDefault_Light_Dialog));
        builder.setTitle(title)
                .setMessage(message)
                .setCancelable(true);
        AlertDialog alert = builder.create();
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.show();
    }

    @Override
    public void openDialogsActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public ProgressDialog getProgressDialog() {
        return mProgressDialog;
    }

    private void initializeViews() {
        mRegistrationButton = (Button) findViewById(R.id.authentication_registration_button);
        mAuthButton = (Button) findViewById(R.id.authentication_auth_button);
        mUsernameEditText = (EditText) findViewById(R.id.authentication_username_edit_text);
        mUsernameEditText.setText(PreferencesService.getUsernameFromPreferences());
        mPasswordEditText = (EditText) findViewById(R.id.authentication_password_edit_text);
        mActivityRootView = findViewById(R.id.activity_authentication);
        mScrollView = (ScrollView) findViewById(R.id.authenctication_scroll_view);
        mUsernameLayout = (TextInputLayout) findViewById(R.id.authentication_username_layout);
        mPasswordLayout = (TextInputLayout) findViewById(R.id.authentication_password_layout);
        mMainIcon = (TextView) findViewById(R.id.authenctication_main_icon);

        mMainIconPaddingTop = mMainIcon.getPaddingTop();
        mMainIconPaddingButtom = mMainIcon.getPaddingBottom();

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setTitle(null);
    }

    private void setListeners() {
        //resize the screen when keyboard appears
        mActivityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                DisplayMetrics metrics = AuthenticationActivity.this.getResources().getDisplayMetrics();
                final float dp200 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, metrics);
                final int heightDiff = mActivityRootView.getRootView().getHeight() - mActivityRootView.getHeight();

                if (heightDiff > dp200) {
                    mMainIcon.setPadding(
                            mMainIcon.getPaddingLeft(),
                            mMainIconPaddingButtom / 2,
                            mMainIcon.getPaddingRight(),
                            mMainIconPaddingButtom / 2
                    );
                    mScrollView.post(new Runnable() {
                        public void run() {
                            mScrollView.scrollTo(0, 1000000);
                        }
                    });
                } else {
                    mMainIcon.setPadding(
                            mMainIcon.getPaddingLeft(),
                            mMainIconPaddingTop,
                            mMainIcon.getPaddingRight(),
                            mMainIconPaddingButtom
                    );
                    mScrollView.post(new Runnable() {
                        public void run() {
                            mScrollView.scrollTo(0, 0);
                            mActivityRootView.refreshDrawableState();
                        }
                    });
                }
            }
        });

        //on button clicks
        View.OnClickListener onButtonClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = view.getId();
                if (id == R.id.authentication_registration_button
                        || id == R.id.authentication_auth_button) {
                    mPresenter.onButtonClick(id);
                }
            }
        };
        mAuthButton.setOnClickListener(onButtonClick);
        mRegistrationButton.setOnClickListener(onButtonClick);

        //call auth button onClick when user finished to write the password
        mPasswordEditText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    mAuthButton.callOnClick();
                }
                return false;
            }
        });
    }
}