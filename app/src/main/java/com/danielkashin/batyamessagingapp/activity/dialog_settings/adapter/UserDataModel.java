package com.danielkashin.batyamessagingapp.activity.dialog_settings.adapter;

import java.util.ArrayList;

/**
 * Created by Кашин on 23.12.2016.
 */

public interface UserDataModel {
  void setUsers(ArrayList<User> users);

  void setOnUserLongClickListener(OnUserLongClickListener onUserLongClickListener);

  void setOnUserClickListener(OnUserClickListener onUserClickListener);

  String getUserIdByPosition(int position);

  String getUsernameByPosition(int position);
}
