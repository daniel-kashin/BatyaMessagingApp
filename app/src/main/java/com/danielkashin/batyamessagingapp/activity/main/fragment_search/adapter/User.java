package com.danielkashin.batyamessagingapp.activity.main.fragment_search.adapter;

/**
 * Created by Кашин on 18.12.2016.
 */

public class User {

    private final String id;
    private final String username;

    public User(String id, String username){
        this.id = id;
        this.username = username;
    }

    public String getId(){
        return id;
    }

    public String getUsername(){
        return username;
    }
}
