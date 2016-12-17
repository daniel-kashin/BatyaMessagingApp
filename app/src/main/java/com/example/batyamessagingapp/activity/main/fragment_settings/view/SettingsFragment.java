package com.example.batyamessagingapp.activity.main.fragment_settings.view;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.batyamessagingapp.R;
import com.example.batyamessagingapp.activity.main.fragment_settings.presenter.SettingsPresenter;
import com.example.batyamessagingapp.activity.main.fragment_settings.presenter.SettingsService;
import com.example.batyamessagingapp.activity.main.view.MainView;


/**
 * Created by Кашин on 10.12.2016.
 */

public class SettingsFragment extends PreferenceFragmentCompat implements SettingsView {

    private Preference mLogoutButton;
    private Preference mFullLogoutButton;

    private SettingsPresenter mPresenter;
    private MainView mActivity;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.fragment_settings);

        mActivity = (MainView) getActivity();
        mPresenter = new SettingsService(this, getActivity());

        initializeViews();
        setOnClickListeners();
    }

    @Override
    public void openAuthenticationActivity() {
        if (activityInitialized()) mActivity.openAuthenticationActivity();
    }

    @Override
    public void startProgressDialog(String message) {
        if (activityInitialized()) mActivity.startProgressDialog(message);
    }

    @Override
    public void stopProgressDialog() {
        if (activityInitialized()) mActivity.stopProgressDialog();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.onResume();
    }

    @Override
    public void showAlert(String message, String title) {
        if (activityInitialized()) mActivity.showAlert(message, title);
    }

    @Override
    public void setOrdinaryToolbarLabelText() {
        if (activityInitialized())
            mActivity.setToolbarLabelText(getString(R.string.fragment_settings_title));
    }

    private boolean activityInitialized() {
        return isAdded() && getActivity() != null;
    }

    private void initializeViews() {
        mLogoutButton = findPreference("log_out_current");
        mFullLogoutButton = findPreference("log_out_all");
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
    }
}
