package com.wilco375.roosternotification.`object`

import java.io.Serializable
import java.util.*

class Schedule : Serializable {
    private val scheduleDays = ArrayList<ScheduleDay>()

    /**
     * Gets the schedule for a certain day
     */
    fun getScheduleByDay(day: Date) : ScheduleDay {
        return scheduleDays.firstOrNull { it -> it.day.isOnSameDayAs(day) } ?: ScheduleDay()
    }

    /**
     * Adds a new schedule item to the correct day
     * @param item item to add
     */
    fun addScheduleItem(item: ScheduleItem) {
        val date = item.start.toCalendar()
        val day = scheduleDays.firstOrNull { it -> it.day.time == date.timeInMillis || it.day.toCalendar().isOnSameDayAs(date) }
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

    operator fun plusAssign(item: ScheduleItem) = addScheduleItem(item)

    operator fun get(day: Date) = getScheduleByDay(day)

    operator fun iterator() : Iterator<ScheduleItem>
            = getAllScheduleItems().iterator()

    fun getAllScheduleItems() : List<ScheduleItem>
        = scheduleDays.map { it -> it.items }.flatten()
}
