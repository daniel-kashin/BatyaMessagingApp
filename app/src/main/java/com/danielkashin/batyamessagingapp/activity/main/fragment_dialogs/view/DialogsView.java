package com.danielkashin.batyamessagingapp.activity.main.fragment_dialogs.view;

/**
 * Created by Кашин on 26.11.2016.
 */

public interface DialogsView {
    void showNoDialogsTextView();
    void hideNoDialogsTextView();
    void openAuthenticationActivity();
    void openChatActivity(String dialogId, String dialogName);
    void showProgressBar();
    void hideProgressBar();
    void setNoInternetToolbarLabelText();
    void setCommonToolbarLabelText();
}
