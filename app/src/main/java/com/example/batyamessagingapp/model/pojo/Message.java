package com.example.batyamessagingapp.model.pojo;

/**
 * Created by Кашин on 23.10.2016.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Message {

    @SerializedName("guid")
    @Expose
    private String guid;

    @SerializedName("dialog_id")
    @Expose
    private String dialogId;

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
        this.dialogId = type;
        this.sender = type;
        this.guid = type;
        timestamp = 0;
    }


    public String getGuid() {
        return guid;
    }

    public String getDialogId() {
        return dialogId;
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
