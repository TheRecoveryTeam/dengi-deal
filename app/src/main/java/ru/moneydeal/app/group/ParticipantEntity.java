package ru.moneydeal.app.group;

import ru.moneydeal.app.models.BaseUser;
import ru.moneydeal.app.network.GroupApi;

public class ParticipantEntity extends BaseUser {
    public ParticipantEntity(String id, String login, String firstName, String lastName) {
        super(id, login, firstName, lastName);
    }
}
