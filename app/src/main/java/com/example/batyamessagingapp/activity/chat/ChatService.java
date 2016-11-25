package com.example.batyamessagingapp.activity.chat;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.util.Pair;

import com.example.batyamessagingapp.activity.chat.adapter.ChatMessage;
import com.example.batyamessagingapp.activity.chat.adapter.ChatMessageAdapter;
import com.example.batyamessagingapp.model.NetworkService;
import com.example.batyamessagingapp.model.PreferencesService;
import com.example.batyamessagingapp.model.pojo.Message;
import com.example.batyamessagingapp.model.pojo.MessageArray;
import com.example.batyamessagingapp.model.pojo.PairMessageDialogId;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by Кашин on 15.11.2016.
 */

public class ChatService implements ChatPresenter {

    private final String mDialogId;
    private Context mContext;
    private ChatMessageAdapter mAdapter;
    private SendMessageAsyncTask mSendMessageAsyncTask;
    private GetMessagesAsyncTask mGetMessagesAsyncTask;

    private ChatView mView;


    ChatService(ChatView view, String dialogId) {
        mView = view;
        mContext = (Context)view;
        mDialogId = dialogId;
    }

    @Override
    public void onSendMessageButtonClick() {
        mSendMessageAsyncTask = new SendMessageAsyncTask(mView.getMessageString());
        mSendMessageAsyncTask.execute();
    }

    @Override
    public void onLoad(){
        mGetMessagesAsyncTask = new GetMessagesAsyncTask(100,0);
        mGetMessagesAsyncTask.execute();
    }

    private class GetMessagesAsyncTask
            extends AsyncTask<Void, Void, Pair<MessageArray,ErrorType>>{

        private final int limit;
        private final int offset;

        public GetMessagesAsyncTask(int limit, int offset){
            this.limit = limit;
            this.offset = offset;
        }

        protected Pair<MessageArray, ErrorType> doInBackground(Void... params) {
            try {
                Response<MessageArray> response = NetworkService
                        .getGetMessagesCall(mDialogId, limit, offset)
                        .execute();

                MessageArray answer = response.body();

                if (response.code() == 200 && answer != null) {
                    return new Pair<>(answer, ErrorType.NoError);
                } else {
                    return new Pair<>(null, ErrorType.NoAccess);
                }
            } catch (ConnectException | SocketTimeoutException e) {
                return new Pair<>(null, ErrorType.NoInternetConnection);
            } catch (IOException e) {
                return new Pair<>(null, ErrorType.NoAccess);
            }
        }

        protected void onPostExecute(Pair<MessageArray, ErrorType> resultPair) {
            //TODO

            ArrayList<PairMessageDialogId> pairList = resultPair.first.getMessages();

            ArrayList<ChatMessage> chatMessageList = new ArrayList<>();

            for (int i = 0; i < pairList.size(); ++i){
                PairMessageDialogId pair = pairList.get(i);
                Message message = pair.getMessage();
                String dialogId = pair.getDialogId();

                ChatMessage chatMessage = null;
                if (dialogId.equals(PreferencesService.getUsernameFromPreferences())){
                    chatMessage = new ChatMessage(message.getContent(),
                            ChatMessage.Direction.Outcoming);
                }
                else {
                    chatMessage = new ChatMessage(message.getContent(),
                            ChatMessage.Direction.Incoming);
                }

                chatMessageList.add(chatMessage);
            }

            if (chatMessageList.size() > 0){
                mView.addMessagesToAdapter(chatMessageList);
            }

        }
    }

    private class SendMessageAsyncTask extends AsyncTask<Void, Void, Pair<ResponseBody, ErrorType>> {

        private final String messageText;

        public SendMessageAsyncTask(String messageText) {
            this.messageText = messageText;
        }

        protected Pair<ResponseBody, ErrorType> doInBackground(Void... voids) {
            try {
                Response<ResponseBody> response = NetworkService
                        .getSendMessageCall(mDialogId, "text", messageText)
                        .execute();

                ResponseBody answer = response.body();

                if (response.code() == 200 && answer != null) {
                    return new Pair<>(answer, ErrorType.NoError);
                } else {
                    return new Pair<>(null, ErrorType.NoAccess);
                }
            } catch (ConnectException | SocketTimeoutException e) {
                return new Pair<>(null, ErrorType.NoInternetConnection);
            } catch (IOException e) {
                return new Pair<>(null, ErrorType.NoAccess);
            }
        }

        protected void onPostExecute(Pair<ResponseBody, ErrorType> resultPair) {
            if (resultPair.second == ErrorType.NoError) {
                ChatMessage chatMessage = new ChatMessage(messageText, ChatMessage.Direction.Outcoming);
                mView.addMessageToAdapter(chatMessage);
                mView.clearMessageEditText();
            } else if (resultPair.second == ErrorType.NoInternetConnection) {
                String message = "No internet connection. Unable to send messageText";
                mView.showToast(message);
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