package ru.moneydeal.app.network;

import androidx.annotation.NonNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface GroupApi {
    public static class Group {
        @NonNull
        public String _id;

        @NonNull
        public String name;

        @NonNull
        public String description;

        @NonNull
        public List<String> users_ids;
    }

    public static class GroupData {
        public List<Group> groups;
    }

    public static class GroupsResponse extends BaseResponse {
        public GroupData data;
    }

    public static class GroupCreationResponse extends BaseResponse {
        public Group data;
    }

    @GET("group/list")
    Call<GroupsResponse> fetchGroups();

    @FormUrlEncoded
    @POST("group/create")
    Call<GroupCreationResponse> createGroup(
            @Field("name") String name,
            @Field("description") String description
    );
}
