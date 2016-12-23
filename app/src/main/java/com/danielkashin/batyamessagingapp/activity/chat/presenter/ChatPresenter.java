package com.danielkashin.batyamessagingapp.activity.chat.presenter;

/**
 * Created by Кашин on 15.11.2016.
 */

public interface ChatPresenter {
  void onSendMessageButtonClick();

  void onLoad();

  void onPause();

  boolean initialized();

  boolean onInitializeProperties(boolean isGroup);
}

