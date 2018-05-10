package com.wilco375.roosternotification.activity

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import com.wilco375.roosternotification.R
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

        showGroupCheckbox.setOnCheckedChangeListener({ _, isChecked -> sp.edit().putBoolean("group", isChecked).apply() })

        showNotificationCheckbox.setOnCheckedChangeListener({ _, isChecked -> sp.edit().putBoolean("notify", isChecked).apply() })

        showCancelledNotificationCheckbox.setOnCheckedChangeListener({ _, isChecked -> sp.edit().putBoolean("notifyCancel", isChecked).apply() })

        showDayScheduleCheckbox.setOnCheckedChangeListener({ _, isChecked -> sp.edit().putBoolean("notifyDaySchedule", isChecked).apply() })
    }
}
