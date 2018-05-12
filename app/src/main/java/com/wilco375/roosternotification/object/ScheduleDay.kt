package com.wilco375.roosternotification.`object`

import java.io.Serializable
import java.util.*

class ScheduleDay(val day: Date = Date()) : Serializable {
    val items = ArrayList<ScheduleItem>()

    fun addItem(scheduleItem: ScheduleItem) {
        items.add(scheduleItem)
        items.sortBy { it -> it.start }
    }

    operator fun iterator() : Iterator<ScheduleItem>
            = items.iterator()
}
