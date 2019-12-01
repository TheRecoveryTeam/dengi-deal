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

    private RegisterData mLastRegisterData = new RegisterData("", "", "", "");
    private LoginData mLastLoginData = new LoginData("", "");

    private MediatorLiveData<AuthState> mAuthState = new MediatorLiveData<>();

    public AuthViewModel(@NonNull Application application) {
        super(application);
        mAuthState.setValue(AuthState.NONE);
    }

    public LiveData<AuthState> getProgress() {
        return mAuthState;
    }

    public void checkAuth() {
        mAuthState.postValue(AuthState.IN_PROGRESS);
        final LiveData<AuthRepo.OpProgress> progressLiveData = AuthRepo.getInstance(getApplication()).checkAuth();
        mAuthState.addSource(progressLiveData, authProgress -> {
            if (authProgress == AuthRepo.OpProgress.SUCCESS) {
                mAuthState.postValue(AuthState.SUCCESS);
                mAuthState.removeSource(progressLiveData);
            } else if (authProgress == AuthRepo.OpProgress.FAILED) {
                mAuthState.postValue(AuthState.FAILED);
                mAuthState.removeSource(progressLiveData);
            }
        });
    }

    public void login(String login, String password) {
        LoginData last = mLastLoginData;
        mLastLoginData = new LoginData(login, password);

        if (!mLastLoginData.isValid()) {
            mAuthState.postValue(AuthState.FAILED);
        } else if (last != null && last.equals(mLastLoginData)) {
            Log.d("LoginViewModel", "Ignoring duplicate request with login data");
        } else if (mAuthState.getValue() != AuthState.IN_PROGRESS) {
            requestLogin(mLastLoginData);
        }
    }

    private void requestLogin(final LoginData registerData) {
        mAuthState.postValue(AuthState.IN_PROGRESS);
        final LiveData<AuthRepo.OpProgress> progressLiveData = AuthRepo
                .getInstance(getApplication())
                .login(
                        registerData.getLogin(),
                        registerData.getPassword()
                );

        mAuthState.addSource(progressLiveData, authProgress -> {
            if (authProgress == AuthRepo.OpProgress.SUCCESS) {
                mAuthState.postValue(AuthState.SUCCESS);
                mAuthState.removeSource(progressLiveData);
            } else if (authProgress == AuthRepo.OpProgress.FAILED) {
                mAuthState.postValue(AuthState.FAILED);
                mAuthState.removeSource(progressLiveData);
            }
        });
    }

    public void register(String login, String password) {
        RegisterData last = mLastRegisterData;
        mLastRegisterData = new RegisterData(login, password, "Elena", "Chernega");

        if (!mLastRegisterData.isValid()) {
            mAuthState.postValue(AuthState.FAILED);
        } else if (last != null && last.equals(mLastRegisterData)) {
            Log.d("LoginViewModel", "Ignoring duplicate request with register data");
        } else if (mAuthState.getValue() != AuthState.IN_PROGRESS) {
            requestRegister(mLastRegisterData);
        }
    }

    private void requestRegister(final RegisterData registerData) {
        mAuthState.postValue(AuthState.IN_PROGRESS);
        final LiveData<AuthRepo.OpProgress> progressLiveData = AuthRepo
                .getInstance(getApplication())
                .register(
                        registerData.getLogin(),
                        registerData.getPassword(),
                        registerData.getFirstName(),
                        registerData.getLastName()
                );

        mAuthState.addSource(progressLiveData, authProgress -> {
            if (authProgress == AuthRepo.OpProgress.SUCCESS) {
                mAuthState.postValue(AuthState.SUCCESS);
                mAuthState.removeSource(progressLiveData);
            } else if (authProgress == AuthRepo.OpProgress.FAILED) {
                mAuthState.postValue(AuthState.FAILED);
                mAuthState.removeSource(progressLiveData);
            }
        });
    }

    public enum AuthState {
        NONE,
        IN_PROGRESS,
        SUCCESS,
        FAILED
    }

    public static class LoginData {
        protected String mLogin;
        protected String mPassword;

        public LoginData(String login, String password) {
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
            LoginData registerData = (LoginData) o;
            return Objects.equals(mLogin, registerData.mLogin) &&
                    Objects.equals(mPassword, registerData.mPassword);
        }

        @Override
        public int hashCode() {
            return Objects.hash(mLogin, mPassword);
        }
    }

    public static class RegisterData extends LoginData {
        private String mFirstName;
        private String mLastName;

        public RegisterData(String login, String password, String firstName, String lastName) {
            super(login, password);

            mFirstName = firstName;
            mLastName = lastName;
        }

        public String getFirstName() {
            return mFirstName;
        }

        public String getLastName() {
            return mLastName;
        }

        @Override
        public boolean isValid() {
            return super.isValid()
                    && !TextUtils.isEmpty(mFirstName)
                    && !TextUtils.isEmpty(mLastName);
        }

        @Override
        public boolean equals(Object o) {
            if (!super.equals(o)) {
                return false;
            }

            RegisterData registerData = (RegisterData) o;
            return Objects.equals(mFirstName, registerData.mFirstName) &&
                    Objects.equals(mLastName, registerData.mLastName);
        }


        @Override
        public int hashCode() {
            return Objects.hash(mLogin, mPassword, mFirstName, mLastName);
        }
    }
}
