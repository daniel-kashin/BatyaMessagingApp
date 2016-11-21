package com.example.batyamessagingapp.activity.dialog;


import com.example.batyamessagingapp.activity.dialog.recycler_view.Message;

/**
 * Created by Кашин on 15.11.2016.
 */

public interface DialogView {
    String getMessageString();
    void clearMessageEditText();
    void showToast(String message);
    void addMessageToAdapter(String message, Message.Direction direction);
}
