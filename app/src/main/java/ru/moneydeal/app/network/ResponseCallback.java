package ru.moneydeal.app.network;

import android.util.Log;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class ResponseCallback<R extends BaseResponse> implements Callback<R> {
    private Class<R> mOkClazz;

    public ResponseCallback(Class<R> okClazz) {
        mOkClazz = okClazz;
    }

    @Override
    public final void onResponse(Call<R> call, Response<R> response) {
        BaseResponse baseResponse = response.body();
        if (baseResponse == null) {
            Log.d("ResponseCallback", "baseResponse == null");
            createEmptyError();
            return;
        }

        try {
            if (baseResponse.status.equals(BaseResponse.STATUS_OK)) {
                Log.d("ResponseCallback", "status ok");
                onOk(response.body());
                return;
            }
            if (baseResponse.status.equals(BaseResponse.STATUS_ERROR)) {
                Log.d("ResponseCallback", "status error");
                createErrorFromResponse(response.raw().body().string());
                return;
            }
            Log.d("ResponseCallback", "undefined status: " + baseResponse.status);
            createEmptyError();
        } catch (Exception e) {
            Log.d("ResponseCallback", "exception " + e.getMessage());
            createEmptyError();
        }
    }

    @Override
    public final void onFailure(Call<R> call, Throwable t) {
        Log.d("ResponseCallback", "failure " + t.getMessage());
        createEmptyError();
    }

    private void createEmptyError() {
        onError(new ErrorResponse());
    }

    private void createOkFromResponse(String json) throws IOException {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<R> jsonAdapter = moshi.adapter(mOkClazz);

        R response = jsonAdapter.fromJson(json);
        onOk(response);
    }

    private void createErrorFromResponse(String json) throws IOException {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<ErrorResponse> jsonAdapter = moshi.adapter(ErrorResponse.class);

        ErrorResponse response = jsonAdapter.fromJson(json);
        onError(response);
    }

    public abstract void onOk(R response);

    public abstract void onError(ErrorResponse response);
}
