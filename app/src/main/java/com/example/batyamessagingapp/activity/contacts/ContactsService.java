package com.example.batyamessagingapp.activity.contacts;

import android.content.Context;
import android.os.AsyncTask;

import com.example.batyamessagingapp.model.NetworkService;
import com.example.batyamessagingapp.model.PreferencesService;
import com.example.batyamessagingapp.model.pojo.APIAnswer;

import java.io.IOException;
import java.net.ConnectException;

import retrofit2.Response;


/**
 * Created by Кашин on 29.10.2016.
 */

public class ContactsService implements ContactsPresenter {

    private ContactsView view;
    private DisconnectionAsyncTask disconnectionAsyncTask;
    private Context context;
    private ErrorType errorType = ErrorType.NoError;

    public ContactsService(ContactsView view, Context context) {
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


    class DisconnectionAsyncTask extends AsyncTask<Void, Void, APIAnswer> {
        private final DisconnectionType disconnectionType;

        public DisconnectionAsyncTask(DisconnectionType disconnectionType) {
            this.disconnectionType = disconnectionType;
        }

        protected APIAnswer doInBackground(Void... voids) {
            try {
                Response<APIAnswer> response;
                if (disconnectionType == DisconnectionType.Full)
                    response = NetworkService.getFullLogoutCall().execute();
                else
                    response = NetworkService.getLogoutCall().execute();

                APIAnswer apiAnswer = response.body();

                if (apiAnswer == null)
                    errorType = ErrorType.NoAccess;
                else
                    errorType = ErrorType.NoError;

                return apiAnswer;
            } catch (ConnectException e) {
                errorType = ErrorType.NoInternetConnection;
                return null;
            } catch (IOException e) {
                errorType = ErrorType.NoAccess;
                return null;
            }
        }

        protected void onPostExecute(APIAnswer apiAnswer) {
            if (errorType == ErrorType.NoInternetConnection && disconnectionType == DisconnectionType.Full) {
                view.showAlert("No internet connection. Unable to leave active sessions", "Log out error");
                errorType = ErrorType.NoError;
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