package ru.moneydeal.app;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.moneydeal.app.checks.BaseCheckEntity;
import ru.moneydeal.app.checks.CheckEntity;
import ru.moneydeal.app.checks.CheckRepo;
import ru.moneydeal.app.group.GroupRepo;
import ru.moneydeal.app.userList.UserEntity;
import ru.moneydeal.app.userList.UsersRepo;

public class CheckViewModel extends AndroidViewModel {

    private MediatorLiveData<CheckRepo.GroupChecks> mChecksMediatorLiveData;

    public CheckViewModel(@NonNull Application application) {
        super(application);
    }

    private CheckRepo getCheckRepo() {
        return CheckRepo.getInstance(getApplication());
    }

    public LiveData<CheckRepo.GroupChecks> getChecks() {
        if (mChecksMediatorLiveData == null) {
            mChecksMediatorLiveData = new MediatorLiveData<>();
        }

        return mChecksMediatorLiveData;
    }

    public void fetchChecks(String groupId) {
        LiveData<CheckRepo.GroupChecks> groupChecksLiveData = getCheckRepo().fetchChecks(groupId);
        mChecksMediatorLiveData.addSource(groupChecksLiveData, groupChecks -> {
            mChecksMediatorLiveData.postValue(groupChecks);

            if (groupChecks.progress == CheckRepo.Progress.FAILED || groupChecks.progress == CheckRepo.Progress.SUCCESS) {
                mChecksMediatorLiveData.removeSource(groupChecksLiveData);
            }
        });
    }

    public CheckEntity getLoadedCheck(String checkId) {
        return getCheckRepo().getLoadedCheck(checkId);
    }

    public LiveData<Map<String, UserEntity>> getCheckUsers(String checkId) {
        CheckEntity loadedCheck = getLoadedCheck(checkId);
        MediatorLiveData<Map<String, UserEntity>> groupUsersResult = new MediatorLiveData<>();

        if (loadedCheck == null) {
            groupUsersResult.postValue(new HashMap<>());
            return groupUsersResult;
        }

        final LiveData<List<String>> groupUserIds
                = GroupRepo.getInstance(getApplication()).getGroupUserIds(loadedCheck.groupId);

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

    public LiveData<CheckRepo.Progress> createCheck(BaseCheckEntity check) {
        MediatorLiveData<CheckRepo.Progress> liveData = new MediatorLiveData<>();

        LiveData<CheckRepo.Progress> createCheck = getCheckRepo().createCheck(check);

        liveData.addSource(createCheck, progress -> {
            liveData.postValue(progress);
            if (progress != CheckRepo.Progress.IN_PROGRESS) {
                liveData.removeSource(createCheck);
            }
        });

        return liveData;
    }
}
