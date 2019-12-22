package ru.moneydeal.app.checks;

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
import ru.moneydeal.app.network.CheckApi;
import ru.moneydeal.app.network.ErrorResponse;
import ru.moneydeal.app.network.ResponseCallback;

public class CheckRepo {
    private final ApiRepo mApiRepo;

    public CheckRepo(ApplicationModified context) {
        mApiRepo = context.getApis();
    }

    @NonNull
    public static CheckRepo getInstance(Context context) {
        return ApplicationModified.from(context).getCheckRepo();
    }

    public LiveData<GroupChecks> fetchChecks(String groupId) {
        MutableLiveData<GroupChecks> liveData = new MutableLiveData<>();
        liveData.setValue(new GroupChecks());

        processFetchChecks(liveData, groupId);

        return liveData;
    }

    private void processFetchChecks(MutableLiveData<GroupChecks> liveData, String groupId) {
        AsyncTask.execute(() -> mApiRepo
                .getCheckApi()
                .fetchChecks(groupId)
                .enqueue(new ChecksResponseCallback(liveData)));
    }

    public enum Progress {
        IN_PROGRESS,
        SUCCESS,
        FAILED
    }

    public class GroupChecks {
        public Progress progress;
        public List<CheckEntity> entities;

        public GroupChecks() {
            this(Progress.IN_PROGRESS, new ArrayList<>());
        }

        public GroupChecks(Progress progress, List<CheckEntity> entities) {
            this.progress = progress;
            this.entities = entities;
        }
    }

    public class ChecksResponseCallback extends ResponseCallback<CheckApi.ChecksGetResponse> {
        private final MutableLiveData<GroupChecks> mLiveData;

        public ChecksResponseCallback(MutableLiveData<GroupChecks> liveData) {
            this.mLiveData = liveData;
        }

        @Override
        public void onOk(CheckApi.ChecksGetResponse response) {
            try {
                ArrayList<CheckEntity> checkEntities = new ArrayList<>();
                List<CheckApi.Check> checks = response.data.checks;
                for (CheckApi.Check check : checks) {
                    ArrayList<CheckChunkEntity> checkChunkEntities = new ArrayList<>();
                    Integer amount = 0;
                    for (CheckApi.CheckChunk chunk : check.chunks) {
                        checkChunkEntities.add(new CheckChunkEntity(
                                check._id,
                                chunk.user_id,
                                chunk.amount
                        ));
                        amount += chunk.amount;
                    }

                    CheckEntity checkEntity = new CheckEntity(
                            check._id,
                            check.name,
                            check.description,
                            check.group_id,
                            check.user_id,
                            amount,
                            checkChunkEntities
                    );
                    checkEntities.add(checkEntity);
                }

                Log.d("CheckRepo", "fetch ok with check count" + checks.size());
                mLiveData.postValue(new GroupChecks(Progress.SUCCESS, checkEntities));
            }
            catch (Exception e) {
                Log.d("CheckRepo", "fetch error " + e.getMessage());
                mLiveData.postValue(new GroupChecks(Progress.FAILED, new ArrayList<>()));
            }
        }

        @Override
        public void onError(ErrorResponse response) {
            Log.d("CheckRepo", "fetch error " + response.data.message);
            mLiveData.postValue(new GroupChecks(Progress.FAILED, new ArrayList<>()));
        }
    }
}
