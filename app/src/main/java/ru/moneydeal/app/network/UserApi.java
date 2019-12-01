package ru.moneydeal.app.network;


import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface UserApi {
    public static final String INVALID_LOGIN_OR_PASSWORD = "INVALID_LOGIN_OR_PASSWORD";
    public static final String WRONG_LOGIN_OR_PASSWORD = "WRONG_LOGIN_OR_PASSWORD";
    public static final String LOGIN_ALREADY_EXISTS = "LOGIN_ALREADY_EXISTS";

    public static class User {
        public String login;
        public String first_name;
        public String last_name;
    }

    public static class AuthData {
        public String token;
        public User user;
    }

    public static class AuthResponse extends BaseResponse {
        public AuthData data;
    }

    @FormUrlEncoded
    @POST("register")
    Call<AuthResponse> register(
            @Field("login") String login,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("login")
    Call<AuthResponse> login(
            @Field("login") String login,
            @Field("password") String password
    );

    @GET("check_auth")
    Call<AuthResponse> checkAuth();
}
