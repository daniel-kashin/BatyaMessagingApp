package com.example.batyamessagingapp.activity.authentication.presenter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Pair;

import com.example.batyamessagingapp.R;
import com.example.batyamessagingapp.activity.authentication.view.AuthenticationView;
import com.example.batyamessagingapp.model.NetworkService;
import com.example.batyamessagingapp.model.PreferencesService;
import com.example.batyamessagingapp.model.pojo.Token;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

import retrofit2.Response;

/**
 * Created by Кашин on 29.10.2016.
 */

public class AuthenticationService implements AuthenticationPresenter {

    private Context mContext;
    private ConnectionAsyncTask mConnectionAsyncTask;

    private AuthenticationView mView;


    public AuthenticationService(AuthenticationView view) {
        mView = view;
        this.mContext = (Context) view;
    }

    @Override
    public void onButtonClick(int id) {

        ConnectionType connectionType = null;

        if (mView.checkInputs()) {
            if (id == R.id.authButton) {
                connectionType = ConnectionType.Login;
            } else if (id == R.id.registrationButton) {
                connectionType = ConnectionType.Register;
            }

            if (connectionType != null) {
                mConnectionAsyncTask = new ConnectionAsyncTask(
                        mView.getProgressDialog(),
                        connectionType,
                        mView.getUsername(),
                        mView.getPassword()
                );
                mConnectionAsyncTask.execute();
            }
        }
    }

    class ConnectionAsyncTask extends AsyncTask<Void, Void, Pair<Token, ErrorType>> {

        private final ProgressDialog progressDialog;
        private final ConnectionType connectionType;
        private final String username;
        private final String password;

        public ConnectionAsyncTask(ProgressDialog progressDialog, ConnectionType connectionType,
                                   String username, String password) {

            this.progressDialog = progressDialog;
            this.connectionType = connectionType;
            this.username = username;
            this.password = password;

            progressDialog.setCancelable(true);
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    cancel(true);
                }
            });
        }

        protected void onPreExecute() {
            mView.startProgressDialog("Loading...");
        }

        protected Pair<Token, ErrorType> doInBackground(Void... voids) {
            try {
                Response<Token> response;
                if (connectionType == ConnectionType.Login) {
                    response = NetworkService.getAuthCall(username, password).execute();
                } else {
                    response = NetworkService.getRegisterCall(username, password).execute();
                }

                Token token = response.body();

                if (response.code() == 200 && token != null) {
                    return new Pair<>(token, ErrorType.NoError);
                } else {
                    return new Pair<>(null, ErrorType.NoAccess);
                }
            } catch (ConnectException | SocketTimeoutException e) {
                return new Pair<>(null, ErrorType.NoInternetConnection);
            } catch (IOException e) {
                return new Pair<>(null, ErrorType.NoAccess);
            }
        }

        protected void onCancelled(Token token) {
            //do nothing
        }

        protected void onPostExecute(Pair<Token, ErrorType> resultPair) {
            mView.stopProgressDialog();

            if (resultPair.second == ErrorType.NoError && resultPair.first != null) {
                PreferencesService.saveTokenAndUsernameToPreferences(resultPair.first, username);
                mView.openDialogsActivity();
            } else { //error occured
                String message = "";

                if (resultPair.second == ErrorType.NoInternetConnection) {
                    message = "No internet connection";
                } else if (resultPair.second == ErrorType.NoAccess) {
                    if (connectionType == ConnectionType.Login) {
                        message = "Invalid username or password";
                    } else {
                        message = "Chosen username is already exists";
                    }
                }

                mView.showAlert(message, "Auth error");
            }
        }
    }

    private enum ConnectionType {
        Register,
        Login
    }

    private enum ErrorType {
        NoInternetConnection,
        NoAccess,
        NoError
    }
}