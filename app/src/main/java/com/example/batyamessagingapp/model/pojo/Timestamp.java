package com.example.batyamessagingapp.model.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Кашин on 09.12.2016.
 */

public class Timestamp {

    @SerializedName("timestamp")
    @Expose
    private long timestamp;

    public long getTimestamp(){
        return timestamp;
    }
}
