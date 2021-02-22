package com.wilco375.roosternotification

import android.app.Application
import com.wilco375.roosternotification.general.Utils
import io.multimoon.colorful.Defaults
import io.multimoon.colorful.ThemeColor
import io.multimoon.colorful.initColorful

class RoosterApp : Application() {
    override fun onCreate() {
        super.onCreate()

        val defaults = Defaults(
                primaryColor = ThemeColor.BLUE,
                accentColor = ThemeColor.BLUE,
                useDarkTheme = Utils.isNightModeEnabled(this),
                translucent = false)
        initColorful(this, defaults)
        Utils.updateColorful(this)
    }
}