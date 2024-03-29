package ru.moneydeal.app.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class ApiRepo {
    private final UserApi mUserApi;
    private final GroupApi mGroupApi;
    private final CheckApi mCheckApi;
    private final OkHttpClient mOkHttpClient;

    public ApiRepo(AuthorizationTokenInterceptor.ITokenRepo tokenRepo) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();

        mOkHttpClient = new OkHttpClient()
                .newBuilder()
                .addInterceptor(logging)
                .addInterceptor(new AuthorizationTokenInterceptor(tokenRepo))
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create())
                .baseUrl("http://192.168.43.160:8080/api/")
                .client(mOkHttpClient)
                .build();

        mUserApi = retrofit.create(UserApi.class);
        mGroupApi = retrofit.create(GroupApi.class);
        mCheckApi = retrofit.create(CheckApi.class);
    }

    public UserApi getUserApi() {
        return mUserApi;
    }

    public GroupApi getGroupApi() {
        return mGroupApi;
    }

    public CheckApi getCheckApi() {
        return mCheckApi;
    }
}
