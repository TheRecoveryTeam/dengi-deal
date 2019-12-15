package ru.moneydeal.app.auth;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "auth")
public class AuthEntity {
    @PrimaryKey
    @NonNull
    public String login;

    @NonNull
    public String firstName;

    @NonNull
    public String lastName;

    @NonNull
    public String token;

    public AuthEntity(String login, String firstName, String lastName, String token) {
        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
        this.token = token;
    }
}
