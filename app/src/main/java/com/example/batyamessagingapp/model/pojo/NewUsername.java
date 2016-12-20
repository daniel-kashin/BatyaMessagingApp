package com.example.batyamessagingapp.model.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Кашин on 20.12.2016.
 */

public class NewUsername {

    @SerializedName("new_name")
    @Expose
    private String newUsername;

    public NewUsername(String newUsername){
        this.newUsername = newUsername;
    }

}
