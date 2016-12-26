package com.danielkashin.batyamessagingapp.activity.dialog_settings.presenter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Pair;

import com.danielkashin.batyamessagingapp.activity.dialog_settings.adapter.OnUserClickListener;
import com.danielkashin.batyamessagingapp.activity.dialog_settings.adapter.OnUserLongClickListener;
import com.danielkashin.batyamessagingapp.activity.dialog_settings.adapter.User;
import com.danielkashin.batyamessagingapp.activity.dialog_settings.adapter.UserDataModel;
import com.danielkashin.batyamessagingapp.activity.dialog_settings.view.DialogSettingsView;
import com.danielkashin.batyamessagingapp.lib.CircleBitmapFactory;
import com.danielkashin.batyamessagingapp.lib.TimestampHelper;
import com.danielkashin.batyamessagingapp.model.BasicAsyncTask;
import com.danielkashin.batyamessagingapp.model.APIService;
import com.danielkashin.batyamessagingapp.model.PreferencesService;
import com.danielkashin.batyamessagingapp.model.pojo.DialogName;
import com.danielkashin.batyamessagingapp.model.pojo.GroupUser;
import com.danielkashin.batyamessagingapp.model.pojo.GroupUsers;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Response;


/**
 * Created by Кашин on 21.12.2016.
 */

public class DialogSettingsService implements DialogSettingsPresenter {

  private String mDialogId;

  private Context mContext;
  private DialogSettingsView mView;
  private UserDataModel mUserDataModel;

  public DialogSettingsService(DialogSettingsView view, String dialogId, UserDataModel userDataModel) {
    mView = view;
    mContext = (Context) view;
    mDialogId = dialogId;
    mUserDataModel = userDataModel;

    if (mView.isAdmin()) {
      userDataModel.setOnUserLongClickListener(new OnUserLongClickListener() {
        @Override
        public void onItemLongClick(UserDataModel adapter, int position) {
          try {
            String userId = adapter.getUserIdByPosition(position);

            if (position == 0 || userId.equals(PreferencesService.getIdFromPreferences())) {
              mView.showAlert("You cannot kick yourself", "Error");
            } else {
              confirmKickUser(userId);
            }
          } catch (Throwable t) {
            //do nothing
          }
        }
      });

      userDataModel.setOnUserClickListener(new OnUserClickListener() {
        @Override
        public void onItemClick(UserDataModel adapter, int position) {
          try {

            String userId = adapter.getUserIdByPosition(position);
            String userName = adapter.getUsernameByPosition(position);

            mView.openUserProfileActivity(userId, userName);
          } catch (Throwable t) {
            //do nothing
          }
        }
      });

    } else {
      userDataModel.setOnUserLongClickListener(null);
    }
  }

  @Override
  public void onConfirmIconClick(final String newDialogName) {
    BasicAsyncTask.AsyncTaskCompleteListener
        <Pair<ResponseBody, BasicAsyncTask.ErrorType>> callback
        = new BasicAsyncTask.AsyncTaskCompleteListener
        <Pair<ResponseBody, BasicAsyncTask.ErrorType>>() {
      @Override
      public void onTaskComplete(Pair<ResponseBody, BasicAsyncTask.ErrorType> result) {
        if (result.second == BasicAsyncTask.ErrorType.NoError) {
          mView.showToast("Group name was successfully changed");
          mView.setDialogName(newDialogName);
          mView.hideConfirmIcon();
        } else if (result.second == BasicAsyncTask.ErrorType.NoInternetConnection) {
          mView.showAlert("No internet connection", "Error");
        } else {
          mView.openChatActivity();
        }
      }
    };

    new BasicAsyncTask<ResponseBody>(
        APIService.getChangeUsernameCall(newDialogName, mDialogId),
        mContext,
        false,
        callback
    ).execute();
  }

  @Override
  public void onLoad() {
    BasicAsyncTask.AsyncTaskCompleteListener
        <Pair<GroupUsers, BasicAsyncTask.ErrorType>> callback =
        new BasicAsyncTask.AsyncTaskCompleteListener<Pair<GroupUsers, BasicAsyncTask.ErrorType>>() {
          @Override
          public void onTaskComplete(Pair<GroupUsers, BasicAsyncTask.ErrorType> result) {
            if (result.second == BasicAsyncTask.ErrorType.NoError) {
              handleGroupUsers(result.first);
            } else if (result.second == BasicAsyncTask.ErrorType.NoInternetConnection) {
              mView.setNoInternetConnectionToolbarLabel();
              onLoad();
            } else {
              mView.openMainActivity();
            }
          }
        };


    new BasicAsyncTask<GroupUsers>(
        APIService.getGetGroupUsersCall(mDialogId),
        null,
        false,
        callback
    ).execute();
  }

