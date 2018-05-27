package com.wilco375.roosternotification.`object`

import org.json.JSONArray

operator fun JSONArray.iterator(): Iterator<Any> = (0 until length()).asSequence().map { get(it) }.iterator()

fun JSONArray.items(): List<Any> = (0 until length()).asSequence().map { get(it) }.toList()