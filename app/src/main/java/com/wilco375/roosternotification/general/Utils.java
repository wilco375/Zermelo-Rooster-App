package com.wilco375.roosternotification.general;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.wilco375.roosternotification.R;
import com.wilco375.roosternotification.receiver.AutoStartUp;
import com.wilco375.roosternotification.widget.LesdagWidgetProvider;
import com.wilco375.roosternotification.widget.LesuurWidgetProvider;

import java.util.Calendar;
import java.util.Date;

public class Utils {
    //Networking
    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return wifi.isConnected();
    }

    //Set alarm
    public static void setAlarm(Context context) {
        Intent autoStartUp = new Intent(context, AutoStartUp.class);
        context.startService(autoStartUp);
    }

    //Update widgets
    public static void updateWidgets(Context context) {
        int[] ids = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, LesdagWidgetProvider.class));
        LesdagWidgetProvider lesdagWidget = new LesdagWidgetProvider();
        lesdagWidget.onUpdate(context, AppWidgetManager.getInstance(context), ids);

        int[] ids2 = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, LesuurWidgetProvider.class));
        LesuurWidgetProvider lesuurWidget = new LesuurWidgetProvider();
        lesuurWidget.onUpdate(context, AppWidgetManager.getInstance(context), ids2);
    }

    //Time
    private static long unixStartOfWeekCache = -1;
    public static long getUnixStartOfWeek() {
        if (unixStartOfWeekCache == -1) {
            Calendar calendar = Calendar.getInstance();
            if (calendar.get(Calendar.HOUR_OF_DAY) > 17)
                calendar.add(Calendar.DAY_OF_WEEK, 1);
            if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                calendar.add(Calendar.WEEK_OF_YEAR, 1);
            }

            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.clear(Calendar.MINUTE);
            calendar.clear(Calendar.SECOND);
            calendar.clear(Calendar.MILLISECOND);
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            unixStartOfWeekCache = calendar.getTimeInMillis() / 1000L;
        }
        return unixStartOfWeekCache;
    }

    public static long getUnixEndOfWeek() {
        return getUnixStartOfWeek() + 7 * 24 * 60 * 60 - 1;
    }

    public static String currentDay() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        if (calendar.get(Calendar.HOUR_OF_DAY) > 17) day += 1;
        if (day >= Calendar.SATURDAY || day == Calendar.SUNDAY) day = Calendar.MONDAY;
        return dayIntToStr(day);
    }

    public static Date getCurrentScheduleDate() {
        Calendar calendar = Calendar.getInstance();
        int offset = 0;
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        if (calendar.get(Calendar.HOUR_OF_DAY) > 17) offset = 1;
        if (day >= Calendar.SATURDAY) offset = 2;
        if (day == Calendar.SUNDAY) offset = 1;
        calendar.add(Calendar.HOUR, 24*offset);
        return calendar.getTime();
    }

    public static int currentWeek() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        if (calendar.get(Calendar.HOUR_OF_DAY) > 17) day += 1;

        // If saturday or sunday or sunday (after 17:00)
        if (day >= Calendar.SATURDAY || day == Calendar.SUNDAY || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
            return calendar.get(Calendar.WEEK_OF_YEAR) + 1;
        else return calendar.get(Calendar.WEEK_OF_YEAR);
    }

    public static Calendar unixToCalendar(long unixTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(unixTime * 1000);
        return calendar;
    }

    public static double calendarTimeToDouble(Calendar calendar) {
        double hours = (double) calendar.get(Calendar.HOUR_OF_DAY);
        double minutes = (double) calendar.get(Calendar.MINUTE);
        return hours + (minutes / 60);
    }

    public static String calendarTimeToString(Calendar calendar) {
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        String hoursStr;
        String minutesStr;
        if (hours < 10) hoursStr = "0" + String.valueOf(hours);
        else hoursStr = String.valueOf(hours);
        if (minutes < 10) minutesStr = "0" + String.valueOf(minutes);
        else minutesStr = String.valueOf(minutes);
        return hoursStr + ":" + minutesStr;
    }

    public static String dayIntToStr(int day) {
        boolean nextWeek = false;
        while (day > 7) {
            nextWeek = true;
            day -= 7;
        }

        String dayStr;
        switch (day) {
            case Calendar.MONDAY:
                dayStr = "Maandag";
                break;
            case Calendar.TUESDAY:
                dayStr = "Dinsdag";
                break;
            case Calendar.WEDNESDAY:
                dayStr = "Woensdag";
                break;
            case Calendar.THURSDAY:
                dayStr = "Donderdag";
                break;
            case Calendar.FRIDAY:
                dayStr = "Vrijdag";
                break;
            case Calendar.SATURDAY:
                dayStr = "Zaterdag";
                break;
            case Calendar.SUNDAY:
                dayStr = "Zondag";
                break;
            default:
                return "Error";
        }

        return nextWeek ? "Volgende week " + dayStr.toLowerCase() : dayStr;
    }

    //String utils
    public static String replaceLast(String string, String substring, String replacement) {
        int index = string.lastIndexOf(substring);
        if (index == -1) return string;
        return string.substring(0, index) + replacement + string.substring(index + substring.length());
    }

    public static String strNotNull(String string) {
        if (string == null) return "";
        else return string;
    }

    //Copy to clipboard
    public static void copyText(Activity activity, final Context context, final String title, final String content, final boolean toast) {
        activity.runOnUiThread(() -> {
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText(title, content);
            clipboard.setPrimaryClip(clip);
            if (toast) Toast.makeText(context, R.string.copied, Toast.LENGTH_SHORT).show();
        });
    }
}
