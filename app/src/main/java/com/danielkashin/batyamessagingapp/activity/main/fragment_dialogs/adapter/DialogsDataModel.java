package com.danielkashin.batyamessagingapp.activity.main.fragment_dialogs.adapter;

import com.danielkashin.batyamessagingapp.model.pojo.PairLastMessageDialogId;

import java.util.ArrayList;

/**
 * Created by Кашин on 29.10.2016.
 */

public interface DialogsDataModel {
  boolean noChanges(ArrayList<PairLastMessageDialogId> dialogs);

  int getSize();

  void addDialog(Dialog dialog);

  int findDialogPositionById(String id);

  String getDialogIdByPosition(int position);

  String getDialogNameByPosition(int position);

  void setDialogMessageAndTimestamp(int position, String message, long timestamp);

  void setOnDialogClickListener(OnDialogClickListener onDialogClickListener);

  void refresh();
}
