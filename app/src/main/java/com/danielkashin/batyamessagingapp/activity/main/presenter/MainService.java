package com.danielkashin.batyamessagingapp.activity.main.presenter;

import android.content.Context;

import com.danielkashin.batyamessagingapp.activity.main.view.MainView;


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