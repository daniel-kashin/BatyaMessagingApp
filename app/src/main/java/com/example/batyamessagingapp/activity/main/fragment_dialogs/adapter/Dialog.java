package com.example.batyamessagingapp.activity.main.fragment_dialogs.adapter;

import android.graphics.Bitmap;

/**
 * Created by Кашин on 25.11.2016.
 */

public class Dialog {
    private Bitmap bitmap;
    private String id;
    private String message;
    private long timestamp;

    public Dialog(Bitmap bitmap, String id, String message, long timestamp){
        this.bitmap = bitmap;
        this.id = id;
        this.message = message;
        this.timestamp = timestamp;
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

    public long getTimestamp(){
        return timestamp;
    }

    public void setMessage(String message){
        this.message = message;
    }

    public void setTimestamp(long timestamp){
        this.timestamp = timestamp;
    }

}
