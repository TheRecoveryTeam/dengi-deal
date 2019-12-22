package ru.moneydeal.app.checks;

import java.util.List;

public class BaseCheckEntity {
    public String name;

    public String description;

    public String groupId;

    public List<CheckChunkEntity> chunks;

    public BaseCheckEntity(
            String name,
            String description,
            String groupId,
            List<CheckChunkEntity> chunks
    ) {
        this.name = name;
        this.description = description;
        this.groupId = groupId;
        this.chunks = chunks;
    }
}
