package com.example.alarmClock;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity (tableName = "alarm_table")
public class AlarmTable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String date;

    private int calendarId;

    public AlarmTable(String date, int calendarId) {
        this.date = date;
        this.calendarId = calendarId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public int getCalendarId() {
        return calendarId;
    }
}
