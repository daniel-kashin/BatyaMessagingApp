package com.danielkashin.batyamessagingapp.model.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Кашин on 22.12.2016.
 */

public class GroupUser {

  @SerializedName("user_id")
  @Expose
  private String userId;

  @SerializedName("join_time")
  @Expose
  private long joinTime;

  public String getUserId(){
    return userId;
  }

  public long getJoinTime(){
    return joinTime;
  }

}
