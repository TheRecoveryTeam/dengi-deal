package ru.moneydeal.app;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import java.util.Objects;

import ru.moneydeal.app.auth.AuthRepo;

@SuppressWarnings("WeakerAccess")
public class AuthViewModel extends AndroidViewModel {

    private RegisterData mLastRegisterData = new RegisterData("", "");

    private MediatorLiveData<LoginState> mLoginState = new MediatorLiveData<>();

    public AuthViewModel(@NonNull Application application) {
        super(application);
        mLoginState.setValue(LoginState.NONE);
    }

    public LiveData<LoginState> getProgress() {
        return mLoginState;
    }

    public void checkAuth() {
        mLoginState.postValue(LoginState.IN_PROGRESS);
        final LiveData<AuthRepo.AuthProgress> progressLiveData = AuthRepo.getInstance(getApplication()).checkAuth();
        mLoginState.addSource(progressLiveData, authProgress -> {
            if (authProgress == AuthRepo.AuthProgress.SUCCESS) {
                mLoginState.postValue(LoginState.SUCCESS);
                mLoginState.removeSource(progressLiveData);
            } else if (authProgress == AuthRepo.AuthProgress.FAILED) {
                mLoginState.postValue(LoginState.FAILED);
                mLoginState.removeSource(progressLiveData);
            }
        });
    }

    public void register(String login, String password) {
        RegisterData last = mLastRegisterData;
        RegisterData registerData = new RegisterData(login, password);
        mLastRegisterData = registerData;

        if (!registerData.isValid()) {
            mLoginState.postValue(LoginState.FAILED);
        } else if (last != null && last.equals(registerData)) {
            Log.w("LoginViewModel", "Ignoring duplicate request with login data");
        } else if (mLoginState.getValue() != LoginState.IN_PROGRESS) {
            requestRegister(registerData);
        }
    }

    private void requestRegister(final RegisterData registerData) {
        mLoginState.postValue(LoginState.IN_PROGRESS);
        final LiveData<AuthRepo.AuthProgress> progressLiveData = AuthRepo.getInstance(getApplication()).register(registerData.getLogin(), registerData.getPassword());
        mLoginState.addSource(progressLiveData, authProgress -> {
            if (authProgress == AuthRepo.AuthProgress.SUCCESS) {
                mLoginState.postValue(LoginState.SUCCESS);
                mLoginState.removeSource(progressLiveData);
            } else if (authProgress == AuthRepo.AuthProgress.FAILED) {
                mLoginState.postValue(LoginState.FAILED);
                mLoginState.removeSource(progressLiveData);
            }
        });
    }

    public enum LoginState {
        NONE,
        IN_PROGRESS,
        SUCCESS,
        FAILED
    }

    public static class RegisterData {
        private final String mLogin;
        private final String mPassword;

        public RegisterData(String login, String password) {
            mLogin = login;
            mPassword = password;
        }

        public String getLogin() {
            return mLogin;
        }

        public String getPassword() {
            return mPassword;
        }

        public boolean isValid() {
            return !TextUtils.isEmpty(mLogin) && !TextUtils.isEmpty(mPassword);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RegisterData registerData = (RegisterData) o;
            return Objects.equals(mLogin, registerData.mLogin) &&
                    Objects.equals(mPassword, registerData.mPassword);
        }

        @Override
        public int hashCode() {
            return Objects.hash(mLogin, mPassword);
        }
    }
}
