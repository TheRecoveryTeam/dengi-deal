package ru.moneydeal.app.network;


import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface UserApi {
     class ResponsePlain {
        public String token;
    }

    class ResponsePing {
        String hello;
    }

    @FormUrlEncoded
    @POST("register")
    Call<ResponsePlain> register(@Field("login") String login,
                                 @Field("password") String password);

    @GET("ping")
    Call<ResponsePing> ping();
}
