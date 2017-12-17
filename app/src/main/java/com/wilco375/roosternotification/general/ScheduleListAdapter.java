package com.wilco375.roosternotification.general;

import android.content.SharedPreferences;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wilco375.roosternotification.R;
import com.wilco375.roosternotification.Schedule;

import java.util.List;

public class ScheduleListAdapter extends BaseAdapter{
    private SharedPreferences sp;
    private LayoutInflater inflater;
    private List<Schedule> schedule;

    public ScheduleListAdapter(List<Schedule> schedule, SharedPreferences sp, LayoutInflater inflater) {
        this.sp = sp;
        this.inflater = inflater;
        this.schedule = schedule;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return schedule.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView == null)
            convertView = inflater.inflate(R.layout.schedule_list, null);

        Schedule lesson = schedule.get(position);

        TextView timeslot = convertView.findViewById(R.id.timeslot);
        TextView info = convertView.findViewById(R.id.info);
        TextView time = convertView.findViewById(R.id.time);
        timeslot.setText(String.valueOf(lesson.getTimeslot()));
        info.setText(getInfo(lesson));
        time.setText(lesson.getStart()+" - "+lesson.getEnd());
        if(lesson.getCancelled()){
            info.setPaintFlags(info.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            info.setPaintFlags(0);
        }
        return convertView;
    }

    private String getInfo(Schedule lesson){
        String info = "";
        if(!lesson.getSubject().equals("")) info = lesson.getSubjectAndGroup(sp);
        if(!lesson.getType().equals("Les")) info += " ("+lesson.getType()+")";
        if(!lesson.getLocation().equals("")) info += " - "+lesson.getLocation();
        return info;
    }
}
