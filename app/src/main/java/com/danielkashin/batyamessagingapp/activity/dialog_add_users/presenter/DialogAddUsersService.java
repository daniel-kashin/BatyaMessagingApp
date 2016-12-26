package com.danielkashin.batyamessagingapp.activity.dialog_add_users.presenter;

import android.content.Context;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.widget.Toast;

import com.danielkashin.batyamessagingapp.activity.dialog_add_users.adapter.OnUserClickListener;
import com.danielkashin.batyamessagingapp.activity.dialog_add_users.adapter.User;
import com.danielkashin.batyamessagingapp.activity.dialog_add_users.adapter.UserDataModel;
import com.danielkashin.batyamessagingapp.activity.dialog_add_users.view.DialogAddUsersView;
import com.danielkashin.batyamessagingapp.model.BasicAsyncTask;
import com.danielkashin.batyamessagingapp.model.APIService;
import com.danielkashin.batyamessagingapp.model.pojo.DialogArray;
import com.danielkashin.batyamessagingapp.model.pojo.DialogName;
import com.danielkashin.batyamessagingapp.model.pojo.PairLastMessageDialogId;
import com.danielkashin.batyamessagingapp.model.pojo.UserIds;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Response;

import static com.danielkashin.batyamessagingapp.model.BasicAsyncTask.ErrorType.NoAccess;
import static com.danielkashin.batyamessagingapp.model.BasicAsyncTask.ErrorType.NoError;
import static com.danielkashin.batyamessagingapp.model.BasicAsyncTask.ErrorType.NoInternetConnection;

/**
 * Created by Кашин on 25.12.2016.
 */

public class DialogAddUsersService implements DialogAddUsersPresenter {

  private Context mContext;
  private String mDialogId;
  private AddUsersAsyncTask mAddUsersAsyncTask;
  private AddFriendsAsyncTask mAddFriendsAsyncTask;
  private ArrayList<User> mFiends;

  private DialogAddUsersView mView;
  private UserDataModel mDataModel;

  public DialogAddUsersService(DialogAddUsersView view, Context context,
                               UserDataModel dataModel, String dialogId) {
    mView = view;
    mContext = context;
    mDataModel = dataModel;
    mDialogId = dialogId;

    mDataModel.setOnUserClickListener(new OnUserClickListener() {
      @Override
      public void onItemClick(UserDataModel adapter, int position) {
        try {
          BasicAsyncTask.AsyncTaskCompleteListener
              <Pair<ResponseBody, BasicAsyncTask.ErrorType>> callback =
              new BasicAsyncTask.AsyncTaskCompleteListener<Pair<ResponseBody, BasicAsyncTask.ErrorType>>() {
                @Override
                public void onTaskComplete(Pair<ResponseBody, BasicAsyncTask.ErrorType> result) {
                  if (result.second == NoError) {
                    Toast.makeText(mContext, "User was successfully added", Toast.LENGTH_SHORT)
                        .show();
                  } else if (result.second == NoInternetConnection) {
                    mView.showAlert("No internet connection", "Error");
                  } else {
                    Toast.makeText(mContext, "This user had been already added", Toast.LENGTH_SHORT)
                        .show();
                  }
                }
              };

          new BasicAsyncTask<>(
              APIService.getInviteUserToGroupCall(mDialogId, adapter.getUserIdByPosition(position)),
              mContext,
              false,
              callback
          ).execute();
        } catch (Throwable t) {
          // do nothing
        }
      }
    });
  }

  @Override
  public void onResume() {
    addFriends();

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
            addFriends();
          } else {
            mView.showClearIcon();
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
    if (mAddFriendsAsyncTask != null) {
      mAddFriendsAsyncTask.cancel(false);
    }

    mAddUsersAsyncTask = new AddUsersAsyncTask(searchRequest);
    mAddUsersAsyncTask.execute();
  }

  private void addFriends() {
    if (mAddUsersAsyncTask != null) {
      mAddUsersAsyncTask.cancel(false);
    }

    if (mFiends == null) {
      mAddFriendsAsyncTask = new AddFriendsAsyncTask();
      mAddFriendsAsyncTask.execute();
    } else {
      mDataModel.clearUsers();
      mView.hideProgressBar();

      if (mFiends.size() == 0) {
        mView.showNoFriendsTextView();
      } else {
        mDataModel.setUsers(mFiends);
        mView.hideTextView();
      }
    }
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
      mDataModel.clearUsers();
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
            ArrayList<User> users = new ArrayList<>(25);

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
        mView.openMainActivity();
      }
    }
  }

  private class AddFriendsAsyncTask
      extends AsyncTask<Void, Void, Pair<ArrayList<User>, BasicAsyncTask.ErrorType>> {
    @Override
    protected void onPreExecute() {
      mView.hideTextView();
      mView.showProgressBar();
      mDataModel.clearUsers();
    }

    @Override
    protected Pair<ArrayList<User>, BasicAsyncTask.ErrorType> doInBackground(Void... params) {
      try {
        Response<DialogArray> responseUserIds =
            APIService.getGetDialogsCall(0).execute();

        DialogArray resultDialogArray = responseUserIds.body();

        if (responseUserIds.code() == 200 && resultDialogArray != null) {
          ArrayList<User> users = new ArrayList<>(25);

          ArrayList<String> userIds = new ArrayList<>(25);
          for (PairLastMessageDialogId pair : resultDialogArray.getDialogs()) {
            if (pair.getDialogId().charAt(0) != '+') {
              userIds.add(pair.getDialogId());
            }
          }

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
          mView.showNoFriendsTextView();
          mView.hideClearIcon();
        } else {
          mFiends = result.first;
          addFriends();
        }
      } else if (result.second == BasicAsyncTask.ErrorType.NoInternetConnection) {
        mView.showNoInternetConnectionTextView();
      } else {
        mView.openMainActivity();
      }
    }
  }


}
