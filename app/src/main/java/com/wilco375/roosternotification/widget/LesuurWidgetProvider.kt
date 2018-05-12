package com.wilco375.roosternotification.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.text.SpannableString
import android.text.style.StrikethroughSpan
import android.widget.RemoteViews
import com.wilco375.roosternotification.R
import com.wilco375.roosternotification.activity.MainActivity
import com.wilco375.roosternotification.general.ScheduleHandler
import com.wilco375.roosternotification.general.Utils
import java.util.*

class LesuurWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        for (appWidgetId in appWidgetIds) {
            val sp = context.getSharedPreferences("Main", Context.MODE_PRIVATE)

            val dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
            if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) return

            val schedule = ScheduleHandler.getSchedule(context)[Utils.getCurrentScheduleDate()]
            if (schedule.items.isEmpty()) return

            var subject = ""
            var location = ""
            var timeslot = 0
            var cancelled = false
            //Get 15 minutes before current time
            val currentTime = Calendar.getInstance().also { it.add(Calendar.MINUTE, 15) }.time
            for (lesson in schedule) {
                if (currentTime >= lesson.start && currentTime <= lesson.end) {
                    subject = lesson.getSubjectAndGroup(sp)
                    location = lesson.location
                    timeslot = timeslot
                    cancelled = lesson.cancelled
                }
            }

            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

            val views = RemoteViews(context.packageName, R.layout.app_widget_lesuur)
            views.setOnClickPendingIntent(R.id.app_widget_lesuur_layout, pendingIntent)

            if (!(subject == "" && location == "") && !cancelled) {
                if (timeslot != 0)
                    views.setTextViewText(R.id.app_widget_lesuur_title, "$timeslot: $subject")
                else
                    views.setTextViewText(R.id.app_widget_lesuur_title, subject)

                views.setTextViewText(R.id.app_widget_lesuur_text, location)
            } else if (cancelled) {
                val spannableString1: SpannableString
                if (timeslot != 0)
                    spannableString1 = SpannableString("$timeslot: $subject")
                else
                    spannableString1 = SpannableString(subject)
                val spannableString2 = SpannableString(location)
                spannableString1.setSpan(StrikethroughSpan(), 0, spannableString1.toString().length, 0)
                spannableString2.setSpan(StrikethroughSpan(), 0, spannableString2.toString().length, 0)
                views.setTextViewText(R.id.app_widget_lesuur_title, spannableString1)
                views.setTextViewText(R.id.app_widget_lesuur_text, spannableString2)
            } else {
                views.setTextViewText(R.id.app_widget_lesuur_title, "")
                views.setTextViewText(R.id.app_widget_lesuur_text, "")
            }

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
