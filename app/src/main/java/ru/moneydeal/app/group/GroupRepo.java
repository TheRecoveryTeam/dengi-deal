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
import ru.moneydeal.app.auth.AuthDao;
import ru.moneydeal.app.auth.AuthRepo;
import ru.moneydeal.app.network.ApiRepo;
import ru.moneydeal.app.network.ErrorResponse;
import ru.moneydeal.app.network.GroupApi;
import ru.moneydeal.app.network.ResponseCallback;
import ru.moneydeal.app.network.UserApi;

public class GroupRepo {
    private final ApiRepo mApiRepo;
    private final GroupDao mGroupDao;

    private MutableLiveData<GroupData> mGroupData;


    public GroupRepo(ApplicationModified context) {
        mApiRepo = context.getApis();
        mGroupDao = context.getDB().getGroupDao();
        mGroupData = new MutableLiveData<>();
    }

    @NonNull
    public static GroupRepo getInstance(Context context) {
        if (ApplicationModified.from(context).getGroupRepo() == null) {
            Log.d("GroupRepo", "get null instance");
        } else {
            Log.d("GroupRepo", "non nul instance");
        }
        return ApplicationModified.from(context).getGroupRepo();
    }

    public LiveData<GroupData> fetchGroups() {
        AsyncTask.execute(() -> {
            GroupApi api = mApiRepo.getGroupApi();
            api.fetchGroups().enqueue(new GroupResponseCallback());
        });

        return mGroupData;
    }


    private void saveGroups(GroupApi.GroupData data) {
        AsyncTask.execute(() -> {
            List<GroupEntity> groupsEntities = new ArrayList<>();
            List<Group> groups = new ArrayList<>();
            Log.d("save groups", "start");
            for (GroupApi.Group group: data.groups) {
                groupsEntities.add(new GroupEntity(
                        group.name,
                        group.description
                ));
                Log.d("save groups", group.name);
                groups.add(new Group(
                        group.name,
                        group.description
                ));
            }

            Log.d(
                    "AuthRepo",
                    "saved groups data");

            try {
                mGroupDao.insert(groupsEntities);
                mGroupData.postValue(new GroupData(groups));
            } catch (Exception e) {
                Log.d("GroupRepo", "failed" + e.getMessage());
            }
        });
    }



    public static class Group {
        @NonNull
        public String name;

        @NonNull
        public String description;

        public Group(String name, String description) {
            this.name = name;
            this.description = description;
        }
    }

    public static class GroupData {
        public List<Group> groups;

        public GroupData(List<Group> groups) {
            this.groups = groups;
        }

        public int getSize() {
            return groups == null ? 0 : groups.size();
        }
    }

    public class GroupResponseCallback extends ResponseCallback<GroupApi.GroupsResponse> {
        @Override
        public void onOk(GroupApi.GroupsResponse response) {
            saveGroups(response.data);
        }

        @Override
        public void onError(ErrorResponse response) {
            Log.d("mGroupRepo", "fetch failed " + response.data.message);
        }
    }
}
