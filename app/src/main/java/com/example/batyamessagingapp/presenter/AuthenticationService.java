package com.example.batyamessagingapp.presenter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.view.View;
import android.widget.TextView;

import com.example.batyamessagingapp.Batya;
import com.example.batyamessagingapp.SecurePreferences;
import com.example.batyamessagingapp.model.NetworkService;
import com.example.batyamessagingapp.util.LoginData;
import com.example.batyamessagingapp.util.Token;
import com.example.batyamessagingapp.view.AuthenticationView;

import retrofit2.Call;

/**
 * Created by Кашин on 29.10.2016.
 */

public class AuthenticationService implements AuthenticationPresenter {
    private AuthenticationView view;
    AuthAsyncTask authAsyncTask;
    RegisterAsyncTask registerAsyncTask;

    public AuthenticationService(AuthenticationView view) {
        this.view = view;
    }

    public void onAuthButtonClick() {
        if (view.checkInputs()) {
            try {
                authAsyncTask = new AuthAsyncTask(view.getProgressDialog());
                authAsyncTask.execute();
            } catch (Exception ex) {
            }
        }
    }

    public void onRegistrationButtonClick() {
        if (view.checkInputs()) {
            try {
                registerAsyncTask = new RegisterAsyncTask(view.getProgressDialog());
                registerAsyncTask.execute();
            } catch (Exception ex) {
            }
        }
    }

    class RegisterAsyncTask extends AsyncTask<Void, Void, Token> {
        private final ProgressDialog progressDialog;

        public RegisterAsyncTask(ProgressDialog progressDialog) {
            this.progressDialog = progressDialog;

            progressDialog.setCancelable(true);
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    cancel(true);
                }
            });

        }

        protected void onPreExecute() {
            view.startProgressDialog("Logging in...");
        }

        protected Token doInBackground(Void... voids) {
            try {
                Token token = NetworkService.getRegisterCall(view.getUsername(), view.getPassword()).execute().body();
                Thread.sleep(3000);

                return token;
            } catch (Exception e) {
                return null;
            }
        }

        protected void onCancelled(Token token){
        }

        protected void onPostExecute(Token token) {
            view.stopProgressDialog();

            if (token != null) {
                NetworkService.cacheTokenAndUsername(token, view.getUsername());
                view.openContactsActivity();
            }
        }
    }

    class AuthAsyncTask extends AsyncTask<Void, Void, Token> {
        private final ProgressDialog progressDialog;

        public AuthAsyncTask(ProgressDialog progressDialog) {
            this.progressDialog = progressDialog;

            progressDialog.setCancelable(true);
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    cancel(true);
                }
            });

        }

        protected void onPreExecute() {
            view.startProgressDialog("Logging in...");
        }

        protected Token doInBackground(Void... voids) {
            try {
                Token token = NetworkService.getAuthCall(view.getUsername(), view.getPassword()).execute().body();
                Thread.sleep(3000);

                return token;
            } catch (Exception e) {
                return null;
            }
        }

        protected void onCancelled(Token token){
        }

        protected void onPostExecute(Token token) {
            view.stopProgressDialog();

            if (token != null) {
                NetworkService.cacheTokenAndUsername(token, view.getUsername());
                view.openContactsActivity();
            }
        }
    }

}