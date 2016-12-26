package com.danielkashin.batyamessagingapp.activity.chat.presenter;

import android.content.Context;
import android.os.Handler;
import android.util.Pair;
import android.widget.Toast;

import com.danielkashin.batyamessagingapp.activity.chat.adapter.ChatMessageDataModel;
import com.danielkashin.batyamessagingapp.activity.chat.view.ChatView;
import com.danielkashin.batyamessagingapp.activity.chat.adapter.ChatMessage;
import com.danielkashin.batyamessagingapp.activity.chat.adapter.ChatMessageAdapter;
import com.danielkashin.batyamessagingapp.lib.TimestampHelper;
import com.danielkashin.batyamessagingapp.model.BasicAsyncTask;
import com.danielkashin.batyamessagingapp.model.APIService;
import com.danielkashin.batyamessagingapp.model.PreferencesService;
import com.danielkashin.batyamessagingapp.model.pojo.DialogName;
import com.danielkashin.batyamessagingapp.model.pojo.GroupUsers;
import com.danielkashin.batyamessagingapp.model.pojo.Message;
import com.danielkashin.batyamessagingapp.model.pojo.MessageArray;
import com.danielkashin.batyamessagingapp.model.pojo.Timestamp;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by Кашин on 15.11.2016.
 */

public class ChatService implements ChatPresenter {

  private final int GET_MESSAGES_INTERVAL = 2000;

  private boolean mInitializedDialogs;
  private boolean mInitializedProperties;
  private boolean mRunning;
  private final String mDialogId;
  private final String mDialogName;
  private Context mContext;
  private ChatMessageAdapter mAdapter;
  private Handler mHandler;
  private Runnable mGetMessagesWithInterval;
  private String currentMessage;

  private ChatView mView;
  private ChatMessageDataModel mDataModel;

  public ChatService(ChatView view, String dialogId, String dialogName, ChatMessageDataModel dataModel) {
    mView = view;
    mContext = (Context) view;
    mDialogId = dialogId;
    mDialogName = dialogName;
    mDataModel = dataModel;

    // set looper to find new messages
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
                  addMessagesToAdapter(messages, false);
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
            APIService.getGetMessagesCall(mDialogId, 20, 0),
            null,
            false,
            callback).execute();

        mHandler.postDelayed(mGetMessagesWithInterval, GET_MESSAGES_INTERVAL);
      }
    };
  }

  @Override
  public void onSendMessageButtonClick() {
    if (mView.getInputMessage() != null && !mView.getInputMessage().isEmpty()) {
      currentMessage = mView.getInputMessage();
      mView.clearMessageEditText();

      BasicAsyncTask.AsyncTaskCompleteListener
          <Pair<Timestamp, BasicAsyncTask.ErrorType>> callback =
          new BasicAsyncTask.AsyncTaskCompleteListener
              <Pair<Timestamp, BasicAsyncTask.ErrorType>>() {
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
              } else if (result.second == BasicAsyncTask.ErrorType.NoInternetConnection) {
                mView.setNoInternetToolbarLabelText();
              } else {
                stopGetMessagesWithInterval();
                mView.openDialogsActivity();
              }
            }
          };

      new BasicAsyncTask<Timestamp>(
          APIService.getSendMessageCall(mDialogId, "text", currentMessage),
          null,
          false,
          callback).execute();
    }
  }

  @Override
  public void onLoad() {
    try {
      if (!mInitializedProperties || !mInitializedDialogs) {
        if ((mInitializedProperties || onInitializeProperties(mView.isGroup())) && !mInitializedDialogs) {
          initializeDialogs();
        } else {
          onLoad();
        }
      } else {
        startGetMessagesWithInterval();
      }
    } catch (IllegalAccessException e) {
      Toast.makeText(mContext, "You does not have permissions to read this dialog", Toast.LENGTH_LONG)
          .show();
      mView.openDialogsActivity();
    }
  }

  private void initializeDialogs() {
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
              mInitializedDialogs = true;
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
        APIService.getGetMessagesCall(mDialogId, 200, 0),
        null,
        false,
        callback
    ).execute();
  }

  @Override
  public void onPause() {
    stopGetMessagesWithInterval();
    mInitializedProperties = false;
  }

  @Override
  public boolean initialized() {
    return mInitializedDialogs;
  }

  public boolean onInitializeProperties(boolean isGroup) throws IllegalAccessException {
    boolean success;

    boolean isGroupOriginator = false;
    int groupCount = -1;

    if (isGroup) {
      try {
        Pair<GroupUsers, BasicAsyncTask.ErrorType> resultUsers =
            new BasicAsyncTask<GroupUsers>(
                APIService.getGetGroupUsersCall(mDialogId),
                null,
                false,
                null
            ).execute().get();

        Pair<DialogName, BasicAsyncTask.ErrorType> resultName =
            new BasicAsyncTask<DialogName>(
                APIService.getGetDialogNameCall(mDialogId),
                null,
                false,
                null
            ).execute().get();

        if (resultUsers.second == BasicAsyncTask.ErrorType.NoError &&
            resultName.second == BasicAsyncTask.ErrorType.NoError) {
          if (resultUsers.first.getOriginatorId() != null &&
              resultUsers.first.getOriginatorId().equals(PreferencesService.getIdFromPreferences())) {
            isGroupOriginator = true;
          }
          if (resultUsers.first.getUsers() != null) {
            groupCount = resultUsers.first.getUsers().size() + 1;
          } else {
            groupCount = 1;
          }
          mView.setProperties(isGroupOriginator, groupCount, resultName.first.getDialogName());
          mView.setToolbarSmallLabelText();
          mView.setCommonToolbarLabelText();
          success = true;
        } else if (resultUsers.second == BasicAsyncTask.ErrorType.NoInternetConnection) {
          mView.setNoInternetToolbarLabelText();
          success = false;
        } else {
          throw new IllegalAccessException();
        }
      } catch (ExecutionException | InterruptedException e) {
        throw new IllegalAccessException();
      }
    } else {
      mView.setProperties(false, -1, null);
      mView.setToolbarSmallLabelText();
      success = true;
    }

    mInitializedProperties = success;

    return success;
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

      if (!mDataModel.hasItemWithId(message.getGuid())) {
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
            contentToSet = "The group was created"
                + " at "
                + TimestampHelper.formatTimestampToTime(message.getTimestamp());
          } else if (contentLength > 8 && messageContent.substring(0, 8).equals("invited|")) {
            contentToSet = "Invited: "
                + messageContent.substring(8, contentLength)
                + " at "
                + TimestampHelper.formatTimestampToTime(message.getTimestamp());
          } else if (contentLength > 7 && messageContent.substring(0, 7).equals("kicked|")) {
            contentToSet = "Kicked: "
                + messageContent.substring(7, contentLength)
                + " at "
                + TimestampHelper.formatTimestampToTime(message.getTimestamp());
          } else if (contentLength > 5 && messageContent.substring(0, 5).equals("left|")) {
            contentToSet = "Left: "
                + messageContent.substring(5, contentLength)
                + " at "
                + TimestampHelper.formatTimestampToTime(message.getTimestamp());
          }
        }

        if (contentToSet != null) {
          outputMessages.add(new ChatMessage(
              contentToSet,
              message.getSender(),
              message.getTimestamp(),
              direction,
              message.getGuid()
          ));
        }
      }
    }

    if (outputMessages.size() > 0) {
      mDataModel.addMessagesToEnd(outputMessages);
      if (initCall) mView.scrollRecyclerViewToLast();
    }
  }
}