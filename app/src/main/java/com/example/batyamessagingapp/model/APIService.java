package com.example.batyamessagingapp.model;

import com.example.batyamessagingapp.model.pojo.APIAnswer;
import com.example.batyamessagingapp.model.pojo.LoginData;
import com.example.batyamessagingapp.model.pojo.Message;
import com.example.batyamessagingapp.model.pojo.Token;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.ResponseBody;
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

    @POST ("/{token}/logout")
    Call<APIAnswer> logout(@Path("token") String token);

    @POST ("/{token}/logoutall")
    Call<APIAnswer> fullLogout(@Path("token") String token);

    @POST ("/{token}/messages/send/{dialog_id}")
    Call<ResponseBody> sendMessage(@Path("token") String token, @Path("dialog_id") String dialogId, @Body Message message);

    //todo: добавить несколько методов, разобраться с API

    //todo: изменить hashmap
    @GET("/{token}/contacts/{offset}")
    Call<HashMap<String, Message>> getUsers(@Path("token") Token token, @Path("offset") String offset);

    //todo: изменить лист
    @GET("/{token}/messages/{dialog_id}/unread")
    Call<ArrayList<Message>> getMessages(@Path("token") Token token, @Path("dialog_id") String dialogId);
}
