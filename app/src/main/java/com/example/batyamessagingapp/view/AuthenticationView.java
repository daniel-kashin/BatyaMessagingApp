package com.example.batyamessagingapp.view;

/**
 * Created by Кашин on 29.10.2016.
 */

public interface AuthenticationView {
    String getUsername();
    String getPassword();
    void showToast(String message);
    void goToContactsActivity();
}
