package com.wilco375.roosternotification;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ScheduleHandler {
    public static Schedule[] getSchedule(Context context){
        return readSchedule(context);
    }

    public static void setSchedule(Context context, Schedule[] schedule){
        writeSchedule(context,schedule);
    }

    public static Schedule[] getScheduleByDay(Context context, int day){
        Schedule[] schedule = readSchedule(context);
        List<Schedule> scheduleByDay = new ArrayList<>();
        for(Schedule scheduleItem : schedule){
            if(scheduleItem.getDay() == day){
                scheduleByDay.add(scheduleItem);
            }
        }
        return Utils.scheduleListToArray(scheduleByDay);
    }

    private static void writeSchedule(Context context, Schedule[] schedule){
        try {
            File file = new File(context.getFilesDir()+"/schedule");
            if(file.exists()) file.delete();
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(schedule);
            oos.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private static Schedule[] readSchedule(Context context){
        try {
            File file = new File(context.getFilesDir() + "/schedule");
            if (!file.exists()) return new Schedule[0];
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            return (Schedule[]) ois.readObject();
        }catch (IOException e){
            e.printStackTrace();
            return new Schedule[0];
        }catch (ClassNotFoundException e){
            e.printStackTrace();
            return new Schedule[0];
        }
    }
}
