package com.example.batyamessagingapp.activity.chat.presenter;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.util.Pair;

import com.example.batyamessagingapp.activity.chat.adapter.MessagesDataModel;
import com.example.batyamessagingapp.activity.chat.view.ChatView;
import com.example.batyamessagingapp.activity.chat.adapter.ChatMessage;
import com.example.batyamessagingapp.activity.chat.adapter.ChatMessageAdapter;
import com.example.batyamessagingapp.lib.TimestampHelper;
import com.example.batyamessagingapp.model.NetworkService;
import com.example.batyamessagingapp.model.PreferencesService;
import com.example.batyamessagingapp.model.pojo.Message;
import com.example.batyamessagingapp.model.pojo.MessageArray;
import com.example.batyamessagingapp.model.pojo.Timestamp;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

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
    public boolean initialized() {
        return mInitialized;
    }

    @Override
    public void onSendMessageButtonClick() {
        mSendMessageAsyncTask = new SendMessageAsyncTask(mView.getMessage());
        mSendMessageAsyncTask.execute();
    }

    @Override
    public void onLoad() {
        if (!mInitialized) {
            mView.setLoadingToolbarLabelText();
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
                    String messageGuid = message.getGuid();

                    if (!mDataModel.hasItemWithId(messageGuid)) {
                        String messageSender = message.getSender();
                        String messageContent = message.getContent();

                        ChatMessage.Direction direction;
                        if (messageSender.equals("")) {
                            direction = ChatMessage.Direction.System;
                        } else if (messageSender.equals(PreferencesService.getUsernameFromPreferences())) {
                            direction = ChatMessage.Direction.Outcoming;
                        } else {
                            direction = ChatMessage.Direction.Incoming;
                        }

                        String contentToSet = null;
                        if (direction == ChatMessage.Direction.Outcoming && initCall
                                || direction == ChatMessage.Direction.Incoming) {
                            contentToSet = messageContent;
                        } else if (direction == ChatMessage.Direction.System) {
                            int contentLength = messageContent.length();
                            if (messageContent.equals("created")) {
                                contentToSet = "The group was created.";
                            } else if (contentLength > 8 && messageContent.substring(0, 7).equals("invited|")) {
                                contentToSet = "User was invited: "
                                        + messageContent.substring(8, contentLength - 1);
                            } else if (contentLength > 7 && messageContent.substring(0, 6).equals("kicked|")) {
                                contentToSet = "User was kicked: "
                                        + messageContent.substring(7, contentLength - 1);
                            } else if (contentLength > 5 && messageContent.substring(0, 4).equals("left|")){
                                contentToSet = "User left the group: "
                                        + messageContent.substring(5, contentLength - 1);
                            }
                        }

                        if (contentToSet != null) {
                            outputMessages.add(new ChatMessage(
                                    contentToSet, // text
                                    message.getTimestamp(), //timestamp
                                    direction, // direction
                                    messageGuid // guid
                            ));
                        }
                    }
                }

                if (outputMessages.size() > 0) {
                    mDataModel.addMessagesToEnd(outputMessages);
                    if (initCall) mView.scrollRecyclerViewToLast();
                    mView.hideNoMessagesTextView();
                }

                if (initCall) {
                    mInitialized = true;
                    startGetMessagesWithInterval();
                }

                mView.setCommonToolbarLabelText();
            } else if (resultPair.second == ErrorType.NoInternetConnection) {
                mView.setNoInternetToolbarLabelText();
                if (initCall) onLoad();
            } else {
                stopGetMessagesWithInterval();
                mView.openDialogsActivity();
            }
        }
    }

    private class SendMessageAsyncTask extends AsyncTask<Void, Void, Pair<Timestamp, ErrorType>> {

        private final String messageText;

        public SendMessageAsyncTask(String messageText) {
            this.messageText = messageText;
        }

        protected Pair<Timestamp, ErrorType> doInBackground(Void... voids) {
            try {
                Response<Timestamp> response = NetworkService
                        .getSendMessageCall(mDialogId, "text", messageText)
                        .execute();

                Timestamp answer = response.body();

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

        protected void onPostExecute(Pair<Timestamp, ErrorType> resultPair) {
            if (resultPair.second == ErrorType.NoError) {
                ChatMessage message = new ChatMessage(
                        messageText,
                        resultPair.first.getTimestamp(),
                        ChatMessage.Direction.Outcoming,
                        ""
                );

                mDataModel.addMessage(message);
                mView.clearMessageEditText();
                mView.scrollRecyclerViewToLast();
                mView.hideNoMessagesTextView();
            } else if (resultPair.second == ErrorType.NoInternetConnection) {
                mView.setNoInternetToolbarLabelText();
            } else {
                stopGetMessagesWithInterval();
                mView.openDialogsActivity();
            }
        }
    }


    private enum ErrorType {
        NoInternetConnection,
        NoAccess,
        NoError
    }
}