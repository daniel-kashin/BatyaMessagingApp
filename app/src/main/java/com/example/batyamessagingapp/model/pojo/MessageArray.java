package com.example.batyamessagingapp.model.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Кашин on 21.11.2016.
 */

public class MessageArray {

    @SerializedName("messages")
    @Expose
    public ArrayList<PairMessageDialogId> messages;

    public MessageArray(ArrayList<PairMessageDialogId> messages){
        this.messages = messages;
    }

    public ArrayList<PairMessageDialogId> getMessages(){
        return messages;
    }
}
