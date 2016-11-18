package com.example.batyamessagingapp.activity.authentication;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;

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
    private AuthenticationView view;
    private ConnectionAsyncTask connectionAsyncTask;
    private ErrorType errorType = ErrorType.NoError;

    public AuthenticationService(AuthenticationView view) {
        this.view = view;
    }

    public void onAuthButtonClick() {
        if (view.checkInputs()) {
            try {
                connectionAsyncTask = new ConnectionAsyncTask(view.getProgressDialog(), ConnectionType.Login);
                connectionAsyncTask.execute();
            } catch (Exception ex) {
            }
        }
    }

    public void onRegistrationButtonClick() {
        if (view.checkInputs()) {
            try {
                connectionAsyncTask = new ConnectionAsyncTask(view.getProgressDialog(), ConnectionType.Register);
                connectionAsyncTask.execute();
            } catch (Exception ex) {
            }
        }
    }


    class ConnectionAsyncTask extends AsyncTask<Void, Void, Token> {

        private final ProgressDialog progressDialog;
        private final ConnectionType connectionType;

        public ConnectionAsyncTask(ProgressDialog progressDialog, ConnectionType connectionType) {
            this.progressDialog = progressDialog;
            this.connectionType = connectionType;

            progressDialog.setCancelable(true);
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    cancel(true);
                }
            });
        }

        protected void onPreExecute() {
            view.startProgressDialog("Loading...");
        }

        protected Token doInBackground(Void... voids) {
            try {
                Response<Token> response;
                if (connectionType == ConnectionType.Login) {
                    response = NetworkService.getAuthCall(view.getUsername(), view.getPassword()).execute();
                } else {
                    response = NetworkService.getRegisterCall(view.getUsername(), view.getPassword()).execute();
                }

                Token token = response.body();

                if (token == null) {
                    errorType = ErrorType.NoAccess;
                } else {
                    errorType = ErrorType.NoError;
                }

                return token;

            } catch (ConnectException|SocketTimeoutException e) {
                errorType = ErrorType.NoInternetConnection;
                return null;
            } catch (IOException e) {
                errorType = ErrorType.NoAccess;
                return null;
            }
        }

        protected void onCancelled(Token token) {
            //do nothing
        }

        protected void onPostExecute(Token token) {
            view.stopProgressDialog();

            if (errorType == ErrorType.NoError && token!=null) {
                PreferencesService.saveTokenAndUsernameToPreferences(token, view.getUsername());
                view.openContactsActivity();
            } else {
                String message;
                if (errorType == ErrorType.NoInternetConnection) {
                    message = "No internet connection";
                } else if (connectionType == ConnectionType.Login) {
                    message = "Invalid username or password";
                } else {
                    message = "Chosen username is already exists";
                }
                view.showAlert(message, "Auth error");

                errorType = ErrorType.NoError;
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