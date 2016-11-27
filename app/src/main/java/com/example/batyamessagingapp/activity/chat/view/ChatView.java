package com.example.batyamessagingapp.activity.chat.view;


import com.example.batyamessagingapp.activity.chat.adapter.ChatMessage;

import java.util.List;

/**
 * Created by Кашин on 15.11.2016.
 */

public interface ChatView {
    String getMessageString();
    void clearMessageEditText();
    void showToast(String text);
    void scrollRecyclerViewToLast();
    void scrollRecyclerViewToFirst();
}
