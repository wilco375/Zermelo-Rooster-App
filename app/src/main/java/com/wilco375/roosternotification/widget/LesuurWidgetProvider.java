package com.wilco375.roosternotification.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.SpannableString;
import android.text.style.StrikethroughSpan;
import android.widget.RemoteViews;

import com.wilco375.roosternotification.R;
import com.wilco375.roosternotification.Schedule;
import com.wilco375.roosternotification.activity.MainActivity;
import com.wilco375.roosternotification.general.ScheduleHandler;
import com.wilco375.roosternotification.general.Utils;

import java.util.Calendar;

public class LesuurWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            int appWidgetId = appWidgetIds[i];

            SharedPreferences sp = context.getSharedPreferences("Main", context.MODE_PRIVATE);

            int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
            if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) return;

            Schedule[] schedule = ScheduleHandler.getScheduleByDay(context, dayOfWeek);
            if (schedule.length < 1) return;

            String subject = "";
            String location = "";
            String timeslot = "";
            boolean cancelled = false;
            //Get 15 minutes before current time
            double currentTime = Utils.calendarTimeToDouble(Calendar.getInstance()) + 0.25;
            for (Schedule lesson : schedule) {
                if (currentTime >= lesson.getStartHour() && currentTime <= lesson.getEndHour()) {
                    subject = lesson.getSubjectAndGroup(sp);
                    location = lesson.getLocation();
                    timeslot = String.valueOf(lesson.getTimeslot());
                    cancelled = lesson.getCancelled();
                }
            }

            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget_lesuur);
            views.setOnClickPendingIntent(R.id.app_widget_lesuur_layout, pendingIntent);
            if (!(subject.equals("") && location.equals("")) && !cancelled) {
                if (!timeslot.equals("0") && !timeslot.equals(""))
                    views.setTextViewText(R.id.app_widget_lesuur_title, timeslot + ": " + subject);
                else views.setTextViewText(R.id.app_widget_lesuur_title, subject);

                views.setTextViewText(R.id.app_widget_lesuur_text, location);
            } else if (cancelled) {
                SpannableString spannableString1;
                if (!timeslot.equals("0") && !timeslot.equals(""))
                    spannableString1 = new SpannableString(timeslot + ": " + subject);
                else spannableString1 = new SpannableString(subject);
                SpannableString spannableString2 = new SpannableString(location);
                spannableString1.setSpan(new StrikethroughSpan(), 0, spannableString1.toString().length(), 0);
                spannableString2.setSpan(new StrikethroughSpan(), 0, spannableString2.toString().length(), 0);
                views.setTextViewText(R.id.app_widget_lesuur_title, spannableString1);
                views.setTextViewText(R.id.app_widget_lesuur_text, spannableString2);
            } else {
                views.setTextViewText(R.id.app_widget_lesuur_title, "");
                views.setTextViewText(R.id.app_widget_lesuur_text, "");
            }

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
