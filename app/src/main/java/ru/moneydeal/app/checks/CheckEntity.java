package ru.moneydeal.app.checks;

import java.util.List;

public class CheckEntity {
    public String id;

    public String name;

    public String description;

    public String groupId;

    public String userId;

    public Integer amount;

    public List<CheckChunkEntity> chunks;

    public CheckEntity(
            String id,
            String name,
            String description,
            String groupId,
            String userId,
            Integer amount,
            List<CheckChunkEntity> chunks
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.groupId = groupId;
        this.userId = userId;
        this.amount = amount;
        this.chunks = chunks;
    }
}
