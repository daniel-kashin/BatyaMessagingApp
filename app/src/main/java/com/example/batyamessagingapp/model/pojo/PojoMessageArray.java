package com.example.batyamessagingapp.model.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Кашин on 21.11.2016.
 */

public class PojoMessageArray {

    @SerializedName("timestamp")
    @Expose
    public ArrayList<PojoMessage> pojoMessages;

    public PojoMessageArray(ArrayList<PojoMessage> pojoMessages){
        this.pojoMessages = pojoMessages;
    }

    public ArrayList<PojoMessage> getPojoMessages(){
        return pojoMessages;
    }


}
