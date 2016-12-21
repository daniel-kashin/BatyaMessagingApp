package com.example.batyamessagingapp.model;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;

import com.example.batyamessagingapp.R;
import com.example.batyamessagingapp.model.pojo.ConferenceId;
import com.example.batyamessagingapp.model.pojo.DialogName;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutionException;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by Кашин on 17.12.2016.
 */

public class NetworkExecutor {


    //-----------------------------------High-level methods-----------------------------------------

    public static String getDialogNameFromId(String dialogId)
            throws InterruptedException, ExecutionException, IOException {
        Pair<DialogName, NetworkExecutor.ErrorType> pairNameError =
                new GetDialogNameFromIdTask(dialogId).execute().get();

        if (pairNameError.second == NetworkExecutor.ErrorType.NoError
                && pairNameError.first!= null){
            return pairNameError.first.getDialogName();
        } else if (pairNameError.second == NetworkExecutor.ErrorType.NoInternetConnection){
            throw new ConnectException();
        } else {
            throw new IOException();
        }
    }

    public static void changePassword(String password, String newPassword,
                                      Context context, AsyncTaskCompleteListener<ErrorType> callback) {
        new ChangePasswordTask(password, newPassword, context, callback).execute();
    }

    public static void changeUsername(String newUsername, String dialogId, Context context)
            throws InterruptedException, ExecutionException, IOException {
        ErrorType error = new ChangeUsernameTask(newUsername, dialogId, context).execute().get();

        if (error == ErrorType.NoInternetConnection){
            throw new ConnectException();
        } else if (error == ErrorType.NoAccess){
            throw new IOException();
        }
    }

    public static String getNewConferenceId(Context context)
            throws  InterruptedException, ExecutionException, IOException {

        Pair<String,ErrorType> result = new GetNewConferenceIdTask(context).execute().get();

        if (result.second == ErrorType.NoInternetConnection){
            throw  new ConnectException();
        } else if (result.second == ErrorType.NoAccess){
            throw new IOException();
        } else {
            return result.first;
        }
    }


    //-----------------------------------Low-level Async Tasks--------------------------------------

    private static class GetNewConferenceIdTask extends AsyncTask<Void, Void, Pair<String, ErrorType>> {

        private ProgressDialog progressDialog;
        private Context context;

        private GetNewConferenceIdTask(Context context){
            this.context = context;
        }

        @Override
        protected void onPreExecute(){
            this.progressDialog = ProgressDialog.show(context, ""
                    , context.getString(R.string.loading),true,false);
        }

        @Override
        protected Pair<String, ErrorType> doInBackground(Void... params) {
            try {
                Response<ConferenceId> response = NetworkService
                        .getGetNewConferenceIdCall()
                        .execute();

                ConferenceId conferenceId = response.body();

                if (response.code() == 200 && conferenceId != null) {
                    return new Pair<>(conferenceId.toString(),ErrorType.NoError);
                } else {
                    return new Pair<>(null, ErrorType.NoAccess);
                }
            } catch (ConnectException | SocketTimeoutException e) {
                return new Pair<>(null, ErrorType.NoInternetConnection);
            } catch (IOException e) {
                return new Pair<>(null, ErrorType.NoAccess);
            }
        }

        @Override
        protected void onPostExecute(Pair<String,ErrorType> pair){
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }


    private static class ChangeUsernameTask extends AsyncTask<Void, Void, ErrorType> {

        private final String newUsername;
        private final String dialogId;
        private ProgressDialog progressDialog;
        private Context context;

        private ChangeUsernameTask(String newUsername, String dialogId, Context context) {
            this.newUsername = newUsername;
            this.dialogId = dialogId;
            this.context = context;
        }

        @Override
        protected void onPreExecute(){
            this.progressDialog = ProgressDialog.show(context, ""
                    , context.getString(R.string.loading),true,false);
        }

        @Override
        protected ErrorType doInBackground(Void... params) {
            try {
                Response<ResponseBody> response = NetworkService
                        .getChangeUsernameCall(newUsername, dialogId)
                        .execute();

                ResponseBody responseBody = response.body();

                if (response.code() == 200) {
                    return ErrorType.NoError;
                } else {
                    return ErrorType.NoAccess;
                }
            } catch (ConnectException | SocketTimeoutException e) {
                return ErrorType.NoInternetConnection;
            } catch (IOException e) {
                return ErrorType.NoAccess;
            }
        }

        @Override
        protected void onPostExecute(ErrorType error){
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }

    private static class ChangePasswordTask extends AsyncTask<Void, Void, ErrorType> {

        private AsyncTaskCompleteListener<ErrorType> callback;

        private final String password;
        private final String newPassword;
        private ProgressDialog progressDialog;
        private Context context;


        private ChangePasswordTask(String password, String newPassword,
                                   Context context, AsyncTaskCompleteListener<ErrorType> callback) {
            this.password = password;
            this.newPassword = newPassword;
            this.context = context;
            this.callback = callback;
        }

        @Override
        protected void onPreExecute(){
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage(context.getString(R.string.loading));
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(true);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();
        }

        @Override
        protected ErrorType doInBackground(Void... params) {
            try {
                Response<ResponseBody> response = NetworkService
                        .getChangePasswordCall(password, newPassword)
                        .execute();

                ResponseBody responseBody = response.body();

                if (response.code() == 200) {
                    return ErrorType.NoError;
                } else {
                    return ErrorType.NoAccess;
                }
            } catch (ConnectException | SocketTimeoutException e) {
                return ErrorType.NoInternetConnection;
            } catch (IOException e) {
                return ErrorType.NoAccess;
            }
        }

        @Override
        protected void onPostExecute(ErrorType error) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            callback.onTaskComplete(error);
        }
    }

    private static class GetDialogNameFromIdTask extends AsyncTask<Void, Void, Pair<DialogName, ErrorType>> {

        private final String dialogId;

        private GetDialogNameFromIdTask(String dialogId) {
            this.dialogId = dialogId;
        }

        @Override
        protected Pair<DialogName, ErrorType> doInBackground(Void... params) {
            try {
                Response<DialogName> response = NetworkService
                        .getGetDialogNameCall(dialogId)
                        .execute();

                DialogName dialogName = response.body();

                if (response.code() == 200 && dialogName != null) {
                    return new Pair<>(dialogName, ErrorType.NoError);
                } else {
                    return new Pair<>(null, ErrorType.NoAccess);
                }
            } catch (ConnectException | SocketTimeoutException e) {
                return new Pair<>(null, ErrorType.NoInternetConnection);
            } catch (IOException e) {
                return new Pair<>(null, ErrorType.NoAccess);
            }
        }
    }

    public enum ErrorType {
        NoInternetConnection,
        NoAccess,
        NoError
    }

    public interface AsyncTaskCompleteListener<T> {
        public void onTaskComplete(T result);
    }
}
