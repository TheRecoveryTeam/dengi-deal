package ru.moneydeal.app;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import java.util.List;

import ru.moneydeal.app.group.GroupEntity;
import ru.moneydeal.app.group.GroupRepo;
import ru.moneydeal.app.userList.UserEntity;
import ru.moneydeal.app.userList.UsersRepo;


public class GroupViewModel extends AndroidViewModel {
    private Boolean connectedToGroupList;
    private MediatorLiveData<List<GroupEntity>> mGroupState = new MediatorLiveData<>();
    private MediatorLiveData<GroupEntity> mGroup = new MediatorLiveData<>();

    public GroupViewModel(@NonNull Application application) {
        super(application);
        connectedToGroupList = false;
    }

    public LiveData<List<GroupEntity>> getGroups() {
        return mGroupState;
    }

    public LiveData<GroupEntity> getGroup() {
        return mGroup;
    }

    public LiveData<List<UserEntity>> getGroupUsers(String groupId) {
        final LiveData<List<String>> groupUserIds
                = GroupRepo.getInstance(getApplication()).getGroupUserIds(groupId);

        MediatorLiveData<List<UserEntity>> groupUsersResult = new MediatorLiveData<>();

        groupUsersResult.addSource(groupUserIds, ids -> {
            groupUsersResult.removeSource(groupUserIds);
            UsersRepo usersRepo = UsersRepo.getInstance(getApplication());
            LiveData<List<UserEntity>> users = usersRepo.getUsers(ids);

            groupUsersResult.addSource(users, result -> {
                groupUsersResult.postValue(result);
                groupUsersResult.removeSource(users);
            });
        });

        return groupUsersResult;
    }

    public void fetchGroups() {
        if (!connectedToGroupList) {
            connectedToGroupList = true;

            final LiveData<List<GroupEntity>> progressLiveData = GroupRepo.getInstance(getApplication()).fetchGroups();
            mGroupState.addSource(progressLiveData, groups -> {
                mGroupState.postValue(groups);
            });
        }
    }

    public void selectGroup(String groupId) {
        LiveData<GroupEntity> group = GroupRepo.getInstance(getApplication()).getGroup(groupId);
        mGroup.addSource(group, item -> {
            mGroup.postValue(item);
            mGroup.removeSource(group);
        });
    }
}
