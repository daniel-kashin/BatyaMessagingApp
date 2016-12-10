package com.example.batyamessagingapp.activity.dialogs.view;

import android.app.ProgressDialog;

/**
 * Created by Кашин on 29.10.2016.
 */

public interface DialogsView {
    void openAuthenticationActivity();
    void showAlert(String message, String title);
    void openChatActivity(String dialogId);
    void setToolbarLabel(String newLabel);
    void startProgressDialog(String message);
    void stopProgressDialog();
    ProgressDialog getProgressDialog();
}
