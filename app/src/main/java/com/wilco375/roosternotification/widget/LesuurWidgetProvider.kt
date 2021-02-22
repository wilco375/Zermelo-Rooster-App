package com.wilco375.roosternotification.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import com.wilco375.roosternotification.R
import com.wilco375.roosternotification.`object`.Schedule
import com.wilco375.roosternotification.`object`.ScheduleItem
import com.wilco375.roosternotification.activity.MainActivity
import com.wilco375.roosternotification.general.Utils
import io.multimoon.colorful.Colorful
import java.text.SimpleDateFormat
import java.util.*

class LesuurWidgetProvider : AppWidgetProvider() {
    private val hourFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        for (appWidgetId in appWidgetIds) {
            val mainActivityIntent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(context, 0, mainActivityIntent, 0)
            val views = RemoteViews(context.packageName, R.layout.app_widget_lesuur)

            views.setOnClickPendingIntent(R.id.app_widget_lesdag_layout, pendingIntent)

            val schedule = Schedule.getInstance(context)[Utils.currentScheduleDate()]

            // Get first upcoming lesson with and offset of 15 minutes
            val currentTime = Calendar.getInstance().also { it.add(Calendar.MINUTE, 15) }.time
            val upcomingItem = schedule.getItems().firstOrNull { currentTime <= it.end }

            val sp = Utils.getSharedPreferences(context)
            val teacher = sp.getBoolean("teacher", false)
            val teacherFull = sp.getBoolean("teacherFull", false)
            if (!teacher && !teacherFull) {
                views.setViewVisibility(R.id.app_widget_lesdag_teacher, View.GONE)
            } else {
                views.setViewVisibility(R.id.app_widget_lesdag_teacher, View.VISIBLE)
            }

            val primaryColor = Colorful().getPrimaryColor().getColorPack().normal().asInt()

            views.setImageViewBitmap(R.id.app_widget_lesdag_location_bg,
                    Utils.getRoundedSquareBitmap(50.0, 6.0, primaryColor)
            )

            if (upcomingItem != null) {
                views.setViewVisibility(R.id.has_content, View.VISIBLE)
                views.setViewVisibility(R.id.no_content, View.GONE)

                var summary = upcomingItem.getSubjectAndGroup(sp)
                if (upcomingItem.timeslot != 0) {
                    summary = "${upcomingItem.timeslot}. " + summary
                }
                if (upcomingItem.type != "Les") summary += " (${upcomingItem.type})"
                views.setTextViewText(R.id.app_widget_lesdag_subject, summary)

                if (teacherFull) {
                    views.setTextViewText(R.id.app_widget_lesdag_teacher, upcomingItem.teacherFull)
                } else if (teacher) {
                    views.setTextViewText(R.id.app_widget_lesdag_teacher, upcomingItem.teacher)
                }
                views.setTextViewText(R.id.app_widget_lesdag_time,
                        "${hourFormat.format(upcomingItem.start)} - ${hourFormat.format(upcomingItem.end)}")
                views.setTextViewText(R.id.app_widget_lesdag_location, upcomingItem.location)
            } else {
                views.setViewVisibility(R.id.has_content, View.GONE)
                views.setViewVisibility(R.id.no_content, View.VISIBLE)
            }

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
