package com.danielkashin.batyamessagingapp.model.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Кашин on 21.11.2016.
 */

public class MessageArray {

  @SerializedName("messages")
  @Expose
  public ArrayList<Message> messages;

  public MessageArray(ArrayList<Message> messages) {
    this.messages = messages;
  }

  public ArrayList<Message> getMessages() {
    return messages;
  }
}
