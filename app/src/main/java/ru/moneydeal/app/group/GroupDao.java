package ru.moneydeal.app.group;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface GroupDao {
    @Insert
    void insert(List<GroupEntity> entities);

    @Query("DELETE FROM `group`")
    void reset();

    @Query("SELECT * FROM `group`")
    List<GroupEntity> getGroups();

    @Query("SELECT * FROM `group` WHERE id = :id LIMIT 1")
    List<GroupEntity> getGroup(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insetGroupUsers(List<GroupUserEntity> entities);

    @Query("SELECT userId FROM group_users WHERE groupId = :groupId")
    List<String> selectGroupUsers(String groupId);
}
