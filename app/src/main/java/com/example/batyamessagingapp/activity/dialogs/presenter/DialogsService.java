package com.example.batyamessagingapp.activity.dialogs.presenter;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.util.Pair;

import com.example.batyamessagingapp.activity.dialogs.view.DialogsView;
import com.example.batyamessagingapp.model.NetworkService;
import com.example.batyamessagingapp.model.PreferencesService;
import com.example.batyamessagingapp.model.pojo.APIAnswer;

import java.io.IOException;
import java.net.ConnectException;

import retrofit2.Response;


/**
 * Created by Кашин on 29.10.2016.
 */

public class DialogsService implements DialogsPresenter  {

    private DisconnectionAsyncTask mDisconnectionAsyncTask;
    private DialogsView mView;
    private Context mContext;

    public DialogsService(DialogsView dialogsView){
        mView = dialogsView;
        mContext = (Context)dialogsView;
    }

    public void onLogoutButtonClick() {
        mDisconnectionAsyncTask = new DisconnectionAsyncTask(DisconnectionType.Normal);
        mDisconnectionAsyncTask.execute();
    }

    public void onFullLogoutButtonClick() {
        mDisconnectionAsyncTask = new DisconnectionAsyncTask(DisconnectionType.Full);
        mDisconnectionAsyncTask.execute();
    }

    class DisconnectionAsyncTask
            extends AsyncTask<Void, Void, Pair<APIAnswer, DialogsService.ErrorType>> {

        private final DisconnectionType disconnectionType;

        public DisconnectionAsyncTask(DisconnectionType disconnectionType) {
            this.disconnectionType = disconnectionType;
        }

        protected Pair<APIAnswer, DialogsService.ErrorType> doInBackground(Void... voids) {
            try {
                Response<APIAnswer> response;
                if (disconnectionType == DisconnectionType.Full)
                    response = NetworkService.getFullLogoutCall().execute();
                else //disconnectionType == DisconnectionType.Normal
                    response = NetworkService.getLogoutCall().execute();

                APIAnswer apiAnswer = response.body();

                if (response.code() == 200 && apiAnswer != null) {
                    return new Pair<>(apiAnswer, DialogsService.ErrorType.NoError);
                } else {
                    return new Pair<>(null, DialogsService.ErrorType.NoAccess);
                }
            } catch (ConnectException e) {
                return new Pair<>(null, DialogsService.ErrorType.NoInternetConnection);
            } catch (IOException e) {
                return new Pair<>(null, DialogsService.ErrorType.NoAccess);
            }
        }

        protected void onPostExecute(Pair<APIAnswer, DialogsService.ErrorType> resultPair) {
            if (resultPair.second == DialogsService.ErrorType.NoInternetConnection && disconnectionType == DisconnectionType.Full) {
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