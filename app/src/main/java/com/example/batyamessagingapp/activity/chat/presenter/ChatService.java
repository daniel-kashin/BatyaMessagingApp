package com.example.batyamessagingapp.activity.chat.presenter;

import android.content.Context;
import android.os.Handler;
import android.util.Pair;

import com.example.batyamessagingapp.activity.chat.adapter.MessagesDataModel;
import com.example.batyamessagingapp.activity.chat.view.ChatView;
import com.example.batyamessagingapp.activity.chat.adapter.ChatMessage;
import com.example.batyamessagingapp.activity.chat.adapter.ChatMessageAdapter;
import com.example.batyamessagingapp.model.BasicAsyncTask;
import com.example.batyamessagingapp.model.NetworkService;
import com.example.batyamessagingapp.model.PreferencesService;
import com.example.batyamessagingapp.model.pojo.Message;
import com.example.batyamessagingapp.model.pojo.MessageArray;
import com.example.batyamessagingapp.model.pojo.Timestamp;

import java.util.ArrayList;

/**
 * Created by Кашин on 15.11.2016.
 */

public class ChatService implements ChatPresenter {

  private final int GET_MESSAGES_INTERVAL = 2000;

  private boolean mInitialized;
  private boolean mRunning;
  private final String mDialogId;
  private final String mDialogName;
  private Context mContext;
  private ChatMessageAdapter mAdapter;
  private Handler mHandler;
  private Runnable mGetMessagesWithInterval;
  private String currentMessage;

  private ChatView mView;
  private MessagesDataModel mDataModel;

  public ChatService(ChatView view, String dialogId, String dialogName, MessagesDataModel dataModel) {
    mView = view;
    mContext = (Context) view;
    mDialogId = dialogId;
    mDialogName = dialogName;
    mDataModel = dataModel;

    mHandler = new Handler();
    mGetMessagesWithInterval = new Runnable() {
      @Override
      public void run() {
        BasicAsyncTask.AsyncTaskCompleteListener
            <Pair<MessageArray, BasicAsyncTask.ErrorType>> callback =
            new BasicAsyncTask.AsyncTaskCompleteListener<Pair<MessageArray, BasicAsyncTask.ErrorType>>() {
              @Override
              public void onTaskComplete(Pair<MessageArray, BasicAsyncTask.ErrorType> result) {
                if (result.second == BasicAsyncTask.ErrorType.NoError) {
                  ArrayList<Message> messages = result.first.getMessages();
                  mView.setCommonToolbarLabelText();
                } else if (result.second == BasicAsyncTask.ErrorType.NoInternetConnection) {
                  mView.setNoInternetToolbarLabelText();
                } else {
                  stopGetMessagesWithInterval();
                  mView.openDialogsActivity();
                }
              }
            };

        new BasicAsyncTask<MessageArray>(
            NetworkService.getGetMessagesCall(mDialogId, 20, 0),
            null,
            false,
            callback).execute();

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
    if (mView.getInputMessage() != null && !mView.getInputMessage().isEmpty()) {
      currentMessage = mView.getInputMessage();

      BasicAsyncTask.AsyncTaskCompleteListener
          <Pair<Timestamp, BasicAsyncTask.ErrorType>> callback =
          new BasicAsyncTask.AsyncTaskCompleteListener<Pair<Timestamp, BasicAsyncTask.ErrorType>>() {
            @Override
            public void onTaskComplete(Pair<Timestamp, BasicAsyncTask.ErrorType> result) {
              if (result.second == BasicAsyncTask.ErrorType.NoError) {
                ChatMessage message = new ChatMessage(
                    currentMessage,
                    PreferencesService.getIdFromPreferences(),
                    result.first.getTimestamp(),
                    ChatMessage.Direction.Outcoming,
                    "");
                mDataModel.addMessage(message);
                mView.clearMessageEditText();
                mView.scrollRecyclerViewToLast();
                mView.hideNoMessagesTextView();
              } else if (result.second == BasicAsyncTask.ErrorType.NoInternetConnection) {
                mView.setNoInternetToolbarLabelText();
              } else {
                stopGetMessagesWithInterval();
                mView.openDialogsActivity();
              }
            }
          };

      new BasicAsyncTask<Timestamp>(
          NetworkService.getSendMessageCall(mDialogId, "text", currentMessage),
          null,
          false,
          callback).execute();
    }
  }

  @Override
  public void onLoad() {
    if (!mInitialized) {
      mView.setLoadingToolbarLabelText();

      BasicAsyncTask.AsyncTaskCompleteListener
          <Pair<MessageArray, BasicAsyncTask.ErrorType>> callback =
          new BasicAsyncTask.AsyncTaskCompleteListener
              <Pair<MessageArray, BasicAsyncTask.ErrorType>>() {
            @Override
            public void onTaskComplete(Pair<MessageArray, BasicAsyncTask.ErrorType> result) {
              if (result.second == BasicAsyncTask.ErrorType.NoError) {
                ArrayList<Message> messages = result.first.getMessages();
                addMessagesToAdapter(messages, true);
                mInitialized = true;
                startGetMessagesWithInterval();
                mView.setCommonToolbarLabelText();
              } else if (result.second == BasicAsyncTask.ErrorType.NoInternetConnection) {
                mView.setNoInternetToolbarLabelText();
                onLoad();
              } else {
                stopGetMessagesWithInterval();
                mView.openDialogsActivity();
              }
            }
          };

      new BasicAsyncTask<MessageArray>(
          NetworkService.getGetMessagesCall(mDialogId, 200, 0),
          null,
          false,
          callback
      ).execute();
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

  private void addMessagesToAdapter(ArrayList<Message> messages, boolean initCall) {
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
        } else if (messageSender.equals(PreferencesService.getIdFromPreferences())) {
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
            contentToSet = "The group was created";
          } else if (contentLength > 8 && messageContent.substring(0, 7).equals("invited|")) {
            contentToSet = "User was invited: "
                + messageContent.substring(8, contentLength - 1);
          } else if (contentLength > 7 && messageContent.substring(0, 6).equals("kicked|")) {
            contentToSet = "User was kicked: "
                + messageContent.substring(7, contentLength - 1);
          } else if (contentLength > 5 && messageContent.substring(0, 4).equals("left|")) {
            contentToSet = "User left the group: "
                + messageContent.substring(5, contentLength - 1);
          }
        }

        if (contentToSet != null) {
          outputMessages.add(new ChatMessage(
              contentToSet, // text
              message.getSender(),
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
  }
}