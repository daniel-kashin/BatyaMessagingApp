package com.example.batyamessagingapp.activity.dialogs.fragment_settings.view;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.batyamessagingapp.R;
import com.example.batyamessagingapp.activity.dialogs.fragment_settings.presenter.SettingsPresenter;
import com.example.batyamessagingapp.activity.dialogs.fragment_settings.presenter.SettingsService;
import com.example.batyamessagingapp.activity.dialogs.view.DialogsView;

/**
 * Created by Кашин on 10.12.2016.
 */

public class SettingsFragment extends Fragment implements SettingsView {

    private Button mLogoutButton;
    private Button mFullLogoutButton;

    private SettingsPresenter mPresenter;
    private DialogsView mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, null);

        initializeViews(rootView);
        setOnClickListeners();

        mActivity = (DialogsView)getActivity();
        mPresenter = new SettingsService(this, getActivity());

        return rootView;
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
    public void onResume(){
        super.onResume();
        mPresenter.onResume();
    }

    @Override
    public void showAlert(String message, String title) {
        if (activityInitialized()) mActivity.showAlert(message, title);
    }

    @Override
    public void setOrdinaryToolbarLabelText() {
        if (activityInitialized()) mActivity.setToolbarLabelText(getString(R.string.fragment_settings_title));
    }

    private boolean activityInitialized(){
        return isAdded() && getActivity()!= null;
    }

    private void initializeViews(View view){
        mLogoutButton = (Button)view.findViewById(R.id.settings_logout_button);
        mFullLogoutButton = (Button)view.findViewById(R.id.settings_full_logout_button);
    }

    private void setOnClickListeners(){
        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.onLogoutButtonClick();
            }
        });

        mFullLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.onFullLogoutButtonClick();
            }
        });
    }
}
