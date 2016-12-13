package com.example.batyamessagingapp.activity.dialogs.fragment_settings.presenter;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v4.util.Pair;
import android.view.View;

import com.example.batyamessagingapp.R;
import com.example.batyamessagingapp.activity.dialogs.fragment_settings.view.SettingsView;
import com.example.batyamessagingapp.activity.dialogs.presenter.DialogsService;
import com.example.batyamessagingapp.model.NetworkService;
import com.example.batyamessagingapp.model.PreferencesService;
import com.example.batyamessagingapp.model.pojo.APIAnswer;

import java.io.IOException;
import java.net.ConnectException;

import retrofit2.Response;

/**
 * Created by Кашин on 10.12.2016.
 */

public class SettingsService implements SettingsPresenter {

    private DisconnectionAsyncTask mDisconnectionAsyncTask;
    private Context mContext;
    private SettingsView mView;

    public SettingsService(SettingsView view, Context context){
        mView = view;
        mContext = context;
    }

    @Override
    public void onResume() {
        mView.setOrdinaryToolbarLabelText();
    }

    @Override
    public void onLogoutButtonClick() {
        mDisconnectionAsyncTask = new DisconnectionAsyncTask(DisconnectionType.Normal);
        mDisconnectionAsyncTask.execute();
    }

    @Override
    public void onFullLogoutButtonClick() {
        mDisconnectionAsyncTask = new DisconnectionAsyncTask(DisconnectionType.Full);
        mDisconnectionAsyncTask.execute();
    }

    private class DisconnectionAsyncTask
            extends AsyncTask<Void, Void, Pair<APIAnswer, ErrorType>> {
        private final DisconnectionType disconnectionType;

        DisconnectionAsyncTask(DisconnectionType disconnectionType) {
            this.disconnectionType = disconnectionType;
        }

        @Override
        protected void onPreExecute(){
            mView.startProgressDialog(mContext.getString(R.string.loading));
        }

        protected Pair<APIAnswer, ErrorType> doInBackground(Void... voids) {
            try {
                Response<APIAnswer> response;
                if (disconnectionType == DisconnectionType.Full) {
                    response = NetworkService.getFullLogoutCall().execute();
                } else {
                    response = NetworkService.getLogoutCall().execute();
                }

                APIAnswer apiAnswer = response.body();

                if (response.code() == 200 && apiAnswer != null) {
                    return new Pair<>(apiAnswer, ErrorType.NoError);
                } else {
                    return new Pair<>(null, ErrorType.NoAccess);
                }
            } catch (ConnectException e) {
                return new Pair<>(null, ErrorType.NoInternetConnection);
            } catch (IOException e) {
                return new Pair<>(null, ErrorType.NoAccess);
            }
        }

        protected void onPostExecute(Pair<APIAnswer, ErrorType> resultPair) {
            mView.stopProgressDialog();

            if (resultPair.second == ErrorType.NoInternetConnection
                    && disconnectionType == DisconnectionType.Full) {
                mView.showAlert("No internet connection. Unable to leave active sessions", "Log out error");
            } else {
                PreferencesService.deleteTokenFromPreferences();
                mView.openAuthenticationActivity();
            }
        }
    }

    private enum DisconnectionType {
        Normal,
        Full
    }

    private enum ErrorType {
        NoInternetConnection,
        NoAccess,
        NoError
    }

}
