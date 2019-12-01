package ru.moneydeal.app;

import android.app.Application;
import android.content.Context;

import ru.moneydeal.app.auth.AuthRepo;
import ru.moneydeal.app.auth.TokenRepo;
import ru.moneydeal.app.network.ApiRepo;

public class ApplicationModified extends Application {

    private TokenRepo mTokenRepo;
    private ApiRepo mApiRepo;
    private AuthRepo mAuthRepo;
    private AppDatabase mDatabase;

    @Override
    public void onCreate() {
        super.onCreate();
        mDatabase = AppDatabase.getInstance(this);
        mTokenRepo = new TokenRepo(this);
        mApiRepo = new ApiRepo(mTokenRepo);
        mAuthRepo = new AuthRepo(this);
    }

    public TokenRepo getTokenRepo() { return mTokenRepo; }

    public AuthRepo getAuthRepo() {
        return mAuthRepo;
    }

    public ApiRepo getApis() {
        return mApiRepo;
    }

    public AppDatabase getDB() { return mDatabase; }

    public static ApplicationModified from(Context context) {
        return (ApplicationModified) context.getApplicationContext();
    }
}

