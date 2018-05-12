package com.wilco375.roosternotification.`object`

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable
import java.util.*

class ScheduleDay(val day: Date = Date()) : Serializable, Parcelable {
    val items = ArrayList<ScheduleItem>()

    constructor(parcel: Parcel) : this(Date(parcel.readLong())) {
        parcel.readList(items, ScheduleItem::class.java.classLoader)
    }

    fun addItem(scheduleItem: ScheduleItem) {
        items.add(scheduleItem)
        items.sortBy { it -> it.start }
    }

    operator fun iterator() : Iterator<ScheduleItem>
            = items.iterator()

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
