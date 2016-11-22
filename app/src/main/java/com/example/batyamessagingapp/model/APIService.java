package com.example.batyamessagingapp.model;

import com.example.batyamessagingapp.model.pojo.PojoAPIAnswer;
import com.example.batyamessagingapp.model.pojo.PojoLoginData;
import com.example.batyamessagingapp.model.pojo.PojoMessage;
import com.example.batyamessagingapp.model.pojo.PojoMessageArray;
import com.example.batyamessagingapp.model.pojo.PojoToken;

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
    Call<PojoToken> login(@Body PojoLoginData body);

    @POST("/register")
    Call<PojoToken> register(@Body PojoLoginData body);

    @POST ("/{token}/logout")
    Call<PojoAPIAnswer> logout(@Path("token") String token);

    @POST ("/{token}/logoutall")
    Call<PojoAPIAnswer> fullLogout(@Path("token") String token);

    @POST ("/{token}/pojoMessages/send/{dialog_id}")
    Call<ResponseBody> sendMessage(@Path("token") String token,
                                   @Path("dialog_id") String dialogId,
                                   @Body PojoMessage pojoMessage);

    @GET("/{token}/pojoMessages/{dialog_id}/limit/{limit}/skip/{offset}")
    Call<PojoMessageArray> getMessages(@Path("token") String token,
                                       @Path("dialog_id") String dialogId,
                                       @Path("limit") int limit,
                                       @Path("offset") int offset);

    //TODO add another
}
