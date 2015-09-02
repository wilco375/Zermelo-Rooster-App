package com.wilco375.roosternotification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {

    int notificationId = 001;

    NotificationCompat.Builder builder;

    @Override
    public void onReceive(Context context, Intent intent) {
        createNotification(context);
    }

    private void createNotification(Context context){
        String subject;
        String classroom;
        String string = "";

        SharedPreferences sp = context.getSharedPreferences("Main", context.MODE_PRIVATE);

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
        string = string + String.valueOf(lesuur());

        subject = sp.getString(string + "3", "");
        classroom = sp.getString(string+"4","");

        if(!subject.equals("") && !classroom.equals("") && !sp.getBoolean(string+"2",false)){
            builder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle(subject)
                    .setContentText(classroom);
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            notificationManagerCompat.notify(notificationId, builder.build());
        }
    }

    public static int lesuur(){
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        //Show quarter earlier
        minute = minute + 15;
        if(minute >= 60){
            hour = hour + 1;
            minute = minute - 60;
        }

        //hour = 10;
        //minute = 22;

        if((hour == 8 && minute >= 15)||(hour == 9 && minute <= 5)){
            return 1;
        }else if(hour == 9 && minute >= 5 && minute <= 55){
            return 2;
        }else if((hour == 9 && minute >= 55) || (hour == 10 && minute <= 45)){
            return 3;
        }else if(hour == 11 && minute >= 5 && minute <= 55){
            return 4;
        }else if((hour == 11 && minute >= 55) || (hour == 12 && minute <= 45)){
            return 5;
        }else if((hour == 13 && minute >= 15)||(hour == 14 && minute <= 5)){
            return 6;
        }else if(hour == 14 && minute >= 5 && minute <= 55){
            return 7;
        }else if((hour == 14 && minute >= 55) || (hour == 15 && minute <= 45)){
            return 8;
        }

        else return 0;
    }
}
