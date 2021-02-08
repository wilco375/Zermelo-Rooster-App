package com.wilco375.roosternotification.online

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import com.wilco375.roosternotification.R
import com.wilco375.roosternotification.`object`.Schedule
import com.wilco375.roosternotification.`object`.ScheduleItem
import com.wilco375.roosternotification.`object`.items
import com.wilco375.roosternotification.activity.MainActivity
import com.wilco375.roosternotification.exception.*
import com.wilco375.roosternotification.general.Config
import com.wilco375.roosternotification.general.Utils
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

class ZermeloSync(private val context: Context) {
    private val sp = Utils.getSharedPreferences(context)

    fun syncZermelo(updateMainActivity: Boolean = false, username: String = "~me") {
        if (!updateMainActivity && !Utils.isWifiConnected(context)) return

        Thread {
            // List of all the lessons that have been cancelled
            val cancelledItems = ArrayList<ScheduleItem>()

            // List of school branches
            val schools = HashSet<String>()

            // Get start of this week in unix
            val start = Utils.unixStartOfWeek() - Config.SYNC_WINDOW * 24 * 3600
            // Set end two weeks later
            val end = start + 2 * Config.SYNC_WINDOW * 24 * 3600

            // Get schedule from api
            val website = sp.getString("website", "")!!
            val token = sp.getString("token", "")!!
            val jsonSchedule = Api(website).getSchedule(token, username, start, end) ?: return@Thread

            try {
                // Format to JSONArray
                val schedule = Schedule.getInstance(context, username)

                // Sync names for all the school branches in the schedule
                jsonSchedule.items()
                        .filter { (it as JSONObject).has("branchOfSchool") }
                        .forEach { schools.add((it as JSONObject).getString("branchOfSchool")) }

                val database = Schedule.getInstance()
                val names = Api(website).getNames(token, schools)
                        .mapKeys { it.key.toUpperCase(Locale.getDefault()) }
                names.forEach { database.addName(it.key, it.value) }

                // Loop trough all lessons and create an object array with all lessons
                jsonSchedule.items()
                        .map { ScheduleItem(it as JSONObject) }.forEach {
                            // Add item to schedule
                            schedule += it

                            // If the item is cancelled add it to the cancelled items list
                            if (it.cancelled && username == "~me") {
                                cancelledItems.add(it)
                            }

                            // Get the full teacher name
                            it.teacherFull = it.teacher
                                    .split("/")
                                    .joinToString("/") {
                                        code -> names.getOrElse(code, { code })
                                    }
                        }

                // Notify cancelled lessons
                cancelNotification(cancelledItems.filter { it.isThisWeek() && !schedule.isCancelledNotified(it) }, context)

                // Save schedule
                schedule.save()

                // Update widgets
                if (username == "~me") {
                    Utils.updateWidgets(context)
                }

                // Restart app if necessary
                if (updateMainActivity && context is MainActivity) {
                    context.getSchedule()
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }.start()
    }

    private fun cancelNotification(schedule: List<ScheduleItem>, context: Context) {
        if (schedule.isEmpty()) return

        if (sp.getBoolean("notifyCancel", true)) {
            val builder = Utils.getNotificationBuilder(context, Utils.CURRENT_SCHEDULE)
                    .setSmallIcon(R.drawable.notification)
                    .setContentIntent(PendingIntent.getActivity(context, 0, Intent(context, MainActivity::class.java), 0))
            val spe = sp.edit()
            val notificationManagerCompat = NotificationManagerCompat.from(context)

            if (schedule.size < 5) {
                for (item in schedule) {
                    // Under 5 cancelled lessons, show separate notifications
                    builder.setContentTitle(String.format(context.resources.getString(R.string.hour_cancelled_on), item.getDay().toLowerCase(Locale.getDefault())))

                    if (item.timeslot != 0)
                        builder.setContentText("${item.timeslot}. ${item.subject}")
                    else
                        builder.setContentText(item.subject)

                    val notId = sp.getInt("notId", 2)
                    spe.putInt("notId", notId + 1).apply()
                    notificationManagerCompat.notify(notId, builder.build())
                }
            } else {
                // Over 5 cancelled lessons, show a notification with a summary
                builder.setContentTitle(String.format(context.resources.getString(R.string.hours_cancelled_count), schedule.size))
                        .setContentText(context.resources.getString(R.string.check_app_for_info))
                val notId = sp.getInt("notId", 2)
                spe.putInt("notId", notId + 1).apply()
                notificationManagerCompat.notify(notId, builder.build())
            }
        }
    }

    @Throws(InvalidCodeException::class, NoInternetException::class, UnknownAuthenticationException::class, InvalidWebsiteException::class)
    fun authenticate(website: String, code: String): Boolean {
        if (website.isBlank()) {
            throw InvalidWebsiteException()
        }
        if (code.isBlank()) {
            throw InvalidCodeException()
        }

        if (!Utils.isConnected(context)) {
            throw NoInternetException()
        }

        val token = Api(website).getToken(code)
                ?: throw UnknownAuthenticationException("Error getting API token: Token is null")

        if (token.isBlank()) {
            throw UnknownAuthenticationException("Error getting API token: Token is empty")
        }

        Toast.makeText(context, R.string.auth_success, Toast.LENGTH_LONG).show()

        val spe = sp.edit()
        spe.putString("website", website)
        spe.putString("token", token)
        spe.putBoolean("zermeloSync", true)
        spe.apply()

        return true
    }
}