  @Override
  public void onLeaveButtonClick() {
    BasicAsyncTask.AsyncTaskCompleteListener
        <Pair<ResponseBody, BasicAsyncTask.ErrorType>> callback
        = new BasicAsyncTask.AsyncTaskCompleteListener
        <Pair<ResponseBody, BasicAsyncTask.ErrorType>>() {
      @Override
      public void onTaskComplete(Pair<ResponseBody, BasicAsyncTask.ErrorType> result) {
        if (result.second == BasicAsyncTask.ErrorType.NoError) {
          mView.openMainActivity();
        } else if (result.second == BasicAsyncTask.ErrorType.NoInternetConnection) {
          mView.showAlert("No internet connection", "Error");
        } else {
          mView.openChatActivity();
        }
      }
    };

    new BasicAsyncTask<ResponseBody>(
        APIService.getLeaveGroupCall(mDialogId),
        mContext,
        false,
        callback
    ).execute();
  }

  private void confirmKickUser(final String userId) {
    // create alertdialog
    AlertDialog.Builder alertDialog = new AlertDialog.Builder((Context) mView);
    alertDialog.setTitle("Are you sure to kick user?");

    // set positive button listener
    alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
      public void onClick(final DialogInterface dialog, int which) {
        kickUser(userId);
      }
    });

    // set negative button listener
    alertDialog.setNegativeButton("NO",
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
          }
        });

    alertDialog.show();
  }

  private void kickUser(String userId) {
    BasicAsyncTask.AsyncTaskCompleteListener
        <Pair<ResponseBody, BasicAsyncTask.ErrorType>> callback =
        new BasicAsyncTask.AsyncTaskCompleteListener<Pair<ResponseBody, BasicAsyncTask.ErrorType>>() {
          @Override
          public void onTaskComplete(Pair<ResponseBody, BasicAsyncTask.ErrorType> result) {
            if (result.second == BasicAsyncTask.ErrorType.NoError) {
              onLoad();
              mView.showToast("User was successfully deleted");
            } else if (result.second == BasicAsyncTask.ErrorType.NoInternetConnection) {
              mView.showAlert("No internet connection", "Error");
            } else {
              mView.openMainActivity();
            }
          }
        };

    new BasicAsyncTask<ResponseBody>(
        APIService.getKickUserFromGroupCall(mDialogId, userId),
        mContext,
        false,
        callback
    ).execute();
  }

  private void handleGroupUsers(GroupUsers groupUsers) {
    ArrayList<GroupUser> users = groupUsers.getUsers();
    int usersCount = users.size();

    final String[] ids = new String[usersCount + 1];
    final long[] timestamps = new long[usersCount + 1];

    ids[0] = groupUsers.getOriginatorId();
    timestamps[0] = -1;

    for (int i = 1; i < usersCount + 1; ++i) {
      ids[i] = users.get(i - 1).getUserId();
      timestamps[i] = users.get(i - 1).getJoinTime();
    }

    handleIds(ids, timestamps);
  }

  private void handleIds(final String[] ids, final long[] timestamps) {
    HandleIdsAsyncTask handleIdsAsyncTask = new HandleIdsAsyncTask(ids, timestamps);
    handleIdsAsyncTask.execute();
  }

  private class HandleIdsAsyncTask extends
      AsyncTask<Void, Void, Pair<ArrayList<User>, BasicAsyncTask.ErrorType>> {

    private String[] ids;
    private long[] timestamps;

    private HandleIdsAsyncTask(final String[] ids, final long[] timestamps) {
      this.ids = ids;
      this.timestamps = timestamps;
    }

    @Override
    protected Pair<ArrayList<User>, BasicAsyncTask.ErrorType> doInBackground(Void... params) {
      try {
        ArrayList<User> users = new ArrayList<>();

        for (int i = 0; i < ids.length; ++i) {
          Response<DialogName> response = APIService.getGetDialogNameCall(ids[i]).execute();
          DialogName dialogName = response.body();

          if (dialogName != null && response.code() == 200) {
            String username = dialogName.getDialogName();
            Bitmap bitmap = CircleBitmapFactory.generateCircleBitmap(
                mContext,
                CircleBitmapFactory.getMaterialColor(ids[i].hashCode()),
                55,
                CircleBitmapFactory.getFirstLetter(username));

            String date;
            if (i == 0) {
              date = "group creator";
            } else {
              date = "added\n" + TimestampHelper.formatTimestampToDate(timestamps[i]);
            }

            users.add(new User(bitmap, ids[i], username, date));
          } else {
            throw new IOException();
          }
        }

        return new Pair<>(users, BasicAsyncTask.ErrorType.NoError);
      } catch (ConnectException e) {
        return new Pair<>(null, BasicAsyncTask.ErrorType.NoInternetConnection);
      } catch (IOException e) {
        return new Pair<>(null, BasicAsyncTask.ErrorType.NoAccess);
      }
    }

    @Override
    protected void onPostExecute(Pair<ArrayList<User>, BasicAsyncTask.ErrorType> result) {
      if (result.second == BasicAsyncTask.ErrorType.NoError) {
        mView.setCommonToolbarLabel();
        mUserDataModel.setUsers(result.first);
      } else if (result.second == BasicAsyncTask.ErrorType.NoInternetConnection) {
        mView.setNoInternetConnectionToolbarLabel();
        handleIds(ids, timestamps);
      } else {
        mView.openMainActivity();
      }
    }
  }

}
