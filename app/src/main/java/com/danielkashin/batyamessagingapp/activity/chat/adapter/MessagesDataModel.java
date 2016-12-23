package com.danielkashin.batyamessagingapp.activity.chat.adapter;

import java.util.List;

/**
 * Created by Кашин on 26.11.2016.
 */

public interface MessagesDataModel {
    void addMessage(ChatMessage message);
    void addMessagesToEnd(List<ChatMessage> messages);
    void addMessagesToBegin(List<ChatMessage> messages);
    int getSize();
    boolean hasItemWithId(String guid);
}
