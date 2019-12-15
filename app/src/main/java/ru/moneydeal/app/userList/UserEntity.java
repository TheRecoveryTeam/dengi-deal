package ru.moneydeal.app.userList;

import androidx.room.Entity;

import ru.moneydeal.app.models.BaseUser;

@Entity(tableName = "users")
public class UserEntity extends BaseUser {
    public UserEntity(String id, String login, String firstName, String lastName) {
        super(id, login, firstName, lastName);
    }
}
