package com.example.batyamessagingapp.model;

import com.example.batyamessagingapp.model.pojo.ConferenceId;
import com.example.batyamessagingapp.model.pojo.DialogArray;
import com.example.batyamessagingapp.model.pojo.DialogName;
import com.example.batyamessagingapp.model.pojo.Message;
import com.example.batyamessagingapp.model.pojo.APIAnswer;
import com.example.batyamessagingapp.model.pojo.LoginData;
import com.example.batyamessagingapp.model.pojo.MessageArray;
import com.example.batyamessagingapp.model.pojo.NewUsername;
import com.example.batyamessagingapp.model.pojo.OldNewPassword;
import com.example.batyamessagingapp.model.pojo.Timestamp;
import com.example.batyamessagingapp.model.pojo.Token;
import com.example.batyamessagingapp.model.pojo.UserIds;

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
    Call<Timestamp> sendMessage(@Path("token") String token,
                                @Path("dialog_id") String dialogId,
                                @Body Message message);

    @GET("/{token}/messages/{dialog_id}/limit/{limit}/skip/{offset}")
    Call<MessageArray> getMessages(@Path("token") String token,
                                   @Path("dialog_id") String dialogId,
                                   @Path("limit") int limit,
                                   @Path("offset") int offset);

    @GET("/{token}/contacts/offset/{offset}")
    Call<DialogArray> getDialogs(@Path("token") String token, @Path("offset") int offset);

    @GET("/{token}/name/{dialog_id}")
    Call<DialogName> getDialogName(@Path("token") String token, @Path("dialog_id") String dialogId);

    @GET("/{token}/search_users/{search_request}")
    Call<UserIds> getSearchedUsers(@Path("token") String token, @Path("search_request") String searchRequest);

    @POST("/{token}")
    Call<ResponseBody> changePassword(@Path("token") String token, @Body OldNewPassword oldNewPassword);

    @POST("/{token}/name/{dialog_id}")
    Call<ResponseBody> changeUsername(@Path("token") String token,
                                      @Path("dialog_id") String dialogId,
                                      @Body NewUsername newUsername);

    @POST("/{token}/name")
    Call<ResponseBody> changeUsername(@Path("token") String token,
                                      @Body NewUsername newUsername);

    @POST("/{token}/conferences/create")
    Call<ConferenceId> getNewConferenceId(@Path("token") String token);
}
