package ru.moneydeal.app.group;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface GroupDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<GroupEntity> entities);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertStatistics(List<StatisticEntity> entities);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insetGroupUsers(List<GroupUserEntity> entities);

    @Query("DELETE FROM `group`")
    void reset();

    @Query("SELECT * FROM `group`")
    List<GroupEntity> getGroups();

    @Query("SELECT * FROM `group` WHERE id = :id LIMIT 1")
    List<GroupEntity> getGroup(String id);

    @Query("SELECT userId FROM group_users WHERE groupId = :groupId")
    List<String> selectGroupUsers(String groupId);

    @Query("SELECT * FROM statistics WHERE groupId = :groupId")
    List<StatisticEntity> selectStatistics(String groupId);
}
