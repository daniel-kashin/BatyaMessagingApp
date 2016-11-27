package com.example.batyamessagingapp.activity.chat.adapter;

import java.util.List;

/**
 * Created by Кашин on 26.11.2016.
 */

public interface MessagesDataModel {
    void addMessage(ChatMessage message);
    void addMessages(List<ChatMessage> messages);
    int getSize();
}
