package com.wilco375.roosternotification;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

import java.util.Calendar;

public class AutoStartUp extends IntentService {

    int notificationId = 001;

    NotificationCompat.Builder builder;

    @Override
    protected void onHandleIntent(Intent intent) {
        createNotification();
        AlarmManager alarmMananger = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(getBaseContext(),AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0,i,PendingIntent.FLAG_CANCEL_CURRENT);
        alarmMananger.setRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(),60000,pendingIntent);
    }

    public AutoStartUp(){
        super("AutoStartUp");
    }

    private void createNotification(){
        String subject;
        String classroom;
        int uur;
        String string = "";

        SharedPreferences sp = getSharedPreferences("Main", MODE_PRIVATE);

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        if(day == Calendar.MONDAY){
            string = "a";
        }else if(day == Calendar.TUESDAY){
            string = "b";
        }else if(day == Calendar.WEDNESDAY){
            string = "c";
        }else if(day == Calendar.THURSDAY){
            string = "d";
        }else if(day == Calendar.FRIDAY){
            string = "e";
        }
        uur = AlarmReceiver.lesuur();
        string = string + String.valueOf(uur);

        subject = sp.getString(string + "3", "");
        classroom = sp.getString(string+"4","");

        if(!subject.equals("") && !classroom.equals("") && !sp.getBoolean(string+"2",false)){
            builder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle(String.valueOf(uur) + ": " + subject)
                    .setContentText(classroom);
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
            notificationManagerCompat.notify(notificationId, builder.build());
        }
    }
}
