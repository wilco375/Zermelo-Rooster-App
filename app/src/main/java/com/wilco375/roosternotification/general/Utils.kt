package com.wilco375.roosternotification.general

import android.app.*
import android.appwidget.AppWidgetManager
import android.content.*
import android.net.ConnectivityManager
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.wilco375.roosternotification.R
import com.wilco375.roosternotification.receiver.AlarmReceiver
import com.wilco375.roosternotification.widget.LesdagWidgetProvider
import com.wilco375.roosternotification.widget.LesuurWidgetProvider
import java.util.*

object Utils {

    // Time
    private var unixStartOfWeekCache: Long = -1

    fun unixStartOfWeek(): Long {
        if (unixStartOfWeekCache == -1L) {
            val calendar = Calendar.getInstance()
            if (calendar.get(Calendar.HOUR_OF_DAY) > 17 || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
                calendar.add(Calendar.DAY_OF_WEEK, 1)
            if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                calendar.add(Calendar.DAY_OF_WEEK, 2)
            }

            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.clear(Calendar.MINUTE)
            calendar.clear(Calendar.SECOND)
            calendar.clear(Calendar.MILLISECOND)
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            unixStartOfWeekCache = calendar.timeInMillis / 1000L
        }
        return unixStartOfWeekCache
    }

    fun unixEndOfWeek() = unixStartOfWeek() + 7 * 24 * 60 * 60 - 1

    fun currentScheduleDate(): Date {
        val calendar = Calendar.getInstance()
        var offset = 0
        val day = calendar.get(Calendar.DAY_OF_WEEK)
        if (calendar.get(Calendar.HOUR_OF_DAY) > 17) offset = 1
        if (day >= Calendar.SATURDAY) offset = 2
        if (day == Calendar.SUNDAY) offset = 1
        calendar.add(Calendar.HOUR, 24 * offset)
        return calendar.time
    }

    // Networking
    fun isConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?

        val activeNetwork = cm?.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting ?: false
    }

    fun isWifiConnected(context: Context): Boolean {
        val connManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        val wifi = connManager?.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        return wifi?.isConnected ?: false
    }

    // Set alarm
    fun setAlarm(context: Context) {
        Thread {
            AlarmReceiver.createNotification(context)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager?
            val i = Intent(context, AlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT)
            alarmManager?.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 300000, pendingIntent)
        }.start()
    }

    // Update widgets
    fun updateWidgets(context: Context) {
        val ids = AppWidgetManager.getInstance(context).getAppWidgetIds(ComponentName(context, LesdagWidgetProvider::class.java))
        val lesdagWidget = LesdagWidgetProvider()
        lesdagWidget.onUpdate(context, AppWidgetManager.getInstance(context), ids)

        val ids2 = AppWidgetManager.getInstance(context).getAppWidgetIds(ComponentName(context, LesuurWidgetProvider::class.java))
        val lesuurWidget = LesuurWidgetProvider()
        lesuurWidget.onUpdate(context, AppWidgetManager.getInstance(context), ids2)
    }

    fun currentDay(): String {
        val calendar = Calendar.getInstance()
        var day = calendar.get(Calendar.DAY_OF_WEEK)
        if (calendar.get(Calendar.HOUR_OF_DAY) > 17) day += 1
        if (day >= Calendar.SATURDAY || day == Calendar.SUNDAY) day = Calendar.MONDAY
        return dayIntToStr(day)
    }

    fun currentWeek(): Int {
        val calendar = Calendar.getInstance()
        var day = calendar.get(Calendar.DAY_OF_WEEK)
        if (calendar.get(Calendar.HOUR_OF_DAY) > 17) day += 1

        // If saturday or sunday or sunday (after 17:00)
        if (day >= Calendar.SATURDAY || day == Calendar.SUNDAY || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
            return calendar.get(Calendar.WEEK_OF_YEAR) + 1
        else
            return calendar.get(Calendar.WEEK_OF_YEAR)
    }

    fun unixToCalendar(unixTime: Long): Calendar {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = unixTime * 1000
        return calendar
    }

    fun dayIntToStr(day: Int): String {
        return when (day) {
            Calendar.MONDAY -> "Maandag"
            Calendar.TUESDAY -> "Dinsdag"
            Calendar.WEDNESDAY -> "Woensdag"
            Calendar.THURSDAY -> "Donderdag"
            Calendar.FRIDAY -> "Vrijdag"
            Calendar.SATURDAY -> "Zaterdag"
            Calendar.SUNDAY -> "Zondag"
            else -> return "Error"
        }
    }

    // Copy to clipboard
    fun copyText(activity: Activity, context: Context, title: String, content: String, toast: Boolean) {
        activity.runOnUiThread({
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
            val clip = ClipData.newPlainText(title, content)
            clipboard?.primaryClip = clip
            if (toast) Toast.makeText(context, R.string.copied, Toast.LENGTH_SHORT).show()
        })
    }

    // Notification manager
    const val CURRENT_SCHEDULE = "current"
    const val LESSON_CANCELLED = "cancelled"
    fun getNotificationBuilder(context: Context, forChannel: String) : NotificationCompat.Builder {
        // Register notification channel
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val description = context.resources.getString(when(forChannel) {
                CURRENT_SCHEDULE -> R.string.notify
                LESSON_CANCELLED -> R.string.notify_cancel
                else -> throw IllegalArgumentException("forChannel must be one of CURRENT_SCHEDULE and LESSON_CANCELLED")
            })
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(forChannel, description, importance)
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        // Get builder
        return NotificationCompat.Builder(context, forChannel)
    }
}
