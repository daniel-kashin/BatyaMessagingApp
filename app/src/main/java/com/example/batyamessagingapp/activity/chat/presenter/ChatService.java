package com.example.batyamessagingapp.activity.chat.presenter;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.util.Pair;

import com.example.batyamessagingapp.R;
import com.example.batyamessagingapp.activity.chat.adapter.MessagesDataModel;
import com.example.batyamessagingapp.activity.chat.view.ChatView;
import com.example.batyamessagingapp.activity.chat.adapter.ChatMessage;
import com.example.batyamessagingapp.activity.chat.adapter.ChatMessageAdapter;
import com.example.batyamessagingapp.lib.TimestampHelper;
import com.example.batyamessagingapp.model.NetworkService;
import com.example.batyamessagingapp.model.PreferencesService;
import com.example.batyamessagingapp.model.pojo.Message;
import com.example.batyamessagingapp.model.pojo.MessageArray;

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

    private final int SEND_MESSAGE_INTERVAL = 2000;
    private boolean initialized;
    private boolean running;
    private final String mDialogId;
    private Context mContext;
    private ChatMessageAdapter mAdapter;
    private SendMessageAsyncTask mSendMessageAsyncTask;
    private GetMessagesAsyncTask mGetMessagesAsyncTask;
    private Handler mHandler;
    private Runnable mGetMessageWithInterval;

    private ChatView mView;
    private MessagesDataModel mDataModel;

    public ChatService(ChatView view, String dialogId, MessagesDataModel dataModel) {
        mView = view;
        mContext = (Context) view;
        mDialogId = dialogId;
        mDataModel = dataModel;

        mHandler = new Handler();
        mGetMessageWithInterval = new Runnable() {
            @Override
            public void run() {
                mGetMessagesAsyncTask = new GetMessagesAsyncTask(20, 0, false);
                mGetMessagesAsyncTask.execute();
                mHandler.postDelayed(mGetMessageWithInterval, 2000);
            }
        };
    }

    @Override
    public void onRefreshIconClick() {
        if (!initialized) {
            onLoad();
        } else {
            startGetMessagesWithInterval();
        }
    }

    @Override
    public void startGetMessagesWithInterval(){
        if (!running) {
            running = true;
            mGetMessageWithInterval.run();
        }
    }

    @Override
    public void stopGetMessagesWithInterval(){
        running = false;
        mHandler.removeCallbacks(mGetMessageWithInterval);
    }

    @Override
    public void onSendMessageButtonClick() {
        mSendMessageAsyncTask = new SendMessageAsyncTask(mView.getMessageString());
        mSendMessageAsyncTask.execute();
        if (mDataModel.getSize() != 0){
            mView.hideNoMessagesTextView();
        }
    }

    @Override
    public void onLoad() {
        mGetMessagesAsyncTask = new GetMessagesAsyncTask(150, 0, true);
        mGetMessagesAsyncTask.execute();
    }

    private class GetMessagesAsyncTask
            extends AsyncTask<Void, Void, Pair<MessageArray, ErrorType>> {
        private final int limit;
        private final int offset;
        private final boolean initCall;

        public GetMessagesAsyncTask(int limit, int offset, boolean initCall) {
            this.limit = limit;
            this.offset = offset;
            this.initCall = initCall;
        }

        @Override
        protected void onPreExecute(){
            mView.hideRefreshButton();
            if (initCall) {
                mView.setToolbarLabelText(mContext.getString(R.string.loading));
            } else {
                mView.setToolbarLabelText(mDialogId);
            }
        }

        @Override
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

        @Override
        protected void onPostExecute(Pair<MessageArray, ErrorType> resultPair) {
            if (resultPair.first!=null && resultPair.second == ErrorType.NoError) {

                ArrayList<Message> messages = resultPair.first.getMessages();
                ArrayList<ChatMessage> outputMessages = new ArrayList<>();
                for (int i = 0; i < messages.size(); ++i) {
                    Message message = messages.get(i);
                    ChatMessage.Direction direction = null;
                    boolean isMy = message.getSender()
                            .equals(PreferencesService.getUsernameFromPreferences());
                    String id = message.getGuid();

                    if ((!isMy || initCall) && !mDataModel.hasItemWithId(id)) {
                        ChatMessage chatMessage = new ChatMessage(
                                message.getContent(),
                                TimestampHelper.formatTimestampWithoutDate(message.getTimestamp()),
                                isMy ? ChatMessage.Direction.Outcoming : ChatMessage.Direction.Incoming,
                                id
                        );
                        outputMessages.add(chatMessage);
                    }
                }

                if (outputMessages.size() > 0) {
                    mDataModel.addMessages(outputMessages);
                    if (initCall) mView.scrollRecyclerViewToLast();
                }

                if (mDataModel.getSize() != 0) {
                    mView.hideNoMessagesTextView();
                }

                if (initCall) {
                    initialized = true;
                    startGetMessagesWithInterval();
                }

                mView.setToolbarLabelText(mDialogId);
            } else if (resultPair.second == ErrorType.NoInternetConnection){
                stopGetMessagesWithInterval();
                mView.setToolbarLabelText(mContext.getString(R.string.no_internet_connection));
                mView.showRefreshButton();
            } else {
                stopGetMessagesWithInterval();
                mView.openAuthenticationActivity();
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
                ChatMessage message = new ChatMessage(
                        messageText,
                        //TimestampHelper.formatTimestampWithoutDate(resultPair.first.getTimestamp()),
                        "",
                        ChatMessage.Direction.Outcoming,
                        ""
                );

                mDataModel.addMessage(message);
                mView.clearMessageEditText();
                if (mDataModel.getSize()!=0){
                    mView.scrollRecyclerViewToLast();
                    mView.hideNoMessagesTextView();
                }
            } else if (resultPair.second == ErrorType.NoInternetConnection) {
                String message = "No internet connection. Unable to send messageText";
                mView.showToast(message);
            } else {
                stopGetMessagesWithInterval();
                mView.openAuthenticationActivity();
            }


        }
    }

    private enum ErrorType {
        NoInternetConnection,
        NoAccess,
        NoError
    }
}