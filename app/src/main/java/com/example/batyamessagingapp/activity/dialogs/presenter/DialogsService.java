package com.example.batyamessagingapp.activity.dialogs.presenter;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.util.Pair;

import com.example.batyamessagingapp.activity.dialogs.view.DialogsView;
import com.example.batyamessagingapp.model.NetworkService;
import com.example.batyamessagingapp.model.PreferencesService;
import com.example.batyamessagingapp.model.pojo.APIAnswer;

import java.io.IOException;
import java.net.ConnectException;

import okhttp3.ResponseBody;
import retrofit2.Response;


/**
 * Created by Кашин on 29.10.2016.
 */

public class DialogsService implements DialogsPresenter  {

    private CheckUserExistenceAsyncTask mCheckUserExistenceAsyncTask;
    private DialogsView mView;
    private Context mContext;

    public DialogsService(DialogsView dialogsView){
        mView = dialogsView;
        mContext = (Context)dialogsView;
    }

    @Override
    public void onForwardIconButtonClick(final String username) {
        mCheckUserExistenceAsyncTask = new CheckUserExistenceAsyncTask(username);
        mCheckUserExistenceAsyncTask.execute();
    }


    private class CheckUserExistenceAsyncTask extends AsyncTask<Void, Void, ResponseBody>{
        private final String username;

        CheckUserExistenceAsyncTask(String username){
            this.username = username;
        }

        @Override
        protected ResponseBody doInBackground(Void... params) {
            return null;
        }

        protected void onPostExecute(ResponseBody responseBody) {
            mView.openChatActivity(username);
        }
    }


    private enum ErrorType {
        NoInternetConnection,
        NoAccess,
        NoError
    }
}