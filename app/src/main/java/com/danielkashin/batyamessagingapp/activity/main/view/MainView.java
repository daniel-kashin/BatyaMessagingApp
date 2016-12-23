package com.danielkashin.batyamessagingapp.activity.main.view;

import android.app.ProgressDialog;
import android.text.TextWatcher;

/**
 * Created by Кашин on 29.10.2016.
 */

public interface MainView {
    boolean isInputEmpty();
    void openAuthenticationActivity();
    void openChatActivity(String dialogId, String dialogName);
    void setToolbarLabelText(String newLabel);
    void showAlert(String message, String title);
    void startProgressDialog();
    void stopProgressDialog();
    ProgressDialog getProgressDialog();
    void hideSearch();
    void showClearIcon();
    void hideClearIcon();
    void setOnToolbarTextListener(TextWatcher textWatcher);
    void clearOnToolbarTextListener();
    void showSearchInterface();
}
