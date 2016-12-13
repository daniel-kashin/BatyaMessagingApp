package com.example.batyamessagingapp.activity.dialogs.fragment_view_dialogs.view;

import android.app.Activity;

/**
 * Created by Кашин on 26.11.2016.
 */

public interface ViewDialogsView {
    void hideNoDialogsTextView();
    void openAuthenticationActivity();
    void openChatActivity(String dialogId);
    void setLoadingToolbarLabelText();
    void setNoInternetToolbarLabelText();
    void setCommonToolbarLabelText();
}
