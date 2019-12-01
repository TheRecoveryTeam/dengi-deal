package ru.moneydeal.app.network;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class ApiRepo {
    private final UserApi mUserApi;
    private final OkHttpClient mOkHttpClient;

    public ApiRepo(AuthorizationTokenInterceptor.ITokenRepo tokenRepo) {
        mOkHttpClient = new OkHttpClient()
                .newBuilder()
                .addInterceptor(new AuthorizationTokenInterceptor(tokenRepo))
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create())
                .baseUrl("http://192.168.1.15:8080/api/")
                .client(mOkHttpClient)
                .build();

        mUserApi = retrofit.create(UserApi.class);
    }

    public UserApi getUserApi() {
        return mUserApi;
    }
}
