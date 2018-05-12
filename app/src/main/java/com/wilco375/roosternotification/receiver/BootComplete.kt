package com.wilco375.roosternotification.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

import com.wilco375.roosternotification.general.Utils

class BootComplete : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (Intent.ACTION_BOOT_COMPLETED == intent.action || "START_ALARM" == intent.action) {
            Utils.setAlarm(context)
        }
    }
}
