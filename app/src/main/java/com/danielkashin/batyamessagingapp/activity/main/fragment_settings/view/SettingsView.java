package com.danielkashin.batyamessagingapp.activity.main.fragment_settings.view;

/**
 * Created by Кашин on 10.12.2016.
 */

public interface SettingsView {
  void openAuthenticationActivity();

  void startProgressDialog();

  void stopProgressDialog();

  void showAlert(String message, String title);

  void showToast(String message);

  void setCommonToolbarLabelText();
}
