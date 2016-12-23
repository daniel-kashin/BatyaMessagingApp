package com.danielkashin.batyamessagingapp.activity.dialog_settings.adapter;

import android.graphics.Bitmap;

/**
 * Created by Кашин on 23.12.2016.
 */

public class User {
  private Bitmap bitmap;
  private String id;
  private String name;
  private String dateAdded;

  public User(Bitmap bitmap, String id, String name, String dateAdded){
    this.bitmap = bitmap;
    this.id = id;
    this.name = name;
    this.dateAdded = dateAdded;
  }

  public Bitmap getBitmap(){
    return bitmap;
  }

  public String getId(){
    return id;
  }

  public String getName(){
    return name;
  }

  public String getDateAdded(){
    return dateAdded;
  }
}
