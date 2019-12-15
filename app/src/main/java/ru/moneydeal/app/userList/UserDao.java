package ru.moneydeal.app.userList;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<UserEntity> entities);

    @Query("DELETE FROM users")
    void reset();

    @Query("SELECT * FROM users WHERE id = :id")
    List<UserEntity> selectUserById(String id);
}
