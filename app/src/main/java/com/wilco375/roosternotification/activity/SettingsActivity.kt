package com.wilco375.roosternotification.activity

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import com.wilco375.roosternotification.R
import com.wilco375.roosternotification.general.Utils
import io.multimoon.colorful.CAppCompatActivity
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.content_settings.*

class SettingsActivity : CAppCompatActivity() {

    lateinit var sp: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(toolbar)

        // Show back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupSettings()
    }

    /**
     * Save settings to SharedPreferences when changed
     */
    private fun setupSettings() {
        sp = getSharedPreferences("Main", Context.MODE_PRIVATE)

        showGroupCheckbox.isChecked = sp.getBoolean("group", false)
        showNotificationCheckbox.isChecked = sp.getBoolean("notify", true)
        showCancelledNotificationCheckbox.isChecked = sp.getBoolean("notifyCancel", true)
        showDayScheduleCheckbox.isChecked = sp.getBoolean("notifyDaySchedule", true)
        teacherCheckbox.isChecked = sp.getBoolean("teacher", false)
        teacherFullCheckbox.isChecked = sp.getBoolean("teacherFull", false)
        useDarkModeCheckbox.isChecked = sp.getBoolean("useDarkMode", Utils.isNightModeEnabled(this))

        showGroupCheckbox.setOnCheckedChangeListener { _, isChecked -> sp.edit().putBoolean("group", isChecked).apply() }

        showNotificationCheckbox.setOnCheckedChangeListener { _, isChecked -> sp.edit().putBoolean("notify", isChecked).apply() }

        showCancelledNotificationCheckbox.setOnCheckedChangeListener { _, isChecked -> sp.edit().putBoolean("notifyCancel", isChecked).apply() }

        showDayScheduleCheckbox.setOnCheckedChangeListener { _, isChecked -> sp.edit().putBoolean("notifyDaySchedule", isChecked).apply() }

        teacherCheckbox.setOnCheckedChangeListener { _, isChecked -> sp.edit().putBoolean("teacher", isChecked).apply() }

        teacherFullCheckbox.setOnCheckedChangeListener { _, isChecked -> sp.edit().putBoolean("teacherFull", isChecked).apply() }

        useDarkModeCheckbox.setOnCheckedChangeListener { _, isChecked ->
            sp.edit().putBoolean("useDarkMode", isChecked).apply()
            AppCompatDelegate.setDefaultNightMode(
                    if (isChecked) {
                        AppCompatDelegate.MODE_NIGHT_YES
                    } else {
                        AppCompatDelegate.MODE_NIGHT_NO
                    }
            )
        }
    }
}
