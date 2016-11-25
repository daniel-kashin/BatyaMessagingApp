package com.example.batyamessagingapp.activity.dialogs;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.util.Pair;

import com.example.batyamessagingapp.activity.dialogs.adapter.Dialog;
import com.example.batyamessagingapp.lib.CircleBitmapFactory;
import com.example.batyamessagingapp.model.NetworkService;
import com.example.batyamessagingapp.model.PreferencesService;
import com.example.batyamessagingapp.model.pojo.APIAnswer;
import com.example.batyamessagingapp.model.pojo.DialogArray;
import com.example.batyamessagingapp.model.pojo.Message;
import com.example.batyamessagingapp.model.pojo.PairLastMessageDialogId;
import com.example.batyamessagingapp.model.pojo.PairMessageDialogId;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import retrofit2.Response;


/**
 * Created by Кашин on 29.10.2016.
 */

public class DialogsService implements DialogsPresenter {

    private Context mContext;
    private DisconnectionAsyncTask mDisconnectionAsyncTask;
    private GetDialogsAsyncTask mGetDialogsAsyncTask;

    private DialogsView mView;


    DialogsService(DialogsView view, Context context) {
        mView = view;
        mContext = context;
    }

    public void onLoad() {
        startGetDialogsAsyncTask(0);
    }

    //add this method to enable recursion
    public void startGetDialogsAsyncTask(int offset) {
        mGetDialogsAsyncTask = new GetDialogsAsyncTask(offset);
        mGetDialogsAsyncTask.execute();
    }

    public void onLogoutButtonClick() {
        mDisconnectionAsyncTask = new DisconnectionAsyncTask(DisconnectionType.Normal);
        mDisconnectionAsyncTask.execute();
    }

    public void onFullLogoutButtonClick() {
        mDisconnectionAsyncTask = new DisconnectionAsyncTask(DisconnectionType.Full);
        mDisconnectionAsyncTask.execute();
    }

    class GetDialogsAsyncTask extends AsyncTask<Void, Void, Pair<DialogArray, ErrorType>> {

        private final int offset;

        public GetDialogsAsyncTask(int offset) {
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

        }

    }

    private void addDialogArrayToAdapter(DialogArray dialogArray){

        ArrayList<PairLastMessageDialogId> pairList = dialogArray.getDialogs();

        for (int i = 0; i < pairList.size(); ++i) {
            PairLastMessageDialogId pair = pairList.get(i);
            String dialogId = pair.getDialogId();
            Message message = pair.getMessage();

            String content = message.getContent();
            String time = "" + message.getTimestamp();

            int dialogPosition = mView
                    .findDialogPositionByIdInAdapter(dialogId);

            if (dialogPosition == -1) {
                //TODO: add sizes of the squares
                int color = CircleBitmapFactory.getMaterialColor(message.hashCode());
                String firstLetter = CircleBitmapFactory.getFirstLetter(dialogId);
                Bitmap bitmap = CircleBitmapFactory
                        .generateCircleBitmap(mContext, color, 50, firstLetter);

                Dialog dialog = new Dialog(bitmap, dialogId, content, time);

                mView.addDialogToAdapter(dialog);
            } else { //dialog exists
                mView.setDialogMessageAndTimeInAdapter(
                        dialogPosition, content, time);
            }

        }//for

        mView.refreshAdapter();

        if (pairList.size() == 25) {
            startGetDialogsAsyncTask(mView.getAdapterSize());
        }
    }

    class DisconnectionAsyncTask extends AsyncTask<Void, Void, Pair<APIAnswer, ErrorType>> {

        private final DisconnectionType disconnectionType;

        public DisconnectionAsyncTask(DisconnectionType disconnectionType) {
            this.disconnectionType = disconnectionType;
        }

        protected Pair<APIAnswer, ErrorType> doInBackground(Void... voids) {
            try {
                Response<APIAnswer> response;
                if (disconnectionType == DisconnectionType.Full)
                    response = NetworkService.getFullLogoutCall().execute();
                else //disconnectionType == DisconnectionType.Normal
                    response = NetworkService.getLogoutCall().execute();

                APIAnswer apiAnswer = response.body();

                if (response.code() == 200 && apiAnswer != null) {
                    return new Pair<>(apiAnswer, ErrorType.NoError);
                } else {
                    return new Pair<>(null, ErrorType.NoAccess);
                }
            } catch (ConnectException e) {
                return new Pair<>(null, ErrorType.NoInternetConnection);
            } catch (IOException e) {
                return new Pair<>(null, ErrorType.NoAccess);
            }
        }

        protected void onPostExecute(Pair<APIAnswer, ErrorType> resultPair) {
            if (resultPair.second == ErrorType.NoInternetConnection && disconnectionType == DisconnectionType.Full) {
                mView.showAlert("No internet connection. Unable to leave active sessions", "Log out error");
            } else {
                PreferencesService.deleteTokenFromPreferences();
                mView.openAuthenticationActivity();
            }
        }
    }

    private enum DisconnectionType {
        Normal,
        Full
    }

    private enum ErrorType {
        NoInternetConnection,
        NoAccess,
        NoError
    }
}