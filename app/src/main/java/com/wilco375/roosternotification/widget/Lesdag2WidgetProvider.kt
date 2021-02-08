package com.wilco375.roosternotification.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import android.widget.TextView
import com.wilco375.roosternotification.R
import com.wilco375.roosternotification.`object`.Schedule
import com.wilco375.roosternotification.activity.MainActivity
import com.wilco375.roosternotification.general.Utils
import io.multimoon.colorful.Colorful
import io.multimoon.colorful.Defaults
import io.multimoon.colorful.ThemeColor
import io.multimoon.colorful.initColorful
import java.text.SimpleDateFormat
import java.util.*


class Lesdag2WidgetProvider : AppWidgetProvider() {
    private val hourFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        Utils.updateColorful(context)

        for (appWidgetId in appWidgetIds) {
            val mainActivityIntent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(context, 0, mainActivityIntent, 0)
            val views = RemoteViews(context.packageName, R.layout.app_widget_lesdag_2)

            views.setOnClickPendingIntent(R.id.app_widget_lesdag_layout, pendingIntent)
            views.setTextViewText(R.id.app_widget_lesdag_dag, Utils.currentDay())

            val serviceIntent = Intent(context, ScheduleListSmallService::class.java)
            views.setRemoteAdapter(R.id.app_widget_lesdag_content, serviceIntent)

            views.setPendingIntentTemplate(R.id.app_widget_lesdag_content, pendingIntent)

            val primaryColor = Colorful().getPrimaryColor().getColorPack().normal().asInt()
            views.setInt(R.id.app_widget_lesdag_dag, "setTextColor", primaryColor)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
