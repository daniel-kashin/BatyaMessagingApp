package com.danielkashin.batyamessagingapp.activity.dialog_settings.adapter;

import android.support.v7.widget.RecyclerView;

import com.danielkashin.batyamessagingapp.model.pojo.GroupUser;

import java.util.ArrayList;

/**
 * Created by Кашин on 23.12.2016.
 */

public interface UsersDataModel {
  void setUsers(ArrayList<User> users);

  void setOnUserClickListener(OnUserClickListener onUserClickListener);

  String getUserIdByPosition(int position);
}
