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
import com.wilco375.roosternotification.general.ScheduleHandler;
import com.wilco375.roosternotification.activity.MainActivity;
import com.wilco375.roosternotification.general.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LesdagWidgetProvider extends AppWidgetProvider{

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        final int N = appWidgetIds.length;
        for (int i=0; i<N; i++) {
            int appWidgetId = appWidgetIds[i];

            int day = Utils.currentDay();

            SharedPreferences sp = context.getSharedPreferences("Main", Context.MODE_PRIVATE);

            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget_lesdag);
            views.setOnClickPendingIntent(R.id.app_widget_lesdag_layout, pendingIntent);
            views.setTextViewText(R.id.app_widget_lesdag_dag, Utils.dayIntToStr(day));

            String widgetText = "";

            Schedule[] schedule = ScheduleHandler.getScheduleByDay(context,day);
            List<Integer> strikethroughStartIndex = new ArrayList<>();
            List<Integer> strikethroughEndIndex = new ArrayList<>();

            Arrays.sort(schedule, new Schedule.ScheduleComparator());
            for(Schedule lesson : schedule){
                int timeslot = lesson.getTimeslot();
                if(timeslot == 0){
                    if(lesson.getCancelled()){
                        String string;
                        if(!lesson.getType().equals("Les")) string = lesson.getSubjectAndGroup(sp) + " (" + lesson.getType() + ") " + lesson.getLocation() + "\n";
                        else string = lesson.getSubjectAndGroup(sp) + " " + lesson.getLocation()+"\n";
                        strikethroughStartIndex.add(widgetText.length());
                        strikethroughEndIndex.add(widgetText.length()+string.length());
                        widgetText += string;
                    }else{
                        if(!lesson.getType().equals("Les")) widgetText += lesson.getSubjectAndGroup(sp) + " (" + lesson.getType() + ") " + lesson.getLocation() + "\n";
                        else widgetText += lesson.getSubjectAndGroup(sp) + " " + lesson.getLocation()+"\n";
                    }
                }else{
                    if(lesson.getCancelled()){
                        String string;
                        if(!lesson.getType().equals("Les")) string = String.valueOf(timeslot) + ". " + lesson.getSubjectAndGroup(sp) + " (" + lesson.getType() + ") " + lesson.getLocation() + "\n";
                        else string = String.valueOf(timeslot) + ". " + lesson.getSubjectAndGroup(sp) + " " + lesson.getLocation()+"\n";
                        strikethroughStartIndex.add(widgetText.length());
                        strikethroughEndIndex.add(widgetText.length()+string.length());
                        widgetText += string;
                    }else{
                        if(!lesson.getType().equals("Les")) widgetText += String.valueOf(timeslot) + ". " + lesson.getSubjectAndGroup(sp) + " (" + lesson.getType() + ") " + lesson.getLocation() + "\n";
                        else widgetText += String.valueOf(timeslot) + ". " + lesson.getSubjectAndGroup(sp) + " " + lesson.getLocation()+"\n";
                    }
                }
            }

            SpannableString widgetTextSpan = new SpannableString(widgetText);

            for(int j=0;j<strikethroughEndIndex.size();j++){
                widgetTextSpan.setSpan(new StrikethroughSpan(),strikethroughStartIndex.get(j),strikethroughEndIndex.get(j),0);
            }

            views.setTextViewText(R.id.app_widget_lesdag_content,widgetTextSpan);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
