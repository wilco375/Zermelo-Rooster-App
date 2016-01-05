package com.wilco375.roosternotification;

import android.app.Activity;
import android.app.Notification;
import android.appwidget.AppWidgetManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;

import go.zermelogo.Zermelogo;

public class ZermeloSync {
    public static int SHORTENED = 0;
    public static int DAY = 1;
    public static int SUBJECTS = 2;
    public static int GROUPS = 3;
    public static int LOCATIONS = 4;
    public static int CANCELLED = 5;
    public static int TIMESLOT = 6;

    JSONObject jsonObject;
    long unixStart;
    long unixEnd;
    JSONArray subjectsArray;
    String subjectsString;
    JSONArray groupsArray;
    String groupsString;
    JSONArray locationsArray;
    String locationsString;

    Calendar calendar;

    SharedPreferences sp;

    public void syncZermelo(final Context context, final Activity activity, final boolean restartApp, final boolean copyClipboard){
		ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        //System.out.println("syncZermelo");

		if ((!mWifi.isConnected() && !restartApp) || !context.getSharedPreferences("Main",Context.MODE_PRIVATE).getBoolean("zermeloSync",false)) {
            //System.out.println("returning");
			return;
		}
        new Thread(new Runnable() {
            @Override
            public void run() {
                //System.out.println("Starting sync thread");

                //Get start and end of week in unix time
                long startOfWeek = getTime()[0];
                long endOfWeek = getTime()[1];

                //Get token
                sp = context.getSharedPreferences("Main",Context.MODE_PRIVATE);
                String token = sp.getString("token", "");

                //Get schedule string
                final String scheduleString = Zermelogo.GetScheduleByTimeInJson("jfc",token, String.valueOf(startOfWeek),String.valueOf(endOfWeek));
                //System.out.println("scheduleString: "+scheduleString);

                //If necessary copy string to clipboard
                if(copyClipboard){
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("Rooster JSON", scheduleString);
                            clipboard.setPrimaryClip(clip);
                            Toast.makeText(context, "Gekopieërd", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                try{
                    //Format to JSONArray
                    JSONArray schedule = new JSONObject(scheduleString).getJSONObject("response").getJSONArray("data");
                    Object[][] scheduleArray = new Object[schedule.length()][7];
                    //System.out.println("Starting for loop");

                    //Loop trough all lessons and create an object array with all lessons
                    for(int i = 0; i < schedule.length(); i++) {
                        jsonObject = schedule.getJSONObject(i);

                        unixStart = jsonObject.getLong("start");
                        unixEnd = jsonObject.getLong("start");
                        scheduleArray[i][DAY] = getDayChar(unixToCalendar(unixStart).get(Calendar.DAY_OF_WEEK));
                        scheduleArray[i][SHORTENED] = unixEnd-unixStart == 40*60;

                        subjectsArray = jsonObject.getJSONArray("subjects");
                        subjectsString = "";
                        for(int j = 0; j < subjectsArray.length(); j++){
                            if(!subjectsString.equals("")) subjectsString += "/";
                            subjectsString += subjectsArray.get(j).toString().toUpperCase();
                        }
                        scheduleArray[i][SUBJECTS] = subjectsString;

                        groupsArray = jsonObject.getJSONArray("groups");
                        groupsString = "";
                        for(int j = 0; j < groupsArray.length(); j++){
                            if(!groupsString.equals("")) groupsString += "/";
                            groupsString += groupsArray.get(j).toString().toUpperCase();
                        }
                        scheduleArray[i][GROUPS] = groupsString;

                        locationsArray = jsonObject.getJSONArray("locations");
                        locationsString = "";
                        for(int j = 0; j < locationsArray.length(); j++){
                            if(!locationsString.equals("")) locationsString += "/";
                            locationsString += locationsArray.get(j).toString();
                        }
                        scheduleArray[i][LOCATIONS] = locationsString;

                        scheduleArray[i][CANCELLED] = jsonObject.getBoolean("cancelled");
                        scheduleArray[i][TIMESLOT] = jsonObject.getInt("startTimeSlot");
                    }
                    //System.out.println("For loop finished");
                    //System.out.println("scheduleArray: "+Arrays.deepToString(scheduleArray));

                    SharedPreferences.Editor spe = sp.edit();
                    boolean shortened = false;

                    //Loop through all lessons and save them to SharedPreferences
                    for(Object[] lesson : scheduleArray){
                        if(!shortened && (boolean) lesson[SHORTENED]){
                            shortened = true;
                            spe.putBoolean("fourtyMinuteSchedule",true);
                        }
                        String daySlot = lesson[DAY].toString() + lesson[TIMESLOT].toString();
                        spe.putBoolean(daySlot + "2",(boolean) lesson[CANCELLED]);
                        if((boolean) lesson[CANCELLED]){
                            cancelNotification(getDayWord(lesson[DAY].toString()),getDayInt(lesson[DAY].toString()),Integer.valueOf(lesson[TIMESLOT].toString()),lesson[SUBJECTS].toString(),context);
                        }
                        if(!sp.getBoolean("group",false)) spe.putString(daySlot + "3", lesson[SUBJECTS].toString());
                        else spe.putString(daySlot + "3",lesson[SUBJECTS].toString()+"-"+lesson[GROUPS].toString());
                        spe.putString(daySlot + "4", lesson[LOCATIONS].toString());
                    }

                    if(!shortened){
                        spe.putBoolean("fourtyMinuteSchedule",false);
                    }

                    spe.apply();

                    //Update widgets
                    int[] ids = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, LesdagWidgetProvider.class));
                    LesdagWidgetProvider lesdagWidget = new LesdagWidgetProvider();
                    lesdagWidget.onUpdate(context, AppWidgetManager.getInstance(context), ids);

                    int[] ids2 = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, LesuurWidgetProvider.class));
                    LesuurWidgetProvider lesuurWidget = new LesuurWidgetProvider();
                    lesuurWidget.onUpdate(context, AppWidgetManager.getInstance(context), ids2);

                    //Restart app if necessary
                    if(restartApp){
                        Intent i = new Intent(context,RefreshActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        //System.out.println("restarting, zermeloSync,false: " + sp.getBoolean("zermeloSync", false) + " zermeloSync,true: " + sp.getBoolean("zermeloSync", true));
                        context.startActivity(i);
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }catch (ClassCastException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private Calendar unixToCalendar(long unixTime){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(unixTime * 1000);
        return calendar;
    }

    private String getDayChar(int dayInt){
        switch (dayInt){
            case 2: return "a";
            case 3: return "b";
            case 4: return "c";
            case 5: return "d";
            case 6: return "e";
            default: return "";
        }
    }

    private String getDayWord(String dayChar){
        switch (dayChar){
            case "a": return "Maandag";
            case "b": return "Dinsdag";
            case "c": return "Woensdag";
            case "d": return "Donderdag";
            case "e": return "Vrijdag";
            default: return "";
        }
    }

    private int getDayInt(String dayChar){
        switch (dayChar){
            case "a": return 2;
            case "b": return 3;
            case "c": return 4;
            case "d": return 5;
            case "e": return 6;
            default: return 0;
        }
    }

    private long[] getTime(){
        //Start of today
        long start = (System.currentTimeMillis() / 1000L) - ((System.currentTimeMillis()/1000L) % (24 * 60 * 60));
        //End of today
        long end = start + (60*60*24);

        calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        //If friday after 5pm assume its saturday
        if(dayOfWeek == 6 && calendar.get(Calendar.HOUR_OF_DAY) >= 17){
            dayOfWeek = dayOfWeek+1;
            start += 60*60*24;
            end += 60*60*24;
        }

        long startOfWeek;
        long endOfWeek;

        if(dayOfWeek != 7) {
            startOfWeek = start - (dayOfWeek * 24 * 60 * 60);
            endOfWeek = end - (dayOfWeek * 24 * 60 * 60);
        }else{
            //if saturday startofweek is start
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

	private void cancelNotification(String day, int dayInt, int hour, String subject, Context context){
        //System.out.println("notification");
        if(sp.getBoolean("notifyCancel",true)) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.notification_logo)
                    .setContentTitle("Er valt een uur uit op " + day.toLowerCase())
                    .setContentText(hour + ". " + subject);

            Notification notification = builder.build();

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);


            int currentDay = calendar.get(Calendar.DAY_OF_WEEK);
            int currentWeek = calendar.get(Calendar.WEEK_OF_YEAR);
            if(currentDay == 7 || (currentDay == 6 && calendar.get(Calendar.HOUR_OF_DAY)>=17)){
                currentDay=1;
                currentWeek=currentWeek+1;
            }

            String currentNotString = intStr(calendar.get(Calendar.YEAR))+intStr(currentWeek)+intStr(dayInt)+intStr(hour);
            if(dayInt>=currentDay && !sp.getString("prevNots","").contains(currentNotString)) {
                int notId = sp.getInt("notId", 2);
                SharedPreferences.Editor spe = sp.edit();
                spe.putInt("notId", notId + 1);
                spe.putString("prevNots", sp.getString("prevNots","")+currentNotString);
                spe.apply();
                //System.out.println("notification sent");
                notificationManagerCompat.notify(notId, notification);
            }
            //else System.out.println("notification prevented");
        }
	}

    private String intStr(int integer){
        return String.valueOf(integer);
    }
}
