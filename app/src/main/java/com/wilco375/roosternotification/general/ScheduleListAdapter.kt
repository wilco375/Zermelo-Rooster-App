package com.wilco375.roosternotification.general

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.wilco375.roosternotification.R
import com.wilco375.roosternotification.`object`.ScheduleDay
import java.text.SimpleDateFormat
import java.util.*

class ScheduleListAdapter(private val schedule: ScheduleDay, private val sp: SharedPreferences, private val inflater: LayoutInflater) : BaseAdapter() {
    private val hourFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return schedule.getItems().size
    }

    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertViewParam: View?, parent: ViewGroup): View {
        var convertView = convertViewParam
        if (convertView == null)
            convertView = inflater.inflate(R.layout.schedule_list, null)

        val scheduleItem = schedule.getItems()[position]

        val timeslot = convertView!!.findViewById<TextView>(R.id.timeslot)
        val info = convertView.findViewById<TextView>(R.id.info)
        val time = convertView.findViewById<TextView>(R.id.time)
        timeslot.text = (scheduleItem.timeslot).toString()
        info.text = scheduleItem.getSummary(sp)
        time.text = "${hourFormat.format(scheduleItem.start)} - ${hourFormat.format(scheduleItem.end)}"
        if (scheduleItem.cancelled) {
            info.paintFlags = info.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            info.paintFlags = 0
        }
        return convertView
    }
}
