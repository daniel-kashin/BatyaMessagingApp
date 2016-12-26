package com.danielkashin.batyamessagingapp.activity.main.fragment_search.presenter;

import android.content.Context;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;

import com.danielkashin.batyamessagingapp.activity.main.fragment_search.adapter.OnUserClickListener;
import com.danielkashin.batyamessagingapp.activity.main.fragment_search.adapter.User;
import com.danielkashin.batyamessagingapp.activity.main.fragment_search.adapter.UserDataModel;
import com.danielkashin.batyamessagingapp.activity.main.fragment_search.view.SearchView;
import com.danielkashin.batyamessagingapp.model.BasicAsyncTask;
import com.danielkashin.batyamessagingapp.model.APIService;
import com.danielkashin.batyamessagingapp.model.pojo.DialogName;
import com.danielkashin.batyamessagingapp.model.pojo.UserIds;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;

import retrofit2.Response;

import static com.danielkashin.batyamessagingapp.model.BasicAsyncTask.ErrorType.NoAccess;
import static com.danielkashin.batyamessagingapp.model.BasicAsyncTask.ErrorType.NoError;
import static com.danielkashin.batyamessagingapp.model.BasicAsyncTask.ErrorType.NoInternetConnection;


/**
 * Created by Кашин on 18.12.2016.
 */

public class SearchService implements SearchPresenter {

  private Context mContext;
  private AddUsersAsyncTask mAddUsersAsyncTask;

  private SearchView mView;
  private UserDataModel mDataModel;

  public SearchService(SearchView view, Context context, UserDataModel dataModel) {
    mView = view;
    mContext = context;
    mDataModel = dataModel;

    mDataModel.setOnUserClickListener(new OnUserClickListener() {
      @Override
      public void onItemClick(UserDataModel adapter, int position) {
        try {
          String dialogId = ((UserDataModel) adapter).getUserIdByPosition(position);
          String dialogName = ((UserDataModel) adapter).getUsernameByPosition(position);
          mView.openChatActivity(dialogId, dialogName);
        } catch (Throwable t) {
          // do nothing club
        }
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
        boolean foundNewLine = false;

        for (int i = s.length(); i > 0; --i) {
          if (s.subSequence(i - 1, i).toString().equals("\n") ||
              s.subSequence(i - 1, i).toString().equals("\r")) {
            foundNewLine = true;
            s.replace(i - 1, i, "");
          }
        }

        if (!foundNewLine) {
          String searchRequest = s.toString();

          mDataModel.clearUsers();

          if (searchRequest.equals("")) {
            mView.showNoUsersTextView();
            mView.hideClearIcon();
            mDataModel.clearUsers();
          } else {
            mView.showClearIcon();
            mView.hideTextView();
            mView.showProgressBar();

            addUsers(searchRequest);
          }
        }
      }
    });
  }

  private void addUsers(String searchRequest) {
    if (mAddUsersAsyncTask != null) {
      mAddUsersAsyncTask.cancel(false);
    }

    mAddUsersAsyncTask = new AddUsersAsyncTask(searchRequest);
    mAddUsersAsyncTask.execute();
  }


  private class AddUsersAsyncTask
      extends AsyncTask<Void, Void, Pair<ArrayList<User>, BasicAsyncTask.ErrorType>> {

    private String searchRequest;

    private AddUsersAsyncTask(String searchRequest) {
      this.searchRequest = searchRequest;
    }

    @Override
    protected void onPreExecute() {
      mView.hideTextView();
      mView.showProgressBar();
    }

    @Override
    protected Pair<ArrayList<User>, BasicAsyncTask.ErrorType> doInBackground(Void... params) {
      try {
        Response<UserIds> responseUserIds =
            APIService.getGetSearchedUsersCall(searchRequest).execute();

        UserIds resultUserIds = responseUserIds.body();

        if (responseUserIds.code() == 200 && resultUserIds != null) {
          if (mView.isInputEmpty()) {
            return new Pair<>(null, NoError);
          } else {
            ArrayList<String> userIds = resultUserIds.toList();
            ArrayList<User> users = new ArrayList<User>(25);

            for (String id : userIds) {
              Response<DialogName> response = APIService
                  .getGetDialogNameCall(id)
                  .execute();
              DialogName resultDialogName = response.body();

              if (response.code() == 200 && resultDialogName != null) {
                users.add(new User(id, resultDialogName.getDialogName()));
              } else {
                throw new IOException();
              }
            }

            return new Pair<>(users, NoError);
          }
        } else {
          return new Pair<>(null, NoAccess);
        }
      } catch (ConnectException e) {
        return new Pair<>(null, NoInternetConnection);
      } catch (IOException e) {
        return new Pair<>(null, NoAccess);
      }
    }

    @Override
    protected void onPostExecute(Pair<ArrayList<User>, BasicAsyncTask.ErrorType> result) {
      mView.hideProgressBar();

      if (result.second == NoError) {
        if (result.first == null) {
          mView.showNoUsersTextView();
          mView.hideClearIcon();
        } else {
          if (result.first.size() > 0) {
            mDataModel.setUsers(result.first);
          } else {
            mView.showNoUsersTextView();
          }
        }
      } else if (result.second == BasicAsyncTask.ErrorType.NoInternetConnection) {
        mView.showNoInternetConnectionTextView();
      } else {
        mView.openAuthenticationActivity();
      }
    }
  }
}
