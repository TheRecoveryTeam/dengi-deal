package ru.moneydeal.app.network;

import androidx.annotation.NonNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

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
        public List<UserApi.User> users;
    }

    public static class GroupsResponse extends BaseResponse {
        public GroupData data;
    }

    @GET("group/list")
    Call<GroupsResponse> fetchGroups();
}
