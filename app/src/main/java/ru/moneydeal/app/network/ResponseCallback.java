package ru.moneydeal.app.network;

import android.util.Log;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class ResponseCallback<R extends BaseResponse> implements Callback<R> {
    @Override
    public final void onResponse(Call<R> call, Response<R> response) {
        BaseResponse baseResponse = response.body();

        try {
            if (baseResponse == null) {
                Log.d("ResponseCallback", "baseResponse == null");
                createErrorFromResponse(response.errorBody().string());
                return;
            }
            if (baseResponse.status.equals(BaseResponse.STATUS_OK)) {
                Log.d("ResponseCallback", "status ok");
                onOk(response.body());
                return;
            }
            if (baseResponse.status.equals(BaseResponse.STATUS_ERROR)) {
                Log.d("ResponseCallback", "status error");
                createErrorFromResponse(response.errorBody().string());
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
        Log.d("ResponseCallback", "Retrofit Exception -> " + ((t != null && t.getMessage() != null) ? t.getMessage() : "---"));
        createEmptyError();
    }

    private void createEmptyError() {
        onError(new ErrorResponse());
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
