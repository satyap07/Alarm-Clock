package com.example.alarmClock;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class AlarmViewModel extends AndroidViewModel {

    private AlarmRepository repository;
    private LiveData<List<AlarmTable>> allAlarms;
    public AlarmViewModel(@NonNull Application application) {
        super(application);
        repository = new AlarmRepository(application);
        allAlarms = repository.getAllAlarms();
    }
    public void insert(AlarmTable table) {
        repository.insert(table);
    }
    public void update(AlarmTable table) {
        repository.update(table);
    }
    public void delete(AlarmTable table) {
        repository.delete(table);
    }
    public void deleteAllNotes() {
        repository.deleteAllAlarms();
    }
    public LiveData<List<AlarmTable>> getAllAlarms() {
        return allAlarms;
    }
}
