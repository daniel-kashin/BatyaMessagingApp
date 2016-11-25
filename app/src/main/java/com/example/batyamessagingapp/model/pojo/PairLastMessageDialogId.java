package com.example.batyamessagingapp.model.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Кашин on 25.11.2016.
 */

public class PairLastMessageDialogId {
    @SerializedName("dialog_id")
    @Expose
    private String dialogId;

    @SerializedName("last_message")
    @Expose
    private Message lastMessage;

    public PairLastMessageDialogId(String dialogId, Message message){
        this.dialogId = dialogId;
        this.lastMessage = message;
    }

    public Message getMessage(){
        return lastMessage;
    }

    public String getDialogId(){
        return dialogId;
    }
}
