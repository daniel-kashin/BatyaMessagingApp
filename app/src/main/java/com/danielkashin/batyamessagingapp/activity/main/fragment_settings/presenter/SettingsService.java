package com.danielkashin.batyamessagingapp.activity.main.fragment_settings.presenter;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.util.Pair;

import com.danielkashin.batyamessagingapp.activity.main.fragment_settings.view.SettingsView;
import com.danielkashin.batyamessagingapp.model.APIService;
import com.danielkashin.batyamessagingapp.model.PreferencesService;
import com.danielkashin.batyamessagingapp.model.pojo.APIAnswer;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

import retrofit2.Response;

/**
 * Created by Кашин on 10.12.2016.
 */

public class SettingsService implements SettingsPresenter {

  private DisconnectionAsyncTask mDisconnectionAsyncTask;
  private Context mContext;
  private SettingsView mView;

  public SettingsService(SettingsView view, Context context) {
    mView = view;
    mContext = context;
  }

  @Override
  public void onResume() {
    mView.setCommonToolbarLabelText();
  }

  @Override
  public void onLogoutButtonClick() {
    mDisconnectionAsyncTask = new DisconnectionAsyncTask(DisconnectionType.Normal);
    mDisconnectionAsyncTask.execute();
  }

  @Override
  public void onFullLogoutButtonClick() {
    mDisconnectionAsyncTask = new DisconnectionAsyncTask(DisconnectionType.Full);
    mDisconnectionAsyncTask.execute();
  }

  private class DisconnectionAsyncTask
      extends AsyncTask<Void, Void, Pair<APIAnswer, ErrorType>> {
    private final DisconnectionType disconnectionType;

    DisconnectionAsyncTask(DisconnectionType disconnectionType) {
      this.disconnectionType = disconnectionType;
    }

    @Override
    protected void onPreExecute() {
      mView.startProgressDialog();
    }

    protected Pair<APIAnswer, ErrorType> doInBackground(Void... voids) {
      try {
        Response<APIAnswer> response;
        if (disconnectionType == DisconnectionType.Full) {
          response = APIService.getFullLogoutCall().execute();
        } else {
          response = APIService.getLogoutCall().execute();
        }

        APIAnswer apiAnswer = response.body();

        if (response.code() == 200 && apiAnswer != null) {
          return new Pair<>(apiAnswer, ErrorType.NoError);
        } else {
          return new Pair<>(null, ErrorType.NoAccess);
        }
      } catch (ConnectException | SocketTimeoutException e) {
        return new Pair<>(null, ErrorType.NoInternetConnection);
      } catch (IOException e) {
        return new Pair<>(null, ErrorType.NoAccess);
      }
    }

    protected void onPostExecute(Pair<APIAnswer, ErrorType> resultPair) {
      mView.stopProgressDialog();

      if (resultPair.second == ErrorType.NoInternetConnection
          && disconnectionType == DisconnectionType.Full) {
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
