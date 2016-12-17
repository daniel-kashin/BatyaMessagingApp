package com.example.batyamessagingapp.activity.main.presenter;

import android.content.Context;
import android.os.AsyncTask;

import com.example.batyamessagingapp.activity.main.view.MainView;

import okhttp3.ResponseBody;


/**
 * Created by Кашин on 29.10.2016.
 */

public class MainService implements MainPresenter {

    private CheckUserExistenceAsyncTask mCheckUserExistenceAsyncTask;
    private MainView mView;
    private Context mContext;

    public MainService(MainView mainView){
        mView = mainView;
        mContext = (Context) mainView;
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
            mView.hideSearch();
        }
    }


    private enum ErrorType {
        NoInternetConnection,
        NoAccess,
        NoError
    }
}