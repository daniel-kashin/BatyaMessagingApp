package com.example.batyamessagingapp.activity.chat.adapter;

import com.example.batyamessagingapp.lib.TimestampHelper;

/**
 * Created by Кашин on 18.11.2016.
 */

public class ChatMessage {

    private String content;
    private String time;
    private long timestamp;
    private Direction direction;
    private String guid;

    public ChatMessage(String content, long timestamp, Direction direction, String guid){
        this.content = content;
        this.time = TimestampHelper.formatTimestampToTime(timestamp);
        this.timestamp = timestamp;
        this.direction = direction;
        this.guid = guid;
    }

    public String getContent(){
        return content;
    }

    public String getTime() {
        return time;
    }

    public Direction getDirection(){
        return direction;
    }

    public String getGuid(){
        return guid;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setContent(String content){
        this.content = content;
    }

    public enum Direction {
        Incoming(0),
        Outcoming(1),
        System(2);

        private int intValue;
        Direction(int value) {
            intValue = value;
        }

        public int getIntValue(){
            return intValue;
        }
    }
}
