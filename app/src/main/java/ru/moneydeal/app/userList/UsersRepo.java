package ru.moneydeal.app.userList;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import ru.moneydeal.app.ApplicationModified;
import ru.moneydeal.app.network.UserApi;

public class UsersRepo {
    private UserDao mUserDao;

    public UsersRepo(ApplicationModified context) {
        mUserDao = context.getDB().getUserDao();
    }

    @NonNull
    public static UsersRepo getInstance(Context context) {
        return ApplicationModified.from(context).getUsersRepo();
    }

    public void addUsers(List<UserApi.User> users) {
        AsyncTask.execute(() -> {
            try {
                List<UserEntity> list = new ArrayList<>();

                for(UserApi.User user: users) {
                    list.add(new UserEntity(
                            user._id,
                            user.login,
                            user.first_name,
                            user.last_name
                    ));
                }

                mUserDao.insert(list);
            } catch (Exception e) {
                Log.d("UsersRepo", "failed" + e.getMessage());
            }
        });
    }

    public LiveData<List<UserEntity>> getUsers(List<String> userIds) {
        MutableLiveData<List<UserEntity>> users = new MutableLiveData<>();

        AsyncTask.execute(() -> {
            List<UserEntity> result = new ArrayList<>();
            for (String id: userIds) {
                List<UserEntity> cur = mUserDao.selectUserById(id);
                if (cur.size() > 0) {
                    result.add(cur.get(0));
                }
            }

            users.postValue(result);
        });

        return users;
    }
}
