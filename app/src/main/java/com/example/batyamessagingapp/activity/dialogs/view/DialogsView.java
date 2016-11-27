package com.example.batyamessagingapp.activity.dialogs.view;

/**
 * Created by Кашин on 29.10.2016.
 */

public interface DialogsView {
    void openAuthenticationActivity();
    void showAlert(String message, String title);
    void openChatActivity(String dialogId);
}
