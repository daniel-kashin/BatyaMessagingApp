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

    private final int GET_MESSAGES_INTERVAL = 2000;

    private boolean mInitialized;
    private boolean mRunning;
    private final String mDialogId;
    private Context mContext;
    private ChatMessageAdapter mAdapter;
    private SendMessageAsyncTask mSendMessageAsyncTask;
    private GetMessagesAsyncTask mGetMessagesAsyncTask;
    private Handler mHandler;
    private Runnable mGetMessagesWithInterval;

    private ChatView mView;
    private MessagesDataModel mDataModel;

    public ChatService(ChatView view, String dialogId, MessagesDataModel dataModel) {
        mView = view;
        mContext = (Context) view;
        mDialogId = dialogId;
        mDataModel = dataModel;

        mHandler = new Handler();
        mGetMessagesWithInterval = new Runnable() {
            @Override
            public void run() {
                mGetMessagesAsyncTask = new GetMessagesAsyncTask(20, 0, false);
                mGetMessagesAsyncTask.execute();
                mHandler.postDelayed(mGetMessagesWithInterval, GET_MESSAGES_INTERVAL);
            }
        };
    }

    @Override
    public void onRefreshIconClick() {
        onLoad();
    }

    @Override
    public boolean initialized(){
        return mInitialized;
    }

    @Override
    public void onSendMessageButtonClick() {
        mSendMessageAsyncTask = new SendMessageAsyncTask(mView.getMessage());
        mSendMessageAsyncTask.execute();
        if (mDataModel.getSize() != 0) {
            mView.hideNoMessagesTextView();
        }
    }

    @Override
    public void onLoad() {
        if (!mInitialized) {
            mGetMessagesAsyncTask = new GetMessagesAsyncTask(150, 0, true);
            mGetMessagesAsyncTask.execute();
        } else {
            startGetMessagesWithInterval();
        }
    }

    @Override
    public void onPause() {
        stopGetMessagesWithInterval();
    }

    private void startGetMessagesWithInterval() {
        if (!mRunning) {
            mRunning = true;
            mGetMessagesWithInterval.run();
        }
    }

    private void stopGetMessagesWithInterval() {
        mRunning = false;
        mHandler.removeCallbacks(mGetMessagesWithInterval);
    }

    private class GetMessagesAsyncTask
            extends AsyncTask<Void, Void, Pair<MessageArray, ErrorType>> {
        private final int limit;
        private final int offset;
        private final boolean initCall;

        private GetMessagesAsyncTask(int limit, int offset, boolean initCall) {
            this.limit = limit;
            this.offset = offset;
            this.initCall = initCall;
        }

        @Override
        protected void onPreExecute() {
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
            if (resultPair.first != null && resultPair.second == ErrorType.NoError) {

                ArrayList<Message> messages = resultPair.first.getMessages();
                ArrayList<ChatMessage> outputMessages = new ArrayList<>();

                for (int i = 0; i < messages.size(); ++i) {
                    Message message = messages.get(i);
                    boolean isMy = message.getSender()
                            .equals(PreferencesService.getUsernameFromPreferences());
                    String id = message.getGuid();

                    if ((!isMy || initCall) && !mDataModel.hasItemWithId(id)) {
                        ChatMessage chatMessage = new ChatMessage(
                                message.getContent(), // text
                                TimestampHelper.formatTimestampWithoutDate(message.getTimestamp()), // date
                                isMy ? ChatMessage.Direction.Outcoming : ChatMessage.Direction.Incoming, // direction
                                id // guid
                        );
                        outputMessages.add(chatMessage);
                    }
                }

                if (outputMessages.size() > 0) {
                    mDataModel.addMessages(outputMessages);
                    if (initCall) mView.scrollRecyclerViewToLast();
                    mView.hideNoMessagesTextView();
                }

                if (initCall) {
                    mInitialized = true;
                    startGetMessagesWithInterval();
                }

                mView.setToolbarLabelText(mDialogId);

            } else if (resultPair.second == ErrorType.NoInternetConnection) {
                stopGetMessagesWithInterval();
                mView.setToolbarLabelText(mContext.getString(R.string.no_internet_connection));
                mView.showRefreshIcon();
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
                mView.scrollRecyclerViewToLast();
                mView.hideNoMessagesTextView();
            } else if (resultPair.second == ErrorType.NoInternetConnection) {
                stopGetMessagesWithInterval();
                mView.setToolbarLabelText(mContext.getString(R.string.no_internet_connection));
                mView.showRefreshIcon();
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