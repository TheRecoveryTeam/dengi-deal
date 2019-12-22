package ru.moneydeal.app;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.moneydeal.app.checks.CheckChunkEntity;
import ru.moneydeal.app.checks.CheckEntity;
import ru.moneydeal.app.checks.CheckRepo;
import ru.moneydeal.app.group.GroupRepo;
import ru.moneydeal.app.userList.UserEntity;
import ru.moneydeal.app.userList.UsersRepo;

public class CheckViewModel extends AndroidViewModel {
    public CheckViewModel(@NonNull Application application) {
        super(application);
    }

    private CheckRepo getCheckRepo() {
        return CheckRepo.getInstance(getApplication());
    }

    public LiveData<CheckRepo.GroupChecks> getChecks(String groupId) {
        final MediatorLiveData<CheckRepo.GroupChecks> data = new MediatorLiveData<>();

        LiveData<CheckRepo.GroupChecks> groupChecksLiveData = getCheckRepo().fetchChecks(groupId);
        data.addSource(groupChecksLiveData, groupChecks -> {
            data.postValue(groupChecks);

            if (groupChecks.progress == CheckRepo.Progress.FAILED || groupChecks.progress == CheckRepo.Progress.SUCCESS) {
                data.removeSource(groupChecksLiveData);
            }
        });

        return data;
    }

    public CheckEntity getLoadedCheck(String checkId) {
        return getCheckRepo().getLoadedCheck(checkId);
    }

    public LiveData<Map<String, UserEntity>> getCheckUsers(String checkId) {
        CheckEntity loadedCheck = getLoadedCheck(checkId);
        if (loadedCheck == null) {
            return null;
        }

        final LiveData<List<String>> groupUserIds
                = GroupRepo.getInstance(getApplication()).getGroupUserIds(loadedCheck.groupId);

        MediatorLiveData<Map<String, UserEntity>> groupUsersResult = new MediatorLiveData<>();

        groupUsersResult.addSource(groupUserIds, ids -> {
            groupUsersResult.removeSource(groupUserIds);
            UsersRepo usersRepo = UsersRepo.getInstance(getApplication());
            LiveData<List<UserEntity>> users = usersRepo.getUsers(ids);

            groupUsersResult.addSource(users, result -> {
                groupUsersResult.removeSource(users);

                Map<String, UserEntity> map = new HashMap<>();
                for (UserEntity entity : result) {
                    map.put(entity.id, entity);
                }

                groupUsersResult.postValue(map);
            });
        });

        return groupUsersResult;
    }
}
