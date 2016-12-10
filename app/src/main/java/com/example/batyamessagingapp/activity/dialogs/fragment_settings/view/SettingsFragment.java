package com.example.batyamessagingapp.activity.dialogs.fragment_settings.view;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.batyamessagingapp.R;
import com.example.batyamessagingapp.activity.dialogs.fragment_settings.presenter.SettingsPresenter;
import com.example.batyamessagingapp.activity.dialogs.fragment_settings.presenter.SettingsService;
import com.example.batyamessagingapp.activity.dialogs.fragment_view_dialogs.adapter.DialogAdapter;
import com.example.batyamessagingapp.activity.dialogs.fragment_view_dialogs.presenter.ViewDialogsService;
import com.example.batyamessagingapp.activity.dialogs.view.DialogsActivity;
import com.example.batyamessagingapp.activity.dialogs.view.DialogsView;

/**
 * Created by Кашин on 10.12.2016.
 */

public class SettingsFragment extends Fragment implements SettingsView {

    private Button mLogoutButton;
    private Button mFullLogoutButton;
    private SettingsPresenter mPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void openAuthenticationActivity() {
        ((DialogsView)getActivity()).openAuthenticationActivity();
    }

    @Override
    public void showAlert(String message, String title) {
        ((DialogsView)getActivity()).showAlert(message, title);
    }

    @Override
    public void startProgressDialog(String message) {
        ((DialogsView)getActivity()).startProgressDialog(message);
    }

    @Override
    public void stopProgressDialog() {
        ((DialogsView)getActivity()).stopProgressDialog();
    }

    @Override
    public ProgressDialog getProgressDialog(){
        return ((DialogsView)getActivity()).getProgressDialog();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, null);

        initializeViews(rootView);
        setOnClickListeners();

        mPresenter = new SettingsService(this, getActivity());

        return rootView;
    }

    @Override
    public void onResume(){
        super.onResume();
        ((DialogsView)getActivity()).setToolbarLabel(getString(R.string.fragment_settings_title));
    }



    private void initializeViews(View view){
        mLogoutButton = (Button)view.findViewById(R.id.settings_logout_button);
        mFullLogoutButton = (Button)view.findViewById(R.id.settings_full_logout_button);
        ((DialogsView)getActivity()).setToolbarLabel(getString(R.string.fragment_settings_title));
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
