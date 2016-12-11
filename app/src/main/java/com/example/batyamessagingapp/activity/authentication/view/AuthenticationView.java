package com.example.batyamessagingapp.activity.authentication.view;

import android.app.ProgressDialog;

/**
 * Created by Кашин on 29.10.2016.
 */

public interface AuthenticationView {
    String getUsername();
    String getPassword();

    void startProgressDialog(String message);
    void stopProgressDialog();
    ProgressDialog getProgressDialog();

    void showAlert(String message, String title);
    boolean checkInputs();
    void openDialogsActivity();
}
