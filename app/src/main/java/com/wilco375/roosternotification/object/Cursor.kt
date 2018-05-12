package com.wilco375.roosternotification.`object`

import android.database.Cursor
import java.util.*

fun Cursor.getString(column: String): String {
    return this.getString(this.getColumnIndex(column))
}

fun Cursor.getLong(column: String): Long {
    return this.getLong(this.getColumnIndex(column))
}

fun Cursor.getInt(column: String): Int {
    return this.getInt(this.getColumnIndex(column))
}

fun Cursor.getBoolean(column: String): Boolean {
    return this.getInt(this.getColumnIndex(column)) != 0
}

fun Cursor.getDate(column: String): Date {
    return Date(this.getLong(column))
}