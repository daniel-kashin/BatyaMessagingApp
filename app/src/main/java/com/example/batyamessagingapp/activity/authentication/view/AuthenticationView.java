package com.example.batyamessagingapp.activity.authentication.view;

import android.app.ProgressDialog;

/**
 * Created by Кашин on 29.10.2016.
 */

public interface AuthenticationView {
    String getUsername();
    String getPassword();
    void showAlert(String message, String title);
    void openDialogsActivity();
    boolean checkInputs();
    void startProgressDialog(String message);
    void stopProgressDialog();
    ProgressDialog getProgressDialog();
}
