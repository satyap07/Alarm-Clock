package com.example.alarmClock;


import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = AlarmTable.class, version = 1, exportSchema = false)
public abstract class AlarmDatabase extends RoomDatabase {

    public static AlarmDatabase instance;
    public abstract AlarmDao alarmDao();

    public static synchronized AlarmDatabase getInstance(Context context){
        if(instance==null){

           instance = Room.databaseBuilder(context.getApplicationContext(),
                   AlarmDatabase.class,"alarm_database")
                   .fallbackToDestructiveMigration()
                   .addCallback(roomCallback)
                   .build();
        }
        return instance;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
        }
    };
    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {
        private AlarmDao alarmDao;

        private PopulateDbAsyncTask(AlarmDatabase db) {
            alarmDao = db.alarmDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
                        return null;
        }
    }
}


