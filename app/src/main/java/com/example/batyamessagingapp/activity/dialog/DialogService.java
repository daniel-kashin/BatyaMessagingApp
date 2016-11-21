package com.example.batyamessagingapp.activity.dialog;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.batyamessagingapp.activity.dialog.recycler_view.Message;
import com.example.batyamessagingapp.activity.dialog.recycler_view.MessageAdapter;
import com.example.batyamessagingapp.model.NetworkService;
import com.example.batyamessagingapp.model.pojo.APIAnswer;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by Кашин on 15.11.2016.
 */

public class DialogService implements DialogPresenter {

    private String dialogId = "danildanil";
    private DialogView view;
    private Context context;
    private MessageAdapter adapter;
    private SendMessageAsyncTask sendMessageAsyncTask;

    DialogService(DialogView view) {
        this.view = view;
        this.context = (Context)view;
    }

    @Override
    public void onSendMessageButtonClick() {
        sendMessageAsyncTask = new SendMessageAsyncTask(view.getMessageString());
        sendMessageAsyncTask.execute();
    }


    private class SendMessageAsyncTask extends AsyncTask<Void, Void, Pair<ResponseBody, ErrorType>> {

        private final String message;

        public SendMessageAsyncTask(String message) {
            this.message = message;
        }

        protected Pair<ResponseBody, ErrorType> doInBackground(Void... voids) {
            try {
                Response<ResponseBody> response = NetworkService
                        .getSendMessageCall(dialogId, "text", message)
                        .execute();

                ResponseBody answer = response.body();

                if (response.code() == 200 && answer != null) {
                    return new Pair<>(answer, ErrorType.NoError);
                } else {
                    return new Pair<>(null, ErrorType.NoAccess);
                }
            } catch (ConnectException connectException) {
                return new Pair<>(null, ErrorType.NoInternetConnection);
            } catch (IOException ioException) {
                return new Pair<>(null, ErrorType.NoAccess);
            }
        }

        protected void onPostExecute(Pair<ResponseBody, ErrorType> resultPair) {
            if (resultPair.second == ErrorType.NoError) {
                view.addMessageToAdapter(message, Message.Direction.Outcoming);
            } else if (resultPair.second == ErrorType.NoInternetConnection) {
                String message = "No internet connection. Unable to send message";
                view.showToast(message);
            } else {
                //TODO
            }
        }
    }

    private enum ErrorType {
        NoInternetConnection,
        NoAccess,
        NoError
    }


}
