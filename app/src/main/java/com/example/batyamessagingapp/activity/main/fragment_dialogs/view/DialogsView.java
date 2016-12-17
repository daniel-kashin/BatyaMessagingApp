package com.example.batyamessagingapp.activity.main.fragment_dialogs.view;

/**
 * Created by Кашин on 26.11.2016.
 */

public interface DialogsView {
    void hideNoDialogsTextView();
    void openAuthenticationActivity();
    void openChatActivity(String dialogId);
    void setLoadingToolbarLabelText();
    void setNoInternetToolbarLabelText();
    void setCommonToolbarLabelText();
}
