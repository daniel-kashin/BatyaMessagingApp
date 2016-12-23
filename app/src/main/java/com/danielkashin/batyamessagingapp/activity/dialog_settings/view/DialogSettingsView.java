package com.danielkashin.batyamessagingapp.activity.dialog_settings.view;

/**
 * Created by Кашин on 21.12.2016.
 */

public interface DialogSettingsView {
  void setDialogUsername(String newDialogUsername);

  void showToast(String message);

  void showAlert(String message, String title);

  void openChatActivity();

  boolean checkInputs();

  void openMainActivity();

  boolean isAdmin();

  void setNoInternetConnectionToolbarLabel();

  void setCommonToolbarLabel();

  void hideConfirmIcon();
}
