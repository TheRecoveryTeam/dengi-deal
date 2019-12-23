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
import ru.moneydeal.app.userList.UsersRepo;

public class GroupRepo {
    private final ApiRepo mApiRepo;
    private final UsersRepo mUsersRepo;
    private final GroupDao mGroupDao;

    private MutableLiveData<List<GroupEntity>> mGroupData;
    private MutableLiveData<CreationProgress> mCreationProgress;

    public GroupRepo(ApplicationModified context) {
        mApiRepo = context.getApis();
        mUsersRepo = context.getUsersRepo();
        mGroupDao = context.getDB().getGroupDao();
        mGroupData = new MutableLiveData<>();
        mCreationProgress = new MutableLiveData<>();

        loadGroupDataFromDB();
    }

    private void loadGroupDataFromDB() {
        AsyncTask.execute(() -> mGroupData.postValue(mGroupDao.getGroups()));
    }

    @NonNull
    public static GroupRepo getInstance(Context context) {
        return ApplicationModified.from(context).getGroupRepo();
    }

    public LiveData<List<GroupEntity>> fetchGroups() {
        AsyncTask.execute(() -> {
            GroupApi api = mApiRepo.getGroupApi();
            api.fetchGroups().enqueue(new GroupResponseCallback());
        });

        return mGroupData;
    }

    public LiveData<ParticipantEntity> fetchParticipant(String login, String groupId) {
        MutableLiveData<ParticipantEntity> participant = new MutableLiveData<>();

        AsyncTask.execute(() -> {
            GroupApi api = mApiRepo.getGroupApi();
            api.getParticipant(login, groupId).enqueue(new GroupParticipantResponseCallback(participant));
        });

        return participant;
    }

    private void saveGroups(GroupApi.GroupData data) {
        AsyncTask.execute(() -> {
            List<GroupEntity> groupsEntities = new ArrayList<>();
            List<GroupUserEntity> groupUserEntities = new ArrayList<>();
            List<StatisticEntity> statisticEntities = new ArrayList<>();

            try {
                for (GroupApi.Group group: data.groups) {
                    GroupEntity gEntity = new GroupEntity(group._id, group.name, group.description);
                    groupsEntities.add(gEntity);

                    for (String userId: group.users_ids) {
                        GroupUserEntity uEntity = new GroupUserEntity(group._id, userId);
                        groupUserEntities.add(uEntity);
                    }

                    for (GroupApi.Statistic stat: group.statistics) {
                        StatisticEntity sEntity = new StatisticEntity(group._id, stat.from, stat.to, stat.amount);
                        statisticEntities.add(sEntity);
                    }
                }

                mUsersRepo.addUsers(data.users);
                Log.d("GroupRepo", "stataaaaa" + statisticEntities.size());

                mGroupDao.reset();
                mGroupDao.resetGroupUsers();;
                mGroupDao.resetStatistics();

                mGroupDao.insert(groupsEntities);
                mGroupDao.insetGroupUsers(groupUserEntities);
                mGroupDao.insertStatistics(statisticEntities);

                mGroupData.postValue(groupsEntities);
            } catch (Exception e) {
                Log.d("GroupRepo", "failed" + e.getMessage());
            }
        });
    }

