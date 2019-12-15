package ru.moneydeal.app;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import ru.moneydeal.app.auth.AuthDao;
import ru.moneydeal.app.group.GroupDao;
import ru.moneydeal.app.auth.AuthEntity;
import ru.moneydeal.app.group.GroupEntity;

@Database(entities = {AuthEntity.class, GroupEntity.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract AuthDao getAuthDao();

    public abstract GroupDao getGroupDao();

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
