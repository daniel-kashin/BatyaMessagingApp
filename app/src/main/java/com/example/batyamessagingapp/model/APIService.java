package com.example.batyamessagingapp.model;

import com.example.batyamessagingapp.util.LoginData;
import com.example.batyamessagingapp.util.Message;
import com.example.batyamessagingapp.util.Token;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Кашин on 29.10.2016.
 */

public interface APIService {
    @POST("/login")
    Call<Token> login(@Body LoginData body);

    @POST("/register")
    Call<Token> register(@Body LoginData body);

    //todo: изменить hashmap
    @GET("GET /{token}/contacts/{offset}")
    Call<HashMap<String, Message>> getUsers(@Path("token") Token token, @Path("offset") String offset);

    //todo: изменить лист
    @GET("/{token}/messages/{dialog_id}/unread")
    Call<ArrayList<Message>> getMessages(@Path("token") Token token, @Path("dialog_id") String dialog_id);
}