    public LiveData<GroupEntity> getGroup(String groupId) {
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

    public LiveData<List<StatisticEntity>> getStatistics(String groupId) {
        MutableLiveData<List<StatisticEntity>> liveData = new MutableLiveData<>();

        AsyncTask.execute(() -> {
            List<StatisticEntity> list = mGroupDao.selectStatistics(groupId);
            Log.d("GroupRepo", " statistics size " + list.size());

            if (list.size() == 0) {
                liveData.postValue(null);
            } else {
                liveData.postValue(list);
            }
        });

        return liveData;
    }

    public LiveData<List<String>> getGroupUserIds(String groupId) {
        MutableLiveData<List<String>> liveData = new MutableLiveData<>();

        AsyncTask.execute(() -> {
            List<String> ids = mGroupDao.selectGroupUsers(groupId);
            liveData.postValue(ids);
        });

        return liveData;
    }

    public MutableLiveData<CreationProgress> createGroup(
            @NonNull String name,
            @NonNull String description) {
        mCreationProgress.setValue(CreationProgress.IN_PROGRESS);

        GroupApi api = mApiRepo.getGroupApi();
        api.createGroup(name, description).enqueue(new GroupCreationResponseCallback());
        return mCreationProgress;
    }

    public LiveData<ParticipantEntity> addParticipant(@NonNull String userId,
                               @NonNull String groupId) {
        MutableLiveData<ParticipantEntity> participant = new MutableLiveData<>();

        GroupApi api = mApiRepo.getGroupApi();
        api.addUser(userId, groupId).enqueue(new GroupAddParticipantResponseCallback(participant, groupId));

        return participant;
    }

    public enum CreationProgress {
        NONE,
        IN_PROGRESS,
        SUCCESS,
        FAILED
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

    public class GroupAddParticipantResponseCallback extends ResponseCallback<GroupApi.AddParticipantResponse> {
        String mGroupId;
        MutableLiveData<ParticipantEntity> mParticipant;

        public GroupAddParticipantResponseCallback(MutableLiveData<ParticipantEntity> participant, String groupId) {
            mGroupId = groupId;
            mParticipant = participant;
        }
        private void saveParticipant(GroupApi.ParticipantData data) {
            AsyncTask.execute(() -> {
                List<GroupUserEntity> groupUserEntities = new ArrayList<>();

                if (data.user == null) {
                    return;
                }
                try {
                    GroupUserEntity user = new GroupUserEntity(mGroupId, data.user._id);
                    ParticipantEntity participant = new ParticipantEntity(
                            data.user._id,
                            data.user.login,
                            data.user.first_name,
                            data.user.last_name);

                    groupUserEntities.add(user);
                    mGroupDao.reset();
                    mGroupDao.insetGroupUsers(groupUserEntities);
                    mParticipant.postValue(participant);
                } catch (Exception e) {
                    Log.d("GroupRepo", "failed" + e.getMessage());
                }
            });
        }

        @Override
        public void onOk(GroupApi.AddParticipantResponse response) {
            Log.d("GroupRepo", "fetch ok");
            saveParticipant(response.data);
        }

        @Override
        public void onError(ErrorResponse response) {
            Log.d("GroupRepo", "fetch error " + response.data.message);
        }
    }

    public class GroupParticipantResponseCallback extends ResponseCallback<GroupApi.GroupParticipantResponse> {
        MutableLiveData<ParticipantEntity> mParticipant;

        private void saveParticipant(GroupApi.ParticipantData data) {
            if (data.user == null) {
                mParticipant.postValue(null);
                return;
            }
            ParticipantEntity participant = new ParticipantEntity(data.user._id, data.user.login, data.user.first_name, data.user.last_name);

            mParticipant.postValue(participant);
        }

        public GroupParticipantResponseCallback(MutableLiveData<ParticipantEntity> participant) {
            mParticipant = participant;
        }

        @Override
        public void onOk(GroupApi.GroupParticipantResponse response) {
            Log.d("GroupRepo", "fetch participant");
            saveParticipant(response.data);
        }

        @Override
        public void onError(ErrorResponse response) {
            Log.d("GroupRepo", "fetch error " + response.data.message);
        }
    }

    public class GroupCreationResponseCallback extends ResponseCallback<GroupApi.GroupCreationResponse> {
        @Override
        public void onOk(GroupApi.GroupCreationResponse response) {
            mCreationProgress.setValue(CreationProgress.SUCCESS);
            Log.d("GroupRepo", "fetch ok " + response.data.name);
        }

        @Override
        public void onError(ErrorResponse response) {
            mCreationProgress.setValue(CreationProgress.FAILED);
            Log.d("GroupRepo", "fetch error " + response.data.message);
        }
    }
}
