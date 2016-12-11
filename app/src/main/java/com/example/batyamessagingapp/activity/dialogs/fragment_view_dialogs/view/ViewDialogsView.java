package com.example.batyamessagingapp.activity.dialogs.fragment_view_dialogs.view;

import android.app.Activity;

/**
 * Created by Кашин on 26.11.2016.
 */

public interface ViewDialogsView {
    void hideNoDialogsTextView();
    Activity getParentActivity();
    void openAuthenticationActivity();
    void onRefreshIconClick();
    void refreshToolbarLabelText();
    void showNoInternetConnection();
}
