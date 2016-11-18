package com.example.batyamessagingapp.activity.dialog;

import android.os.AsyncTask;

import com.example.batyamessagingapp.model.NetworkService;
import com.example.batyamessagingapp.model.pojo.APIAnswer;

import java.io.IOException;
import java.net.ConnectException;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by Кашин on 15.11.2016.
 */

public class DialogService implements DialogPresenter {
    private DialogView view;
    private String dialogId = "danildanil";
    private ErrorType errorType;
    private SendMessageAsyncTask sendMessageAsyncTask;

    @Override
    public void onSendMessageButtonClick() {
        sendMessageAsyncTask = new SendMessageAsyncTask();
        sendMessageAsyncTask.execute();
    }

    public DialogService(DialogView view) {
        this.view = view;
    }

    private class SendMessageAsyncTask extends AsyncTask<Void, Void, ResponseBody> {
        protected void onPreExecute() {
        }

        protected ResponseBody doInBackground(Void... voids) {
            try {
                Response<ResponseBody> response;
                response = NetworkService.getSendMessageCall(dialogId, "text", view.getMessageString()).execute();

                if (response.code() != 200) {
                    throw new IOException();
                }

                ResponseBody answer = response.body();
                errorType = ErrorType.NoError;
                return answer;
            } catch (ConnectException connectException) {
                errorType = ErrorType.NoInternetConnection;
                return null;
            } catch (IOException ioException) {
                errorType = ErrorType.NoAccess;
                return null;
            }
        }

        protected void onPostExecute(APIAnswer apiAnswer) {

            if (errorType == ErrorType.NoError) {

            } else if (errorType == ErrorType.NoInternetConnection) {
                String message = "No internet connection";
                view.showAlert(message, "Auth error");
            }

            errorType = ErrorType.NoError;
        }

    }


    private enum ErrorType {
        NoInternetConnection,
        NoAccess,
        NoError
    }
}
