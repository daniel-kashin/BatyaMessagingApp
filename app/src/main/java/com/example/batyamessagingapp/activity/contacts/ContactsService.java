package com.example.batyamessagingapp.activity.contacts;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.util.Pair;

import com.example.batyamessagingapp.model.NetworkService;
import com.example.batyamessagingapp.model.PreferencesService;
import com.example.batyamessagingapp.model.pojo.PojoAPIAnswer;

import java.io.IOException;
import java.net.ConnectException;

import retrofit2.Response;


/**
 * Created by Кашин on 29.10.2016.
 */

public class ContactsService implements ContactsPresenter {

    private ContactsView view;
    private Context context;
    private DisconnectionAsyncTask disconnectionAsyncTask;

    ContactsService(ContactsView view, Context context) {
        this.view = view;
        this.context = context;
    }

    public void onLogoutButtonClick() {
        disconnectionAsyncTask = new DisconnectionAsyncTask(DisconnectionType.Normal);
        disconnectionAsyncTask.execute();
    }

    public void onFullLogoutButtonClick() {
        disconnectionAsyncTask = new DisconnectionAsyncTask(DisconnectionType.Full);
        disconnectionAsyncTask.execute();
    }


    class DisconnectionAsyncTask extends AsyncTask<Void, Void, Pair<PojoAPIAnswer, ErrorType>> {

        private final DisconnectionType disconnectionType;

        public DisconnectionAsyncTask(DisconnectionType disconnectionType) {
            this.disconnectionType = disconnectionType;
        }

        protected Pair<PojoAPIAnswer, ErrorType> doInBackground(Void... voids) {
            try {
                Response<PojoAPIAnswer> response;
                if (disconnectionType == DisconnectionType.Full)
                    response = NetworkService.getFullLogoutCall().execute();
                else //disconnectionType == DisconnectionType.Normal
                    response = NetworkService.getLogoutCall().execute();

                PojoAPIAnswer pojoApiAnswer = response.body();

                if (response.code() == 200 && pojoApiAnswer != null) {
                    return new Pair<>(pojoApiAnswer, ErrorType.NoError);
                } else {
                    return new Pair<>(null, ErrorType.NoAccess);
                }
            } catch (ConnectException e) {
                return new Pair<>(null, ErrorType.NoInternetConnection);
            } catch (IOException e) {
                return new Pair<>(null, ErrorType.NoAccess);
            }
        }

        protected void onPostExecute(Pair<PojoAPIAnswer, ErrorType> resultPair) {
            if (resultPair.second == ErrorType.NoInternetConnection && disconnectionType == DisconnectionType.Full) {
                view.showAlert("No internet connection. Unable to leave active sessions", "Log out error");
            } else {
                PreferencesService.deleteTokenFromPreferences();
                view.openAuthenticationActivity();
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