package com.example.batyamessagingapp.activity.dialog;

/**
 * Created by Кашин on 15.11.2016.
 */

public interface DialogView {
    String getMessageString();
    void showAlert(String message, String title);
}
