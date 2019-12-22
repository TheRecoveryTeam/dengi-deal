package ru.moneydeal.app;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import ru.moneydeal.app.auth.AuthDao;
import ru.moneydeal.app.checks.CheckChunkEntity;
import ru.moneydeal.app.checks.CheckEntity;
import ru.moneydeal.app.group.GroupDao;
import ru.moneydeal.app.auth.AuthEntity;
import ru.moneydeal.app.group.GroupEntity;
import ru.moneydeal.app.group.GroupUserEntity;
import ru.moneydeal.app.userList.UserDao;
import ru.moneydeal.app.userList.UserEntity;

@Database(entities = {
        AuthEntity.class,
        GroupEntity.class,
        GroupUserEntity.class,
        UserEntity.class,
}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract AuthDao getAuthDao();

    public abstract GroupDao getGroupDao();

    public abstract UserDao getUserDao();

    private static AppDatabase instance;

    static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = create(context);
        }

        return instance;
    }

    private static AppDatabase create(final Context context) {
        return Room.databaseBuilder(
                context,
                AppDatabase.class,
                "money_deal"
        )
        .fallbackToDestructiveMigration().build();
    }
}
