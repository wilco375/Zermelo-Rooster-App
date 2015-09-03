package com.wilco375.roosternotification;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {

    static int notificationId = 001;

    static NotificationCompat.Builder builder;

    @Override
    public void onReceive(Context context, Intent intent) {
        createNotification(context);
    }

    public static void createNotification(Context context){
        String subject;
        String classroom;
        int uur;
        String string = "";
        String dag = "";

        SharedPreferences sp = context.getSharedPreferences("Main", context.MODE_PRIVATE);
        SharedPreferences.Editor spe = sp.edit();

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        if(day == Calendar.MONDAY) {
            dag = "Maandag";
            string = "a";
        }else if(day == Calendar.TUESDAY){
            dag = "Dinsdag";
            string = "b";
        }else if(day == Calendar.WEDNESDAY){
            dag = "Woensdag";
            string = "c";
        }else if(day == Calendar.THURSDAY){
            dag = "Donderdag";
            string = "d";
        }else if(day == Calendar.FRIDAY){
            dag = "Vrijdag";
            string = "e";
        }
        uur = lesuur(context);

        NotificationCompat.BigTextStyle secondPageStyle = new NotificationCompat.BigTextStyle();
        secondPageStyle.setBigContentTitle(dag)
                .bigText("1. "+ sp.getString(string+"13","")+" "+sp.getString(string+"14","")+"\n"+
                                "2. "+ sp.getString(string+"23","")+" "+sp.getString(string+"24","")+"\n"+
                                "3. "+ sp.getString(string+"33","")+" "+sp.getString(string+"34","")+"\n"+
                                "4. "+ sp.getString(string+"43","")+" "+sp.getString(string+"44","")+"\n"+
                                "5. "+ sp.getString(string+"53","")+" "+sp.getString(string+"54","")+"\n"+
                                "6. "+ sp.getString(string+"63","")+" "+sp.getString(string+"64","")+"\n"+
                                "7. "+ sp.getString(string+"73","")+" "+sp.getString(string+"74","")+"\n"+
                                "8. "+ sp.getString(string+"83","")+" "+sp.getString(string+"84","")
                );

        if(uur != 1) {
            for (int i = 1; i < uur;i++){
                if(sp.getBoolean(string+String.valueOf(i)+"2",false)){
                    spe.putBoolean(string+String.valueOf(i)+"2",false);
                }
            }
            spe.apply();
        }

        string = string + String.valueOf(uur);

        subject = sp.getString(string + "3", "");
        classroom = sp.getString(string + "4", "");

        if(!subject.equals("") && !classroom.equals("") && !sp.getBoolean(string+"2",false) && sp.getBoolean("notify",true)){
            builder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.jfc)
                    .setContentTitle(String.valueOf(uur) + ": " + subject)
                    .setContentText(classroom);

            Notification secondPageNotification = new NotificationCompat.Builder(context)
                    .setStyle(secondPageStyle).build();

            Notification notification = builder.extend(new NotificationCompat.WearableExtender().addPage(secondPageNotification)).build();

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            notificationManagerCompat.notify(notificationId, notification);
        }else{
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            notificationManagerCompat.cancelAll();
        }
    }

    public static int lesuur(Context context){
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
        }else if((hour == 10 && minute >= 45) || (hour == 11 && minute <= 55)){
            return 4;
        }else if((hour == 11 && minute >= 55) || (hour == 12 && minute <= 45)){
            return 5;
        }else if((hour == 12 && minute >= 45) || (hour == 13) || (hour == 14 && minute <= 5)){
            return 6;
        }else if(hour == 14 && minute >= 5 && minute <= 55){
            return 7;
        }else if((hour == 14 && minute >= 55) || (hour == 15 && minute <= 45)){
            return 8;
        }

        else{
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.cancel(notificationId);
            return 0;
        }
    }
}
