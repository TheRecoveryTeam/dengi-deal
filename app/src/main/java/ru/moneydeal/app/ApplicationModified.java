package ru.moneydeal.app;

import android.app.Application;
import android.content.Context;

import ru.moneydeal.app.auth.AuthRepo;
import ru.moneydeal.app.network.ApiRepo;

public class ApplicationModified extends Application {

    private ApiRepo mApiRepo;
    private AuthRepo mAuthRepo;
    private AppDatabase mDatabase;

    @Override
    public void onCreate() {
        super.onCreate();
        mApiRepo = new ApiRepo();
        mAuthRepo = new AuthRepo(mApiRepo, this);
        mDatabase = AppDatabase.getInstance(getApplicationContext());
    }

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

