package ru.moneydeal.app;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import ru.moneydeal.app.checks.CheckRepo;

public class CheckViewModel extends AndroidViewModel {
    public CheckViewModel(@NonNull Application application) {
        super(application);
    }

    private CheckRepo getCheckRepo() {
        return CheckRepo.getInstance(getApplication());
    }

    public LiveData<CheckRepo.GroupChecks> getChecks(String groupId) {
        final MediatorLiveData<CheckRepo.GroupChecks> data = new MediatorLiveData<>();

        LiveData<CheckRepo.GroupChecks> groupChecksLiveData = getCheckRepo().fetchChecks(groupId);
        data.addSource(groupChecksLiveData, groupChecks -> {
            data.postValue(groupChecks);

            if (groupChecks.progress == CheckRepo.Progress.FAILED || groupChecks.progress == CheckRepo.Progress.SUCCESS) {
                data.removeSource(groupChecksLiveData);
            }
        });

        return data;
    }
}
