package com.example.batyamessagingapp.activity.main.fragment_dialogs.presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;

import com.example.batyamessagingapp.activity.main.fragment_dialogs.adapter.Dialog;
import com.example.batyamessagingapp.activity.main.fragment_dialogs.adapter.DialogsDataModel;
import com.example.batyamessagingapp.activity.main.fragment_dialogs.adapter.OnDialogClickListener;
import com.example.batyamessagingapp.activity.main.fragment_dialogs.view.DialogsView;
import com.example.batyamessagingapp.lib.CircleBitmapFactory;
import com.example.batyamessagingapp.model.BasicAsyncTask;
import com.example.batyamessagingapp.model.NetworkService;
import com.example.batyamessagingapp.model.PreferencesService;
import com.example.batyamessagingapp.model.pojo.DialogArray;
import com.example.batyamessagingapp.model.pojo.DialogName;
import com.example.batyamessagingapp.model.pojo.Message;
import com.example.batyamessagingapp.model.pojo.PairLastMessageDialogId;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by Кашин on 29.10.2016.
 */

public class DialogsService implements DialogsPresenter {

  private int MAX_DIALOGS_PER_CALL = 25;
  private int GET_DIALOGS_INTERVAL = 3000;
  private boolean mInitialized;
  private boolean mRunning;
  private Context mContext;
  private Handler mHandler;
  private Runnable mGetDialogsWithInterval;

  private DialogsView mView;
  private DialogsDataModel mDataModel;

  public DialogsService(DialogsView view, Context context, DialogsDataModel dataModel) {
    mView = view;
    mContext = context;
    mDataModel = dataModel;

    mDataModel.setOnDialogClickListener(new OnDialogClickListener() {
      @Override
      public void onItemClick(RecyclerView.Adapter adapter, int position) {
        try {
          String dialogId = ((DialogsDataModel) adapter).getDialogIdByPosition(position);
          String dialogName = ((DialogsDataModel) adapter).getDialogNameByPosition(position);
          mView.openChatActivity(dialogId, dialogName);
        } catch (IndexOutOfBoundsException e) {
          // do nothing club
        }
      }
    });

    mHandler = new Handler();
    mGetDialogsWithInterval = new Runnable() {
      @Override
      public void run() {
        startGetDialogsAsyncTask(0,false);
        mHandler.postDelayed(mGetDialogsWithInterval, GET_DIALOGS_INTERVAL);
      }
    };
  }

  private void startGetDialogsWithInterval() {
    if (!mRunning) {
      mRunning = true;
      mGetDialogsWithInterval.run();
    }
  }

  private void stopGetDialogsWithInterval() {
    mRunning = false;
    mHandler.removeCallbacks(mGetDialogsWithInterval);
  }

  @Override
  public void onLoad() {
    if (!mInitialized) {
      mView.showProgressBar();
      mView.hideNoDialogsTextView();
      startGetDialogsAsyncTask(0, true);
    } else {
      startGetDialogsWithInterval();
    }
  }

  @Override
  public void onPause() {
    stopGetDialogsWithInterval();
  }

  //added this method to enable recursion
  private void startGetDialogsAsyncTask(final int currentOffset, final boolean initCall) {

    BasicAsyncTask.AsyncTaskCompleteListener
        <Pair<DialogArray, BasicAsyncTask.ErrorType>> callback =
        new BasicAsyncTask.AsyncTaskCompleteListener<Pair<DialogArray, BasicAsyncTask.ErrorType>>() {
          @Override
          public void onTaskComplete(Pair<DialogArray, BasicAsyncTask.ErrorType> result) {
            handleResult(result, initCall, currentOffset);
          }
        };

    new BasicAsyncTask<DialogArray>(
        NetworkService.getGetDialogsCall(currentOffset),
        null,
        false,
        callback
    ).execute();
  }

  private void handleResult(Pair<DialogArray, BasicAsyncTask.ErrorType> result,
                            boolean initCall, int offset) {
    try {
      if (result.second == BasicAsyncTask.ErrorType.NoError) {
        mView.setCommonToolbarLabelText();

        addDialogArrayToAdapter(result.first.getDialogs());

        if (mDataModel.getSize() != 0) {
          mView.hideNoDialogsTextView();
        } else {
          mView.showNoDialogsTextView();
        }

        if (initCall) {
          mInitialized = true;
          startGetDialogsWithInterval();
        }

        if (result.first.getDialogs().size() >= MAX_DIALOGS_PER_CALL) {
          startGetDialogsAsyncTask(offset + 25, false);
        }
      } else if (result.second == BasicAsyncTask.ErrorType.NoInternetConnection) {
        mView.setNoInternetToolbarLabelText();
        if (initCall) onLoad();
      } else {
        stopGetDialogsWithInterval();
        mView.openAuthenticationActivity();
      }
    } catch (InterruptedException | ExecutionException | IOException e) {
      mView.openAuthenticationActivity();
    } finally {
      if (initCall) mView.hideProgressBar();
    }
  }

  private void addDialogArrayToAdapter(ArrayList<PairLastMessageDialogId> dialogs)
      throws InterruptedException, ExecutionException, IOException {
    if (dialogs.size() != 0 && !mDataModel.noChanges(dialogs)) {
      for (int i = dialogs.size() - 1; i >= 0; --i) {
        // get dialog data
        PairLastMessageDialogId pair = dialogs.get(i);
        final String dialogId = pair.getDialogId();
        Message message = pair.getMessage();

        // try to find the dialog in data model
        int dialogPosition = mDataModel
            .findDialogPositionById(dialogId);

        // whether last message is mine
        boolean isMe = message.getSender()
            .equals(PreferencesService.getIdFromPreferences());

        if (dialogPosition == -1) { // couldn`t find the dialog, add it
          Pair<DialogName, BasicAsyncTask.ErrorType> result = new BasicAsyncTask<DialogName>(
              NetworkService.getGetDialogNameCall(dialogId),
              null, false, null).execute().get();

          if (result.second == BasicAsyncTask.ErrorType.NoError) {
            int color = CircleBitmapFactory.getMaterialColor(dialogId.hashCode());
            String firstLetter = CircleBitmapFactory.getFirstLetter(result.first.getDialogName());
            Bitmap bitmap = CircleBitmapFactory
                .generateCircleBitmap(mContext, color, 55, firstLetter);

            // create and add dialog
            Dialog dialog = new Dialog(
                bitmap,
                dialogId,
                result.first.getDialogName(),
                (isMe ? "you: " : "") + message.getContent(),
                message.getTimestamp());
            mDataModel.addDialog(dialog);
          } else if (result.second == BasicAsyncTask.ErrorType.NoInternetConnection){
            mView.setNoInternetToolbarLabelText();
          } else {
            stopGetDialogsWithInterval();
            mView.openAuthenticationActivity();
          }
        } else { // dialog exists
          // refresh the data
            mDataModel.setDialogMessageAndTimestamp(
                dialogPosition,
                (isMe ? "you: " : "") + message.getContent(),
                message.getTimestamp());
        }
      }//for

      mDataModel.refresh();
    }
  }
}

