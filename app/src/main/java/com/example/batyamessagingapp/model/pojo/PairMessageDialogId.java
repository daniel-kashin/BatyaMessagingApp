package com.example.batyamessagingapp.model.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Кашин on 23.11.2016.
 */

public class PairMessageDialogId {
    @SerializedName("dialog_id")
    @Expose
    private String dialogId;

    @SerializedName("message")
    @Expose
    private Message message;

    public PairMessageDialogId(String dialogId, Message message){
        this.dialogId = dialogId;
        this.message = message;
    }

    public Message getMessage(){
        return message;
    }

    public String getDialogId(){
        return dialogId;
    }
}
