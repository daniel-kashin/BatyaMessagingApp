package com.example.batyamessagingapp.view;

import com.example.batyamessagingapp.SecurePreferences;

/**
 * Created by Кашин on 29.10.2016.
 */

public interface AuthenticationView {
    String getUsername();
    String getPassword();
    void showToast(String message);
    void openContactsActivity();
    boolean checkInputs();
    void setUsernameEditText(String username);
}
