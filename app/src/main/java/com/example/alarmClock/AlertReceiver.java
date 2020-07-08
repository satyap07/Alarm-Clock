package com.example.alarmClock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;

public class AlertReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

            String input = "Wake Up!!!";
            Intent serviceIntent = new Intent(context, RingtoneService.class);
            serviceIntent.putExtra("inputExtra", input);
            ContextCompat.startForegroundService(context, serviceIntent);

    }
}
