package com.example.batyamessagingapp.activity.main.fragment_search.adapter;

import com.example.batyamessagingapp.activity.main.fragment_dialogs.adapter.Dialog;
import com.example.batyamessagingapp.activity.main.fragment_dialogs.adapter.OnDialogClickListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

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
