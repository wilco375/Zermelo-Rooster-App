package com.wilco375.roosternotification.receiver

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.wilco375.roosternotification.R
import com.wilco375.roosternotification.`object`.Schedule
import com.wilco375.roosternotification.activity.MainActivity
import com.wilco375.roosternotification.general.ScheduleHandler
import com.wilco375.roosternotification.general.Utils
import com.wilco375.roosternotification.online.ZermeloSync
import java.util.*

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        createNotification(context)
    }

    companion object {

        private val NOTIFICATION_ID = 1

        fun createNotification(context: Context) {
            val sp = context.getSharedPreferences("Main", Context.MODE_PRIVATE)
            val spe = sp.edit()

            //Sync with Zermelo
            if (sp.getInt("syncCount", 0) == 3) {
                ZermeloSync().syncZermelo(context, false, false)
                spe.putInt("timesSynced", sp.getInt("timesSynced", 0) + 1)
                spe.putInt("syncCount", 0)
            } else {
                spe.putInt("syncCount", sp.getInt("syncCount", 0) + 1)
                spe.putInt("timesNotSynced", sp.getInt("timesNotSynced", 0) + 1)
            }
            spe.apply()

            // Return if notifications are disabled
            if (!sp.getBoolean("notify", true)) return

            // Get schedule of current day
            val schedule = Schedule.getInstance(context)[Date()]
            if (schedule.getItems().isEmpty()) return

            // Get all the notification texts
            var bigText = ""
            var subject = ""
            var location = ""
            var timeslot = 0
            var title = ""
            // Get 15 minutes before current time
            val currentTime = Calendar.getInstance().also { it.add(Calendar.MINUTE, 15) }.time
            for (lesson in schedule) {
                // Main notification
                if (currentTime >= lesson.start && currentTime <= lesson.end && !lesson.cancelled) {
                    // Current lesson
                    subject = lesson.getSubjectAndGroup(sp)
                    location = lesson.location
                    timeslot = lesson.timeslot
                }

                title = if (timeslot != 0) "$timeslot: $subject" else subject

                //Second Page for Android Wear
                val wearTitle = if (lesson.timeslot <= 0)
                    ""
                else
                    "${lesson.timeslot}. "

                bigText += wearTitle + if (!lesson.cancelled)
                    lesson.getSubjectAndGroup(sp) + " " + lesson.location + "\n"
                else
                    "X\n"
            }

            // If the notification is already sent, return
            val spQuery = "$timeslot: $subject $location"
            if (sp.getString("lastNotification", "") == spQuery) return

            // Update last notification String
            spe.putString("lastNotification", spQuery).apply()

            if (!subject.isEmpty() || !location.isEmpty()) {
                // Style that shows the entire day's schedule
                val daySchedule = NotificationCompat.BigTextStyle()
                daySchedule.setBigContentTitle(Utils.dayIntToStr(Calendar.getInstance().get(Calendar.DAY_OF_WEEK)))
                        .bigText(Utils.replaceLast(bigText, "\n", ""))

                // Create main notification with daySchedule as extended notification
                // TODO Add notification channel
                val builder = NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.notification_logo)
                        .setContentText(location)
                        .extend(NotificationCompat.WearableExtender()
                                .addPage(
                                        NotificationCompat.Builder(context)
                                                .setStyle(daySchedule).build()
                                )
                        )
                        .setContentTitle(title)
                        .setContentIntent(PendingIntent.getActivity(context, 0, Intent(context, MainActivity::class.java), 0))

                if (sp.getBoolean("notifyDaySchedule", true)) builder.setStyle(daySchedule)

                val notification = builder.build()
                val notificationManagerCompat = NotificationManagerCompat.from(context)
                notificationManagerCompat.notify(NOTIFICATION_ID, notification)
            } else {
                // Cancel all notifications if there's no subject and location
                val notificationManagerCompat = NotificationManagerCompat.from(context)
                notificationManagerCompat.cancelAll()
            }
        }
    }
}
