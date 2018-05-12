package com.wilco375.roosternotification.`object`

import android.content.SharedPreferences
import com.wilco375.roosternotification.general.Utils
import org.json.JSONException
import org.json.JSONObject
import java.io.Serializable
import java.util.*

class ScheduleItem : Serializable {
    val subject: String
    val group: String
    val location: String
    val type: String
    val cancelled: Boolean
    val start: Date
    val end: Date
    val timeslot: Int
    // Date on the same day as start
    var day: Date

    @Throws(JSONException::class)
    constructor(jsonObject: JSONObject) {
        //Subject
        val subjectsArray = jsonObject.getJSONArray("subjects")
        subject = subjectsArray.items().joinToString("/") { it -> it.toString().toUpperCase() }

        //Group
        val groupsArray = jsonObject.getJSONArray("groups")
        group = groupsArray.items().joinToString("/") { it -> it.toString().toUpperCase() }

        //Location
        val locationsArray = jsonObject.getJSONArray("locations")
        location = locationsArray.items().joinToString("/") { it -> it.toString() }

        // Type
        type = jsonObject.getString("type")

        // Cancelled
        cancelled = jsonObject.takeUnless { jsonObject.isNull("cancelled") }?.getBoolean("cancelled") ?: false

        // Start
        start = Utils.unixToCalendar(jsonObject.getLong("start")).time
        day = start

        // End
        end = Utils.unixToCalendar(jsonObject.getLong("end")).time

        // Timeslot
        timeslot = jsonObject.takeUnless { jsonObject.isNull("startTimeSlot") }?.getInt("startTimeSlot") ?: 0
    }

    constructor(subject: String, group: String, location: String, type: String, cancelled: Boolean, start: Date, end: Date, timeslot: Int) {
        this.subject = subject
        this.group = group
        this.location = location
        this.type = type
        this.cancelled = cancelled
        this.start = start
        this.day = start
        this.end = end
        this.timeslot = timeslot
    }

    /**
     * Returns a human readable day of the week on which the scheduled item takes place
     */
    fun getDay() : String
        = Utils.dayIntToStr(day.toCalendar().get(Calendar.DAY_OF_WEEK))

    /**
     * Checks if the scheduled item takes place this week
     */
    fun isThisWeek() = day.isThisWeek()

    fun getSummary(sp: SharedPreferences): String {
        var info = ""
        if (subject != "") info = getSubjectAndGroup(sp)
        if (type != "Les") info += " ($type)"
        if (location != "") info += " - $location"
        return info
    }

    fun getSubjectAndGroup(sp: SharedPreferences): String {
        return if (sp.getBoolean("group", false) && this.group != "")
            this.subject + "-" + this.group
        else
            this.subject
    }

    override fun toString(): String {
        return "$timeslot. $subject - $location"
    }


}
