package com.danielkashin.batyamessagingapp.model.pojo;

/**
 * Created by Кашин on 23.10.2016.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Message {

    @SerializedName("guid")
    @Expose
    private String guid;

    @SerializedName("sender")
    @Expose
    private String sender;

    @SerializedName("timestamp")
    @Expose
    private long timestamp;

    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("content")
    @Expose
    private String content;

    public Message(String type, String content){
        this.type = type;
        this.content = content;
    }

    public Message(){
    }

    public String getGuid() {
        return guid;
    }

    public String getSender() {
        return sender;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getType() {
        return type;
    }

    public String getContent() {
        return content;
    }
}

