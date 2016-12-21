package com.example.batyamessagingapp.activity.authentication.presenter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Pair;

import com.example.batyamessagingapp.R;
import com.example.batyamessagingapp.activity.authentication.view.AuthenticationView;
import com.example.batyamessagingapp.model.NetworkExecutor;
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
            if (id == R.id.authentication_auth_button) {
                connectionType = ConnectionType.Login;
            } else if (id == R.id.authentication_registration_button) {
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

    private class ConnectionAsyncTask extends AsyncTask<Void, Void, Pair<Token, NetworkExecutor.ErrorType>> {

        private final ProgressDialog progressDialog;
        private final ConnectionType connectionType;
        private final String username;
        private final String password;

        ConnectionAsyncTask(ProgressDialog progressDialog, ConnectionType connectionType,
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

        @Override
        protected void onCancelled() {
            //do nothing
        }

        @Override
        protected void onPreExecute() {
            mView.startProgressDialog();
        }

        @Override
        protected Pair<Token, NetworkExecutor.ErrorType> doInBackground(Void... voids) {
            try {
                Response<Token> response;
                if (connectionType == ConnectionType.Login) {
                    response = NetworkService.getAuthCall(username, password).execute();
                } else {
                    response = NetworkService.getRegisterCall(username, password).execute();
                }

                Token token = response.body();

                if (response.code() == 200 && token != null) {
                    return new Pair<>(token, NetworkExecutor.ErrorType.NoError);
                } else {
                    return new Pair<>(null, NetworkExecutor.ErrorType.NoAccess);
                }
            } catch (ConnectException | SocketTimeoutException e) {
                return new Pair<>(null, NetworkExecutor.ErrorType.NoInternetConnection);
            } catch (IOException e) {
                return new Pair<>(null, NetworkExecutor.ErrorType.NoAccess);
            }
        }

        @Override
        protected void onPostExecute(Pair<Token, NetworkExecutor.ErrorType> resultPair) {
            mView.stopProgressDialog();

            if (resultPair.second == NetworkExecutor.ErrorType.NoError && resultPair.first != null) {
                PreferencesService.saveTokenAndUsernameToPreferences(resultPair.first, username);
                mView.openDialogsActivity();
            } else { //error occured
                String message = "";

                if (resultPair.second == NetworkExecutor.ErrorType.NoInternetConnection) {
                    message = "No internet connection";
                } else if (resultPair.second == NetworkExecutor.ErrorType.NoAccess) {
                    if (connectionType == ConnectionType.Login) {
                        message = "Invalid username or password";
                    } else {
                        message = "Chosen username is already exists";
                    }
                }

                mView.showAlert(message, "Error");
            }
        }
    } // Connection async task

    private enum ConnectionType {
        Register,
        Login
    }
}