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

import java.util.Arrays;
import java.util.Calendar;

import go.zermelogo.Zermelogo;

public class ZermeloSync {

    public static int SUBJECT = 0;
    public static int CLASSROOM = 1;
    Calendar calendar;

    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    SharedPreferences sp;

    public void syncZermelo(final Context context, final Activity activity, final boolean restartApp, final boolean copyClipboard){
		ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        System.out.println("syncZermelo");

		if ((!mWifi.isConnected() && !restartApp) || !context.getSharedPreferences("Main",Context.MODE_PRIVATE).getBoolean("zermeloSync",false)) {
            System.out.println("returning");
			return;
		}
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Starting sync thread");

                //Start of today
                long start = (System.currentTimeMillis() / 1000L) - ((System.currentTimeMillis()/1000L) % (24 * 60 * 60));
                //End of today
                long end = start + (60*60*24);

                calendar = Calendar.getInstance();
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                //If friday after 5pm assume its saturday
                //System.out.println("dayOfWeek: "+dayOfWeek+" hour of day + 3: "+ (calendar.get(Calendar.HOUR_OF_DAY)+3));
                if(dayOfWeek == 6 && calendar.get(Calendar.HOUR_OF_DAY) >= 17){
                    dayOfWeek = dayOfWeek+1;
                    start += 60*60*24;
                    end += 60*60*24;
                    //System.out.println("dayofweek is now "+dayOfWeek + "start is now "+ start);
                }

                long startOfWeek;
                long endOfStartWeek;

                if(dayOfWeek != 7) {
                    startOfWeek = start - (dayOfWeek * 24 * 60 * 60);
                    endOfStartWeek = end - (dayOfWeek * 24 * 60 * 60);
                }else{
                    //if saturday startofweek is start
                    startOfWeek = start;
                    endOfStartWeek = end;
                }

                //System.out.println("start: "+ start + " end: "+end+" startOfWeek: "+startOfWeek + " endOfWeek: "+endOfStartWeek + "System time: "+System.currentTimeMillis()/1000L + " day: "+calendar.get(Calendar.DAY_OF_WEEK)+" hour of day: "+calendar.get(Calendar.HOUR_OF_DAY));

                boolean overrideAll = false;
                if(startOfWeek+(2*60*60*24) > start){
                    //System.out.println("overrideAll");
                    overrideAll = true;
                }

                sp = context.getSharedPreferences("Main",Context.MODE_PRIVATE);
                SharedPreferences.Editor spe = sp.edit();

                String token = sp.getString("token","");
                //token = "5le2fbkrabj2ive7cm0u384lj9";

                //System.out.println("Schedule raw m: "+Zermelogo.GetScheduleByTimeInJson("jfc", token, String.valueOf(startOfWeek + (2 * 24 * 60 * 60)), String.valueOf(endOfStartWeek + (2 * 24 * 60 * 60))));
                //System.out.println("Schedule raw t: "+Zermelogo.GetScheduleByTimeInJson("jfc", token, String.valueOf(startOfWeek + (3 * 24 * 60 * 60)), String.valueOf(endOfStartWeek + (3 * 24 * 60 * 60))));
                //System.out.println("Schedule raw w: "+Zermelogo.GetScheduleByTimeInJson("jfc", token, String.valueOf(startOfWeek + (4 * 24 * 60 * 60)), String.valueOf(endOfStartWeek + (4 * 24 * 60 * 60))));
                //System.out.println("Schedule raw t: "+Zermelogo.GetScheduleByTimeInJson("jfc", token, String.valueOf(startOfWeek + (5 * 24 * 60 * 60)), String.valueOf(endOfStartWeek + (5 * 24 * 60 * 60))));
                //System.out.println("Schedule raw f: "+Zermelogo.GetScheduleByTimeInJson("jfc", token, String.valueOf(startOfWeek + (6 * 24 * 60 * 60)), String.valueOf(endOfStartWeek + (6 * 24 * 60 * 60))));

                //System.out.println("getting schedule monday");
                //Monday
                String scheduleMondayStr = Zermelogo.GetScheduleByTime("jfc", token, String.valueOf(startOfWeek + (2 * 24 * 60 * 60)), String.valueOf(endOfStartWeek + (2 * 24 * 60 * 60)));
                String[] scheduleMonday;
                //System.out.println("mondaystring" +scheduleMondayStr);
                if(scheduleMondayStr != null) scheduleMonday = scheduleMondayStr.replaceAll("\n", " ").replaceAll("  ", " ").split(" ");
                else scheduleMonday = new String[0];
                //System.out.println("getting schedule tuesday");
                //Tuesday
                String scheduleTuesdayStr = Zermelogo.GetScheduleByTime("jfc", token, String.valueOf(startOfWeek + (3 * 24 * 60 * 60)), String.valueOf(endOfStartWeek + (3 * 24 * 60 * 60)));
                String[] scheduleTuesday;
                //System.out.println("tuesdaystring" +scheduleTuesdayStr);
                if(scheduleTuesdayStr != null) scheduleTuesday = scheduleTuesdayStr.replaceAll("\n", " ").replaceAll("  ", " ").split(" ");
                else scheduleTuesday = new String[0];
                //System.out.println("getting schedule wednesday");
                String scheduleWednesdayStr = Zermelogo.GetScheduleByTime("jfc", token, String.valueOf(startOfWeek + (4 * 24 * 60 * 60)), String.valueOf(endOfStartWeek + (4 * 24 * 60 * 60)));
                //System.out.println("wednesdaystring" +scheduleWednesdayStr);
                String[] scheduleWednesday;
                if(scheduleWednesdayStr != null) scheduleWednesday = scheduleWednesdayStr.replaceAll("\n"," ").replaceAll("  "," ").split(" ");
                else scheduleWednesday = new String[0];
                //Thursday
                //System.out.println("getting schedule thursday");
                String scheduleThursdayStr = Zermelogo.GetScheduleByTime("jfc", token, String.valueOf(startOfWeek + (5* 24 * 60 * 60)), String.valueOf(endOfStartWeek + (5* 24 * 60 * 60)));
                String[] scheduleThursday;
                //System.out.println("thursdaystring"+scheduleTuesdayStr);
                if(scheduleThursdayStr != null) scheduleThursday = scheduleThursdayStr.replaceAll("\n"," ").replaceAll("  "," ").split(" ");
                else scheduleThursday = new String[0];
                //Friday
                //System.out.println("getting schedule friday");
                String scheduleFridayStr = Zermelogo.GetScheduleByTime("jfc", token, String.valueOf(startOfWeek + (6 * 24*60*60)), String.valueOf(endOfStartWeek+(6*24*60*60)));
                String[] scheduleFriday;
                //System.out.println("fridaystring" +scheduleFridayStr);
                if(scheduleFridayStr != null) scheduleFriday = scheduleFridayStr.replaceAll("\n"," ").replaceAll("  "," ").split(" ");
                else scheduleFriday = new String[0];
                //System.out.println("getting schedule is finished, now getting sp");

                scheduleMonday = removeEmptyStrings(scheduleMonday);
                scheduleTuesday = removeEmptyStrings(scheduleTuesday);
                scheduleWednesday = removeEmptyStrings(scheduleWednesday);
                scheduleThursday = removeEmptyStrings(scheduleThursday);
                scheduleFriday = removeEmptyStrings(scheduleFriday);

                //System.out.println("Monday: "+Arrays.toString(scheduleMonday));
                //System.out.println("Tuesday: " + Arrays.toString(scheduleTuesday));
                //System.out.println("Wednesday: " + Arrays.toString(scheduleWednesday));
                //System.out.println("Thursday: "+Arrays.toString(scheduleThursday));
                //System.out.println("Friday: "+Arrays.toString(scheduleFriday));

				if(dayOfWeek == 2){
					if(fourtyMinuteSchedule(scheduleMonday)) spe.putBoolean("fourtyMinuteSchedule",true);
					else spe.putBoolean("fourtyMinuteSchedule",false);
				}else if(dayOfWeek == 3){
					if(fourtyMinuteSchedule(scheduleTuesday)) spe.putBoolean("fourtyMinuteSchedule",true);
					else spe.putBoolean("fourtyMinuteSchedule",false);
				}else if(dayOfWeek == 4){
					if(fourtyMinuteSchedule(scheduleWednesday)) spe.putBoolean("fourtyMinuteSchedule",true);
					else spe.putBoolean("fourtyMinuteSchedule",false);
				}else if(dayOfWeek == 5){
					if(fourtyMinuteSchedule(scheduleThursday)) spe.putBoolean("fourtyMinuteSchedule",true);
					else spe.putBoolean("fourtyMinuteSchedule",false);
				}else if(dayOfWeek == 6){
					if(fourtyMinuteSchedule(scheduleFriday)) spe.putBoolean("fourtyMinuteSchedule",true);
					else spe.putBoolean("fourtyMinuteSchedule",false);
				}

                //if(fourtyMinuteSchedule(scheduleThursday))System.out.println("donderdag verkort rooster");

                //vb a23 a=(maan)dag 2=uur 3=vak|4=lok

                //System.out.println("running through loop");
                for(int i = 1;i<=9;i++){
					if(!sp.getBoolean("a"+i+"2", false) && lessonCanceled(scheduleMonday, i)){
						spe.putBoolean("a" + i + "2", true);
						cancelNotification("Maandag",2,i,getLesson(scheduleMonday,SUBJECT,i),context);
					}
                    if(overrideAll && !lessonCanceled(scheduleMonday,i) && sp.getBoolean("a"+i+"2",false)){
                        spe.putBoolean("a"+i+"2",false);
                    }
                    spe.putString("a" + i + "3", getLesson(scheduleMonday, SUBJECT, i));
                    spe.putString("a" + i + "4", getLesson(scheduleMonday, CLASSROOM, i));
                    //System.out.println("Monday " + i + ". " + getLesson(scheduleMonday, SUBJECT, i) + " " + getLesson(scheduleMonday, CLASSROOM, i));
					
					if(!sp.getBoolean("b"+i+"2", false) && lessonCanceled(scheduleTuesday, i)){
						spe.putBoolean("b" + i + "2", true);
						cancelNotification("Dinsdag",3,i,getLesson(scheduleTuesday,SUBJECT,i),context);
					}
                    if(overrideAll && !lessonCanceled(scheduleTuesday,i) && sp.getBoolean("b"+i+"2",false)){
                        spe.putBoolean("b" + i + "2", false);
                    }
                    spe.putString("b" + i + "3", getLesson(scheduleTuesday, SUBJECT, i));
                    spe.putString("b" + i + "4", getLesson(scheduleTuesday, CLASSROOM, i));
                    //System.out.println("Tuesday " + i + ". " + getLesson(scheduleTuesday, SUBJECT, i) + " " + getLesson(scheduleTuesday, CLASSROOM, i));
					
					if(!sp.getBoolean("c"+i+"2", false) && lessonCanceled(scheduleWednesday, i)){
						spe.putBoolean("c" + i + "2", true);
						cancelNotification("Woensdag",4,i,getLesson(scheduleWednesday,SUBJECT,i),context);
					}
                    if(overrideAll && !lessonCanceled(scheduleWednesday,i) && sp.getBoolean("c"+i+"2",false)){
                        spe.putBoolean("c" + i + "2", false);
                    }
                    spe.putBoolean("c" + i + "2", lessonCanceled(scheduleWednesday, i));
                    spe.putString("c" + i + "3", getLesson(scheduleWednesday, SUBJECT, i));
                    spe.putString("c" + i + "4", getLesson(scheduleWednesday, CLASSROOM, i));
                    //System.out.println("Wednesday " + i + ". " + getLesson(scheduleWednesday, SUBJECT, i) + " " + getLesson(scheduleWednesday, CLASSROOM, i));
					
					if(!sp.getBoolean("d"+i+"2", false) && lessonCanceled(scheduleThursday, i)){
						spe.putBoolean("d" + i + "2", true);
						cancelNotification("Donderdag",5,i,getLesson(scheduleThursday,SUBJECT,i),context);
					}
                    if(overrideAll && !lessonCanceled(scheduleThursday,i) && sp.getBoolean("d"+i+"2",false)){
                        spe.putBoolean("d" + i + "2", false);
                    }
                    spe.putBoolean("d" + i + "2", lessonCanceled(scheduleThursday, i));
                    spe.putString("d" + i + "3", getLesson(scheduleThursday, SUBJECT, i));
                    spe.putString("d" + i + "4", getLesson(scheduleThursday, CLASSROOM, i));
                    //System.out.println("Donderdag " + i + ". " + getLesson(scheduleThursday, SUBJECT, i) + " " + getLesson(scheduleThursday, CLASSROOM, i));
					
					if(!sp.getBoolean("e"+i+"2", false) && lessonCanceled(scheduleFriday, i)){
						spe.putBoolean("e" + i + "2", true);
						cancelNotification("Vrijdag",6,i,getLesson(scheduleFriday,SUBJECT,i),context);
					}
                    if(overrideAll && !lessonCanceled(scheduleFriday,i) && sp.getBoolean("e"+i+"2",false)){
                        spe.putBoolean("e" + i + "2", false);
                    }
                    spe.putBoolean("e" + i + "2", lessonCanceled(scheduleFriday, i));
                    spe.putString("e" + i + "3", getLesson(scheduleFriday, SUBJECT, i));
                    spe.putString("e" + i + "4", getLesson(scheduleFriday, CLASSROOM, i));
                    //System.out.println("Friday " + i + ". " + getLesson(scheduleFriday, SUBJECT, i) + " " + getLesson(scheduleFriday, CLASSROOM, i));
                }
                //System.out.println("applying sp");
                spe.apply();

                //cancelNotification("Testdag", 1, 6, "eenvak", context);

                int[] ids = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, LesdagWidgetProvider.class));
                LesdagWidgetProvider lesdagWidget = new LesdagWidgetProvider();
                lesdagWidget.onUpdate(context, AppWidgetManager.getInstance(context), ids);

                int[] ids2 = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, LesuurWidgetProvider.class));
                LesuurWidgetProvider lesuurWidget = new LesuurWidgetProvider();
                lesuurWidget.onUpdate(context, AppWidgetManager.getInstance(context), ids2);

                if(copyClipboard){
                    final long startTime = startOfWeek + (2 * 24 * 60 * 60);
                    final long endTime = endOfStartWeek + (6 * 24 * 60 * 60);
                    final String finalToken = token;

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("Rooster JSON", Zermelogo.GetScheduleByTimeInJson("jfc", finalToken, String.valueOf(startTime), String.valueOf(endTime)));
                            clipboard.setPrimaryClip(clip);
                            Toast.makeText(context,"Gekopieërd",Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                if(restartApp){
                    Intent i = new Intent(context,RefreshActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    System.out.println("restarting, zermeloSync,false: " + sp.getBoolean("zermeloSync", false) + " zermeloSync,true: " + sp.getBoolean("zermeloSync", true));
                    context.startActivity(i);
                    //new MainActivity().fillEditTextFields();
                }
            }
        }).start();
    }
	
	private void cancelNotification(String day, int dayInt, int hour, String subject, Context context){
        System.out.println("notification");
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
                System.out.println("notification sent");
                notificationManagerCompat.notify(notId, notification);
            }
            //else System.out.println("notification prevented");
        }
	}

    private String intStr(int integer){
        return String.valueOf(integer);
    }

    private String[] removeEmptyStrings(String[] startingString){
        String[] endingString = new String[0];
        for(String s : startingString){
            if(!s.equals("") && !s.equals(" ")){
                endingString = append(endingString,s);
            }
        }
        return endingString;
    }

    public static String[] append(String[] array, String value) {
        String[] result = Arrays.copyOf(array, array.length + 1);
        result[result.length - 1] = value;
        return result;
    }
	
	private boolean lessonCanceled(String[] scheduleArray, int hour){
		for (int i = 0; i < scheduleArray.length; i += 3) {
            //System.out.println("i = "+i);
            if (scheduleArray[i].replace("V","").replace("X","").equals(String.valueOf(hour))) {
                if(scheduleArray[i].contains("X")){
                    //System.out.println("lesuitval");
                    return true;
                }
            }
        }
        return false;
	}
	
	private boolean fourtyMinuteSchedule(String[] scheduleArray){
		for (int i = 0; i < scheduleArray.length; i += 3) {
            if (scheduleArray[i].contains("V")) {
                //System.out.println("40-min rooster");
                return true;
            }
        }
        return false;
	}

    private String getLesson(String[] scheduleArray,int type,int hour){
        //System.out.println("scheduleArray: "+ Arrays.toString(scheduleArray));
        //scheduleArray = [2,du,205,3,oo,051,4,oo,051,5,oo,051]
        //                [0 ,1,2  ,3,4 ,5  ,6,7 ,8  ,9,10,11 ]
        
        for (int i = 0; i < scheduleArray.length; i += 3) {
            //System.out.println("i = "+i);
            if (scheduleArray[i].replace("V","").replace("X","").equals(String.valueOf(hour))) {
                if (type == SUBJECT) {
                    if(sp.getBoolean("group",false)){
                        return scheduleArray[i + 1].toUpperCase().replace("-"," ");
                    }else return scheduleArray[i + 1].toUpperCase().substring(0,scheduleArray[i + 1].toUpperCase().indexOf("-"));
                } else if (type == CLASSROOM) {
                    return scheduleArray[i + 2];
                }
            }
        }
        
        return "";
    }
}
