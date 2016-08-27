package com.wilco375.roosternotification;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class Utils {
    //Networking
    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static boolean isWifiConnected(Context context){
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return wifi.isConnected();
    }

    //Set alarm
    public static void setAlarm(Context context){
        Intent autoStartUp = new Intent(context, AutoStartUp.class);
        context.startService(autoStartUp);
    }

    //Update widgets
    public static void updateWidgets(Context context){
        int[] ids = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, LesdagWidgetProvider.class));
        LesdagWidgetProvider lesdagWidget = new LesdagWidgetProvider();
        lesdagWidget.onUpdate(context, AppWidgetManager.getInstance(context), ids);

        int[] ids2 = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, LesuurWidgetProvider.class));
        LesuurWidgetProvider lesuurWidget = new LesuurWidgetProvider();
        lesuurWidget.onUpdate(context, AppWidgetManager.getInstance(context), ids2);
    }

    //Time
    public static long[] getUnixWeek(){
        //Start of today
        long start = (System.currentTimeMillis() / 1000L) - ((System.currentTimeMillis()/1000L) % (24 * 60 * 60));
        //End of today
        long end = start + (60*60*24);

        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        //If friday after 5pm assume its saturday
        if(dayOfWeek == Calendar.FRIDAY && calendar.get(Calendar.HOUR_OF_DAY) >= 17){
            dayOfWeek += 1;
            start += 60*60*24;
            end += 60*60*24;
        }

        long startOfWeek;
        long endOfWeek;

        if(dayOfWeek != Calendar.SATURDAY) {
            startOfWeek = start - (dayOfWeek * 24 * 60 * 60);
            endOfWeek = end - (dayOfWeek * 24 * 60 * 60);
        }else{
            //If saturday startOfWeek is start
            startOfWeek = start;
            endOfWeek = end;
        }

        startOfWeek += 2 * 24 * 60 * 60;
        endOfWeek += 6 * 24 * 60 * 60;

        long[] timeArray = new long[2];
        timeArray[0] = startOfWeek;
        timeArray[1] = endOfWeek;

        return timeArray;
    }

    public static Calendar unixToCalendar(long unixTime){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(unixTime * 1000);
        return calendar;
    }

    public static double calendarTimeToDouble(Calendar calendar){
        double hours = (double) calendar.get(Calendar.HOUR_OF_DAY);
        double minutes = (double) calendar.get(Calendar.MINUTE);
        return hours+(minutes/60);
    }

    public static String calendarTimeToString(Calendar calendar){
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        String hoursStr;
        String minutesStr;
        if(hours<10) hoursStr = "0"+String.valueOf(hours);
        else hoursStr = String.valueOf(hours);
        if(minutes<10) minutesStr = "0"+String.valueOf(minutes);
        else minutesStr = String.valueOf(minutes);
        return hoursStr+":"+minutesStr;
    }

    public static String dayIntToStr(int day){
        switch (day){
            case Calendar.MONDAY:
                return "Maandag";
            case Calendar.TUESDAY:
                return "Dinsdag";
            case Calendar.WEDNESDAY:
                return "Woensdag";
            case Calendar.THURSDAY:
                return "Donderdag";
            case Calendar.FRIDAY:
                return "Vrijdag";
            case Calendar.SATURDAY:
                return "Zaterdag";
            case Calendar.SUNDAY:
                return "Zondag";
            default:
                return "";
        }
    }

    //Schedule
    public static Schedule[] scheduleListToArray(List<Schedule> list){
        return list.toArray(new Schedule[list.size()]);
    }

    public static List<Schedule> scheduleArrayToList(Schedule[] schedule){
        return Arrays.asList(schedule);
    }

    //String utils
    public static String replaceLast(String string, String substring, String replacement)
    {
        int index = string.lastIndexOf(substring);
        if (index == -1) return string;
        return string.substring(0, index) + replacement + string.substring(index+substring.length());
    }

    public static String strNotNull(String string){
        if(string == null) return "";
        else return string;
    }

    //Copy to clipboard
    public static void copyText(Activity activity, final Context context, final String title, final String content, final boolean toast){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(title, content);
                clipboard.setPrimaryClip(clip);
                if (toast) Toast.makeText(context, R.string.copied, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static int spToPx(Activity activity,int sp){
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return (int) (sp * dm.scaledDensity);
    }
}
