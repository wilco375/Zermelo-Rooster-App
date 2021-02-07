package com.wilco375.roosternotification.online

import com.wilco375.roosternotification.BuildConfig
import org.json.JSONArray

interface Api {
    fun getSchedule(token: String, username: String, start: Long, end: Long): JSONArray?

    fun getNames(token: String, schools: Set<String>): Map<String, String>

    fun getToken(code: String): String?
}

fun Api(domain: String): Api {
    return if (BuildConfig.DEBUG) {
        MockApi()
    } else {
        LiveApi(domain)
    }
}