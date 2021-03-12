package com.wilco375.roosternotification.activity

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import com.wilco375.roosternotification.R
import com.wilco375.roosternotification.general.Utils
import io.multimoon.colorful.CAppCompatActivity
import io.multimoon.colorful.ThemeColor
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.content_settings.*
import java.util.*
import kotlin.collections.ArrayList


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
        sp = Utils.getSharedPreferences(this)
        var currentColor = Utils.getColorfulColor(sp).name

        showGroupCheckbox.isChecked = sp.getBoolean("group", false)
        showNotificationCheckbox.isChecked = sp.getBoolean("notify", true)
        showCancelledNotificationCheckbox.isChecked = sp.getBoolean("notifyCancel", true)
        showDayScheduleCheckbox.isChecked = sp.getBoolean("notifyDaySchedule", true)
        teacherCheckbox.isChecked = sp.getBoolean("teacher", false)
        teacherFullCheckbox.isChecked = sp.getBoolean("teacherFull", false)
        useDarkModeCheckbox.isChecked = sp.getBoolean("useDarkMode", Utils.isNightModeEnabled(this))
        val themeColors = ThemeColor.values().filter { it != ThemeColor.WHITE && it != ThemeColor.BLACK }
        val colors = themeColors.map { it.name }
        val colorsText = ArrayList<String>()
        for (c in themeColors) {
            colorsText += when (c) {
                ThemeColor.RED -> "Rood"
                ThemeColor.PINK -> "Roze"
                ThemeColor.PURPLE -> "Paars"
                ThemeColor.DEEP_PURPLE -> "Dieppaars"
                ThemeColor.INDIGO -> "Indigo"
                ThemeColor.BLUE -> "Blauw"
                ThemeColor.LIGHT_BLUE -> "Lichtblauw"
                ThemeColor.CYAN -> "Cyaan"
                ThemeColor.TEAL -> "Groenblauw"
                ThemeColor.GREEN -> "Groen"
                ThemeColor.LIGHT_GREEN -> "Lichtgroen"
                ThemeColor.LIME -> "Limoen"
                ThemeColor.YELLOW -> "Geel"
                ThemeColor.AMBER -> "Amber"
                ThemeColor.ORANGE -> "Oranje"
                ThemeColor.DEEP_ORANGE -> "Dieporanje"
                ThemeColor.BROWN -> "Bruin"
                ThemeColor.GREY -> "Grijs"
                ThemeColor.BLUE_GREY -> "Blauwgrijs"
                else -> c.name
                        .toLowerCase(Locale.getDefault())
                        .capitalize(Locale.getDefault())
                        .replace("_", " ")
            }
        }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, colorsText)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        themeColorSpinner.adapter = adapter
        themeColorSpinner.setSelection(colors.indexOf(currentColor))

        showGroupCheckbox.setOnCheckedChangeListener { _, isChecked -> sp.edit().putBoolean("group", isChecked).apply() }

        showNotificationCheckbox.setOnCheckedChangeListener { _, isChecked -> sp.edit().putBoolean("notify", isChecked).apply() }

        showCancelledNotificationCheckbox.setOnCheckedChangeListener { _, isChecked -> sp.edit().putBoolean("notifyCancel", isChecked).apply() }

        showDayScheduleCheckbox.setOnCheckedChangeListener { _, isChecked -> sp.edit().putBoolean("notifyDaySchedule", isChecked).apply() }

        teacherCheckbox.setOnCheckedChangeListener { _, isChecked ->
            sp.edit().putBoolean("teacher", isChecked).apply()
            Utils.updateWidgets(this)
        }

        teacherFullCheckbox.setOnCheckedChangeListener { _, isChecked ->
            sp.edit().putBoolean("teacherFull", isChecked).apply()
            Utils.updateWidgets(this)
        }

        useDarkModeCheckbox.setOnCheckedChangeListener { _, isChecked ->
            sp.edit().putBoolean("useDarkMode", isChecked).apply()
            AppCompatDelegate.setDefaultNightMode(
                    if (isChecked) {
                        AppCompatDelegate.MODE_NIGHT_YES
                    } else {
                        AppCompatDelegate.MODE_NIGHT_NO
                    }
            )
            Utils.updateWidgets(this)
        }

        themeColorSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (Utils.isNightModeEnabled(this@SettingsActivity)) {
                    (adapterView?.getChildAt(0) as TextView?)?.setTextColor(Color.WHITE)
                } else {
                    (adapterView?.getChildAt(0) as TextView?)?.setTextColor(Color.BLACK)
                }
                if (currentColor != colors[position]) {
                    currentColor = colors[position]
                    sp.edit().putString("theme_color", colors[position]).apply()
                    Utils.updateColorful(this@SettingsActivity, colors[position])
                    Utils.updateWidgets(this@SettingsActivity)
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {
            }
        }
    }
}
