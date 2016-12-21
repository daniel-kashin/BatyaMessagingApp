package com.example.batyamessagingapp.model;

import com.example.batyamessagingapp.model.pojo.APIAnswer;
import com.example.batyamessagingapp.model.pojo.ConferenceId;
import com.example.batyamessagingapp.model.pojo.DialogArray;
import com.example.batyamessagingapp.model.pojo.DialogName;
import com.example.batyamessagingapp.model.pojo.LoginData;
import com.example.batyamessagingapp.model.pojo.Message;
import com.example.batyamessagingapp.model.pojo.MessageArray;
import com.example.batyamessagingapp.model.pojo.NewUsername;
import com.example.batyamessagingapp.model.pojo.OldNewPassword;
import com.example.batyamessagingapp.model.pojo.Timestamp;
import com.example.batyamessagingapp.model.pojo.Token;
import com.example.batyamessagingapp.model.pojo.UserIds;

import okhttp3.ResponseBody;
import retrofit2.Call;

import static com.example.batyamessagingapp.model.PreferencesService.getTokenFromPreferences;

/**
 * Created by Кашин on 15.11.2016.
 */

public class NetworkService {

    private static final String API_BASE_URL = "http://146.185.160.146:8080";
    private static APIService sApiService;


    static {
        Build();
    }


    static void Build() {
        sApiService = APIGenerator.createService(APIService.class, API_BASE_URL);
    }

    public static Call<Token> getAuthCall(final String username, final String password) {
        LoginData loginData = new LoginData(username, password);
        Call<Token> call = sApiService.login(loginData);
        return call;
    }

    public static Call<MessageArray> getGetMessagesCall(String dialogId, int limit, int offset){
        return sApiService.getMessages(getTokenFromPreferences(),dialogId,limit,offset);
    }

    public static Call<Token> getRegisterCall(final String username, final String password) {
        LoginData loginData = new LoginData(username, password);
        Call<Token> call = sApiService.register(loginData);
        return call;
    }

    public static Call<DialogArray> getGetDialogsCall(int offset){
        return sApiService.getDialogs(getTokenFromPreferences(), offset);
    }

    public static Call<APIAnswer> getLogoutCall() {
        return sApiService.logout(getTokenFromPreferences());
    }

    public static Call<APIAnswer> getFullLogoutCall() {
        return sApiService.fullLogout(getTokenFromPreferences());
    }

    public static Call<Timestamp> getSendMessageCall(
            String dialogId, String messageType, String messageData) {
        Message message = new Message(messageType,messageData);
        return sApiService.sendMessage(getTokenFromPreferences(), dialogId, message);
    }

    public static Call<DialogName> getGetDialogNameCall(String dialogId){
        return sApiService.getDialogName(getTokenFromPreferences(), dialogId);
    }

    public static Call<UserIds> getGetSearchedUsersCall(String dialogId){
        return sApiService.getSearchedUsers(getTokenFromPreferences(), dialogId);
    }

    public static Call<ResponseBody> getChangePasswordCall(String password, String newPassword){
        return  sApiService.changePassword(getTokenFromPreferences(), new OldNewPassword(password, newPassword));
    }

    public static Call<ResponseBody> getChangeUsernameCall(String newUsername, String dialogId){
        if (dialogId == null){
            return sApiService.changeUsername(getTokenFromPreferences(), new NewUsername(newUsername));
        } else {
            return sApiService.changeUsername(getTokenFromPreferences(), dialogId, new NewUsername(newUsername));
        }
    }

    public static Call<ConferenceId> getGetNewConferenceIdCall(){
        return sApiService.getNewConferenceId(getTokenFromPreferences());
    }
}
