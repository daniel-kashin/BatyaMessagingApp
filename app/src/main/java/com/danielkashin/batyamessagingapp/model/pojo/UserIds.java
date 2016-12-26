package com.danielkashin.batyamessagingapp.model.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Кашин on 19.12.2016.
 */

public class UserIds {
  @SerializedName("user_ids")
  @Expose
  private ArrayList<String> userIds;

  public ArrayList<String> toList() {
    ArrayList<String> output = new ArrayList<>();
    for (String id : userIds) {
      output.add(id);
    }
    return output;
  }
}
