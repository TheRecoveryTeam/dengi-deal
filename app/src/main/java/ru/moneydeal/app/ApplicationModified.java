package ru.moneydeal.app;

import android.app.Application;
import android.content.Context;

import ru.moneydeal.app.auth.AuthRepo;
import ru.moneydeal.app.auth.TokenRepo;
import ru.moneydeal.app.checks.CheckRepo;
import ru.moneydeal.app.group.GroupRepo;
import ru.moneydeal.app.network.ApiRepo;
import ru.moneydeal.app.userList.UsersRepo;

public class ApplicationModified extends Application {

    private TokenRepo mTokenRepo;
    private UsersRepo mUsersRepo;
    private ApiRepo mApiRepo;
    private AuthRepo mAuthRepo;
    private AppDatabase mDatabase;
    private GroupRepo mGroupRepo;
    private CheckRepo mCheckRepo;

    @Override
    public void onCreate() {
        super.onCreate();
        mDatabase = AppDatabase.getInstance(this);
        mUsersRepo = new UsersRepo(this);
        mTokenRepo = new TokenRepo(this);
        mApiRepo = new ApiRepo(mTokenRepo);
        mAuthRepo = new AuthRepo(this);
        mGroupRepo = new GroupRepo(this);
        mCheckRepo = new CheckRepo(this);
    }

    public TokenRepo getTokenRepo() { return mTokenRepo; }

    public AuthRepo getAuthRepo() {
        return mAuthRepo;
    }

    public GroupRepo getGroupRepo() {
        return mGroupRepo;
    }

    public UsersRepo getUsersRepo() {
        return mUsersRepo;
    }

    public CheckRepo getCheckRepo() {
        return mCheckRepo;
    }

    public ApiRepo getApis() {
        return mApiRepo;
    }

    public AppDatabase getDB() { return mDatabase; }

    public static ApplicationModified from(Context context) {
        return (ApplicationModified) context.getApplicationContext();
    }
}

