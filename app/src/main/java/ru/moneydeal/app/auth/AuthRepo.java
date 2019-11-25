package ru.moneydeal.app.auth;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.moneydeal.app.ApplicationModified;
import ru.moneydeal.app.network.ApiRepo;
import ru.moneydeal.app.network.UserApi;

@SuppressWarnings("WeakerAccess")
public class AuthRepo {

    private final ApiRepo mApiRepo;
    private final ApplicationModified mContext;
    public AuthRepo(ApiRepo apiRepo, ApplicationModified context) {
        mApiRepo = apiRepo;
        mContext = context;
    }

    @NonNull
    public static AuthRepo getInstance(Context context) {
        return ApplicationModified.from(context).getAuthRepo();
    }

    private MutableLiveData<AuthProgress> mAuthProgress;
    public LiveData<AuthProgress> login(@NonNull String login, @NonNull String password) {
        mAuthProgress = new MutableLiveData<>(AuthProgress.IN_PROGRESS);
        login(mAuthProgress, login, password);
        return mAuthProgress;
    }


    private void login(final MutableLiveData<AuthProgress> progress, @NonNull final String login, @NonNull final String password) {
        UserApi api = mApiRepo.getUserApi();

        api.register(login, password).enqueue(new Callback<UserApi.ResponsePlain>() {
            @Override
            public void onResponse(Call<UserApi.ResponsePlain> call,
                                   Response<UserApi.ResponsePlain> response) {
                if (response.body() != null) {
                    String token = response.body().token;


                    AsyncTask.execute(() -> {
                        mContext.getDB().getAuthDao().reset();

                        mContext.getDB().getAuthDao().reset();
                        AuthEntity authEntity = new AuthEntity(login, token);
                        mContext.getDB().getAuthDao().insert(authEntity);

                        List<AuthEntity> users = mContext.getDB().getAuthDao().getUser();
                        int userCount = users.size();
                        Log.d("AuthRepo", "user count: " + userCount);
                        if (userCount > 0) {
                            AuthEntity user = users.get(0);
                            Log.d("AuthRepo", "current user token: " + user.login + " " + user.token);
                        }
                    });
                }
                progress.postValue(AuthProgress.SUCCESS);
            }

            @Override
            public void onFailure(Call<UserApi.ResponsePlain> call, Throwable t) {
                progress.postValue(AuthProgress.FAILED);
            }
        });
    }


    public enum AuthProgress {
        IN_PROGRESS,
        SUCCESS,
        FAILED
    }
}
