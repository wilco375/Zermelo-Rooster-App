package com.wilco375.roosternotification.receiver

import android.app.AlarmManager
import android.app.IntentService
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

class AutoStartUp : IntentService("AutoStartUp") {

    override fun onHandleIntent(intent: Intent?) {
        AlarmReceiver.createNotification(this)
        val alarmManager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager?
        val i = Intent(baseContext, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, i, PendingIntent.FLAG_CANCEL_CURRENT)
        alarmManager?.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 300000, pendingIntent)
    }
}
