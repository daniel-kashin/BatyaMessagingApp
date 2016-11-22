package com.example.batyamessagingapp.activity.dialog;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.util.Pair;

import com.example.batyamessagingapp.activity.dialog.recycler_view.Message;
import com.example.batyamessagingapp.activity.dialog.recycler_view.MessageAdapter;
import com.example.batyamessagingapp.model.NetworkService;
import com.example.batyamessagingapp.model.pojo.PojoMessage;
import com.example.batyamessagingapp.model.pojo.PojoMessageArray;

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
    private GetMessageAsyncTask getMessageAsyncTask;

    DialogService(DialogView view) {
        this.view = view;
        this.context = (Context)view;
    }

    @Override
    public void onSendMessageButtonClick() {
        sendMessageAsyncTask = new SendMessageAsyncTask(view.getMessageString());
        sendMessageAsyncTask.execute();
    }

    @Override
    public void onLoad(){
        getMessageAsyncTask = new GetMessageAsyncTask("danildanil",100,0);
        getMessageAsyncTask.execute();
    }


    private class GetMessageAsyncTask
            extends AsyncTask<Void, Void, Pair<PojoMessageArray,ErrorType>>{

        private final String dialogId;
        private final int limit;
        private final int offset;

        public GetMessageAsyncTask(String dialogId, int limit, int offset){
            this.dialogId = dialogId;
            this.limit = limit;
            this.offset = offset;
        }

        protected Pair<PojoMessageArray, ErrorType> doInBackground(Void... params) {
            try {
                Response<PojoMessageArray> response = NetworkService
                        .getGetMessageCall(dialogId, limit, offset)
                        .execute();

                PojoMessageArray answer = response.body();

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

        protected void onPostExecute(Pair<ArrayList<PojoMessage>, ErrorType> resultPair) {

        }
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
                view.clearMessageEditText();
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
