package ru.moneydeal.app;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.moneydeal.app.network.ApiRepo;
import ru.moneydeal.app.network.UserApi;

@SuppressWarnings("WeakerAccess")
public class AuthRepo {

    private final ApiRepo mApiRepo;
    public AuthRepo(ApiRepo apiRepo) {
        mApiRepo = apiRepo;
    }

    @NonNull
    public static AuthRepo getInstance(Context context) {
        return ApplicationModified.from(context).getAuthRepo();
    }

    private String mCurrentUser;
    private MutableLiveData<AuthProgress> mAuthProgress;

    public LiveData<AuthProgress> login(@NonNull String login, @NonNull String password) {
        if (TextUtils.equals(login, mCurrentUser) && mAuthProgress.getValue() == AuthProgress.IN_PROGRESS) {
            return mAuthProgress;
        } else if (!TextUtils.equals(login, mCurrentUser) && mAuthProgress != null) {
            mAuthProgress.postValue(AuthProgress.FAILED);
        }
        mCurrentUser = login;
        mAuthProgress = new MutableLiveData<>(AuthProgress.IN_PROGRESS);
        login(mAuthProgress, login, password);
        return mAuthProgress;
    }


    private void login(final MutableLiveData<AuthProgress> progress, @NonNull final String login, @NonNull final String password) {
        UserApi api = mApiRepo.getUserApi();

        api.register(new UserApi.UserPlain(login, password)).enqueue(new Callback<UserApi.ResponsePlain>() {
            @Override
            public void onResponse(Call<UserApi.ResponsePlain> call,
                                   Response<UserApi.ResponsePlain> response) {
                Log.d("ocko", response.toString());
                mAuthProgress.postValue(AuthProgress.SUCCESS);
            }

            @Override
            public void onFailure(Call<UserApi.ResponsePlain> call, Throwable t) {
                Log.d("ocko1", "jopa1", t);
                mAuthProgress.postValue(AuthProgress.FAILED);
            }
        });
    }


    enum AuthProgress {
        IN_PROGRESS,
        SUCCESS,
        FAILED
    }
}
