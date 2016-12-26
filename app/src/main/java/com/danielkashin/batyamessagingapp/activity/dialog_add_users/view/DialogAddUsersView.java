package com.danielkashin.batyamessagingapp.activity.dialog_add_users.view;

import android.support.v7.app.AlertDialog;
import android.text.TextWatcher;

/**
 * Created by Кашин on 25.12.2016.
 */

public interface DialogAddUsersView {
  boolean isInputEmpty();

  void showProgressBar();

  void hideProgressBar();

  void openMainActivity();

  void showNoUsersTextView();

  void showNoInternetConnectionTextView();

  void showNoFriendsTextView();

  void setOnToolbarTextListener(TextWatcher textWatcher);

  void hideTextView();

  void showClearIcon();

  void hideClearIcon();

  void showAlert(String message, String title);
}
