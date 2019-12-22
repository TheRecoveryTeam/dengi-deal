package ru.moneydeal.app.checks;

import java.util.List;

public class CheckEntity extends BaseCheckEntity {
    public String id;

    public String userId;

    public Integer amount;

    public CheckEntity(
            String id,
            String name,
            String description,
            String groupId,
            String userId,
            Integer amount,
            List<CheckChunkEntity> chunks
    ) {
        super(name, description, groupId, chunks);
        this.id = id;
        this.userId = userId;
        this.amount = amount;
    }
}
