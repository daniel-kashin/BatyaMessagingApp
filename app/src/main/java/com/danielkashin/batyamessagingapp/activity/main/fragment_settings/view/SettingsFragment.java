package com.danielkashin.batyamessagingapp.activity.main.fragment_settings.view;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.text.InputType;
import android.util.Pair;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.danielkashin.batyamessagingapp.R;
import com.danielkashin.batyamessagingapp.activity.main.fragment_settings.presenter.SettingsPresenter;
import com.danielkashin.batyamessagingapp.activity.main.fragment_settings.presenter.SettingsService;
import com.danielkashin.batyamessagingapp.activity.main.view.MainView;
import com.danielkashin.batyamessagingapp.model.BasicAsyncTask;
import com.danielkashin.batyamessagingapp.model.NetworkService;
import com.danielkashin.batyamessagingapp.model.PreferencesService;
import com.danielkashin.batyamessagingapp.model.pojo.DialogName;

import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;


/**
 * Created by Кашин on 10.12.2016.
 */

public class SettingsFragment extends PreferenceFragmentCompat implements SettingsView {

  private Preference mLogoutButton;
  private Preference mFullLogoutButton;
  private Preference mChangePasswordButton;
  private Preference mChangeUsernameButton;
  private Preference mIdButton;

  private SettingsPresenter mPresenter;
  private MainView mActivity;

  @Override
  public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
    addPreferencesFromResource(R.xml.fragment_settings);

    mActivity = (MainView) getActivity();
    mPresenter = new SettingsService(this, getActivity());

