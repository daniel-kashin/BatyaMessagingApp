package com.example.batyamessagingapp.activity.chat.view;


import com.example.batyamessagingapp.activity.chat.adapter.ChatMessage;

import java.util.List;

/**
 * Created by Кашин on 15.11.2016.
 */

public interface ChatView {
    String getMessage();
    void clearMessageEditText();
    void scrollRecyclerViewToLast();
    void scrollRecyclerViewToFirst();
    void hideNoMessagesTextView();
    void setCommonToolbarLabelText();
    void setLoadingToolbarLabelText();
    void setNoInternetToolbarLabelText();
    void showRefreshIcon();
    void openDialogsActivity();
}
