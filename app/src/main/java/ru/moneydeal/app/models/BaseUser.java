package ru.moneydeal.app.models;

import androidx.annotation.NonNull;
import androidx.room.PrimaryKey;

public abstract class BaseUser {
    @PrimaryKey
    @NonNull
    public String id;

    @NonNull
    public String login;

    @NonNull
    public String firstName;

    @NonNull
    public String lastName;

    public BaseUser(
            @NonNull String id,
            @NonNull String login,
            @NonNull String firstName,
            @NonNull String lastName
    ) {
        this.id = id;
        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
