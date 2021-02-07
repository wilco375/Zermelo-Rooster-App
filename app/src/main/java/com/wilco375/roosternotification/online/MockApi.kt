package com.wilco375.roosternotification.online

import com.wilco375.roosternotification.general.Utils
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.HashMap
import kotlin.random.Random

class MockApi : Api {
    override fun getSchedule(token: String, username: String, start: Long, end: Long): JSONArray? {
        val schedule = JSONArray()
        val startOfWeek = Utils.unixStartOfWeek() * 1000
        for (day in (0 until 5)) {
            val startOfDay = startOfWeek + day*24*3600*1000
            for (hour in (0 until 8)) {
                val startOfAppointment = Calendar.getInstance().apply {
                    timeInMillis = startOfDay
                    set(Calendar.HOUR_OF_DAY, 9+hour)
                }.timeInMillis / 1000
                schedule.put(JSONObject("""
                    {
                        "start": ${startOfAppointment},
                        "end": ${startOfAppointment + 3600},
                        "startTimeSlot": ${hour + 1},
                        "cancelled": false,
                        "branchOfSchool": 0,
                        "appointmentInstance": ${day}${hour},
                        "type": "lesson",
                        "subjects": [
                            "${getRandomString(2)}"
                        ],
                        "teachers": [
                            "${arrayOf("ac", "kf", "ma", "st").random()}"
                        ],
                        "groups": [
                            "h1a"
                        ],
                        "locations": [
                            "${Random.nextInt(100, 300)}"
                        ]
                    }
                """.trimIndent()))
            }
        }
        return schedule
    }

    override fun getNames(token: String, schools: Set<String>): Map<String, String> {
        val names = HashMap<String, String>()
        names["ac"] = "Alyce Clarice"
        names["kf"] = "Kane Francine"
        names["ma"] = "Madisyn Adrian"
        names["st"] = "Sheri Terell"
        return names
    }

    override fun getToken(code: String): String {
        return getRandomString(26, true)
    }

    /**
     * Generate a random string with a specified length
     * @param length Length of the random string.
     * @param alphaNumeric If true, a string in the character range a-z, 0-9 will be generated. If false, only a-z will be used.
     */
    private fun getRandomString(length: Int, alphaNumeric: Boolean = false): String {
        val charPool: MutableList<Char> = ('a'..'z').toMutableList()
        if (alphaNumeric) {
            charPool.addAll(('0'..'9'))
        }

        return (1..length)
                .map { Random.nextInt(0, charPool.size) }
                .map(charPool::get)
                .joinToString("")
    }
}