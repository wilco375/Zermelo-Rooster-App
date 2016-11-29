package com.wilco375.roosternotification.receiver;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.wilco375.roosternotification.R;
import com.wilco375.roosternotification.Schedule;
import com.wilco375.roosternotification.activity.MainActivity;
import com.wilco375.roosternotification.general.ScheduleHandler;
import com.wilco375.roosternotification.general.Utils;
import com.wilco375.roosternotification.online.ZermeloSync;

import java.util.Arrays;
import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {

    static int notificationId = 001;

    static NotificationCompat.Builder builder;

    @Override
    public void onReceive(Context context, Intent intent) {
        createNotification(context);
    }

    public static void createNotification(Context context){
        SharedPreferences sp = context.getSharedPreferences("Main", context.MODE_PRIVATE);
        SharedPreferences.Editor spe = sp.edit();

        //Sync with zermelo
        if(sp.getInt("syncCount",0)==3) {
            new ZermeloSync().syncZermelo(context,null, false, false);
            spe.putInt("timesSynced",sp.getInt("timesSynced",0)+1);
            spe.putInt("syncCount", 0);
        } else{
            spe.putInt("syncCount",sp.getInt("syncCount",0)+1);
            spe.putInt("timesNotSynced", sp.getInt("timesNotSynced", 0) + 1);
        }
        spe.apply();

        //Notification
        if(!sp.getBoolean("notify",true)) return;

        int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        if(dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) return;

        Schedule[] schedule = ScheduleHandler.getScheduleByDay(context,dayOfWeek);
        if(schedule.length < 1) return;
        Arrays.sort(schedule,new Schedule.ScheduleComparator());

        String bigText = "";
        String subject = "";
        String location = "";
        String timeslot = "";
        //Get 15 minutes before current time
        double currentTime = Utils.calendarTimeToDouble(Calendar.getInstance())+0.25;
        for(Schedule lesson : schedule){
            //Main notification
            if(currentTime >= lesson.getStartHour() && currentTime <= lesson.getEndHour() && !lesson.getCancelled()){
                subject = lesson.getSubjectAndGroup(sp);
                location = lesson.getLocation();
                timeslot = String.valueOf(lesson.getTimeslot());
            }

            //Second Page for Android Wear
            String timeslot2;
            if(lesson.getTimeslot() <= 0) timeslot2 = "";
            else timeslot2 = lesson.getTimeslot()+". ";
            if(!lesson.getCancelled()) bigText += timeslot2+lesson.getSubjectAndGroup(sp)+" "+lesson.getLocation()+"\n";
            else bigText += timeslot2+"X\n";
        }

        NotificationCompat.BigTextStyle secondPageStyle = new NotificationCompat.BigTextStyle();
        secondPageStyle.setBigContentTitle(Utils.dayIntToStr(dayOfWeek))
                       .bigText(Utils.replaceLast(bigText,"\n",""));

        String spQuery = timeslot + ": " + subject + " "+location;
        if(sp.getString("lastNotification","").equals(spQuery)) return;

        spe.putString("lastNotification", spQuery).apply();

        if(!(subject.equals("") && location.equals(""))){
            builder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.notification_logo)
                    .setContentText(location)
                    .setStyle(secondPageStyle)
                    .setContentIntent(PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0));

            if(!timeslot.contains("0") && !timeslot.equals("")) builder.setContentTitle(timeslot + ": " + subject);
            else builder.setContentTitle(subject);

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
}
