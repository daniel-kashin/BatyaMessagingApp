package com.example.batyamessagingapp.model.pojo;

/**
 * Created by Кашин on 23.10.2016.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoMessage {

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

    public PojoMessage(String type, String content){
        this.type = type;
        this.content = content;
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
