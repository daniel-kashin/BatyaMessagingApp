package com.danielkashin.batyamessagingapp.activity.dialog_settings.presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;

import com.danielkashin.batyamessagingapp.activity.dialog_settings.adapter.OnUserClickListener;
import com.danielkashin.batyamessagingapp.activity.dialog_settings.adapter.User;
import com.danielkashin.batyamessagingapp.activity.dialog_settings.adapter.UsersDataModel;
import com.danielkashin.batyamessagingapp.activity.dialog_settings.view.DialogSettingsView;
import com.danielkashin.batyamessagingapp.lib.CircleBitmapFactory;
import com.danielkashin.batyamessagingapp.lib.TimestampHelper;
import com.danielkashin.batyamessagingapp.model.BasicAsyncTask;
import com.danielkashin.batyamessagingapp.model.NetworkService;
import com.danielkashin.batyamessagingapp.model.pojo.DialogName;
import com.danielkashin.batyamessagingapp.model.pojo.GroupUser;
import com.danielkashin.batyamessagingapp.model.pojo.GroupUsers;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import okhttp3.ResponseBody;

/**
 * Created by Кашин on 21.12.2016.
 */

public class DialogSettingsService implements DialogSettingsPresenter {

  private String mDialogId;

  private Context mContext;
  private DialogSettingsView mView;
  private UsersDataModel mUsersDataModel;

  public DialogSettingsService(DialogSettingsView view, String dialogId, UsersDataModel usersDataModel){
    mView = view;
    mContext = (Context)view;
    mDialogId = dialogId;
    mUsersDataModel = usersDataModel;

    if (mView.isAdmin()){
      usersDataModel.setOnUserClickListener(new OnUserClickListener() {
        @Override
        public void onItemClick(RecyclerView.Adapter adapter, int position) {

        }
      });
    } else {
      usersDataModel.setOnUserClickListener(null);
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
        if (result.second == BasicAsyncTask.ErrorType.NoError){
          mView.showToast("Group name was successfully changed");
          mView.setDialogUsername(newDialogName);
          mView.hideConfirmIcon();
        } else if (result.second == BasicAsyncTask.ErrorType.NoInternetConnection){
          mView.showAlert("No internet connection", "Error");
        } else {
          mView.openChatActivity();
        }
      }
    };

    new BasicAsyncTask<ResponseBody>(
        NetworkService.getChangeUsernameCall(newDialogName, mDialogId),
        mContext,
        false,
        callback
    ).execute();
  }

  @Override
  public void onLoad(){
    BasicAsyncTask.AsyncTaskCompleteListener
        <Pair<GroupUsers, BasicAsyncTask.ErrorType>> callback =
        new BasicAsyncTask.AsyncTaskCompleteListener<Pair<GroupUsers, BasicAsyncTask.ErrorType>>() {
          @Override
          public void onTaskComplete(Pair<GroupUsers, BasicAsyncTask.ErrorType> result) {
            if (result.second == BasicAsyncTask.ErrorType.NoError){
              handleGroupUsers(result.first);
            } else if (result.second == BasicAsyncTask.ErrorType.NoInternetConnection){
              mView.setNoInternetConnectionToolbarLabel();
              onLoad();
            } else {
              mView.openMainActivity();
            }
          }
        };


    new BasicAsyncTask<GroupUsers>(
        NetworkService.getGetGroupUsersCall(mDialogId),
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
        if (result.second == BasicAsyncTask.ErrorType.NoError){
          mView.openMainActivity();
        } else if (result.second == BasicAsyncTask.ErrorType.NoInternetConnection){
          mView.showAlert("No internet connection", "Error");
        } else {
          mView.openChatActivity();
        }
      }
    };

    new BasicAsyncTask<ResponseBody>(
        NetworkService.getLeaveGroupCall(mDialogId),
        mContext,
        false,
        callback
    ).execute();
  }

  @Override
  public void onAddButtonClick() {

  }

  private void handleGroupUsers(GroupUsers groupUsers){
    ArrayList<GroupUser> users = groupUsers.getUsers();
    int usersCount = users.size();

    String[] ids = new String[usersCount + 1];
    long[] timestamps = new long[usersCount + 1];
    ids[0] = groupUsers.getOriginatorId();
    timestamps[0] = -1;

    for (int i = 1; i < usersCount + 1; ++i){
      ids[i] = users.get(i-1).getUserId();
      timestamps[i] = users.get(i-1).getJoinTime();
    }

    handleIds(ids, timestamps);
  }

  private void handleIds(String[] ids, long[] timestamps){
    try {
      boolean repeat = false;
      ArrayList<User> users = new ArrayList<>(ids.length);

      for (int i = 0; i < ids.length; ++i) {
        Pair<DialogName, BasicAsyncTask.ErrorType> result =
            new BasicAsyncTask<DialogName>(
            NetworkService.getGetDialogNameCall(ids[i]),
            null,
            false,
            null
        ).execute().get();

        if (result.second == BasicAsyncTask.ErrorType.NoError){
          String username = result.first.getDialogName();
          Bitmap bitmap = CircleBitmapFactory.generateCircleBitmap(
              mContext,
              CircleBitmapFactory.getMaterialColor(ids[i].hashCode()),
              55,
              CircleBitmapFactory.getFirstLetter(username));

          String date;
          if (i == 0){
            date = "group creator";
          } else {
            date = TimestampHelper.formatTimestampToDate(timestamps[i]);
          }

          users.add(new User(bitmap, ids[i], username, date));
          mView.setCommonToolbarLabel();
        } else if (result.second == BasicAsyncTask.ErrorType.NoInternetConnection){
          mView.setNoInternetConnectionToolbarLabel();
          repeat = true;
          break;
        } else {
          mView.openMainActivity();
        }
      }

      if (repeat){
        handleIds(ids, timestamps);
      } else {
        mUsersDataModel.setUsers(users);
      }
    } catch (InterruptedException | ExecutionException e){
      mView.openMainActivity();
    }
  }
}
