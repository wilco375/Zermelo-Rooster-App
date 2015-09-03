package com.wilco375.roosternotification;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import java.util.Calendar;

public class LesuurWidgetProvider extends AppWidgetProvider{

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        final int N = appWidgetIds.length;
        for (int i=0; i<N; i++) {
            int appWidgetId = appWidgetIds[i];

            String title = "";
            String text = "";


            String subject;
            String classroom;
            int uur;
            String string = "";

            SharedPreferences sp = context.getSharedPreferences("Main", context.MODE_PRIVATE);
            SharedPreferences.Editor spe = sp.edit();

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
            uur = AlarmReceiver.lesuur(context);

            string = string + String.valueOf(uur);

            subject = sp.getString(string + "3", "");
            classroom = sp.getString(string + "4", "");

            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget_lesuur);
            views.setOnClickPendingIntent(R.id.app_widget_lesuur_layout, pendingIntent);
            if(!subject.equals("") && !classroom.equals("") && !sp.getBoolean(string+"2",false)){
                views.setTextViewText(R.id.app_widget_lesuur_title, uur + ": " +subject);
            }else views.setTextViewText(R.id.app_widget_lesuur_title,"");
            views.setTextViewText(R.id.app_widget_lesuur_text,classroom);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
