package com.example.batyamessagingapp.presenter;

import android.content.Context;

import com.example.batyamessagingapp.Batya;
import com.example.batyamessagingapp.model.NetworkService;
import com.example.batyamessagingapp.view.ContactsView;

/**
 * Created by Кашин on 29.10.2016.
 */

public class ContactsService implements ContactsPresenter {

    private ContactsView view;
    private Context context;

    public ContactsService(ContactsView view, Context context){
        this.view = view;
        this.context = context;
    }

    public void onLogoutButtonClick(){
        NetworkService.deleteToken();
        view.openAuthenticationActivity();
    }

}