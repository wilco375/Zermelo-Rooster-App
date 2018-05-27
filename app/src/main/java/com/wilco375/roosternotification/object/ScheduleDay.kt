package com.wilco375.roosternotification.`object`

import android.database.Cursor
import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable
import java.util.*

class ScheduleDay : Serializable, Parcelable {
    private val items = ArrayList<ScheduleItem>()
    var day: Date = Date()
        private set

    constructor(day: Date = Date()) {
        this.day = day
    }

    constructor(parcel: Parcel) {
        day = Date(parcel.readLong())
        parcel.readList(items, ScheduleItem::class.java.classLoader)
    }

    constructor(cursor: Cursor, day: Date) {
        if (!cursor.isClosed && cursor.moveToNext()) {
            do {
                try {
                    items.add(ScheduleItem(cursor))
                } catch (e: IllegalStateException) {
                    e.printStackTrace()
                }
            } while (cursor.moveToNext())
            this.day = day
        } else {
            this.day = day
        }
        cursor.close()
    }

    fun addItem(scheduleItem: ScheduleItem) {
        if (isAccessedBy(Schedule::class.java)) {
            items.add(scheduleItem)
            items.sortBy { it.start }
        } else {
            throw IllegalAccessError("Only Schedule is allowed to add items")
        }
    }

    private fun isAccessedBy(`class`: Class<*>): Boolean {
        val stackTrace = Thread.currentThread().stackTrace
        return !stackTrace.none { it.className == `class`.name }
    }

    fun getItems(): List<ScheduleItem> {
        return Collections.unmodifiableList(items)
    }

    operator fun iterator(): Iterator<ScheduleItem> = getItems().iterator()

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(day.time)
        parcel.writeList(items)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ScheduleDay> {
        override fun createFromParcel(parcel: Parcel): ScheduleDay {
            return ScheduleDay(parcel)
        }

        override fun newArray(size: Int): Array<ScheduleDay?> {
            return arrayOfNulls(size)
        }
    }
}
