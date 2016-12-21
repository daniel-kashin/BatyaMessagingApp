package com.example.batyamessagingapp.activity.main.fragment_dialogs.presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;

import com.example.batyamessagingapp.activity.main.fragment_dialogs.adapter.Dialog;
import com.example.batyamessagingapp.activity.main.fragment_dialogs.adapter.DialogsDataModel;
import com.example.batyamessagingapp.activity.main.fragment_dialogs.adapter.OnDialogClickListener;
import com.example.batyamessagingapp.activity.main.fragment_dialogs.view.DialogsView;
import com.example.batyamessagingapp.lib.CircleBitmapFactory;
import com.example.batyamessagingapp.model.NetworkExecutor;
import com.example.batyamessagingapp.model.NetworkService;
import com.example.batyamessagingapp.model.PreferencesService;
import com.example.batyamessagingapp.model.pojo.DialogArray;
import com.example.batyamessagingapp.model.pojo.Message;
import com.example.batyamessagingapp.model.pojo.PairLastMessageDialogId;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import retrofit2.Response;


/**
 * Created by Кашин on 29.10.2016.
 */

public class DialogsService implements DialogsPresenter {

    private int MAX_DIALOGS_PER_CALL = 25;
    private int GET_DIALOGS_INTERVAL = 3000;
    private boolean mInitialized;
    private boolean mRunning;
    private Context mContext;
    private GetDialogsAsyncTask mGetDialogsAsyncTask;
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
                } catch (IndexOutOfBoundsException e){
                    // do nothing club
                }
            }
        });

        mHandler = new Handler();
        mGetDialogsWithInterval = new Runnable() {
            @Override
            public void run() {
                mGetDialogsAsyncTask = new GetDialogsAsyncTask(0, false);
                mGetDialogsAsyncTask.execute();
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
    private void startGetDialogsAsyncTask(int currentOffset, boolean initCall) {
        mGetDialogsAsyncTask = new GetDialogsAsyncTask(currentOffset, initCall);
        mGetDialogsAsyncTask.execute();
    }

    private class GetDialogsAsyncTask extends AsyncTask<Void, Void, Pair<DialogArray, NetworkExecutor.ErrorType>> {

        private final int offset;
        private final boolean initCall;

        GetDialogsAsyncTask(int offset, boolean initCall) {
            this.offset = offset;
            this.initCall = initCall;
        }

        @Override
        protected Pair<DialogArray, NetworkExecutor.ErrorType> doInBackground(Void... params) {
            try {
                Response<DialogArray> response = NetworkService
                        .getGetDialogsCall(offset)
                        .execute();

                DialogArray dialogArray = response.body();

                if (response.code() == 200 && dialogArray != null) {
                    return new Pair<>(dialogArray, NetworkExecutor.ErrorType.NoError);
                } else {
                    return new Pair<>(null, NetworkExecutor.ErrorType.NoAccess);
                }
            } catch (ConnectException | SocketTimeoutException e) {
                return new Pair<>(null, NetworkExecutor.ErrorType.NoInternetConnection);
            } catch (IOException e) {
                return new Pair<>(null, NetworkExecutor.ErrorType.NoAccess);
            }
        }

        protected void onPostExecute(Pair<DialogArray, NetworkExecutor.ErrorType> resultPair) {
            try {
                if (resultPair.second == NetworkExecutor.ErrorType.NoError && resultPair.first != null) {
                    mView.setCommonToolbarLabelText();

                    addDialogArrayToAdapter(resultPair.first.getDialogs());
                    if (mDataModel.getSize() != 0) {
                        mView.hideNoDialogsTextView();
                    } else {
                        mView.showNoDialogsTextView();
                    }

                    if (initCall) {
                        mInitialized = true;
                        startGetDialogsWithInterval();
                    }

                    if (resultPair.first.getDialogs().size() >= MAX_DIALOGS_PER_CALL) {
                        startGetDialogsAsyncTask(offset + 25, false);
                    }
                } else if (resultPair.second == NetworkExecutor.ErrorType.NoInternetConnection) {
                    throw new ConnectException();
                } else {
                    throw new IOException();
                }
            } catch (ConnectException | InterruptedException | ExecutionException e){
                mView.setNoInternetToolbarLabelText();
                if (initCall) onLoad();
            } catch (IOException e){
                stopGetDialogsWithInterval();
                mView.openAuthenticationActivity();
            } finally {
                if (initCall) mView.hideProgressBar();
            }
        }
    }

    private void addDialogArrayToAdapter(ArrayList<PairLastMessageDialogId> dialogs)
            throws InterruptedException, ExecutionException, IOException {

        for (int i = dialogs.size() - 1; i >= 0; --i) {
            // get dialog data
            PairLastMessageDialogId pair = dialogs.get(i);
            String dialogId = pair.getDialogId();
            Message message = pair.getMessage();

            // try to find the dialog in data model
            int dialogPosition = mDataModel
                    .findDialogPositionById(dialogId);

            // whether last message is mine
            boolean isMe = message.getSender()
                    .equals(PreferencesService.getIdFromPreferences());

            if (dialogPosition == -1) { // couldn`t find the dialog, add it
                // generate bitmap
                int color = CircleBitmapFactory.getMaterialColor(dialogId.hashCode());
                String firstLetter = CircleBitmapFactory.getFirstLetter(dialogId);
                Bitmap bitmap = CircleBitmapFactory
                        .generateCircleBitmap(mContext, color, 55, firstLetter);

                // create and add dialog
                Dialog dialog = new Dialog(
                        bitmap,
                        dialogId,
                        NetworkExecutor.getDialogNameFromId(dialogId),
                        (isMe ? "you: " : "") + message.getContent(),
                        message.getTimestamp());
                mDataModel.addDialog(dialog);
            } else { // dialog exists
                // refresh the data
                mDataModel.setDialogMessageAndTimestamp(
                        dialogPosition,
                        (isMe ? "you: " : "") + message.getContent(),
                        NetworkExecutor.getDialogNameFromId(dialogId),
                        message.getTimestamp());
            }
        }//for

        mDataModel.refresh();
    }
}