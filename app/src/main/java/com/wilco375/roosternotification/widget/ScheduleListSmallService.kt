package com.wilco375.roosternotification.widget

import android.content.Intent
import android.widget.RemoteViewsService

class ScheduleListSmallService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        println("Widget Remote Views Service called. Returning factory")
        return ScheduleListSmallFactory(this.applicationContext)
    }
}