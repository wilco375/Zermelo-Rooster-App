package com.wilco375.roosternotification;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import java.util.Calendar;

public class LesdagWidgetProvider extends AppWidgetProvider{

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        final int N = appWidgetIds.length;
        for (int i=0; i<N; i++) {
            int appWidgetId = appWidgetIds[i];

            String title = "";
            String text = "";

            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_WEEK);
            String string = "";
            SharedPreferences sp = context.getSharedPreferences("Main", context.MODE_PRIVATE);
            if(day == Calendar.MONDAY) {
                title = "Maandag";
                string = "a";
            }else if(day == Calendar.TUESDAY){
                title = "Dinsdag";
                string = "b";
            }else if(day == Calendar.WEDNESDAY){
                title = "Woensdag";
                string = "c";
            }else if(day == Calendar.THURSDAY){
                title = "Donderdag";
                string = "d";
            }else if(day == Calendar.FRIDAY){
                title = "Vrijdag";
                string = "e";
            }

            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget_lesdag);
            views.setOnClickPendingIntent(R.id.app_widget_lesdag_layout, pendingIntent);
            views.setTextViewText(R.id.app_widget_lesdag_dag, title);
            views.setTextViewText(R.id.app_widget_lesdag_text,"1. "+ sp.getString(string+"13","")+" "+sp.getString(string+"14","")+"\n"+
                    "2. "+ sp.getString(string+"23","")+" "+sp.getString(string+"24","")+"\n"+
                    "3. "+ sp.getString(string+"33","")+" "+sp.getString(string+"34","")+"\n"+
                    "4. "+ sp.getString(string+"43","")+" "+sp.getString(string+"44","")+"\n"+
                    "5. "+ sp.getString(string+"53","")+" "+sp.getString(string+"54","")+"\n"+
                    "6. "+ sp.getString(string+"63","")+" "+sp.getString(string+"64","")+"\n"+
                    "7. "+ sp.getString(string+"73","")+" "+sp.getString(string+"74","")+"\n"+
                    "8. "+ sp.getString(string+"83","")+" "+sp.getString(string+"84",""));

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
