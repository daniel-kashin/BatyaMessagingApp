package com.example.batyamessagingapp.activity.chat.view;


import com.example.batyamessagingapp.activity.chat.adapter.ChatMessage;

import java.util.List;

/**
 * Created by Кашин on 15.11.2016.
 */

public interface ChatView {
  void setProperties(boolean isGroup, boolean isGroupOriginator,
                     boolean isChatWithMyself, int groupCount);

  String getInputMessage();

  void clearMessageEditText();

  void scrollRecyclerViewToLast();

  void scrollRecyclerViewToFirst();

  void setCommonToolbarLabelText();

  void setLoadingToolbarLabelText();

  void setNoInternetToolbarLabelText();

  void openDialogsActivity();
}
