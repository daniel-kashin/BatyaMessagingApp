package com.danielkashin.batyamessagingapp.model.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Кашин on 17.12.2016.
 */

public class DialogName {
    @SerializedName("dialog_name")
    @Expose
    private String dialog_name;

    public String getDialogName() {
        return dialog_name;
    }
}
