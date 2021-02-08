package com.wilco375.roosternotification.widget

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.text.SpannableString
import android.text.style.StrikethroughSpan
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.wilco375.roosternotification.R
import com.wilco375.roosternotification.`object`.Schedule
import com.wilco375.roosternotification.`object`.ScheduleDay
import com.wilco375.roosternotification.general.Utils
import io.multimoon.colorful.Colorful


class ScheduleListSmallFactory(val context: Context) : RemoteViewsService.RemoteViewsFactory {
    private var schedule: ScheduleDay? = null
    private val sp = Utils.getSharedPreferences(context)
    private lateinit var background: Bitmap

    init {
        setBackground()
    }

    override fun onCreate() {
    }

    override fun onDataSetChanged() {
        setBackground()
        schedule = Schedule.getInstance(context)[Utils.currentScheduleDate()]
    }

    override fun onDestroy() {

    }

    override fun getCount(): Int {
        return schedule?.getItems()?.size ?: 0
    }

    override fun getViewAt(position: Int): RemoteViews? {
        if (schedule == null) {
            return null
        }
        val scheduleItem = schedule!!.getItems()[position]

        val views = RemoteViews(context.packageName, R.layout.schedule_list_small)
        views.setTextViewText(R.id.timeslot, scheduleItem.timeslot.toString())

        val summary = SpannableString(scheduleItem.getSummary(sp, true))
        if (scheduleItem.cancelled) {
            summary.setSpan(StrikethroughSpan(), 0, summary.toString().length, 0)
        }
        views.setTextViewText(R.id.info, summary)

        views.setOnClickFillInIntent(R.id.list_view_row, Intent())

        views.setImageViewBitmap(R.id.timeslot_bg,
                Utils.getRoundedSquareBitmap(30.0, 6.0,
                        Colorful().getPrimaryColor().getColorPack().normal().asInt()
                )
        )

        return views
    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemId(position: Int): Long {
        if ((schedule?.getItems()?.size ?: 0) > position) {
            return position.toLong()
        } else {
            return -1
        }
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    private fun setBackground() {
        background = Utils.getRoundedSquareBitmap(50.0, 6.0,
                Colorful().getPrimaryColor().getColorPack().normal().asInt()
        )
    }
}