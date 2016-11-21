package com.example.batyamessagingapp.model.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Кашин on 21.11.2016.
 */

public class MessageArray {

    @SerializedName("timestamp")
    @Expose
    public ArrayList<Message> messages;

    public MessageArray(ArrayList<Message> messages){
        this.messages = messages;
    }


}
