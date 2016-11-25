package com.example.batyamessagingapp.activity.chat.adapter;

/**
 * Created by Кашин on 18.11.2016.
 */

public class ChatMessage {
    private String messageText;
    private Direction direction;

    public ChatMessage(String messageText, Direction direction){
        this.messageText = messageText;
        this.direction = direction;
    }

    public String getMessageText(){
        return messageText;
    }

    public Direction getDirection(){
        return direction;
    }


    public enum Direction {
        Incoming(0),
        Outcoming(1);

        private int intValue;
        private Direction(int value) {
            intValue = value;
        }

        public int getIntValue(){
            return intValue;
        }

    }
}
