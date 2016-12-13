package com.example.batyamessagingapp.activity.dialogs.fragment_settings.view;

import android.app.ProgressDialog;

/**
 * Created by Кашин on 10.12.2016.
 */

public interface SettingsView {
    void openAuthenticationActivity();
    void startProgressDialog(String message);
    void stopProgressDialog();
    void showAlert(String message, String title);
    void setOrdinaryToolbarLabelText();
}
