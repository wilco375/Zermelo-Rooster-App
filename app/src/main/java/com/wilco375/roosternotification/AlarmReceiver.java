package com.wilco375.roosternotification;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.SpannableString;
import android.text.style.StrikethroughSpan;
import android.widget.Toast;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {

    static int notificationId = 001;

    static NotificationCompat.Builder builder;

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("Received Alarm");
        createNotification(context);
    }

    public static void createNotification(Context context){
        String subject;
        String classroom;
        int uur;
        String string = "";
        String dag = "";

        SharedPreferences sp = context.getSharedPreferences("Main", context.MODE_PRIVATE);
        SharedPreferences.Editor spe = sp.edit();

        //spe.putString("lastSync",String.valueOf(System.currentTimeMillis()));

        if(sp.getInt("syncCount",0)==3) {
            System.out.println("Syncing with Zermelo");
            new ZermeloSync().syncZermelo(context,null, false, false);
            spe.putInt("timesSynced",sp.getInt("timesSynced",0)+1);
            spe.putInt("syncCount", 0);
        } else{
            spe.putInt("syncCount",sp.getInt("syncCount",0)+1);
            spe.putInt("timesNotSynced", sp.getInt("timesNotSynced", 0) + 1);
        }
        spe.apply();


        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        if(day == Calendar.MONDAY) {
            dag = "Maandag";
            string = "a";
        }else if(day == Calendar.TUESDAY){
            dag = "Dinsdag";
            string = "b";
        }else if(day == Calendar.WEDNESDAY){
            dag = "Woensdag";
            string = "c";
        }else if(day == Calendar.THURSDAY){
            dag = "Donderdag";
            string = "d";
        }else if(day == Calendar.FRIDAY){
            dag = "Vrijdag";
            string = "e";
        }
        uur = lesuur(context,sp);
        if(uur == 0) return;

        String one =   "1. "+ sp.getString(string+"13","")+" "+sp.getString(string+"14","");
        String two =   "2. "+ sp.getString(string+"23","")+" "+sp.getString(string+"24","");
        String three = "3. "+ sp.getString(string+"33","")+" "+sp.getString(string+"34","");
        String four =  "4. "+ sp.getString(string+"43","")+" "+sp.getString(string+"44","");
        String five =  "5. "+ sp.getString(string+"53","")+" "+sp.getString(string+"54","");
        String six =   "6. "+ sp.getString(string+"63","")+" "+sp.getString(string+"64","");
        String seven = "7. "+ sp.getString(string+"73","")+" "+sp.getString(string+"74","");
        String eight = "8. "+ sp.getString(string+"83","")+" "+sp.getString(string+"84","");
        String nine =  "9. "+ sp.getString(string+"93","")+" "+sp.getString(string+"94","");

        if(sp.getBoolean(string+"12",false)){
            one = "1. X";
        }if(sp.getBoolean(string+"22",false)){
            two = "2. X";
        }if(sp.getBoolean(string+"32",false)){
            three = "3. X";
        }if(sp.getBoolean(string+"42",false)){
            four = "4. X";
        }if(sp.getBoolean(string+"52",false)){
            five = "5. X";
        }if(sp.getBoolean(string+"62",false)){
            six = "6. X";
        }if(sp.getBoolean(string+"72",false)){
            seven = "7. X";
        }if(sp.getBoolean(string+"82",false)){
            eight = "8. X";
        }if(sp.getBoolean(string+"92",false)){
            nine = "9. X";
        }

        NotificationCompat.BigTextStyle secondPageStyle = new NotificationCompat.BigTextStyle();
        secondPageStyle.setBigContentTitle(dag).bigText(one+"\n"+two+"\n"+three+"\n"+four+"\n"+five+"\n"+six+"\n"+seven+"\n"+eight+"\n"+nine);

        string = string + String.valueOf(uur);

        subject = sp.getString(string + "3", "");
        classroom = sp.getString(string + "4", "");

        //System.out.println("1: "+String.valueOf(uur) + ": " + subject + " "+classroom+" 2: " + sp.getString("lastNotification",""));

        if(sp.getString("lastNotification","").equals(String.valueOf(uur) + ": " + subject + " "+classroom)){
            //System.out.println("returning");
            return;
        }

        //System.out.println("continuing");

        spe.putString("lastNotification",String.valueOf(uur) + ": " + subject+ " "+classroom).apply();

        if(!subject.equals("") && !classroom.equals("") && !sp.getBoolean(string+"2",false) && sp.getBoolean("notify",true)){
            builder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.notification_logo)
                    .setContentTitle(String.valueOf(uur) + ": " + subject)
                    .setContentText(classroom);

            Notification secondPageNotification = new NotificationCompat.Builder(context)
                    .setStyle(secondPageStyle).build();

            Notification notification = builder.extend(new NotificationCompat.WearableExtender().addPage(secondPageNotification)).build();

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            notificationManagerCompat.notify(notificationId, notification);

            //System.out.println("created notification");
        }else{
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            notificationManagerCompat.cancelAll();
        }
    }

    public static int lesuur(Context context, SharedPreferences sp){
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        //Show quarter earlier
        minute = minute + 15;
        if(minute >= 60){
            hour = hour + 1;
            minute = minute - 60;
        }
		
		double time = ((double)hour)+(((double)minute)/60);
		
        if(!sp.getBoolean("fourtyMinuteSchedule",false)){
			if(time >= 8.25 && time <= 9.0833){
				return 1;
			}else if(time > 9.0833 && time <= 9.9166){
				return 2;
			}else if(time > 9.9166 && time <= 10.75){
				return 3;
			}else if(time > 10.75 && time <= 11.9166){
				return 4;
			}else if(time > 11.9166 && time <= 12.75){
				return 5;
			}else if(time > 12.75 && time <= 14.0833){
				return 6;
			}else if(time > 14.0833 && time <= 14.9166){
				return 7;
			}else if(time > 14.9166 && time <= 15.75){
				return 8;
			}else if(time > 15.75 && time <= 16.5833){
                return 9;
            }
		}
		
		else{
			if(time >= 8.25 && time <= 8.9166){
				return 1;
			}else if(time > 8.9166 && time <= 9.5833){
				return 2;
			}else if(time > 9.5833 && time <= 10.25){
				return 3;
			}else if(time > 10.25 && time <= 11.25){
				return 4;
			}else if(time > 11.25 && time <= 11.9166){
				return 5;
			}else if(time > 11.9166 && time <= 12.5833){
				return 6;
			}else if(time > 12.5833 && time <= 13.75){
				return 7;
			}else if(time > 13.75 && time <= 14.4166){
				return 8;
			}else if(time > 14.4166 && time <= 15.0833){
                return 9;
            }
		}
        
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel(notificationId);
        return 0;
    }
}
