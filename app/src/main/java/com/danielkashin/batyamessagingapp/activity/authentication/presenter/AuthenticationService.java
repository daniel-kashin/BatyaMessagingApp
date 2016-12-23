package com.danielkashin.batyamessagingapp.activity.authentication.presenter;

import android.content.Context;
import android.util.Pair;

import com.danielkashin.batyamessagingapp.R;
import com.danielkashin.batyamessagingapp.activity.authentication.view.AuthenticationView;
import com.danielkashin.batyamessagingapp.model.BasicAsyncTask;
import com.danielkashin.batyamessagingapp.model.NetworkService;
import com.danielkashin.batyamessagingapp.model.PreferencesService;
import com.danielkashin.batyamessagingapp.model.pojo.Token;

import retrofit2.Call;

/**
 * Created by Кашин on 29.10.2016.
 */

public class AuthenticationService implements AuthenticationPresenter {

  private Context mContext;
  private AuthenticationView mView;

  public AuthenticationService(AuthenticationView view) {
    mView = view;
    this.mContext = (Context) view;
  }

  @Override
  public void onButtonClick(int id) {

    final String username = mView.getUsername();
    String password = mView.getPassword();

    BasicAsyncTask.AsyncTaskCompleteListener<Pair<Token, BasicAsyncTask.ErrorType>> callback;
    Call<Token> call;
    final String errorMessage;


    if (mView.checkInputs()) {
      if (id == R.id.authentication_auth_button) {
        errorMessage = "Invalid username or password";
        call = NetworkService.getAuthCall(username, password);
      } else {
        errorMessage = "Chosen username is already exists";
        call = NetworkService.getRegisterCall(username, password);
      }

      callback = new BasicAsyncTask.AsyncTaskCompleteListener<Pair<Token, BasicAsyncTask.ErrorType>>() {
        @Override
        public void onTaskComplete(Pair<Token, BasicAsyncTask.ErrorType> result) {
          if (result.second == BasicAsyncTask.ErrorType.NoError) {
            PreferencesService.saveTokenAndUsernameToPreferences(result.first, username);
            mView.openDialogsActivity();
          } else if (result.second == BasicAsyncTask.ErrorType.NoInternetConnection) {
            mView.showAlert(mContext.getString(R.string.no_internet_connection), "Error");
          } else { //error occured
            mView.showAlert(errorMessage, "Error");
          }
        }
      };

      new BasicAsyncTask<Token>(call, mContext, true, callback).execute();
    }
  } // on button click
}

