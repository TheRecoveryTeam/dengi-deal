package ru.moneydeal.app.group;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "group")
public class GroupEntity {
    @PrimaryKey
    @NonNull
    public String name;

    @NonNull
    public String description;

    public GroupEntity(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