    initializeViews();
  }

  @Override
  public void openAuthenticationActivity() {
    if (activityInitialized()) mActivity.openAuthenticationActivity();
  }

  @Override
  public void startProgressDialog() {
    if (activityInitialized()) mActivity.startProgressDialog();
  }

  @Override
  public void stopProgressDialog() {
    if (activityInitialized()) mActivity.stopProgressDialog();
  }

  @Override
  public void onResume() {
    super.onResume();
    setOnClickListeners();
    setChangeUsernameButtonData();
    if (activityInitialized()) {
      mActivity.hideSearch();
      mActivity.clearOnToolbarTextListener();
    }
    mPresenter.onResume();
  }

  @Override
  public void showAlert(String message, String title) {
    if (activityInitialized()) mActivity.showAlert(message, title);
  }

  @Override
  public void setCommonToolbarLabelText() {
    if (activityInitialized())
      mActivity.setToolbarLabelText(getString(R.string.fragment_settings_title));
  }

  private boolean activityInitialized() {
    return isAdded() && getActivity() != null;
  }

  private void initializeViews() {
    mIdButton = findPreference("id");
    mLogoutButton = findPreference("log_out_current");
    mFullLogoutButton = findPreference("log_out_all");
    mChangePasswordButton = findPreference("change_password");
    mChangeUsernameButton = findPreference("change_username");

    mIdButton.setTitle(PreferencesService.getIdFromPreferences());
  }

  private void setChangeUsernameButtonData() {
    try {
        Pair<DialogName, BasicAsyncTask.ErrorType> result =
          new BasicAsyncTask<DialogName>(
              NetworkService.getGetDialogNameCall(PreferencesService.getIdFromPreferences()),
              null, false, null)
              .execute()
              .get();

        if (result.second == BasicAsyncTask.ErrorType.NoAccess){
          mActivity.openAuthenticationActivity();
        } else if (result.second == BasicAsyncTask.ErrorType.NoInternetConnection){
          if (activityInitialized()) {
            mChangeUsernameButton.setTitle(((Context)mActivity).getString(R.string.no_internet_connection));
          }
        } else {
          mChangeUsernameButton.setTitle(result.first.getDialogName());
        }
    } catch (ExecutionException | InterruptedException e) {
      openAuthenticationActivity();
    }
  }

  private void setOnClickListeners() {
    mLogoutButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
      @Override
      public boolean onPreferenceClick(Preference preference) {
        mPresenter.onLogoutButtonClick();
        return true;
      }
    });

    mFullLogoutButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
      @Override
      public boolean onPreferenceClick(Preference preference) {
        mPresenter.onFullLogoutButtonClick();
        return true;
      }
    });

    mChangePasswordButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
      @Override
      public boolean onPreferenceClick(Preference preference) {
        if (activityInitialized()) {
          // create alertdialog
          AlertDialog.Builder alertDialog = new AlertDialog.Builder((Context) mActivity);
          alertDialog.setTitle("Change password");

          // create its layout
          LinearLayout layout = new LinearLayout((Context) mActivity);
          layout.setOrientation(LinearLayout.VERTICAL);
          layout.setPadding(30, 0, 30, 0);

          // create three inputs
          final EditText oldPasswordEditText = new EditText((Context) mActivity);
          oldPasswordEditText.setHint("Old password");
          oldPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT |
              InputType.TYPE_TEXT_VARIATION_PASSWORD);
          layout.addView(oldPasswordEditText);

          final EditText newPasswordEditText = new EditText((Context) mActivity);
          newPasswordEditText.setHint("New password");
          newPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT |
              InputType.TYPE_TEXT_VARIATION_PASSWORD);
          layout.addView(newPasswordEditText);

          final EditText newPasswordEditText2 = new EditText((Context) mActivity);
          newPasswordEditText2.setHint("New password");
          newPasswordEditText2.setInputType(InputType.TYPE_CLASS_TEXT |
              InputType.TYPE_TEXT_VARIATION_PASSWORD);
          layout.addView(newPasswordEditText2);

          alertDialog.setView(layout);

          // set positive button listener
          DialogInterface.OnClickListener positiveListener = new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, int which) {
              String oldPassword = oldPasswordEditText.getText().toString();
              String newPassword = newPasswordEditText.getText().toString();
              String newPassword2 = newPasswordEditText2.getText().toString();

              if (newPassword.length() < 10 || newPassword.length() > 256 ||
                  oldPassword.length() < 10 || oldPassword.length() > 256 ||
                  newPassword2.length() < 10 || newPassword2.length() > 256) {
                showAlert("Password length should be 10..256 characters long",
                    "Error");
              } else if (!newPassword.equals(newPassword2)) {
                showAlert("New passwords differ", "Error");
              } else {
                BasicAsyncTask.AsyncTaskCompleteListener<Pair<ResponseBody, BasicAsyncTask.ErrorType>> callback =
                    new BasicAsyncTask.AsyncTaskCompleteListener<Pair<ResponseBody, BasicAsyncTask.ErrorType>>() {
                      @Override
                      public void onTaskComplete(Pair<ResponseBody, BasicAsyncTask.ErrorType> result) {
                        if (result.second == BasicAsyncTask.ErrorType.NoInternetConnection) {
                          showAlert("No internet connection", "Error");
                          dialog.cancel();
                        } else if (result.second == BasicAsyncTask.ErrorType.NoAccess) {
                          showAlert("Wrong password", "Error");
                        } else {
                          showAlert("Your password was successfully changed", "Success");
                          setChangeUsernameButtonData();
                        }
                      }
                    };

                new BasicAsyncTask<>(
                    NetworkService.getChangePasswordCall(oldPassword, newPassword),
                    (Context) mActivity,
                    false,
                    callback
                ).execute();
              }
            }
          };
          alertDialog.setPositiveButton("OK", positiveListener);

          // set negative button listener
          alertDialog.setNegativeButton("CANCEL",
              new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                  dialog.cancel();
                }
              });

          alertDialog.show();
        }

        return true;
      }
    }); // set changePasswordButton listener

    mChangeUsernameButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
      @Override
      public boolean onPreferenceClick(Preference preference) {
        if (activityInitialized()) {
          // create alertdialog
          AlertDialog.Builder alertDialog = new AlertDialog.Builder((Context) mActivity);
          alertDialog.setTitle("Change username");

          // create its layout
          LinearLayout layout = new LinearLayout((Context) mActivity);
          layout.setOrientation(LinearLayout.VERTICAL);
          layout.setPadding(30, 0, 30, 0);

          // create an input
          final EditText newUsernameEditText = new EditText((Context) mActivity);
          newUsernameEditText.setHint("New username");
          layout.addView(newUsernameEditText);

          alertDialog.setView(layout);

          // set positive button listener
          DialogInterface.OnClickListener positiveListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
              String newUsername = newUsernameEditText.getText().toString();

              Pattern usernamePattern = Pattern.compile("[A-Za-z0-9_-]+");

              if (!usernamePattern.matcher(newUsername).matches()) {
                showAlert("Username can contain only letters, numbers and _-", "Error");
              } else if (newUsername.length() < 2 || newUsername.length() > 256) {
                showAlert("Username length should be 2..256 characters long",
                    "Error");
              } else {
                BasicAsyncTask.AsyncTaskCompleteListener<Pair<ResponseBody, BasicAsyncTask.ErrorType>> callback =
                    new BasicAsyncTask.AsyncTaskCompleteListener<Pair<ResponseBody, BasicAsyncTask.ErrorType>>() {
                      @Override
                      public void onTaskComplete(Pair<ResponseBody, BasicAsyncTask.ErrorType> result) {
                        if (result.second == BasicAsyncTask.ErrorType.NoInternetConnection) {
                          showAlert("No internet connection", "Error");
                        } else if (result.second == BasicAsyncTask.ErrorType.NoAccess) {
                          showAlert("Please, try again later or choose different username", "Error");
                        } else {
                          showAlert("Your username was successfully changed", "Success");
                          setChangeUsernameButtonData();
                        }
                      }
                    };

                new BasicAsyncTask<ResponseBody>(
                    NetworkService.getChangeUsernameCall(newUsername, null),
                    (Context) mActivity,
                    false,
                    callback).execute();
              }
            }
          };

          alertDialog.setPositiveButton("OK", positiveListener);

          // set negative button listener
          alertDialog.setNegativeButton("CANCEL",
              new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                  dialog.cancel();
                }
              });

          alertDialog.show();
        }

        return true;
      }
    }); // set changeUsernameButton listener

  } // setOnClickListeners

}
