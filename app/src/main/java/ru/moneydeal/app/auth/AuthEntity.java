package ru.moneydeal.app.auth;

import androidx.annotation.NonNull;
import androidx.room.Entity;

import ru.moneydeal.app.models.BaseUser;

@Entity(tableName = "auth")
public class AuthEntity extends BaseUser {
    @NonNull
    public String token;

    public AuthEntity(
            String id,
            String login,
            String firstName,
            String lastName,
            @NonNull String token
    ) {
        super(id, login, firstName, lastName);

        this.token = token;
    }
}
