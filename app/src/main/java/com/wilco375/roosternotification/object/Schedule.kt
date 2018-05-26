package com.wilco375.roosternotification.`object`

import android.content.Context
import com.wilco375.roosternotification.general.Config
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

class Schedule private constructor(context: Context, val username: String): Serializable {
    private val scheduleDays = ArrayList<ScheduleDay>()
    private var db = context.openOrCreateDatabase("Schedule", Context.MODE_PRIVATE, null)!!

    init {
        db.enableWriteAheadLogging()

        createTables()
    }

    companion object {
        private var instances: ArrayList<Schedule> = ArrayList()

        fun getInstance(context: Context, username: String = "~me"): Schedule {
            if (instances.isEmpty() || !instances.any { it.username == username }) {
                instances.add(Schedule(context, username))
            }
            return instances.first { it.username == username }
        }

        @Throws(IllegalStateException::class)
        fun getInstance() : Schedule {
            if (instances.isEmpty()) {
                throw IllegalStateException("No instances are available and context is needed to create a new instance")
            }
            return instances[0]
        }
    }

    private fun createTables() {
        val lessonTable = "CREATE TABLE IF NOT EXISTS Lesson (" +
                "   instance INTEGER," +
                "   subject TEXT," +
                "   lessonGroup TEXT," +
                "   location TEXT," +
                "   type TEXT," +
                "   cancelled INTEGER," +
                "   start INTEGER," +
                "   end INTEGER," +
                "   timeslot INTEGER," +
                "   day INTEGER," +
                "   username TEXT," +
                "   PRIMARY KEY (instance, username)" +
                ")"
        db.execSQL(lessonTable)

        val notificationTable = "CREATE TABLE IF NOT EXISTS Notification (" +
                "   instance INTEGER," +
                "   type TEXT," +
                "   PRIMARY KEY (instance, type)" +
                ")"
        db.execSQL(notificationTable)

        val namesTable = "CREATE TABLE IF NOT EXISTS Name (" +
                "   code TEXT PRIMARY KEY," +
                "   name TEXT" +
                ")"
        db.execSQL(namesTable)
    }

    /**
     * Gets the schedule for a certain day
     */
    fun getScheduleByDay(day: Date) : ScheduleDay {
        val startOfDay = day.startOfDay().time
        return ScheduleDay(
                db.rawQuery("SELECT * FROM Lesson WHERE start >= $startOfDay AND end < ${startOfDay + 24*3600*1000} AND username == ${username.escape()} ORDER BY start ASC", null),
                day
        )
    }

    /**
     * Adds a new schedule item to the correct day
     * @param item item to add
     */
    fun addScheduleItem(item: ScheduleItem) {
        val date = item.start.toCalendar()
        val day = scheduleDays.firstOrNull { it.day.time == date.timeInMillis || it.day.toCalendar().isOnSameDayAs(date) }
        if(day != null) {
            // Day is already in the list
            day.addItem(item)
            // Set day to same date as other items in that day so it will be faster to match next time
            item.day = day.day
        } else {
            // Day is not in list yet
            val newDay = ScheduleDay(item.start)
            newDay.addItem(item)
            scheduleDays.add(newDay)
        }
    }

    fun save() {
        for (scheduleDay in scheduleDays) {
            val startOfDay = scheduleDay.day.startOfDay().time
            db.execSQL("DELETE FROM Lesson WHERE username = ${username.escape()} AND ((start >= $startOfDay AND end < ${startOfDay + 24*3600*1000}) OR (end < ${startOfDay - Config.SYNC_WINDOW*24*3600*1000L}))")
            for (item in scheduleDay) {
                item.apply {
                    db.execSQL("REPLACE INTO Lesson VALUES ($instance, ${subject.escape()}, ${group.escape()}, ${location.escape()}, ${type.escape()}, ${if(cancelled) 1 else 0}, ${start.time}, ${end.time}, $timeslot, ${day.time}, ${username.escape()})")
                }
            }
        }
        scheduleDays.clear()
    }

    /**
     * Check if the user has been sent a notification yet for a certain item
     */
    fun isCancelledNotified(item: ScheduleItem): Boolean {
        // Check if it has been notified yet
        val cursor = db.rawQuery("SELECT * FROM Notification WHERE instance = ${item.instance} AND type = ${"cancelled".escape()}", null)
        val result = cursor.count > 0
        cursor.close()

        if(!result) {
            // Update database
            db.execSQL("REPLACE INTO Notification VALUES (${item.instance}, ${"cancelled".escape()})")
        }

        return result
    }

    /**
     * Add a name to the database
     * @param code student number or employee code
     * @param name readable name
     */
    fun addName(code: String, name: String) {
        // Update database
        db.execSQL("REPLACE INTO Name VALUES (${code.escape()}, ${name.escape()})")
    }

    fun getNames(): List<Pair<String, String>> {
        val cursor = db.rawQuery("SELECT * FROM Name ORDER BY code ASC", null)
        val result = ArrayList<Pair<String, String>>()
        while (cursor.moveToNext()) {
            result.add(Pair(cursor.getString(0), cursor.getString(1)))
        }
        cursor.close()
        return result
    }

    operator fun plusAssign(item: ScheduleItem) = addScheduleItem(item)

    operator fun get(day: Date) = getScheduleByDay(day)

    operator fun iterator() : Iterator<ScheduleItem>
            = getAllScheduleItems().iterator()

    fun getAllScheduleItems() : List<ScheduleItem>
        = scheduleDays.map { it.getItems() }.flatten()
}
