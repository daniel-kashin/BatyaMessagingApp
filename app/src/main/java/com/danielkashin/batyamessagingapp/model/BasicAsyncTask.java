package com.danielkashin.batyamessagingapp.model;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Pair;

import com.danielkashin.batyamessagingapp.R;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Кашин on 22.12.2016.
 */

public class BasicAsyncTask<T> extends AsyncTask<Void, Void, Pair<T, BasicAsyncTask.ErrorType>> {

  private Call<T> call;
  private AsyncTaskCompleteListener<Pair<T, ErrorType>> callback;
  private Context context;
  private boolean isCancellable;
  private ProgressDialog progressDialog;

  public BasicAsyncTask(Call<T> call, Context context, boolean isCancellable,
                        AsyncTaskCompleteListener<Pair<T, ErrorType>> callback) {
    this.call = call;
    this.context = context;
    this.isCancellable = isCancellable;
    this.callback = callback;
  }

  @Override
  protected void onPreExecute() {
    if (context != null) {
      progressDialog = new ProgressDialog(context);
      progressDialog.setMessage(context.getString(R.string.loading));
      progressDialog.setCancelable(isCancellable);
      progressDialog.setIndeterminate(true);
      progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
      if (isCancellable){
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
          @Override
          public void onCancel(DialogInterface dialog) {
            cancel(true);
          }
        });
      }
      progressDialog.show();
    }
  }

  @Override
  protected Pair<T, ErrorType> doInBackground(Void... params) {
    try {
      Response<T> response = call.execute();

      T responseBody = response.body();

      if (response.code() == 200 &&  responseBody != null) {
        return new Pair<>(responseBody, ErrorType.NoError);
      } else {
        return new Pair<>(null, ErrorType.NoAccess);
      }
    } catch (ConnectException | SocketTimeoutException e) {
      return new Pair<>(null, ErrorType.NoInternetConnection);
    } catch (IOException e) {
      return new Pair<>(null, ErrorType.NoAccess);
    }
  }

  @Override
  protected void onPostExecute(Pair<T, ErrorType> result) {
    if (context != null && progressDialog.isShowing()) {
      progressDialog.dismiss();
    }
    if (callback != null) callback.onTaskComplete(result);
  }


  public enum ErrorType {
    NoInternetConnection,
    NoAccess,
    NoError
  }

  public interface AsyncTaskCompleteListener<T> {
    public void onTaskComplete(T result);
  }
}
