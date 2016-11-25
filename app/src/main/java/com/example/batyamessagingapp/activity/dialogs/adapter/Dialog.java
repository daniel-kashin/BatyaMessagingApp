package com.example.batyamessagingapp.activity.dialogs.adapter;

import android.graphics.Bitmap;

/**
 * Created by Кашин on 25.11.2016.
 */

public class Dialog {
    private Bitmap bitmap;
    private String id;
    private String message;
    private String time;

    public Dialog(Bitmap bitmap, String id, String message, String time){
        this.bitmap = bitmap;
        this.id = id;
        this.message = message;
        this.time = time;
    }

    public Bitmap getBitmap(){
        return bitmap;
    }

    public String getId(){
        return id;
    }

    public String getMessage(){
        return message;
    }

    public String getTime(){
        return time;
    }

    public void setMessage(String message){
        this.message = message;
    }

    public void setTime(String time){
        this.time = time;
    }

}
