package com.danielkashin.batyamessagingapp.model;

import com.danielkashin.batyamessagingapp.model.pojo.WordsData;
import com.danielkashin.batyamessagingapp.model.pojo.GroupId;
import com.danielkashin.batyamessagingapp.model.pojo.DialogArray;
import com.danielkashin.batyamessagingapp.model.pojo.DialogName;
import com.danielkashin.batyamessagingapp.model.pojo.GroupUsers;
import com.danielkashin.batyamessagingapp.model.pojo.Message;
import com.danielkashin.batyamessagingapp.model.pojo.APIAnswer;
import com.danielkashin.batyamessagingapp.model.pojo.LoginData;
import com.danielkashin.batyamessagingapp.model.pojo.MessageArray;
import com.danielkashin.batyamessagingapp.model.pojo.NewUsername;
import com.danielkashin.batyamessagingapp.model.pojo.OldNewPasswordPair;
import com.danielkashin.batyamessagingapp.model.pojo.Timestamp;
import com.danielkashin.batyamessagingapp.model.pojo.Token;
import com.danielkashin.batyamessagingapp.model.pojo.UserIds;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Кашин on 29.10.2016.
 */

public interface APIProvider {

  @POST("/login")
  Call<Token> login(@Body LoginData body);

  @POST("/register")
  Call<Token> register(@Body LoginData body);

  @POST("/{token}/logout")
  Call<APIAnswer> logout(@Path("token") String token);

  @POST("/{token}/logoutall")
  Call<APIAnswer> fullLogout(@Path("token") String token);

  @POST("/{token}/messages/send/{dialog_id}")
  Call<Timestamp> sendMessage(@Path("token") String token,
                              @Path("dialog_id") String dialogId,
                              @Body Message message);

  @GET("/{token}/messages/{dialog_id}/limit/{limit}/skip/{offset}")
  Call<MessageArray> getMessages(@Path("token") String token,
                                 @Path("dialog_id") String dialogId,
                                 @Path("limit") int limit,
                                 @Path("offset") int offset);

  @GET("/{token}/contacts/offset/{offset}")
  Call<DialogArray> getDialogs(@Path("token") String token,
                               @Path("offset") int offset);

  @GET("/{token}/name/{dialog_id}")
  Call<DialogName> getDialogName(@Path("token") String token,
                                 @Path("dialog_id") String dialogId);

  @GET("/{token}/search_users/{search_request}")
  Call<UserIds> getSearchedUsers(@Path("token") String token,
                                 @Path("search_request") String searchRequest);

  @POST("/{token}")
  Call<ResponseBody> changePassword(@Path("token") String token,
                                    @Body OldNewPasswordPair oldNewPasswordPair);

  @POST("/{token}/name/{dialog_id}")
  Call<ResponseBody> changeUsername(@Path("token") String token,
                                    @Path("dialog_id") String dialogId,
                                    @Body NewUsername newUsername);

  @POST("/{token}/name")
  Call<ResponseBody> changeUsername(@Path("token") String token,
                                    @Body NewUsername newUsername);

  @POST("/{token}/conferences/create")
  Call<GroupId> getNewGroupId(@Path("token") String token);

  @POST("/{token}/conferences/{group_id}/user_list")
  Call<GroupUsers> getGroupUsers(@Path("token") String token,
                                 @Path("group_id") String groupId);

  @POST("/{token}/conferences/{group_id}/leave")
  Call<ResponseBody> leaveGroup(@Path("token") String token,
                                @Path("group_id") String groupId);

  @POST("/{token}/conferences/{group_id}/kick/{user_id}")
  Call<ResponseBody> kickUserFromGroup(@Path("token") String token,
                                       @Path("group_id") String groupId,
                                       @Path("user_id") String userId);

  @POST("/{token}/conferences/{group_id}/invite/{user_id}")
  Call<ResponseBody> inviteUserToGroup(@Path("token") String token,
                                       @Path("group_id") String groupId,
                                       @Path("user_id") String userId);

  @GET("/{token}/spelling/{user_id}/month/{year}/{month}")
  Call<WordsData> getWordsData(@Path("token") String token,
                               @Path("user_id") String userId,
                               @Path("year") String year,
                               @Path("month") String month);

  @GET("/{token}/spelling/{user_id}")
  Call<WordsData> getWordsData(@Path("token") String token,
                               @Path("user_id") String userId);
}


