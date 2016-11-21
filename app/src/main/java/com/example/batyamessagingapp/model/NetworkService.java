package com.example.batyamessagingapp.model;

import com.example.batyamessagingapp.model.pojo.APIAnswer;
import com.example.batyamessagingapp.model.pojo.LoginData;
import com.example.batyamessagingapp.model.pojo.Message;
import com.example.batyamessagingapp.model.pojo.MessageArray;
import com.example.batyamessagingapp.model.pojo.Token;

import okhttp3.ResponseBody;
import retrofit2.Call;

import static com.example.batyamessagingapp.model.PreferencesService.getTokenValueFromPreferences;

/**
 * Created by Кашин on 15.11.2016.
 */

public class NetworkService {

    private static final String API_BASE_URL = "http://146.185.160.146:8080";
    private static APIService _apiService;

    static {
        Build();
    }


    static void Build() {
        _apiService = APIGenerator.createService(APIService.class, API_BASE_URL);
    }

    public static Call<Token> getAuthCall(final String username, final String password) {
        LoginData loginData = new LoginData(username, password);
        Call<Token> call = _apiService.login(loginData);
        return call;
    }

    public static Call<MessageArray> getGetMessageCall(String dialogId, int limit, int offset){
        return _apiService.getMessages(getTokenValueFromPreferences(),dialogId,limit,offset);
    }

    public static Call<Token> getRegisterCall(final String username, final String password) {
        LoginData loginData = new LoginData(username, password);
        Call<Token> call = _apiService.register(loginData);
        return call;
    }

    public static Call<APIAnswer> getLogoutCall() {
        return _apiService.logout(getTokenValueFromPreferences());
    }

    public static Call<APIAnswer> getFullLogoutCall() {
        return _apiService.fullLogout(getTokenValueFromPreferences());
    }

    public static Call<ResponseBody> getSendMessageCall(
            String dialogId, String messageType, String messageData) {

        Message message = new Message(messageType,messageData);
        return _apiService.sendMessage(getTokenValueFromPreferences(), dialogId, message);
    }

    public void getUsers() {
/*
        int offset = 0;

        Call<HashMap<String, Message>> call = _apiService.getUsers(_token, ("offset/" + offset));
        call.enqueue(new Callback<HashMap<String, Message>>() {
            @Override
            public void onResponse(Call<HashMap<String, Message>> call, Response<HashMap<String, Message>> response) {
                if (response.isSuccessful()) {


                } else {
                    // error response, no access to resource?
                }
            }

            @Override
            public void onFailure(Call<HashMap<String, Message>> call, Throwable t) {
                // something went completely south (like no internet connection)
            }
        });
*/
    }
}
