package ru.moneydeal.app;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import java.util.Objects;

import ru.moneydeal.app.group.GroupRepo;

public class GroupCreationViewModel extends AndroidViewModel {
    private GroupCreationData mGroupCreationData = new GroupCreationData("", "");

    private MediatorLiveData<GroupCreationState> mGroupCreationState = new MediatorLiveData<>();


    public GroupCreationViewModel(@NonNull Application application) {
        super(application);
        mGroupCreationState.setValue(GroupCreationState.NONE);
    }

    public void createGroup(String name, String description) {
        GroupCreationData last = mGroupCreationData;
        mGroupCreationData = new GroupCreationData(name, description);

        if (!mGroupCreationData.isValid()) {
            mGroupCreationState.setValue(GroupCreationState.FAILED);
        } else if (last != null && last.equals(mGroupCreationData)) {
            Log.d("GroupCreationViewModel", "Ignoring duplicate request with register data");
        } else if (mGroupCreationState.getValue() != GroupCreationState.IN_PROGRESS) {
            create(mGroupCreationData);
        }
    }

    public LiveData<GroupCreationState> getProgress() {
        return mGroupCreationState;
    }

    private void create(GroupCreationData data) {
        mGroupCreationState.setValue(GroupCreationState.IN_PROGRESS);
        final LiveData<GroupRepo.CreationProgress> progressLiveData = GroupRepo
                .getInstance(getApplication())
                .createGroup(
                        data.getmName(),
                        data.getmDescription()
                );

        mGroupCreationState.addSource(progressLiveData, creationProgress -> {
            if (creationProgress == GroupRepo.CreationProgress.SUCCESS) {
                mGroupCreationState.postValue(GroupCreationState.SUCCESS);
                mGroupCreationState.removeSource(progressLiveData);
            } else if (creationProgress == GroupRepo.CreationProgress.FAILED) {
                mGroupCreationState.postValue(GroupCreationState.FAILED);
                mGroupCreationState.removeSource(progressLiveData);
            }
        });
    }

    public enum GroupCreationState {
        NONE,
        IN_PROGRESS,
        SUCCESS,
        FAILED
    }

    public static class GroupCreationData {
        protected String mName;
        protected String mDescription;

        public GroupCreationData(String name, String description) {
            mName = name;
            mDescription = description;
        }

        public String getmName() {
            return mName;
        }

        public String getmDescription() {
            return mDescription;
        }

        public boolean isValid() {
            return !TextUtils.isEmpty(mName) && !TextUtils.isEmpty(mDescription);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            GroupCreationData creationData = (GroupCreationData) o;
            return Objects.equals(mName, creationData.mName) &&
                    Objects.equals(mDescription, creationData.mDescription);
        }

        @Override
        public int hashCode() {
            return Objects.hash(mName, mDescription);
        }
    }
}
