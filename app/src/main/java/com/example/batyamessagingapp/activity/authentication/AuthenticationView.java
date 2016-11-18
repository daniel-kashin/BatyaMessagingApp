package com.example.batyamessagingapp.activity.authentication;

import android.app.ProgressDialog;

/**
 * Created by Кашин on 29.10.2016.
 */

public interface AuthenticationView {
    String getUsername();
    String getPassword();
    void showAlert(String message, String title);
    void openContactsActivity();
    boolean checkInputs();
    void startProgressDialog(String message);
    void stopProgressDialog();
    ProgressDialog getProgressDialog();
}
