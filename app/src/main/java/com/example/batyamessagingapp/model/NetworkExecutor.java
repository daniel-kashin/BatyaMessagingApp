package com.example.batyamessagingapp.model;

import android.os.AsyncTask;
import android.support.v4.util.Pair;

import com.example.batyamessagingapp.model.pojo.DialogName;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutionException;

import retrofit2.Response;

/**
 * Created by Кашин on 17.12.2016.
 */

public class NetworkExecutor {

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

    public static class GetDialogNameFromIdTask extends AsyncTask<Void, Void, Pair<DialogName, ErrorType>> {

        private final String dialogId;

        public GetDialogNameFromIdTask(String dialogId) {
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
}
