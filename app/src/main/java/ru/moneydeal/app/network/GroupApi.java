package ru.moneydeal.app.network;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

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

        @NonNull
        public List<Statistic> statistics;
    }

    public static class Statistic {
        @NonNull
        public String from;

        @NonNull
        public String to;

        @NonNull
        public Integer amount;
    }

    public static class GroupData {
        public List<Group> groups;
        public List<UserApi.User> users;
    }

    public static class GroupsResponse extends BaseResponse {
        public GroupData data;
    }

    public static class GroupCreationResponse extends BaseResponse {
        public Group data;
    }

    public static class ParticipantData {
        @Nullable
        public UserApi.User user;
    }

    public static class GroupParticipantResponse extends BaseResponse {
        public ParticipantData data;
    }
    public static class AddParticipantResponse extends BaseResponse {
        public ParticipantData data;
    }

    @GET("group/list")
    Call<GroupsResponse> fetchGroups();

    @GET("group/participant")
    Call<GroupParticipantResponse> getParticipant(@Query("login") String login, @Query("group_id") String groupId);

    @FormUrlEncoded
    @POST("group/create")
    Call<GroupCreationResponse> createGroup(
            @Field("name") String name,
            @Field("description") String description
    );

    @FormUrlEncoded
    @POST("group/add_user")
    Call<AddParticipantResponse> addUser(
            @Field("user_id") String userId,
            @Field("group_id") String groupId
    );
}
