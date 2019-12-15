package ru.moneydeal.app.group;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface GroupDao {
    @Insert
    void insert(List<GroupEntity> entity);

    @Query("DELETE FROM `group`")
    void reset();

    @Query("SELECT * FROM `group`")
    List<GroupEntity> getGroups();
}
