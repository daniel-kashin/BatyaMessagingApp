package com.example.batyamessagingapp.activity.main.fragment_search.presenter;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;

import com.example.batyamessagingapp.activity.main.fragment_dialogs.adapter.DialogsDataModel;
import com.example.batyamessagingapp.activity.main.fragment_search.adapter.OnUserClickListener;
import com.example.batyamessagingapp.activity.main.fragment_search.adapter.UserDataModel;
import com.example.batyamessagingapp.activity.main.fragment_search.view.SearchView;
import com.example.batyamessagingapp.model.NetworkExecutor;
import com.example.batyamessagingapp.model.NetworkService;
import com.example.batyamessagingapp.model.PreferencesService;
import com.example.batyamessagingapp.model.pojo.UserIds;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutionException;

import retrofit2.Response;

/**
 * Created by Кашин on 18.12.2016.
 */

public class SearchService implements SearchPresenter {

    private Context mContext;

    private SearchView mView;
    private UserDataModel mDataModel;

    public SearchService(SearchView view, Context context, UserDataModel dataModel) {
        mView = view;
        mContext = context;
        mDataModel = dataModel;

        mDataModel.setOnUserClickListener(new OnUserClickListener() {
            @Override
            public void onItemClick(RecyclerView.Adapter adapter, int position) {
                String dialogId = ((UserDataModel) adapter).getUserIdByPosition(position);
                String dialogName = ((UserDataModel) adapter).getUsernameByPosition(position);
                mView.openChatActivity(dialogId, dialogName);
            }
        });
    }

    @Override
    public void onResume() {
        mDataModel.clearUsers();
        mView.setOnToolbarTextListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String result = s.toString();

                if (result.equals("")){
                    mView.showNoUsersTextView();
                    mView.hideClearIcon();
                    mDataModel.clearUsers();

                } else {
                    GetUsersAsyncTask task = new GetUsersAsyncTask(result);
                    task.execute();
                }
            }
        });
    }


    private class GetUsersAsyncTask
            extends AsyncTask<Void, Void, Pair<UserIds, NetworkExecutor.ErrorType>> {

        private final String dialogId;

        GetUsersAsyncTask(String dialogId) {
            this.dialogId = dialogId;
        }

        @Override
        protected void onPreExecute(){
            mView.showClearIcon();
            mView.hideTextView();
            mView.showProgressBar();
        }

        protected Pair<UserIds, NetworkExecutor.ErrorType> doInBackground(Void... voids) {
            try {
                Response<UserIds> response = NetworkService
                        .getGetSearchedUsersCall(dialogId)
                        .execute();

                UserIds userIds = response.body();

                if (response.code() == 200 && userIds!= null) {
                    return new Pair<>(userIds, NetworkExecutor.ErrorType.NoError);
                } else {
                    return new Pair<>(null, NetworkExecutor.ErrorType.NoAccess);
                }
            } catch (ConnectException  | SocketTimeoutException e) {
                return new Pair<>(null, NetworkExecutor.ErrorType.NoInternetConnection);
            } catch (IOException e) {
                return new Pair<>(null, NetworkExecutor.ErrorType.NoAccess);
            }
        }

        protected void onPostExecute(Pair<UserIds, NetworkExecutor.ErrorType> resultPair) {
            mView.hideProgressBar();

            try {
                if (resultPair.second == NetworkExecutor.ErrorType.NoInternetConnection) {
                    throw new ConnectException();
                } else if (resultPair.second == NetworkExecutor.ErrorType.NoError) {
                    if (resultPair.first != null && resultPair.first.toList().size() != 0) {
                        mView.hideTextView();
                        mDataModel.addUsers(resultPair.first.toList());
                    } else {
                        mView.showNoUsersTextView();
                        mDataModel.clearUsers();
                    }
                } else {
                    throw new IOException();
                }
            } catch (ConnectException e){
                mView.showNoInternetConnectionTextView();
            } catch (IOException | InterruptedException | ExecutionException e){
                mView.openAuthenticationActivity();
            }
        }
    }



}
