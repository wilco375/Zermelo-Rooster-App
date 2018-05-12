package com.wilco375.roosternotification.general

import android.content.Context
import com.wilco375.roosternotification.`object`.Schedule
import java.io.*

object ScheduleHandler {
    /**
     * Save the schedule
     */
    fun getSchedule(context: Context): Schedule {
        return readSchedule(context)
    }

    /**
     * Update the schedule
     */
    fun setSchedule(context: Context, schedule: Schedule) {
        writeSchedule(context, schedule)
    }

    /**
     * Write schedule object to storage
     */
    private fun writeSchedule(context: Context, schedule: Schedule) {
        // Don't update the schedule if the new one is empty
        if(schedule.getAllScheduleItems().isEmpty()) return

        try {
            val file = File("${context.filesDir}/schedule")
            if (file.exists() && file.delete() && file.createNewFile()) {
                val fos = FileOutputStream(file)
                val oos = ObjectOutputStream(fos)
                oos.writeObject(schedule)
                oos.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    /**
     * Get schedule object from storage
     */
    private fun readSchedule(context: Context): Schedule {
        try {
            val file = File("${context.filesDir}/schedule")
            if (!file.exists()) return Schedule()
            val fis = FileInputStream(file)
            val ois = ObjectInputStream(fis)
            val obj = ois.readObject()
            if (obj is Schedule) {
                return obj
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }

        return Schedule()
    }
}
