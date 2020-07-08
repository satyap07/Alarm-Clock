package com.example.alarmClock;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    String chosenRingtone;
    private AlarmViewModel alarmViewModel;
    List<AlarmTable> newList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ImageView emptyImageView = findViewById(R.id.empty_image_view);
        newList = new ArrayList<>();
        FloatingActionButton time_picker = findViewById(R.id.button_add_alarm);
        time_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment TimeFragment = new TimePickerFragment();
                TimeFragment.show(getSupportFragmentManager(),"Choose Time");
            }
        });

        final RecyclerView recyclerView = findViewById(R.id.alarm_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        final AlarmAdapter adapter = new AlarmAdapter(this);
        recyclerView.setAdapter(adapter);

        alarmViewModel = new ViewModelProvider(this).get(AlarmViewModel.class);
        alarmViewModel.getAllAlarms().observe(this, new Observer<List<AlarmTable>>() {
            @Override
            public void onChanged(@Nullable List<AlarmTable> tables) {
                if(tables.isEmpty()){
                     recyclerView.setVisibility(View.INVISIBLE);
                     emptyImageView.setVisibility(View.VISIBLE);
                }else{
                    emptyImageView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
                newList = tables;
                adapter.submitList(tables);
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                AlarmTable currentAlarm = adapter.getAlarmAt(viewHolder.getAdapterPosition());
                alarmViewModel.delete(currentAlarm);
                cancelAlarm(currentAlarm.getCalendarId());

            }
        }).attachToRecyclerView(recyclerView);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String title = "";
        if (resultCode == Activity.RESULT_OK && requestCode == 5) {
            assert data != null;
            Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

            if (uri != null) {
                this.chosenRingtone = uri.toString();
                SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.ringtone_preference), MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("ringtone", this.chosenRingtone);
                editor.commit();
                Ringtone ringtone = RingtoneManager.getRingtone(this, Uri.parse(this.chosenRingtone));
                title = ringtone.getTitle(this);
            } else {
                this.chosenRingtone = null;
            }
        }
        Toast.makeText(MainActivity.this, "Ringtone Selected: " + title, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {

        if(timePicker.isEnabled() && hourOfDay >= 0){

            Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR_OF_DAY, hourOfDay);
            c.set(Calendar.MINUTE, minute);
            c.set(Calendar.SECOND, 0);
            String selectedTime =c.getTime().toString();
            for(int i = 0; i<newList.size(); i++){
                AlarmTable alarmTable = newList.get(i);
                String date = alarmTable.getDate();
                if(date.equals(selectedTime)){
                    Toast.makeText(this, "Alarm already exist!!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            startAlarm(c);
        }


        }
    private void startAlarm(Calendar c){


        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        final int id = (int) System.currentTimeMillis();
        AlarmTable alarmTable = new AlarmTable(c.getTime().toString(),id);
        alarmViewModel.insert(alarmTable);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, id, intent, PendingIntent.FLAG_ONE_SHOT);
        if (c.before(Calendar.getInstance())) {
            c.add(Calendar.DATE, 1);
        }
        assert alarmManager != null;
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);

    }

    public void cancelAlarm(int alarmId){

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, alarmId, intent, PendingIntent.FLAG_ONE_SHOT);

        if(isMyServiceRunning()){
                Intent serviceIntent = new Intent(this, RingtoneService.class);
                stopService(serviceIntent);
        }
        assert alarmManager != null;
        alarmManager.cancel(pendingIntent);
        Toast.makeText(this, "Alarm deleted!!", Toast.LENGTH_SHORT).show();

    }

    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        assert manager != null;
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (RingtoneService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.set_ringtone :
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALL);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
                startActivityForResult(intent, 5);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

}