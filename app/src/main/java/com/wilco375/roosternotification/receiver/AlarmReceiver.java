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

    private static final int NOTIFICATION_ID = 1;

    static NotificationCompat.Builder builder;

    @Override
    public void onReceive(Context context, Intent intent) {
        createNotification(context);
    }

    public static void createNotification(Context context){
        SharedPreferences sp = context.getSharedPreferences("Main", Context.MODE_PRIVATE);
        SharedPreferences.Editor spe = sp.edit();

        //Sync with Zermelo
        if(sp.getInt("syncCount",0)==3) {
            new ZermeloSync().syncZermelo(context,null, false, false);
            spe.putInt("timesSynced",sp.getInt("timesSynced",0)+1);
            spe.putInt("syncCount", 0);
        } else{
            spe.putInt("syncCount",sp.getInt("syncCount",0)+1);
            spe.putInt("timesNotSynced", sp.getInt("timesNotSynced", 0) + 1);
        }
        spe.apply();

        // Return if notifications are disabled
        if(!sp.getBoolean("notify", true)) return;

        // Get current day
        int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        if(dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) return;

        // Get schedule of current day
        Schedule[] schedule = ScheduleHandler.getScheduleByDay(context,dayOfWeek);
        if(schedule.length < 1) return;
        Arrays.sort(schedule,new Schedule.ScheduleComparator());

        // Get all the notification texts
        String bigText = "";
        String subject = "";
        String location = "";
        String timeslot = "";
        String title = "";
        //Get 15 minutes before current time
        double currentTime = Utils.calendarTimeToDouble(Calendar.getInstance())+0.25;
        for(Schedule lesson : schedule){
            //Main notification
            if(currentTime >= lesson.getStartHour() && currentTime <= lesson.getEndHour() && !lesson.getCancelled()){
                subject = lesson.getSubjectAndGroup(sp);
                location = lesson.getLocation();
                timeslot = String.valueOf(lesson.getTimeslot());
            }

            title = (!timeslot.equals("0") && !timeslot.equals("")) ? timeslot + ": " + subject : subject;

            //Second Page for Android Wear
            String timeslot2;
            if(lesson.getTimeslot() <= 0) timeslot2 = "";
            else timeslot2 = lesson.getTimeslot()+". ";
            if(!lesson.getCancelled()) bigText += timeslot2+lesson.getSubjectAndGroup(sp)+" "+lesson.getLocation()+"\n";
            else bigText += timeslot2+"X\n";
        }

        // If the notification is already sent, return
        String spQuery = timeslot + ": " + subject + " "+location;
        if(sp.getString("lastNotification","").equals(spQuery)) return;

        // Update last notification String
        spe.putString("lastNotification", spQuery).apply();

        if(!(subject.equals("") && location.equals(""))){
            // Style that shows the entire day's schedule
            NotificationCompat.BigTextStyle daySchedule = new NotificationCompat.BigTextStyle();
            daySchedule.setBigContentTitle(Utils.dayIntToStr(dayOfWeek))
                       .bigText(Utils.replaceLast(bigText,"\n",""));

            // Create main notification with daySchedule as extended notification
            builder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.notification_logo)
                    .setContentText(location)
                    .extend(new NotificationCompat.WearableExtender()
                            .addPage(
                                    new NotificationCompat.Builder(context)
                                            .setStyle(daySchedule).build()
                            )
                    )
                    .setContentTitle(title)
                    .setContentIntent(PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0));

            if(sp.getBoolean("notifyDaySchedule", true)) builder.setStyle(daySchedule);

            Notification notification = builder.build();
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            notificationManagerCompat.notify(NOTIFICATION_ID, notification);
        }else{
            // Cancel all notifications if there's no subject and location
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            notificationManagerCompat.cancelAll();
        }
    }
}
