package com.wilco375.roosternotification.online

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.core.app.NotificationManagerCompat
import com.wilco375.roosternotification.R
import com.wilco375.roosternotification.`object`.Schedule
import com.wilco375.roosternotification.`object`.ScheduleItem
import com.wilco375.roosternotification.`object`.items
import com.wilco375.roosternotification.activity.MainActivity
import com.wilco375.roosternotification.exception.InvalidCodeException
import com.wilco375.roosternotification.exception.InvalidWebsiteException
import com.wilco375.roosternotification.exception.NoInternetException
import com.wilco375.roosternotification.exception.UnknownAuthenticationException
import com.wilco375.roosternotification.general.Config
import com.wilco375.roosternotification.general.Utils
import cz.msebera.android.httpclient.NameValuePair
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity
import cz.msebera.android.httpclient.client.methods.HttpGet
import cz.msebera.android.httpclient.client.methods.HttpPost
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder
import cz.msebera.android.httpclient.message.BasicNameValuePair
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class ZermeloSync {
    lateinit var sp: SharedPreferences

    fun syncZermelo(context: Context, updateMainActivity: Boolean = false, username: String = "~me",copyClipboard: Boolean = false) {
        if (!Utils.isWifiConnected(context) && !updateMainActivity) return

        Thread({
            // List of all the lessons that have been cancelled
            val cancelledItems = ArrayList<ScheduleItem>()

            // Get start of this week in unix
            val start = Utils.unixStartOfWeek() - Config.SYNC_WINDOW * 24 * 3600
            // Set end two weeks later
            val end = start + 2 * Config.SYNC_WINDOW * 24 * 3600

            println("Getting JSON between $start and $end")

            // Get schedule string
            sp = context.getSharedPreferences("Main", Context.MODE_PRIVATE)
            val scheduleString = getScheduleString(sp.getString("website", ""),
                    username,
                    sp.getString("token", ""),
                    start, end) ?: return@Thread

            println("Received JSON: $scheduleString")

            // If necessary copy string to clipboard
            if (copyClipboard && context is Activity)
                Utils.copyText(context, context, context.getResources().getString(R.string.schedule_json), scheduleString, true)

            try {
                // Format to JSONArray
                val jsonSchedule = JSONObject(scheduleString).getJSONObject("response").getJSONArray("data")
                val schedule = Schedule.getInstance(context, username)

                // Loop trough all lessons and create an object array with all lessons
                jsonSchedule.items().map { ScheduleItem(it as JSONObject) }.forEach {
                    schedule += it
                    if(it.cancelled && username == "~me") {
                        cancelledItems.add(it)
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
        }).start()
    }

    private fun cancelNotification(schedule: List<ScheduleItem>, context: Context) {
        if(schedule.isEmpty()) return

        if (sp.getBoolean("notifyCancel", true)) {
            val builder = Utils.getNotificationBuilder(context, Utils.CURRENT_SCHEDULE)
                    .setSmallIcon(R.drawable.notification)
                    .setContentIntent(PendingIntent.getActivity(context, 0, Intent(context, MainActivity::class.java), 0))
            val spe = sp.edit()
            val notificationManagerCompat = NotificationManagerCompat.from(context)

            if(schedule.size < 5) {
                for (item in schedule) {
                    // Under 5 cancelled lessons, show separate notifications
                    builder.setContentTitle(String.format(context.resources.getString(R.string.hour_cancelled_on), item.getDay().toLowerCase()))

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

    @Nullable
    private fun getScheduleString(website: String, username: String, token: String, start: Long, end: Long): String? {
        try {
            val client = HttpClientBuilder.create().build()
            val url = "https://$website/api/v2/appointments?user=$username&start=$start&end=$end&valid=true&fields=appointmentInstance,subjects,cancelled,locations,startTimeSlot,start,end,groups,type&access_token=$token"
            val get = HttpGet(url)
            val response = client.execute(get)

            if (response.statusLine.statusCode == 200) {
                val br = BufferedReader(InputStreamReader((response.entity.content)))
                return br.readLine()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    companion object {
        private fun intStr(integer: Int): String {
            return (integer).toString()
        }

        @Throws(InvalidCodeException::class, NoInternetException::class, UnknownAuthenticationException::class, InvalidWebsiteException::class)
        fun authenticate(website: String, code: String, context: Context, sp: SharedPreferences): Boolean {
            if (website == "") {
                throw InvalidWebsiteException()
            }
            if (code == "") {
                throw InvalidCodeException()
            }

            if (!Utils.isConnected(context)) {
                throw NoInternetException()
            }

            try {
                val client = HttpClientBuilder.create().build()

                val post = HttpPost("https://$website/api/v2/oauth/token?")

                val nameValuePair = ArrayList<NameValuePair>(2)
                nameValuePair.add(BasicNameValuePair("grant_type", "authorization_code"))
                nameValuePair.add(BasicNameValuePair("code", code))

                post.setEntity(UrlEncodedFormEntity(nameValuePair))
                val response = client.execute(post)
                val statusCode = response.getStatusLine().getStatusCode()
                if (statusCode != 200) {
                    if (statusCode == 404) {
                        throw InvalidWebsiteException()
                    } else {
                        throw InvalidCodeException()
                    }
                }

                val tokenJson = JSONObject(BufferedReader(InputStreamReader((response.getEntity().getContent()))).readLine())
                val token = tokenJson.getString("access_token")
                        ?: throw UnknownAuthenticationException("Error getting API token: Token is null")

                if (token == "") {
                    throw UnknownAuthenticationException("Error getting API token: Token is empty")
                }
                Toast.makeText(context, R.string.auth_success, Toast.LENGTH_LONG).show()

                val spe = sp.edit()
                spe.putString("website", website)
                spe.putString("token", token)
                spe.putBoolean("zermeloSync", true)
                spe.apply()
                return true
            } catch (e: JSONException) {
                e.printStackTrace()
                throw UnknownAuthenticationException("Error getting API token: JSON is invalid: " + e.message)
            } catch (e: IOException) {
                e.printStackTrace()
                throw UnknownAuthenticationException("Error getting API token: IOException: " + e.message)
            }
        }
    }
}
