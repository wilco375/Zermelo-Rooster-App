package com.wilco375.roosternotification;

import android.content.SharedPreferences;

import com.wilco375.roosternotification.general.Utils;

import java.io.Serializable;
import java.util.Comparator;

public class Schedule implements Serializable{

    String subject;
    String group;
    String location;
    String type;
    boolean cancelled;
    int day;
    String start;
    String end;
    double startHour;
    double endHour;
    int timeslot;

    public Schedule(){}

    public Schedule(String subject,String group, String location, String type, boolean cancelled, int day, String start, String end, double startHour, double endHour, int timeslot){
        this.subject = subject;
        this.group = group;
        this.location = location;
        this.type = type;
        this.cancelled = cancelled;
        this.day = day;
        this.start = start;
        this.end = end;
        this.startHour = startHour;
        this.endHour = endHour;
        this.timeslot = timeslot;
    }

    public String getSubject(){
        return this.subject;
    }

    public void setSubject(String subject){
        this.subject = subject;
    }

    public String getGroup(){
        return Utils.strNotNull(this.group);
    }

    public void setGroup(String group){
        this.group = group;
    }

    public String getLocation(){
        return Utils.strNotNull(this.location);
    }

    public void setLocation(String location){
        this.location = location;
    }

    public String getType(){
        switch(Utils.strNotNull(this.type)){
            case "lesson": return "Les";
            case "exam": return "Toets";
            case "activity": return "Activiteit";
            case "choice": return "Keuze";
            case "talk": return "Gesprek";
            case "other": return "Anders";
            default: return "Onbekend";
        }
    }

    public void setType(String type){
        this.type = type;
    }

    public boolean getCancelled(){
        return this.cancelled;
    }

    public void setCancelled(boolean cancelled){
        this.cancelled = cancelled;
    }

    public int getDay(){
        return this.day;
    }

    public void setDay(int day){
        this.day = day;
    }

    public String getStart(){
        return Utils.strNotNull(this.start);
    }

    public void setStart(String start){
        this.start = start;
    }

    public String getEnd(){
        return Utils.strNotNull(this.end);
    }

    public void setEnd(String end){
        this.end = end;
    }

    public double getStartHour(){
        return this.startHour;
    }

    public void setStartHour(double startHour){
        this.startHour = startHour;
    }

    public double getEndHour(){
        return this.endHour;
    }

    public void setEndHour(double endHour){
        this.endHour = endHour;
    }

    public int getTimeslot(){
        return this.timeslot;
    }

    public void setTimeslot(int timeslot){
        this.timeslot = timeslot;
    }

    public String getSubjectAndGroup(SharedPreferences sp){
        if(sp.getBoolean("group",false) && !Utils.strNotNull(this.group).equals("")) return this.subject+"-"+this.group;
        else return this.subject;
    }

    public static class ScheduleComparator implements Comparator<Schedule> {
        @Override
        public int compare(Schedule o1, Schedule o2) {
            return Double.compare(o1.getStartHour(),o2.getStartHour());
        }
    }
}


