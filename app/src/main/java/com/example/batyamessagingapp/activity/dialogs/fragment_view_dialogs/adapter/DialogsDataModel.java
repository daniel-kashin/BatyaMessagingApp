package com.example.batyamessagingapp.activity.dialogs.fragment_view_dialogs.adapter;

/**
 * Created by Кашин on 29.10.2016.
 */

public interface DialogsDataModel {
    int getSize();
    void addDialog(Dialog dialog);
    int findDialogPositionById(String id);
    void setDialogMessageAndTimestamp(int position, String message, long timestamp);
    void refresh();
}