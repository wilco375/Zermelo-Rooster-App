package com.wilco375.roosternotification

import android.app.Application
import io.multimoon.colorful.*

class RoosterApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val defaults = Defaults(
                primaryColor = ThemeColor.BLUE,
                accentColor = ThemeColor.BLUE,
                useDarkTheme = false,
                translucent = false)
        initColorful(this, defaults)
    }
}