package ru.moneydeal.app.group;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import ru.moneydeal.app.ApplicationModified;
import ru.moneydeal.app.network.ApiRepo;
import ru.moneydeal.app.network.ErrorResponse;
import ru.moneydeal.app.network.GroupApi;
import ru.moneydeal.app.network.ResponseCallback;

public class GroupRepo {
    private final ApiRepo mApiRepo;
    private final GroupDao mGroupDao;

    private MutableLiveData<List<GroupEntity>> mGroupData;

    public GroupRepo(ApplicationModified context) {
        mApiRepo = context.getApis();
        mGroupDao = context.getDB().getGroupDao();
        mGroupData = new MutableLiveData<>();

        loadGroupDataFromDB();
    }

    private void loadGroupDataFromDB() {
        Log.d("GroupRepo", "fetch groups from db");
        AsyncTask.execute(() -> mGroupData.postValue(mGroupDao.getGroups()));
    }

    @NonNull
    public static GroupRepo getInstance(Context context) {
        return ApplicationModified.from(context).getGroupRepo();
    }

    public LiveData<List<GroupEntity>> fetchGroups() {
        Log.d("GroupRepo", "fetch groups");

        AsyncTask.execute(() -> {
            GroupApi api = mApiRepo.getGroupApi();
            api.fetchGroups().enqueue(new GroupResponseCallback());
        });

        return mGroupData;
    }

    private void saveGroups(GroupApi.GroupData data) {
        AsyncTask.execute(() -> {
            List<GroupEntity> groupsEntities = new ArrayList<>();
            Log.d(
                    "GroupRepo",
                    "start groups saving, count: " + data.groups.size()
            );

            for (GroupApi.Group group: data.groups) {
                groupsEntities.add(new GroupEntity(
                        group._id,
                        group.name,
                        group.description
                ));
                Log.d("GroupRepo", "save group with name " + group.name);
            }

            Log.d(
                    "AuthRepo",
                    "saved groups data"
            );


            try {
                mGroupDao.reset();
                mGroupDao.insert(groupsEntities);
                mGroupData.postValue(groupsEntities);
            } catch (Exception e) {
                Log.d("GroupRepo", "failed" + e.getMessage());
            }
        });
    }

    public MutableLiveData<GroupEntity> getGroup(String groupId) {
        MutableLiveData<GroupEntity> liveData = new MutableLiveData<>();

        AsyncTask.execute(() -> {
            List<GroupEntity> list = mGroupDao.getGroup(groupId);
            if (list.size() == 0) {
                liveData.postValue(null);
            } else {
                liveData.postValue(list.get(0));
            }
        });

        return liveData;
    }

    public class GroupResponseCallback extends ResponseCallback<GroupApi.GroupsResponse> {
        @Override
        public void onOk(GroupApi.GroupsResponse response) {
            Log.d("GroupRepo", "fetch ok");
            saveGroups(response.data);
        }

        @Override
        public void onError(ErrorResponse response) {
            Log.d("GroupRepo", "fetch error " + response.data.message);
        }
    }
}
