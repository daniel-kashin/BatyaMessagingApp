package com.example.batyamessagingapp.activity.main.presenter;

import android.content.Context;
import android.os.AsyncTask;

import com.example.batyamessagingapp.activity.main.view.MainView;

import okhttp3.ResponseBody;


/**
 * Created by Кашин on 29.10.2016.
 */

public class MainService implements MainPresenter {

    private MainView mView;
    private Context mContext;

    public MainService(MainView mainView){
        mView = mainView;
        mContext = (Context) mainView;
    }


}