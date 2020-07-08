package com.example.alarmClock;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AlarmDao {

    @Insert
    void insert(AlarmTable alarmTable);

    @Update
    void update(AlarmTable alarmTable);

    @Delete
    void delete(AlarmTable alarmTable);

    @Query("DELETE FROM alarm_table")
    void deleteAllAlarms();

    @Query("SELECT * FROM alarm_table ORDER BY date ASC")
    LiveData<List<AlarmTable>> getALLAlarms();

}
