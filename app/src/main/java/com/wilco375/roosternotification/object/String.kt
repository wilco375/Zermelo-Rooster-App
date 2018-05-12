package com.wilco375.roosternotification.`object`

import android.database.DatabaseUtils

/**
 * SQL escapes a string
 * @return SQL escaped string
 */
fun String.escape(): String {
    return DatabaseUtils.sqlEscapeString(this)
}