package com.wilco375.roosternotification;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
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

            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_WEEK);
            if(calendar.get(Calendar.HOUR_OF_DAY)>17){
                day += 1;
            }if(day >= 7 || day == 1) day = 2;
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
            }else if(day == Calendar.SATURDAY) title = "Zaterdag";
            else if(day == Calendar.SUNDAY) title = "Zondag";

            //System.out.println("Day: "+title);

            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget_lesdag);
            views.setOnClickPendingIntent(R.id.app_widget_lesdag_layout, pendingIntent);
            views.setTextViewText(R.id.app_widget_lesdag_dag, title);

            SpannableString spannable;
            String textString;
            for(int j=1;j<=9;j++){
                //i+". "+sp.getString(string+i+"3","")+sp.getString(string+i+"4","");

                if(sp.getBoolean(string+j+"2",false)){
                    spannable = new SpannableString(j+". "+sp.getString(string+j+"3","")+" "+sp.getString(string+j+"4",""));
                    spannable.setSpan(new StrikethroughSpan(), 0, spannable.toString().length(), 0);
                    if(j == 1) views.setTextViewText(R.id.app_widget_lesdag_firstHour,spannable);
                    else if(j == 2) views.setTextViewText(R.id.app_widget_lesdag_secondHour,spannable);
                    else if(j == 3) views.setTextViewText(R.id.app_widget_lesdag_thirdHour,spannable);
                    else if(j == 4) views.setTextViewText(R.id.app_widget_lesdag_fourthHour,spannable);
                    else if(j == 5) views.setTextViewText(R.id.app_widget_lesdag_fifthHour,spannable);
                    else if(j == 6) views.setTextViewText(R.id.app_widget_lesdag_sixthHour,spannable);
                    else if(j == 7) views.setTextViewText(R.id.app_widget_lesdag_seventhHour,spannable);
                    else if(j == 8) views.setTextViewText(R.id.app_widget_lesdag_eighthHour,spannable);
                    else if(j == 9) views.setTextViewText(R.id.app_widget_lesdag_ninthHour,spannable);
                    //System.out.println("SpannableString: "+spannable.toString()+" j: "+j+" lenght: "+(spannable.toString().length()));
                }else{
                    textString = j+". "+sp.getString(string+j+"3","")+" "+sp.getString(string+j+"4","");
                    if(j == 1){
                        views.setTextViewText(R.id.app_widget_lesdag_firstHour,textString);
                        //System.out.println("First hour set");
                    }
                    else if(j == 2) views.setTextViewText(R.id.app_widget_lesdag_secondHour,textString);
                    else if(j == 3) views.setTextViewText(R.id.app_widget_lesdag_thirdHour,textString);
                    else if(j == 4) views.setTextViewText(R.id.app_widget_lesdag_fourthHour,textString);
                    else if(j == 5) views.setTextViewText(R.id.app_widget_lesdag_fifthHour,textString);
                    else if(j == 6) views.setTextViewText(R.id.app_widget_lesdag_sixthHour,textString);
                    else if(j == 7) views.setTextViewText(R.id.app_widget_lesdag_seventhHour,textString);
                    else if(j == 8) views.setTextViewText(R.id.app_widget_lesdag_eighthHour,textString);
                    else if(j == 9) views.setTextViewText(R.id.app_widget_lesdag_ninthHour,textString);
                    //System.out.println("String: "+textString+" j: "+j);
                }
            }

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
