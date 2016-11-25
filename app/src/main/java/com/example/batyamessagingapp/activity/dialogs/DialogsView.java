package com.example.batyamessagingapp.activity.dialogs;

import com.example.batyamessagingapp.activity.dialogs.adapter.Dialog;

/**
 * Created by Кашин on 29.10.2016.
 */

public interface DialogsView {
    void openAuthenticationActivity();
    void showAlert(String message, String title);
    int getAdapterSize();
    void addDialogToAdapter(Dialog dialog);
    int findDialogPositionByIdInAdapter(String id);
    void setDialogMessageAndTimeInAdapter(int position, String message, String time);
    void refreshAdapter();
    void openChatActivity(String dialogId);
}
