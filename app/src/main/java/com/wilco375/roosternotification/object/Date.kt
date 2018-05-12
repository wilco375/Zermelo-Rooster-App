package com.wilco375.roosternotification.`object`

import com.wilco375.roosternotification.general.Utils
import java.util.*

/**
 * Checks if two dates are on the same day
 * @param other date to compare to
 * @return true if dates are on the same day
 */
fun Date.isOnSameDayAs(other: Date) : Boolean {
    return this.toCalendar().isOnSameDayAs(other.toCalendar())
}

/**
 * Returns a calendar with this date as the time
 * @return calendar
 */
fun Date.toCalendar() : Calendar {
    val calendar = Calendar.getInstance()
    calendar.time = this
    return calendar
}

/**
 * Checks if the date is in the current week
 */
fun Date.isThisWeek() : Boolean {
    val time = this.time
    return time >= Utils.getUnixStartOfWeek() && time <= Utils.getUnixEndOfWeek()
}