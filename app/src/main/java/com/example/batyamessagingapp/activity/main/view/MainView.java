package com.example.batyamessagingapp.activity.main.view;

import android.app.ProgressDialog;

/**
 * Created by Кашин on 29.10.2016.
 */

public interface MainView {
    void openAuthenticationActivity();
    void openChatActivity(String dialogId);
    void setToolbarLabelText(String newLabel);
    void showAlert(String message, String title);
    void startProgressDialog(String message);
    void stopProgressDialog();
    ProgressDialog getProgressDialog();
    void hideSearch();
    void showSearch();
}
