package com.wilco375.roosternotification

import android.app.Application
import android.content.Context
import io.multimoon.colorful.*

class RoosterApp : Application() {
    override fun onCreate() {
        super.onCreate()

        var color = ThemeColor.BLUE
        val website = getSharedPreferences("Main", Context.MODE_PRIVATE).getString("website", "")
        if (website.startsWith("candea")) {
            color = ThemeColor.ORANGE
        } else if (website.startsWith("jpthijsse")) {
            color = ThemeColor.YELLOW
        }
        val defaults = Defaults(
                primaryColor = color,
                accentColor = color,
                useDarkTheme = false,
                translucent = false)
        initColorful(this, defaults)
    }
}