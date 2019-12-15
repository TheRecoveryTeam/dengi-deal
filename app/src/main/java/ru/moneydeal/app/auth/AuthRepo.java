package ru.moneydeal.app.auth;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import ru.moneydeal.app.ApplicationModified;
import ru.moneydeal.app.network.ApiRepo;
import ru.moneydeal.app.network.ErrorResponse;
import ru.moneydeal.app.network.ResponseCallback;
import ru.moneydeal.app.network.UserApi;

@SuppressWarnings("WeakerAccess")
public class AuthRepo {

    private final TokenRepo mTokenRepo;
    private final ApiRepo mApiRepo;
    private final AuthDao mAuthDao;

    private MutableLiveData<OpProgress> mAuthProgress;

    public AuthRepo(ApplicationModified context) {
        mTokenRepo = context.getTokenRepo();
        mApiRepo = context.getApis();
        mAuthDao = context.getDB().getAuthDao();
        mAuthProgress = new MutableLiveData<>(OpProgress.IN_PROGRESS);
    }

    @NonNull
    public static AuthRepo getInstance(Context context) {
        return ApplicationModified.from(context).getAuthRepo();
    }

    public LiveData<OpProgress> checkAuth() {
        mAuthProgress.setValue(OpProgress.IN_PROGRESS);

        AsyncTask.execute(() -> {
            List<AuthEntity> users = mAuthDao.getUsers();

            if (users.size() == 0) {
                mAuthProgress.postValue(OpProgress.FAILED);
                return;
            }

            UserApi api = mApiRepo.getUserApi();
            api.checkAuth().enqueue(new AuthResponseCallback());
        });

        return mAuthProgress;
    }

    public LiveData<OpProgress> register(
            @NonNull String login,
            @NonNull String password,
            @NonNull String firstName,
            @NonNull String lastName
    ) {
        mAuthProgress.setValue(OpProgress.IN_PROGRESS);
        UserApi api = mApiRepo.getUserApi();
        api.register(login, password, firstName, lastName).enqueue(new AuthResponseCallback());
        return mAuthProgress;
    }

    public LiveData<OpProgress> login(
            @NonNull String login,
            @NonNull String password
    ) {
        mAuthProgress.setValue(OpProgress.IN_PROGRESS);
        UserApi api = mApiRepo.getUserApi();
        api.login(login, password).enqueue(new AuthResponseCallback());
        return mAuthProgress;
    }

    private void saveUserData(UserApi.AuthData data) {
        AsyncTask.execute(() -> {
            String token = data.token;
            if (token == null) {
                token = mTokenRepo.getToken();
            }

            mAuthDao.reset();
            AuthEntity authEntity = new AuthEntity(
                    data.user._id,
                    data.user.login,
                    data.user.first_name,
                    data.user.last_name,
                    token
            );

            try {
                mAuthDao.insert(authEntity);
                mAuthProgress.postValue(OpProgress.SUCCESS);
            } catch (Exception e) {
                Log.d("AuthRepo", "can not save user data " + e.getMessage());
                mAuthProgress.postValue(OpProgress.FAILED);
            }
        });
    }

    public enum OpProgress {
        IN_PROGRESS,
        SUCCESS,
        FAILED
    }

    public class AuthResponseCallback extends ResponseCallback<UserApi.AuthResponse> {
        @Override
        public void onOk(UserApi.AuthResponse response) {
            saveUserData(response.data);
        }

        @Override
        public void onError(ErrorResponse response) {
            mAuthProgress.postValue(OpProgress.FAILED);
        }
    }
}
