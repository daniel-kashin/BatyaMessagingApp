package com.example.batyamessagingapp.activity.main.fragment_dialogs.adapter;

/**
 * Created by Кашин on 29.10.2016.
 */

public interface DialogsDataModel {
    int getSize();
    void addDialog(Dialog dialog);
    int findDialogPositionById(String id);
    String getDialogIdByPosition(int position);
    String getDialogNameByPosition(int position);
    void setDialogMessageAndTimestamp(int position, String message, String dialogName, long timestamp);
    void setOnDialogClickListener(OnDialogClickListener onDialogClickListener);
    void refresh();
}
