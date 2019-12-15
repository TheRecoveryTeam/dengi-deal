package ru.moneydeal.app.group;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "group")
public class GroupEntity {
    @NonNull
    @PrimaryKey
    public String id;

    @NonNull
    public String name;

    @NonNull
    public String description;

    public GroupEntity(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
}
