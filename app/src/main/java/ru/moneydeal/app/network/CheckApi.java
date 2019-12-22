package ru.moneydeal.app.network;

import androidx.annotation.NonNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CheckApi {
    public static class CheckChunk {
        @NonNull
        public String user_id;

        @NonNull
        public Integer amount;
    }

    public static class Check {
        @NonNull
        public String _id;

        @NonNull
        public String name;

        @NonNull
        public String description;

        @NonNull
        public String group_id;

        @NonNull
        public String user_id;

        @NonNull
        public List<CheckChunk> chunks;
    }

    public class ChecksGetResponseData {
        public List<Check> checks;
    }

    public class ChecksGetResponse extends BaseResponse {
        public ChecksGetResponseData data;
    }

    @GET("checks/get")
    Call<ChecksGetResponse> fetchChecks(@Query("group_id") String groupId);
}
