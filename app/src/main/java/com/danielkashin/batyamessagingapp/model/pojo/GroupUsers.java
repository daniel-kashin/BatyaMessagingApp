package com.danielkashin.batyamessagingapp.model.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Кашин on 22.12.2016.
 */

public class GroupUsers {

  @SerializedName("originator")
  @Expose
  private String originatorId;

  @SerializedName("users")
  @Expose
  private ArrayList<GroupUser> users;

  public String getOriginatorId(){
    return originatorId;
  }

  public ArrayList<GroupUser> getUsers(){
    return users;
  }

}
