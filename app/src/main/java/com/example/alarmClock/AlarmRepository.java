package com.example.alarmClock;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class AlarmRepository {

    private AlarmDao alarmDao;
    private LiveData<List<AlarmTable>> allAlarms;

    public AlarmRepository(Application application){
        AlarmDatabase database = AlarmDatabase.getInstance(application);
        alarmDao = database.alarmDao();
        allAlarms = alarmDao.getALLAlarms();
    }

    public void insert(AlarmTable table){
        new InsertNoteAsyncTask(alarmDao).execute(table);
    }

    public void update(AlarmTable table){
        new UpdateNoteAsyncTask(alarmDao).execute(table);
    }

    public void delete(AlarmTable table){
        new DeleteNoteAsyncTask(alarmDao).execute(table);
    }

    public void deleteAllAlarms(){
        new DeleteAllNotesAsyncTask(alarmDao).execute();
    }

    public LiveData<List<AlarmTable>> getAllAlarms(){
        return allAlarms;
    }

    private static class InsertNoteAsyncTask extends AsyncTask<AlarmTable, Void, Void> {
        private AlarmDao alarmDao;

        private InsertNoteAsyncTask(AlarmDao alarmDao){
            this.alarmDao = alarmDao;
        }
        @Override
        protected Void doInBackground(AlarmTable... alarmTables) {
            alarmDao.insert(alarmTables[0]);
            return null;
        }
    }

    private static class UpdateNoteAsyncTask extends AsyncTask<AlarmTable, Void, Void>{
        private AlarmDao alarmDao;

        private UpdateNoteAsyncTask(AlarmDao alarmDao){
            this.alarmDao = alarmDao;
        }
        @Override
        protected Void doInBackground(AlarmTable... alarmTables) {
            alarmDao.update(alarmTables[0]);
            return null;
        }
    }

    private static class DeleteNoteAsyncTask extends AsyncTask<AlarmTable, Void, Void>{
        private AlarmDao alarmDao;

        private DeleteNoteAsyncTask(AlarmDao alarmDao){
            this.alarmDao = alarmDao;
        }
        @Override
        protected Void doInBackground(AlarmTable... alarmTables) {
            alarmDao.delete(alarmTables[0]);
            return null;
        }
    }

    private static class DeleteAllNotesAsyncTask extends AsyncTask<AlarmTable, Void, Void>{
        private AlarmDao alarmDao;

        private DeleteAllNotesAsyncTask(AlarmDao alarmDao){
            this.alarmDao = alarmDao;
        }
        @Override
        protected Void doInBackground(AlarmTable... alarmTables) {
            alarmDao.deleteAllAlarms();
            return null;
        }
    }
}
