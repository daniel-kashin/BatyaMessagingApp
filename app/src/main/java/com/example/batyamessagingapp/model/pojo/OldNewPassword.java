package com.example.batyamessagingapp.model.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Кашин on 20.12.2016.
 */

public class OldNewPassword {
    @SerializedName("password")
    @Expose
    private final String password;

    @SerializedName("new_password")
    @Expose
    private final String newPassword;

    public OldNewPassword(final String password, final String newPassword){
        this.password = password;
        this.newPassword = newPassword;
    }

    public String getPassword(){
        return password;
    }

    public String getNewPassword(){
        return newPassword;
    }
}
