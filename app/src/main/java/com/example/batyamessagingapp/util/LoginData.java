package com.example.batyamessagingapp.util;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Кашин on 23.10.2016.
 */

public class LoginData {

    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("password")
    @Expose
    private String password;

    public LoginData(String username, String password){
        this.username = username;
        this.password = password;
    }
}