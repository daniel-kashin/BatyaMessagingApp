package com.example.batyamessagingapp.model.pojo;

import com.google.gson.annotations.SerializedName;
import com.google.gson.annotations.Expose;

/**
 * Created by Кашин on 23.10.2016.
 */

public class PojoToken {

    @SerializedName("token")
    @Expose
    private String token;

    public String getValue() {
        return token;
    }
}
