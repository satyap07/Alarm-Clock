package com.example.alarmClock;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class AlarmAdapter extends ListAdapter<AlarmTable, AlarmAdapter.AlarmHolder>{

    private OnItemClickListener listener;
    private Context mContext;
    protected AlarmAdapter(Context context) {
        super(DIFF_CALLBACK);
        mContext = context;
    }


    private static final DiffUtil.ItemCallback<AlarmTable> DIFF_CALLBACK = new DiffUtil.ItemCallback<AlarmTable>() {
        @Override
        public boolean areItemsTheSame(@NonNull AlarmTable oldItem, @NonNull AlarmTable newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull AlarmTable oldItem, @NonNull AlarmTable newItem) {
            return oldItem.getDate().equals(newItem.getDate()) &&
                    oldItem.getCalendarId()==(newItem.getCalendarId());
        }
    };
    @NonNull
    @Override
    public AlarmHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item,parent,false);
        return new AlarmHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmHolder holder, int position) {

        final AlarmTable currentAlarm = getItem(position);
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
        try {
            cal.setTime(Objects.requireNonNull(sdf.parse(currentAlarm.getDate())));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String timeText = "";
        timeText += DateFormat.getTimeInstance(DateFormat.SHORT).format(cal.getTime());
        holder.textViewTime.setText(timeText + " Hours");
        holder.cancelAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                cancelAlarm(currentAlarm);

            }
        });


    }

    class AlarmHolder extends RecyclerView.ViewHolder {
        private TextView textViewTime;
        private ImageButton cancelAlarmButton;

        public AlarmHolder(@NonNull View itemView) {
            super(itemView);
            textViewTime = itemView.findViewById(R.id.text_view_time);
            cancelAlarmButton = itemView.findViewById(R.id.cancel_alarm_image_Button);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if(listener !=null && position != RecyclerView.NO_POSITION)
                        listener.onItemClick(getItem(position));
                }
            });
        }
    }

    public AlarmTable getAlarmAt(int position) {
        return getItem(position);
    }

    public interface OnItemClickListener {
        void onItemClick(AlarmTable table);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public void cancelAlarm(AlarmTable currentAlarm){
        if(isMyServiceRunning(RingtoneService.class)){
            AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(mContext, AlertReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, currentAlarm.getCalendarId(), intent, 0);
            alarmManager.cancel(pendingIntent);
            Intent serviceIntent = new Intent(mContext, RingtoneService.class);
            mContext.stopService(serviceIntent);
            AlarmViewModel alarmViewModel = new ViewModelProvider((ViewModelStoreOwner) mContext).get(AlarmViewModel.class);
            alarmViewModel.delete(currentAlarm);
            Toast.makeText(mContext, "Alarm stopped!!", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(mContext, "Alarm has not started yet", Toast.LENGTH_SHORT).show();
        }

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
