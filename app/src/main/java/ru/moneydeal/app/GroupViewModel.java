package ru.moneydeal.app;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import java.util.List;

import ru.moneydeal.app.group.GroupEntity;
import ru.moneydeal.app.group.GroupRepo;


public class GroupViewModel extends AndroidViewModel {
    private MediatorLiveData<List<GroupEntity>> mGroupState = new MediatorLiveData<>();
    private MediatorLiveData<GroupEntity> mGroup = new MediatorLiveData<>();

    public GroupViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<GroupEntity>> getGroups() {
        return mGroupState;
    }

    public LiveData<GroupEntity> getGroup() {
        return mGroup;
    }

    public void fetchGroups() {
        final LiveData<List<GroupEntity>> progressLiveData = GroupRepo.getInstance(getApplication()).fetchGroups();
        mGroupState.addSource(progressLiveData, groups -> {
            mGroupState.postValue(groups);
            mGroupState.removeSource(progressLiveData);
        });
    }

    public void selectGroup(String groupId) {
        LiveData<GroupEntity> group = GroupRepo.getInstance(getApplication()).getGroup(groupId);
        mGroup.addSource(group, item -> {
            mGroup.postValue(item);
            mGroup.removeSource(group);
        });
    }
}
