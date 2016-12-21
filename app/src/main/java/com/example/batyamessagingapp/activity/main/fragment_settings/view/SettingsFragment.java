package com.example.batyamessagingapp.activity.main.fragment_settings.view;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.batyamessagingapp.R;
import com.example.batyamessagingapp.activity.main.fragment_settings.presenter.SettingsPresenter;
import com.example.batyamessagingapp.activity.main.fragment_settings.presenter.SettingsService;
import com.example.batyamessagingapp.activity.main.view.MainView;
import com.example.batyamessagingapp.model.NetworkExecutor;
import com.example.batyamessagingapp.model.PreferencesService;

import java.io.IOException;
import java.net.ConnectException;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;


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
            mChangeUsernameButton.setTitle(
                    NetworkExecutor.getDialogNameFromId(PreferencesService.getIdFromPreferences()));
        } catch (ConnectException e) {
            mChangeUsernameButton.setTitle("No internet connection");
        } catch (InterruptedException | ExecutionException | IOException e) {
            mActivity.openAuthenticationActivity();
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
                                NetworkExecutor.AsyncTaskCompleteListener<NetworkExecutor.ErrorType> callback =
                                        new NetworkExecutor.AsyncTaskCompleteListener<NetworkExecutor.ErrorType>() {
                                            @Override
                                            public void onTaskComplete(NetworkExecutor.ErrorType result) {
                                                if (result == NetworkExecutor.ErrorType.NoInternetConnection) {
                                                    showAlert("No internet connection", "Error");
                                                    dialog.cancel();
                                                } else if (result == NetworkExecutor.ErrorType.NoAccess) {
                                                    showAlert("Wrong password", "Error");
                                                } else {
                                                    showAlert("Your password was successfully changed", "Success");
                                                    setChangeUsernameButtonData();
                                                }
                                            }
                                        };

                                NetworkExecutor.changePassword(
                                        oldPassword,
                                        newPassword,
                                        getActivity(),
                                        callback);
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
                                try {
                                    NetworkExecutor.changeUsername(
                                            newUsername,
                                            null,
                                            getActivity());
                                    showAlert("Your username was successfully changed", "Success");
                                    setChangeUsernameButtonData();
                                } catch (ConnectException e) {
                                    showAlert("No internet connection", "Error");
                                } catch (InterruptedException | ExecutionException | IOException e) {
                                    showAlert("Please, try again later or choose different username", "Error");
                                }
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
