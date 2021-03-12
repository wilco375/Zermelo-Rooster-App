package com.wilco375.roosternotification.general

import android.app.*
import android.appwidget.AppWidgetManager
import android.content.*
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.*
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.wilco375.roosternotification.R
import com.wilco375.roosternotification.receiver.AlarmReceiver
import com.wilco375.roosternotification.widget.Lesdag2WidgetProvider
import com.wilco375.roosternotification.widget.LesdagWidgetProvider
import com.wilco375.roosternotification.widget.LesuurWidgetProvider
import io.multimoon.colorful.Colorful
import io.multimoon.colorful.ThemeColor
import java.util.*
import kotlin.math.roundToInt


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
        if (day == Calendar.FRIDAY && calendar.get(Calendar.HOUR_OF_DAY) > 17) offset = 3
        if (day >= Calendar.SATURDAY) offset = 2
        if (day == Calendar.SUNDAY) offset = 1
        calendar.add(Calendar.HOUR, 24 * offset)
        return calendar.time
    }

    // Networking
    fun isConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?

        val activeNetwork = cm?.activeNetworkInfo
        return activeNetwork?.isConnected ?: false
    }

    fun isWifiConnected(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < 28) {
            return isWifiConnectedCompat(context)
        } else {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
            return cm?.getNetworkCapabilities(cm.activeNetwork)?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ?: false
        }
    }

    @Suppress("Deprecation")
    private fun isWifiConnectedCompat(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        val wifi = cm?.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
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
        LesdagWidgetProvider().onUpdate(context, AppWidgetManager.getInstance(context), ids)

        val ids2 = AppWidgetManager.getInstance(context).getAppWidgetIds(ComponentName(context, LesuurWidgetProvider::class.java))
        LesuurWidgetProvider().onUpdate(context, AppWidgetManager.getInstance(context), ids2)

        val ids3 = AppWidgetManager.getInstance(context).getAppWidgetIds(ComponentName(context, Lesdag2WidgetProvider::class.java))
        Lesdag2WidgetProvider().onUpdate(context, AppWidgetManager.getInstance(context), ids3)
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
        activity.runOnUiThread {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
            val clip = ClipData.newPlainText(title, content)
            clipboard?.setPrimaryClip(clip)
            if (toast) Toast.makeText(context, R.string.copied, Toast.LENGTH_SHORT).show()
        }
    }

    // Notification manager
    const val CURRENT_SCHEDULE = "current"
    const val LESSON_CANCELLED = "cancelled"
    fun getNotificationBuilder(context: Context, forChannel: String): NotificationCompat.Builder {
        // Register notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val description = context.resources.getString(when (forChannel) {
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

    fun isNightModeEnabled(context: Context): Boolean {
        val mode = context.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)
        return when (mode) {
            Configuration.UI_MODE_NIGHT_YES -> true
            else -> false
        }
    }

    fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences("Main", Context.MODE_PRIVATE)
    }

    fun getColorfulColor(sp: SharedPreferences): ThemeColor {
        var color = sp.getString("theme_color", "")!!
        if (color.isBlank()) {
            val website = sp.getString("website", "")!!
            color = when {
                website.startsWith("candea") -> ThemeColor.YELLOW.name
                website.startsWith("jpthijsse") -> ThemeColor.GREEN.name
                else -> ThemeColor.BLUE.name
            }
        }

        return getColorfulColorByName(color)
    }

    private fun getColorfulColorByName(name: String): ThemeColor {
        var themeColor = ThemeColor.BLUE
        for (c in ThemeColor.values()) {
            if (c.name == name) {
                themeColor = c
            }
        }
        return themeColor
    }

    fun updateColorful(context: Context, newColor: String? = null) {
        val color = if (newColor != null) {
            getColorfulColorByName(newColor)
        } else {
            getColorfulColor(getSharedPreferences(context))
        }

        println("Updating color to "+color.name)

        val edit = Colorful().edit()
                .setPrimaryColor(color)
                .setAccentColor(color)
        if (newColor != null && context is Activity) {
            edit.apply(context) {
                context.recreate()
            }
        } else {
            edit.apply(context)
        }
    }

    fun dpToPx(dp: Double): Int {
        return (dp * Resources.getSystem().displayMetrics.density).roundToInt()
    }

    /**
     * Generate a square with rounded corners as a bitmap
     * @param context
     * @param size size of the square in dp
     * @param borderRadius border radius of the square in dp
     * @param color color of the square
     */
    fun getRoundedSquareBitmap(size: Double, borderRadius: Double, color: Int): Bitmap {
        val sizePx = dpToPx(size)
        val borderRadiusPx = dpToPx(borderRadius)

        val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = color
        val rect = RectF(0.0f, 0.0f, sizePx.toFloat(), sizePx.toFloat())
        canvas.drawRoundRect(rect, borderRadiusPx.toFloat(), borderRadiusPx.toFloat(), paint)

        return bitmap
    }
}
