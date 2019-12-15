package ru.moneydeal.app.group;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(tableName = "group_users", primaryKeys = {"groupId", "userId"})
public class GroupUserEntity {
    @NonNull
    public String groupId;

    @NonNull
    public String userId;

    GroupUserEntity(String groupId, String userId) {
        this.groupId = groupId;
        this.userId = userId;
    }
}
