package com.danielkashin.batyamessagingapp.model.pojo;

import com.google.gson.annotations.SerializedName;
import com.google.gson.annotations.Expose;

/**
 * Created by Кашин on 23.10.2016.
 */

public class Token {

    @SerializedName("token")
    @Expose
    private String token;

    public String getValue() {
        return token;
    }
}
