package com.danielkashin.batyamessagingapp.activity.main.fragment_search.presenter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;

import com.danielkashin.batyamessagingapp.activity.main.fragment_search.adapter.OnUserClickListener;
import com.danielkashin.batyamessagingapp.activity.main.fragment_search.adapter.User;
import com.danielkashin.batyamessagingapp.activity.main.fragment_search.adapter.UserDataModel;
import com.danielkashin.batyamessagingapp.activity.main.fragment_search.view.SearchView;
import com.danielkashin.batyamessagingapp.model.BasicAsyncTask;
import com.danielkashin.batyamessagingapp.model.NetworkService;
import com.danielkashin.batyamessagingapp.model.pojo.DialogName;
import com.danielkashin.batyamessagingapp.model.pojo.UserIds;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

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
          } else {
            mView.showClearIcon();
            mView.hideTextView();
            mView.showProgressBar();

            BasicAsyncTask.AsyncTaskCompleteListener
                <Pair<UserIds, BasicAsyncTask.ErrorType>> callback =
                new BasicAsyncTask.AsyncTaskCompleteListener<Pair<UserIds, BasicAsyncTask.ErrorType>>() {
                  @Override
                  public void onTaskComplete(Pair<UserIds, BasicAsyncTask.ErrorType> result) {
                    try {
                      if (result.second == BasicAsyncTask.ErrorType.NoError) {
                        if (mView.isInputEmpty()) {
                          mView.showNoUsersTextView();
                          mView.hideClearIcon();
                        } else {
                          mView.hideTextView();

                          ArrayList<String> userIds = result.first.toList();
                          ArrayList<User> users = new ArrayList<User>(25);

                          for (String id : userIds) {
                            Pair<DialogName, BasicAsyncTask.ErrorType> usernameResult
                                = new BasicAsyncTask<DialogName>(
                                NetworkService.getGetDialogNameCall(id),
                                null,
                                false,
                                null
                            ).execute().get();

                            if (usernameResult.second == BasicAsyncTask.ErrorType.NoError) {
                              users.add(new User(id, usernameResult.first.getDialogName()));
                            } else if (usernameResult.second == BasicAsyncTask.ErrorType.NoInternetConnection) {
                              throw new ConnectException();
                            } else if (usernameResult.second == BasicAsyncTask.ErrorType.NoAccess) {
                              throw new IOException();
                            }
                          }

                          if (users.size() > 0) {
                            mDataModel.setUsers(users);
                          } else {
                            mView.showNoUsersTextView();
                          }
                        }
                      } else if (result.second == BasicAsyncTask.ErrorType.NoInternetConnection) {
                        throw new ConnectException();
                      } else {
                        throw new IOException();
                      }
                    } catch (ConnectException e) {
                      mView.showNoInternetConnectionTextView();
                    } catch (IOException | InterruptedException | ExecutionException e) {
                      mView.openAuthenticationActivity();
                    }

                    mView.hideProgressBar();
                  }
                };

            new BasicAsyncTask<UserIds>(
                NetworkService.getGetSearchedUsersCall(searchRequest),
                null,
                false,
                callback
            ).execute();
          }
        }
      }
    });
  }
}