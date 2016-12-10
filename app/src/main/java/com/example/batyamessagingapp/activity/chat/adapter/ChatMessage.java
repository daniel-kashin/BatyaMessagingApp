package com.example.batyamessagingapp.activity.chat.adapter;

/**
 * Created by Кашин on 18.11.2016.
 */

public class ChatMessage {

    private String messageText;
    private String timeText;
    private Direction direction;
    private String guid;

    public ChatMessage(String messageText, String timeText, Direction direction, String guid){
        this.messageText = messageText;
        this.timeText = timeText;
        this.direction = direction;
        this.guid = guid;
    }

    public String getMessageText(){
        return messageText;
    }

    public String getTimeText() {
        return timeText;
    }

    public Direction getDirection(){
        return direction;
    }

    public String getGuid(){
        return guid;
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
