package ru.moneydeal.app.auth;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface AuthDao {
    @Insert
    void insert(AuthEntity... entity);

    @Query("DELETE FROM auth")
    void reset();

    @Query("SELECT * FROM auth LIMIT 1")
    List<AuthEntity> getUsers();

    @Query("SELECT token FROM auth LIMIT 1")
    List<String> getTokens();
}
