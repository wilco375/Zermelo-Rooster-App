package com.wilco375.roosternotification.`object`

import java.util.*

/**
 * Checks if two calendar dates are on the same day
 * @param other calendar date to compare to
 * @return true if calendar dates are on the same day
 */
fun Calendar.isOnSameDayAs(other: Calendar) : Boolean {
    return this.get(Calendar.YEAR) == other.get(Calendar.YEAR) &&
            this.get(Calendar.DAY_OF_YEAR) == other.get(Calendar.DAY_OF_YEAR)
}