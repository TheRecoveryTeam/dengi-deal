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
        return ApplicationModified.from(context).getGroupRepo();
    }

    public LiveData<GroupData> fetchGroups() {
        AsyncTask.execute(() -> {
//            List<GroupEntity> groups = mGroupDao.getGroups();

            GroupApi api = mApiRepo.getGroupApi();
            api.fetchGroups().enqueue(new GroupResponseCallback());
        });

        return mGroupData;
    }


    private void saveGroups(GroupApi.GroupData data) {
        AsyncTask.execute(() -> {
            List<GroupEntity> groupsEntities = new ArrayList<>();
            List<Group> groups = new ArrayList<>();
            for (int i = 0; i < data.groups.size(); ++i) {
                groupsEntities.set(i, new GroupEntity(
                        data.groups.get(i).name,
                        data.groups.get(i).description
                ));
                groups.set(i, new Group(
                        data.groups.get(i).name,
                        data.groups.get(i).description
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
