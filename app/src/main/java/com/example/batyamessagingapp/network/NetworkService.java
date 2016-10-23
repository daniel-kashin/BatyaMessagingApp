package com.example.batyamessagingapp.network;

import android.app.Service;

import com.example.batyamessagingapp.util.Message;
import com.example.batyamessagingapp.util.NamePassword;
import com.example.batyamessagingapp.util.Token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Кашин on 23.10.2016.
 */

//todo: extends Service
    //http://startandroid.ru/ru/uroki/vse-uroki-spiskom/157-urok-92-service-prostoj-primer.html

public class NetworkService {


    /* inner classes */

    public static class ServiceGenerator {
        private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        private static Retrofit.Builder builder =
                new Retrofit.Builder()
                        .baseUrl(API_BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create());
        public static <S> S createService(Class<S> serviceClass) {
            Retrofit retrofit = builder.client(httpClient.build()).build();
            return retrofit.create(serviceClass);
        }
    }

    public interface APIService {
        @POST("/login")
        Call<Token> login(@Body NamePassword body);

        @POST("/register")
        Call<Token> register(@Body NamePassword body);

        //todo: создать классы:
        //http://stackoverflow.com/questions/31112124/getting-simple-json-object-response-using-retrofit-library
        //http://stackoverflow.com/questions/32942661/how-can-retrofit-2-0-parse-nested-json-object

        //todo: изменить hashmap
        @GET("GET /{token}/contacts/{offset}")
        Call<HashMap<String,Message>> getUsers(@Path("token") Token token, @Path("offset") String offset);

        //todo: изменить лист
        @GET ("/{token}/messages/{dialog_id}/unread")
        Call<ArrayList<Message>> getMessages(@Path("token") Token token, @Path("dialog_id") String dialog_id);
    }



    /*static fields*/

    private static APIService _apiService = null;
    private static Token _token = null;
    private static boolean _isLoggedIn = false;
    private static boolean _isBuilt = false;
    public static final String API_BASE_URL = "http://hui.com";

    public static boolean isLoggedIn(){
        return _isLoggedIn;
    }

    /*methods*/

    static void Build(){
        _apiService = ServiceGenerator.createService(APIService.class);
    }

    static void Login(String name, String password){

        NamePassword namePassword = new NamePassword(name,password);

        Call<Token> call = _apiService.login(namePassword);
        call.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                if (response.isSuccessful()) {

                    _isLoggedIn = true;
                    _token = response.body();

                } else {
                    // error response, no access to resource?
                }
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                // something went completely south (like no internet connection)
            }
        });
    }

    static void Register(String name, String password){

        NamePassword namePassword = new NamePassword(name,password);

        Call<Token> call = _apiService.register(namePassword);
        call.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                if (response.isSuccessful()) {

                    _isLoggedIn = true;
                    _token = response.body();

                } else {
                    // error response, no access to resource?
                }
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                // something went completely south (like no internet connection)
            }
        });
    }

    static void GetUsers(){

        int offset = 0;

        Call<HashMap<String,Message>> call = _apiService.getUsers(_token, ("offset/" + offset));
        call.enqueue(new Callback<HashMap<String,Message>>() {
            @Override
            public void onResponse(Call<HashMap<String,Message>> call, Response<HashMap<String,Message>> response) {
                if (response.isSuccessful()) {

                    _isLoggedIn = true;

                } else {
                    // error response, no access to resource?
                }
            }

            @Override
            public void onFailure(Call<HashMap<String,Message>> call, Throwable t) {
                // something went completely south (like no internet connection)
            }
        });

    }

    static void Exit(){
        _isLoggedIn = false;
        _token = null;
    }

}

