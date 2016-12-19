package com.example.batyamessagingapp.activity.main.fragment_search.view;

import android.text.TextWatcher;

/**
 * Created by Кашин on 18.12.2016.
 */

public interface SearchView {
    void showProgressBar();
    void hideProgressBar();
    void openAuthenticationActivity();
    void openChatActivity(String dialogId, String username);
    void setOnToolbarTextListener(TextWatcher textWatcher);
    void showNoUsersTextView();
    void showNoInternetConnectionTextView();
    void hideTextView();
}
