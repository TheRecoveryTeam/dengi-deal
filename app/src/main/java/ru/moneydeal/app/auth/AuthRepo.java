package ru.moneydeal.app.auth;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import ru.moneydeal.app.ApplicationModified;
import ru.moneydeal.app.network.ApiRepo;
import ru.moneydeal.app.network.BaseResponse;
import ru.moneydeal.app.network.ErrorResponse;
import ru.moneydeal.app.network.ResponseCallback;
import ru.moneydeal.app.network.UserApi;

@SuppressWarnings("WeakerAccess")
public class AuthRepo {

    private final TokenRepo mTokenRepo;
    private final ApiRepo mApiRepo;
    private final AuthDao mAuthDao;

    private MutableLiveData<AuthProgress> mAuthProgress;

    public AuthRepo(ApplicationModified context) {
        mTokenRepo = context.getTokenRepo();
        mApiRepo = context.getApis();
        mAuthDao = context.getDB().getAuthDao();
        mAuthProgress = new MutableLiveData<>(AuthProgress.IN_PROGRESS);
    }

    @NonNull
    public static AuthRepo getInstance(Context context) {
        return ApplicationModified.from(context).getAuthRepo();
    }

    public LiveData<AuthProgress> checkAuth() {
        mAuthProgress.setValue(AuthProgress.IN_PROGRESS);

        AsyncTask.execute(() -> {
            List<AuthEntity> users = mAuthDao.getUsers();

            if (users.size() == 0) {
                mAuthProgress.postValue(AuthProgress.FAILED);
                Log.d("AuthRepo", "has no user");
                return;
            }

            UserApi api = mApiRepo.getUserApi();
            api.checkAuth().enqueue(new ResponseCallback<UserApi.AuthResponse>(UserApi.AuthResponse.class) {
                @Override
                public void onOk(UserApi.AuthResponse response) {
                    saveUserData(response.data);
                    mAuthProgress.postValue(AuthProgress.SUCCESS);
                }

                @Override
                public void onError(ErrorResponse response) {
                    Log.d("AuthRepo", "check auth failed " + response.data.message);
                    mAuthProgress.postValue(AuthProgress.FAILED);
                }
            });
        });

        return mAuthProgress;
    }

    public LiveData<AuthProgress> register(@NonNull String login, @NonNull String password) {
        mAuthProgress.setValue(AuthProgress.IN_PROGRESS);
        register(mAuthProgress, login, password);
        return mAuthProgress;
    }

    private void register(final MutableLiveData<AuthProgress> progress, @NonNull final String login, @NonNull final String password) {
        UserApi api = mApiRepo.getUserApi();

        api.register(login, password).enqueue(new ResponseCallback<UserApi.AuthResponse>(UserApi.AuthResponse.class) {
            @Override
            public void onOk(UserApi.AuthResponse response) {
                saveUserData(response.data);
                mAuthProgress.postValue(AuthProgress.SUCCESS);
            }

            @Override
            public void onError(ErrorResponse response) {
                Log.d("AuthRepo", "register failed " + response.data.message);
                mAuthProgress.postValue(AuthProgress.FAILED);
            }
        });
    }

    private void saveUserData(UserApi.AuthData data) {
        AsyncTask.execute(() -> {
            String token = data.token;
            if (token == null) {
                token = mTokenRepo.getToken();
            }

            mAuthDao.reset();
            AuthEntity authEntity = new AuthEntity(
                    data.user.login,
                    data.user.first_name,
                    data.user.last_name,
                    token
            );

            Log.d("AuthRepo", "save user data:\n" + data.user.first_name + "\n" + data.user.last_name + "\n" + data.user.login + "\n" + token);

            try {
                mAuthDao.insert(authEntity);
            } catch (Exception e) {
                Log.d("AuthRepo", "can not save user data");
            }
        });
    }

    public enum AuthProgress {
        IN_PROGRESS,
        SUCCESS,
        FAILED
    }
}
