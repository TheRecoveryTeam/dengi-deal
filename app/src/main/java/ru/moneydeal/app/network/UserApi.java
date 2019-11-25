package ru.moneydeal.app.network;

import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface UserApi {
    public class UserPlain {
        public String login;
        public String password;

        public UserPlain(String login, String password) {
            login = login;
            password = password;
        }
    }

    class ResponsePlain {
        public String token;
    }

    class ResponsePing {
        String hello;
    }

    @POST("register")
    Call<ResponsePlain> register(@Body UserPlain user);

    @GET("ping")
    Call<ResponsePing> ping();
}
