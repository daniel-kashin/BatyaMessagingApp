package com.danielkashin.batyamessagingapp.activity.main.fragment_search.adapter;

import java.util.ArrayList;

/**
 * Created by Кашин on 18.12.2016.
 */

public interface UserDataModel {
  String getUserIdByPosition(int position);

  String getUsernameByPosition(int position);

  void clearUsers();

  void setUsers(ArrayList<User> users);

  void setOnUserClickListener(OnUserClickListener onUserClickListener);
}
