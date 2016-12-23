package com.danielkashin.batyamessagingapp.activity.chat.view;


/**
 * Created by Кашин on 15.11.2016.
 */

public interface ChatView {
  void setProperties(boolean isGroupOriginator, int groupCount);

  String getInputMessage();

  void clearMessageEditText();

  void scrollRecyclerViewToLast();

  void scrollRecyclerViewToFirst();

  void setCommonToolbarLabelText();

  void setLoadingToolbarLabelText();

  void setNoInternetToolbarLabelText();

  void openDialogsActivity();

  void setToolbarSmallLabelText();

  boolean isGroup();
}
