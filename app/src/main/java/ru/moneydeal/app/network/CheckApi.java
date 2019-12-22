package ru.moneydeal.app.network;

import androidx.annotation.NonNull;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import ru.moneydeal.app.checks.BaseCheckEntity;

public interface CheckApi {
    public static class CheckChunk {
        @NonNull
        public String user_id;

        @NonNull
        public Integer amount;

        public CheckChunk(String userId, Integer amount) {
            this.user_id = userId;
            this.amount = amount;
        }
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

    public static class CreateCheckBody {
        @NonNull
        public String name;

        @NonNull
        public String description;

        @NonNull
        public String group_id;

        @NonNull
        public List<CheckChunk> chunks;

        public CreateCheckBody(String name, String description, String groupId, ArrayList<CheckChunk> chunks) {
            this.name = name;
            this.description = description;
            group_id = groupId;
            this.chunks = chunks;
        }
    }

    public class ChecksGetResponseData {
        public List<Check> checks;
    }

    public class ChecksGetResponse extends BaseResponse {
        public ChecksGetResponseData data;
    }

    @GET("checks/get")
    Call<ChecksGetResponse> fetchChecks(@Query("group_id") String groupId);

    @POST("group/add_check")
    Call<BaseResponse> createCheck(@Body CreateCheckBody object);
}
