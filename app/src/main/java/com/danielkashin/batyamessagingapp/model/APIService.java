package com.danielkashin.batyamessagingapp.model;

import com.danielkashin.batyamessagingapp.model.pojo.APIAnswer;
import com.danielkashin.batyamessagingapp.model.pojo.WordsData;
import com.danielkashin.batyamessagingapp.model.pojo.GroupId;
import com.danielkashin.batyamessagingapp.model.pojo.DialogArray;
import com.danielkashin.batyamessagingapp.model.pojo.DialogName;
import com.danielkashin.batyamessagingapp.model.pojo.GroupUsers;
import com.danielkashin.batyamessagingapp.model.pojo.LoginData;
import com.danielkashin.batyamessagingapp.model.pojo.Message;
import com.danielkashin.batyamessagingapp.model.pojo.MessageArray;
import com.danielkashin.batyamessagingapp.model.pojo.NewUsername;
import com.danielkashin.batyamessagingapp.model.pojo.OldNewPasswordPair;
import com.danielkashin.batyamessagingapp.model.pojo.Timestamp;
import com.danielkashin.batyamessagingapp.model.pojo.Token;
import com.danielkashin.batyamessagingapp.model.pojo.UserIds;

import okhttp3.ResponseBody;
import retrofit2.Call;

import static com.danielkashin.batyamessagingapp.model.PreferencesService.getTokenFromPreferences;

/**
 * Created by Кашин on 15.11.2016.
 */

public class APIService {

  private static final String API_BASE_URL = "http://146.185.160.146:8080";
  private static APIProvider sApiProvider;


  static {
    Build();
  }


  static void Build() {
    sApiProvider = APIGenerator.createService(APIProvider.class, API_BASE_URL);
  }

  public static Call<Token> getAuthCall(final String username, final String password) {
    LoginData loginData = new LoginData(username, password);
    Call<Token> call = sApiProvider.login(loginData);
    return call;
  }

  public static Call<MessageArray> getGetMessagesCall(String dialogId, int limit, int offset) {
    return sApiProvider.getMessages(getTokenFromPreferences(), dialogId, limit, offset);
  }

  public static Call<Token> getRegisterCall(final String username, final String password) {
    LoginData loginData = new LoginData(username, password);
    Call<Token> call = sApiProvider.register(loginData);
    return call;
  }

  public static Call<DialogArray> getGetDialogsCall(int offset) {
    return sApiProvider.getDialogs(getTokenFromPreferences(), offset);
  }

  public static Call<APIAnswer> getLogoutCall() {
    return sApiProvider.logout(getTokenFromPreferences());
  }

  public static Call<APIAnswer> getFullLogoutCall() {
    return sApiProvider.fullLogout(getTokenFromPreferences());
  }

  public static Call<Timestamp> getSendMessageCall(
      String dialogId, String messageType, String messageData) {
    Message message = new Message(messageType, messageData);
    return sApiProvider.sendMessage(getTokenFromPreferences(), dialogId, message);
  }

  public static Call<DialogName> getGetDialogNameCall(String dialogId) {
    return sApiProvider.getDialogName(getTokenFromPreferences(), dialogId);
  }

  public static Call<UserIds> getGetSearchedUsersCall(String dialogId) {
    return sApiProvider.getSearchedUsers(getTokenFromPreferences(), dialogId);
  }

  public static Call<ResponseBody> getChangePasswordCall(String password, String newPassword) {
    return sApiProvider.changePassword(getTokenFromPreferences(), new OldNewPasswordPair(password, newPassword));
  }

  public static Call<ResponseBody> getChangeUsernameCall(String newUsername, String dialogId) {
    if (dialogId == null) {
      return sApiProvider.changeUsername(getTokenFromPreferences(), new NewUsername(newUsername));
    } else {
      return sApiProvider.changeUsername(getTokenFromPreferences(), dialogId, new NewUsername(newUsername));
    }
  }

  public static Call<GroupId> getGetNewGroupIdCall() {
    return sApiProvider.getNewGroupId(getTokenFromPreferences());
  }

  public static Call<GroupUsers> getGetGroupUsersCall(String groupId) {
    return sApiProvider.getGroupUsers(getTokenFromPreferences(), groupId);
  }

  public static Call<ResponseBody> getLeaveGroupCall(String groupId) {
    return sApiProvider.leaveGroup(getTokenFromPreferences(), groupId);
  }

  public static Call<ResponseBody> getKickUserFromGroupCall(String groupId, String userId) {
    return sApiProvider.kickUserFromGroup(getTokenFromPreferences(), groupId, userId);
  }

  public static Call<ResponseBody> getInviteUserToGroupCall(String groupId, String userId) {
    return sApiProvider.inviteUserToGroup(getTokenFromPreferences(), groupId, userId);
  }

  public static Call<WordsData> getGetWordsDataCall(String userId, String year,
                                                    String month) {
    if (year == null || month == null) {
      return sApiProvider.getWordsData(getTokenFromPreferences(), userId);
    } else {
      return sApiProvider.getWordsData(getTokenFromPreferences(), userId, year, month);
    }
  }
}
