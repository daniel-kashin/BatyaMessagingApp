package com.example.batyamessagingapp.model;

import com.example.batyamessagingapp.model.pojo.PojoAPIAnswer;
import com.example.batyamessagingapp.model.pojo.PojoLoginData;
import com.example.batyamessagingapp.model.pojo.PojoMessage;
import com.example.batyamessagingapp.model.pojo.PojoMessageArray;
import com.example.batyamessagingapp.model.pojo.PojoToken;

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

    public static Call<PojoToken> getAuthCall(final String username, final String password) {
        PojoLoginData pojoLoginData = new PojoLoginData(username, password);
        Call<PojoToken> call = _apiService.login(pojoLoginData);
        return call;
    }

    public static Call<PojoMessageArray> getGetMessageCall(String dialogId, int limit, int offset){
        return _apiService.getMessages(getTokenValueFromPreferences(),dialogId,limit,offset);
    }

    public static Call<PojoToken> getRegisterCall(final String username, final String password) {
        PojoLoginData pojoLoginData = new PojoLoginData(username, password);
        Call<PojoToken> call = _apiService.register(pojoLoginData);
        return call;
    }

    public static Call<PojoAPIAnswer> getLogoutCall() {
        return _apiService.logout(getTokenValueFromPreferences());
    }

    public static Call<PojoAPIAnswer> getFullLogoutCall() {
        return _apiService.fullLogout(getTokenValueFromPreferences());
    }

    public static Call<ResponseBody> getSendMessageCall(
            String dialogId, String messageType, String messageData) {

        PojoMessage pojoMessage = new PojoMessage(messageType,messageData);
        return _apiService.sendMessage(getTokenValueFromPreferences(), dialogId, pojoMessage);
    }

    public void getUsers() {
/*
        int offset = 0;

        Call<HashMap<String, PojoMessage>> call = _apiService.getUsers(_token, ("offset/" + offset));
        call.enqueue(new Callback<HashMap<String, PojoMessage>>() {
            @Override
            public void onResponse(Call<HashMap<String, PojoMessage>> call, Response<HashMap<String, PojoMessage>> response) {
                if (response.isSuccessful()) {


                } else {
                    // error response, no access to resource?
                }
            }

            @Override
            public void onFailure(Call<HashMap<String, PojoMessage>> call, Throwable t) {
                // something went completely south (like no internet connection)
            }
        });
*/
    }
}
