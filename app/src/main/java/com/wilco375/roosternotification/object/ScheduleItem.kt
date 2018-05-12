package com.wilco375.roosternotification.`object`

import android.content.SharedPreferences
import android.database.Cursor
import android.os.Parcel
import android.os.Parcelable
import com.wilco375.roosternotification.general.Utils
import org.json.JSONException
import org.json.JSONObject
import java.io.Serializable
import java.util.*

class ScheduleItem : Serializable, Parcelable {
    val subject: String
    val group: String
    val location: String
    val type: String
    get() {
        return when (field) {
            "lesson" -> "Les"
            "exam" -> "Toets"
            "activity" -> "Activiteit"
            "choice" -> "Keuze"
            "talk" -> "Gesprek"
            "other" -> "Anders"
            else -> "Onbekend"
        }
    }
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

    constructor(cursor: Cursor) {
        if(!cursor.isClosed) {
            subject = cursor.getString("subject")
            group = cursor.getString("lessonGroup")
            location = cursor.getString("location")
            type = cursor.getString("type")
            cancelled = cursor.getBoolean("cancelled")
            start = cursor.getDate("start")
            end = cursor.getDate("end")
            timeslot = cursor.getInt("timeslot")
            day = cursor.getDate("day")
        } else {
            throw IllegalArgumentException("Cursor is closed")
        }
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

    constructor(parcel: Parcel) {
        subject = parcel.readString()
        group = parcel.readString()
        location = parcel.readString()
        type = parcel.readString()
        cancelled = parcel.readByte() > 0
        start = Date(parcel.readLong())
        end = Date(parcel.readLong())
        timeslot = parcel.readInt()
        day = Date(parcel.readLong())
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(subject)
        parcel.writeString(group)
        parcel.writeString(location)
        parcel.writeString(type)
        parcel.writeByte(if(cancelled) 1 else 0)
        parcel.writeLong(start.time)
        parcel.writeLong(end.time)
        parcel.writeInt(timeslot)
        parcel.writeLong(day.time)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ScheduleItem> {
        override fun createFromParcel(parcel: Parcel): ScheduleItem {
            return ScheduleItem(parcel)
        }

        override fun newArray(size: Int): Array<ScheduleItem?> {
            return arrayOfNulls(size)
        }
    }
}
