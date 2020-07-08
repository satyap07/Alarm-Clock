package com.example.alarmClock;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import static com.example.alarmClock.App.CHANNEL_ID;

public class RingtoneService extends Service {

    MediaPlayer myPlayer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String input = intent.getStringExtra("inputExtra");

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.ringtone_preference), MODE_PRIVATE);
        String ringTone = sharedPreferences.getString("ringtone", null);
        Uri uri = Uri.parse(ringTone);
        myPlayer = MediaPlayer.create(this, uri);
        myPlayer.setLooping(true);
        myPlayer.start();

        Intent notificationIntent = new Intent(getBaseContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(),
                0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(getBaseContext(), CHANNEL_ID)
                .setContentTitle("Alarm Clock!!")
                .setContentText(input)
                .setSmallIcon(R.drawable.alarm_image)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        myPlayer.stop();
    }
}
