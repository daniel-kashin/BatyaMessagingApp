package com.example.batyamessagingapp.activity.main.view;

import android.app.ProgressDialog;
import android.text.TextWatcher;

/**
 * Created by Кашин on 29.10.2016.
 */

public interface MainView {
    void openAuthenticationActivity();
    void openChatActivity(String dialogId, String dialogName);
    void setToolbarLabelText(String newLabel);
    void showAlert(String message, String title);
    void startProgressDialog(String message);
    void stopProgressDialog();
    ProgressDialog getProgressDialog();
    void hideSearch();
    void showNewConversationInterface();
    void setOnToolbarTextListener(TextWatcher textWatcher);
    void setOnToolbarDefaultTextListener();
    void showSearchInterface();
}
