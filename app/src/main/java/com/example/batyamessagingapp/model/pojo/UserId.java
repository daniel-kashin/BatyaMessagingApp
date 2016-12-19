package com.example.batyamessagingapp.model.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Кашин on 19.12.2016.
 */

public class UserId {

    @SerializedName("user_id")
    @Expose
    private String userId;

    public String toString(){
        return userId;
    }
}
