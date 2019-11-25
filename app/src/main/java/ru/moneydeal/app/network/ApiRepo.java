package ru.moneydeal.app.network;

import android.content.Context;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;
import ru.moneydeal.app.ApplicationModified;

public class ApiRepo {
    private final UserApi mUserApi;
    private final OkHttpClient mOkHttpClient;

    public ApiRepo() {
        mOkHttpClient = new OkHttpClient()
                .newBuilder()
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create())
                .baseUrl("http://10.0.2.2:8080/")
                .client(mOkHttpClient)
                .build();

        mUserApi = retrofit.create(UserApi.class);
    }

    public UserApi getUserApi() {
        return mUserApi;
    }

    public static ApiRepo from(Context context) {
        return ApplicationModified.from(context).getApis();
    }
}

