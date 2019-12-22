package ru.moneydeal.app.group;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(tableName = "statistics", primaryKeys = {"groupId", "from", "to"})
public class StatisticEntity {
    @NonNull
    public String groupId;

    @NonNull
    public String from;

    @NonNull
    public String to;

    @NonNull
    public Integer amount;

    public StatisticEntity(String groupId, String from, String to, Integer amount) {
        this.groupId = groupId;
        this.from = from;
        this.to = to;
        this.amount = amount;
    }
}
