package com.wilco375.roosternotification.`object`

import android.database.DatabaseUtils

/**
 * SQL escapes a string
 * @return SQL escaped string
 */
fun String.escape(): String {
    return DatabaseUtils.sqlEscapeString(this)
}

fun String.replaceLast(substring: String, replacement: String): String {
    val index = this.lastIndexOf(substring)
    if (index == -1) return this
    return this.substring(0, index) + replacement + this.substring(index + substring.length)
}