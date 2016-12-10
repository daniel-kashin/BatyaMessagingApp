package com.example.batyamessagingapp.activity.dialogs.fragment_view_dialogs.presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.util.Pair;

import com.example.batyamessagingapp.activity.dialogs.fragment_view_dialogs.adapter.Dialog;
import com.example.batyamessagingapp.activity.dialogs.fragment_view_dialogs.adapter.DialogsDataModel;
import com.example.batyamessagingapp.activity.dialogs.fragment_view_dialogs.view.ViewDialogsView;
import com.example.batyamessagingapp.lib.CircleBitmapFactory;
import com.example.batyamessagingapp.model.NetworkService;
import com.example.batyamessagingapp.model.pojo.DialogArray;
import com.example.batyamessagingapp.model.pojo.Message;
import com.example.batyamessagingapp.model.pojo.PairLastMessageDialogId;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import retrofit2.Response;


/**
 * Created by Кашин on 29.10.2016.
 */

public class ViewDialogsService implements ViewDialogsPresenter {

    private Context mContext;
    private GetDialogsAsyncTask mGetDialogsAsyncTask;

    private DialogsDataModel mDataModel;
    private ViewDialogsView mView;


    public ViewDialogsService(ViewDialogsView view, Context context, DialogsDataModel dataModel) {
        mView = view;
        mContext = context;
        mDataModel = dataModel;
    }

    public void onLoad() {
        startGetDialogsAsyncTask(0);
    }

    //added this method to enable recursion
    private void startGetDialogsAsyncTask(int offset) {
        mGetDialogsAsyncTask = new GetDialogsAsyncTask(offset);
        mGetDialogsAsyncTask.execute();
    }

    private class GetDialogsAsyncTask extends AsyncTask<Void, Void, Pair<DialogArray, ErrorType>> {

        private final int offset;

        GetDialogsAsyncTask(int offset) {
            this.offset = offset;
        }

        @Override
        protected Pair<DialogArray, ErrorType> doInBackground(Void... params) {
            try {
                Response<DialogArray> response = NetworkService
                        .getGetDialogsCall(offset)
                        .execute();

                DialogArray dialogArray = response.body();

                if (response.code() == 200 && dialogArray != null) {
                    return new Pair<>(dialogArray, ErrorType.NoError);
                } else {
                    return new Pair<>(null, ErrorType.NoAccess);
                }

            } catch (ConnectException | SocketTimeoutException e) {
                return new Pair<>(null, ErrorType.NoInternetConnection);
            } catch (IOException e) {
                return new Pair<>(null, ErrorType.NoAccess);
            }
        }

        protected void onPostExecute(Pair<DialogArray, ErrorType> resultPair) {
            if (resultPair.second == ErrorType.NoError && resultPair.first != null) {
                addDialogArrayToAdapter(resultPair.first);
            }
            //TODO

            if (mDataModel.getSize()!=0){
                mView.hideNoDialogsTextView();
            }
        }

    }

    private void addDialogArrayToAdapter(DialogArray dialogArray){

        ArrayList<PairLastMessageDialogId> pairList = dialogArray.getDialogs();

        for (int i = 0; i < pairList.size(); ++i) {
            PairLastMessageDialogId pair = pairList.get(i);
            String dialogId = pair.getDialogId();
            Message message = pair.getMessage();

            int dialogPosition = mDataModel
                    .findDialogPositionById(dialogId);

            if (dialogPosition == -1) {
                //TODO: add sizes of the squares
                int color = CircleBitmapFactory.getMaterialColor(dialogId.hashCode());
                String firstLetter = CircleBitmapFactory.getFirstLetter(dialogId);
                Bitmap bitmap = CircleBitmapFactory
                        .generateCircleBitmap(mContext, color, 50, firstLetter);

                Dialog dialog = new Dialog(bitmap, dialogId, message.getContent(), message.getTimestamp());

                mDataModel.addDialog(dialog);
            } else { //item_dialog exists
                mDataModel.setDialogMessageAndTimestamp(
                        dialogPosition, message.getContent(), message.getTimestamp());
            }

        }//for

        mDataModel.refresh();

        //TODO
        //if (pairList.size() >= 25) {
        //    startGetDialogsAsyncTask(mDataModel.getSize());
        //}
    }

    private enum ErrorType {
        NoInternetConnection,
        NoAccess,
        NoError
    }
}