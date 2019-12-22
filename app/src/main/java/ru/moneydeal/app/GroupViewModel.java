package ru.moneydeal.app;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import java.util.List;

import ru.moneydeal.app.group.GroupEntity;
import ru.moneydeal.app.group.GroupRepo;
import ru.moneydeal.app.group.ParticipantEntity;
import ru.moneydeal.app.network.GroupApi;
import ru.moneydeal.app.userList.UserEntity;
import ru.moneydeal.app.userList.UsersRepo;


public class GroupViewModel extends AndroidViewModel {
    private Boolean connectedToGroupList;
    private Boolean connectedToUserList;
    private MediatorLiveData<List<GroupEntity>> mGroupState = new MediatorLiveData<>();
    private MediatorLiveData<GroupEntity> mGroup = new MediatorLiveData<>();
    private MediatorLiveData<List<UserEntity>> mGroupUsersResult = new MediatorLiveData<>();


    public GroupViewModel(@NonNull Application application) {
        super(application);
        connectedToGroupList = false;
        connectedToUserList = false;
    }

    public LiveData<List<GroupEntity>> getGroups() {
        return mGroupState;
    }

    public LiveData<GroupEntity> getGroup() {
        return mGroup;
    }

    public void fetchGroupUsers(String groupId) {
        final LiveData<List<String>> groupUserIds
                = GroupRepo.getInstance(getApplication()).getGroupUserIds(groupId);

        mGroupUsersResult.addSource(groupUserIds, ids -> {
            mGroupUsersResult.removeSource(groupUserIds);
            UsersRepo usersRepo = UsersRepo.getInstance(getApplication());
            LiveData<List<UserEntity>> users = usersRepo.getUsers(ids);

            mGroupUsersResult.addSource(users, result -> {
                mGroupUsersResult.postValue(result);
                mGroupUsersResult.removeSource(users);
            });
        });
    }

    public MediatorLiveData<List<UserEntity>> getGroupUsers() {
        return mGroupUsersResult;
    }

    public void fetchGroups() {
        final LiveData<List<GroupEntity>> progressLiveData = GroupRepo.getInstance(getApplication()).fetchGroups();

        if (!connectedToGroupList) {
            connectedToGroupList = true;
            mGroupState.addSource(progressLiveData, groups -> {
                mGroupState.postValue(groups);
            });
        }
    }

    public MediatorLiveData<ParticipantEntity> addUser(String userId, String groupId) {
        MediatorLiveData<ParticipantEntity> participant = new MediatorLiveData<>();
        final LiveData<ParticipantEntity> progressLiveData = GroupRepo.getInstance(getApplication()).addParticipant(userId, groupId);


        participant.addSource(progressLiveData, member -> {
            participant.postValue(member);
            participant.removeSource(progressLiveData);
        });

        return participant;
    }

    @Nullable
    public MediatorLiveData<ParticipantEntity> fetchParticipant(String login, String groupId) {
        MediatorLiveData<ParticipantEntity> participant = new MediatorLiveData<>();
        final LiveData<ParticipantEntity> progressLiveData = GroupRepo.getInstance(getApplication()).fetchParticipant(login, groupId);

        participant.addSource(progressLiveData, member -> {
            participant.postValue(member);
            participant.removeSource(progressLiveData);
        });

        return participant;
    }

    public void selectGroup(String groupId) {
        LiveData<GroupEntity> group = GroupRepo.getInstance(getApplication()).getGroup(groupId);
        mGroup.addSource(group, item -> {
            mGroup.postValue(item);
            mGroup.removeSource(group);
        });
    }
}
