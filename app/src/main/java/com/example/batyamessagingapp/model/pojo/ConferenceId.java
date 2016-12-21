package com.example.batyamessagingapp.model.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Кашин on 21.12.2016.
 */

public class ConferenceId {

    @SerializedName("conference_id")
    @Expose
    private final String congerenceId;

    public ConferenceId(String congerenceId){
        this.congerenceId = congerenceId;
    }

    public String toString(){
        return congerenceId;
    }
}
