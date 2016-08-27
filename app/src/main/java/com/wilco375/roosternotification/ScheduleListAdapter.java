package com.wilco375.roosternotification;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ScheduleListAdapter extends BaseAdapter{
    String [] timeslots;
    String[] infos;
    String[] times;
    boolean[] cancelleds;
    LayoutInflater inflater;

    public ScheduleListAdapter(Context contextArg, String[] timeslotsArg, String[] infosArg, String[] timesArg, boolean[] cancelledsArg) {
        timeslots = timeslotsArg;
        infos = infosArg;
        times = timesArg;
        cancelleds = cancelledsArg;
        inflater = (LayoutInflater) contextArg.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        return timeslots.length;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView == null)
            convertView = inflater.inflate(R.layout.schedule_list, null);
        TextView timeslot = (TextView) convertView.findViewById(R.id.timeslot);
        TextView info = (TextView) convertView.findViewById(R.id.info);
        TextView time = (TextView) convertView.findViewById(R.id.time);
        timeslot.setText(timeslots[position]);
        info.setText(infos[position]);
        time.setText(times[position]);
        if(cancelleds[position]) info.setPaintFlags(info.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);;
        return convertView;
    }
}
