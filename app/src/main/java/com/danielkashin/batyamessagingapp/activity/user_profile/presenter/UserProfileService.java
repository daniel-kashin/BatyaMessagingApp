package com.danielkashin.batyamessagingapp.activity.user_profile.presenter;

import android.os.AsyncTask;
import android.util.Pair;

import com.danielkashin.batyamessagingapp.activity.user_profile.adapter.GrammarData;
import com.danielkashin.batyamessagingapp.activity.user_profile.adapter.GrammarDataModel;
import com.danielkashin.batyamessagingapp.activity.user_profile.view.UserProfileView;
import com.danielkashin.batyamessagingapp.model.APIService;
import com.danielkashin.batyamessagingapp.model.BasicAsyncTask;
import com.danielkashin.batyamessagingapp.model.pojo.WordsData;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Response;


/**
 * Created by Кашин on 25.12.2016.
 */

public class UserProfileService implements UserProfilePresenter {

  private boolean mStopLoading;
  private String mDialogId;

  private UserProfileView mView;
  private GrammarDataModel mDataModel;

  public UserProfileService(UserProfileView view, String dialogId, GrammarDataModel dataModel) {
    mView = view;
    mDialogId = dialogId;
    mDataModel = dataModel;
  }

  @Override
  public void onLoad() {
    Calendar c = Calendar.getInstance();
    int actualYear = c.get(Calendar.YEAR);
    int actualMonth = c.get(Calendar.MONTH);

    mView.setLoadingToolbarLabel();
    GetGrammarDataAsyncTask task = new GetGrammarDataAsyncTask(actualMonth, actualYear);
    task.execute();
  }

  private class GetGrammarDataAsyncTask extends AsyncTask
      <Void, Void, Pair<ArrayList<WordsData>, BasicAsyncTask.ErrorType>> {

    private int currentMonth;
    private int currentYear;

    private GetGrammarDataAsyncTask(int currentMonth, int currentYear) {
      this.currentMonth = currentMonth;
      this.currentYear = currentYear;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected Pair<ArrayList<WordsData>, BasicAsyncTask.ErrorType> doInBackground(Void... params) {
      int month = 11;
      int year = 2016;
      ArrayList<WordsData> data = new ArrayList<>(3);

      while (currentYear >= year && currentMonth >= month) {
        try {
          Response<WordsData> response;
          response = APIService.getGetWordsDataCall(mDialogId, "" + year, "" + month).execute();

          if (response.code() == 200) {
            month = (month + 1) % 12;
            if (month == 0) year++;
            data.add(response.body());
          } else {
            throw new IOException();
          }
        } catch (ConnectException e) {
          return new Pair<>(null, BasicAsyncTask.ErrorType.NoInternetConnection);
        } catch (IOException e) {
          return new Pair<>(null, BasicAsyncTask.ErrorType.NoAccess);
        }
      }

      return new Pair<>(data, BasicAsyncTask.ErrorType.NoError);
    }

    @Override
    protected void onPostExecute(Pair<ArrayList<WordsData>, BasicAsyncTask.ErrorType> result) {
      if (result.second == BasicAsyncTask.ErrorType.NoError) {
        ArrayList<GrammarData> dataToSet = new ArrayList<>();

        long wordsOverall = 0;
        long rightWordsOverall = 0;

        for (int i = 0; i < result.first.size(); ++i) {
          WordsData wordsData = result.first.get(i);

          String date;
          wordsOverall += wordsData.getTotalWords();
          rightWordsOverall += wordsData.getCorrectWords();
          date = (currentMonth + 1) + "." + currentYear;

          dataToSet.add(new GrammarData(
              wordsData.getCorrectWords(),
              wordsData.getTotalWords(),
              date
          ));
        }

        dataToSet.add(0, new GrammarData(
            rightWordsOverall,
            wordsOverall,
            "Overall"
        ));

        mView.setCommonToolbarLabel();
        mDataModel.setData(dataToSet);
      } else if (result.second == BasicAsyncTask.ErrorType.NoAccess) {
        mView.openChatActivity();
      } else {
        mView.setNoInternetConnectionToolbarLabel();
        GetGrammarDataAsyncTask task = new GetGrammarDataAsyncTask(currentYear, currentMonth);
        task.execute();
      }
    }
  }
}
