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
import com.wilco375.roosternotification.`object`.Schedule
import com.wilco375.roosternotification.activity.MainActivity
import com.wilco375.roosternotification.general.Utils
import io.multimoon.colorful.Colorful
import java.util.*

class LesdagWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        for (appWidgetId in appWidgetIds) {
            val sp = context.getSharedPreferences("Main", Context.MODE_PRIVATE)

            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

            val views = RemoteViews(context.packageName, R.layout.app_widget_lesdag)
            views.setOnClickPendingIntent(R.id.app_widget_lesdag_layout, pendingIntent)
            views.setTextViewText(R.id.app_widget_lesdag_dag, Utils.currentDay())
            views.setInt(R.id.app_widget_lesdag_dag, "setBackgroundColor", Colorful().getPrimaryColor().getColorPack().normal().asInt())

            var widgetText = ""

            val schedule = Schedule.getInstance(context)[Utils.currentScheduleDate()]
            val strikethroughStartIndex = ArrayList<Int>()
            val strikethroughEndIndex = ArrayList<Int>()

            for (lesson in schedule) {
                if (lesson.timeslot == 0) {
                    if (lesson.cancelled) {
                        val string: String = if (lesson.type != "Les")
                            lesson.getSubjectAndGroup(sp) + " (${lesson.type}) ${lesson.location}\n"
                        else
                            lesson.getSubjectAndGroup(sp) + " ${lesson.location}\n"
                        strikethroughStartIndex.add(widgetText.length)
                        strikethroughEndIndex.add(widgetText.length + string.length)
                        widgetText += string
                    } else {
                        widgetText += if (lesson.type != "Les")
                            lesson.getSubjectAndGroup(sp) + " (${lesson.type}) ${lesson.location}\n"
                        else
                            lesson.getSubjectAndGroup(sp) + " ${lesson.location}\n"
                    }
                } else {
                    if (lesson.cancelled) {
                        val string: String = if (lesson.type != "Les")
                            lesson.timeslot.toString() + ". " + lesson.getSubjectAndGroup(sp) + " (${lesson.type}) ${lesson.location}\n"
                        else
                            lesson.timeslot.toString() + ". " + lesson.getSubjectAndGroup(sp) + " ${lesson.location}\n"
                        strikethroughStartIndex.add(widgetText.length)
                        strikethroughEndIndex.add(widgetText.length + string.length)
                        widgetText += string
                    } else {
                        widgetText += if (lesson.type != "Les")
                            lesson.timeslot.toString() + ". " + lesson.getSubjectAndGroup(sp) + " (${lesson.type}) ${lesson.location}\n"
                        else
                            lesson.timeslot.toString() + ". " + lesson.getSubjectAndGroup(sp) + " ${lesson.location}\n"
                    }
                }
            }

            val widgetTextSpan = SpannableString(widgetText)

            // Add strikethroughs for cancelled lessons
            for (j in strikethroughEndIndex.indices) {
                widgetTextSpan.setSpan(StrikethroughSpan(), strikethroughStartIndex[j], strikethroughEndIndex[j], 0)
            }

            views.setTextViewText(R.id.app_widget_lesdag_content, widgetTextSpan)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
