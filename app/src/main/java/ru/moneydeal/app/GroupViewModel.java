package ru.moneydeal.app;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import ru.moneydeal.app.group.GroupRepo;


public class GroupViewModel extends AndroidViewModel {
    private MediatorLiveData<GroupRepo.GroupData> mGroupState = new MediatorLiveData<>();

    public GroupViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<GroupRepo.GroupData> getGroups() {
        return mGroupState;
    }

    public void fetchGroups() {
        final LiveData<GroupRepo.GroupData> progressLiveData = GroupRepo.getInstance(getApplication()).fetchGroups();
        Log.d("GroupViewModel", "fetchGroups");
        mGroupState.addSource(progressLiveData, groups -> {
            mGroupState.postValue(groups);
            mGroupState.removeSource(progressLiveData);
        });
    }
}
