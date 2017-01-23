package com.wilco375.roosternotification.general;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wilco375.roosternotification.R;

import java.util.List;

public class ScheduleListAdapter extends BaseAdapter{
    private List<String> timeslots;
    private List<String> infos;
    private List<String> times;
    private List<Boolean> cancelleds;
    private LayoutInflater inflater;

    public ScheduleListAdapter(Context contextArg, List<String> timeslotsArg, List<String> infosArg, List<String> timesArg, List<Boolean> cancelledsArg) {
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
        return timeslots.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView == null)
            convertView = inflater.inflate(R.layout.schedule_list, null);
        TextView timeslot = (TextView) convertView.findViewById(R.id.timeslot);
        TextView info = (TextView) convertView.findViewById(R.id.info);
        TextView time = (TextView) convertView.findViewById(R.id.time);
        timeslot.setText(timeslots.get(position));
        info.setText(infos.get(position));
        time.setText(times.get(position));
        if(cancelleds.get(position)) info.setPaintFlags(info.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);;
        return convertView;
    }
}
