package com.danielkashin.batyamessagingapp.model.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Кашин on 05.11.2016.
 */

public class APIAnswer {
  @SerializedName("message")
  @Expose
  private String message;
}
