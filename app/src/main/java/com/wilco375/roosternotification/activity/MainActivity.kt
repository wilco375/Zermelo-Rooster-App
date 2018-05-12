package com.wilco375.roosternotification.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.StrictMode
import android.view.*
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.wilco375.roosternotification.R
import com.wilco375.roosternotification.R.layout.activity_main
import com.wilco375.roosternotification.`object`.Schedule
import com.wilco375.roosternotification.`object`.ScheduleDay
import com.wilco375.roosternotification.general.ScheduleHandler
import com.wilco375.roosternotification.general.ScheduleListAdapter
import com.wilco375.roosternotification.general.Utils
import com.wilco375.roosternotification.online.ZermeloSync
import io.multimoon.colorful.CAppCompatActivity
import io.multimoon.colorful.Colorful
import io.multimoon.colorful.ThemeColor
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : CAppCompatActivity() {
    private var syncing = false

    private lateinit var sp: SharedPreferences
    private lateinit var schedule: Schedule
    private lateinit var schedulePagerAdapter: SchedulePagerAdapter
    private lateinit var scheduleViewPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sendBroadcast(Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME))
        setContentView(activity_main)
        setSupportActionBar(toolbar)

        Colorful().edit().setPrimaryColor(ThemeColor.RED)

        //Allow internet
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().permitAll().build())

        //Get SharedPreferences
        sp = getSharedPreferences("Main", Context.MODE_PRIVATE)

        //Set alarm
        Utils.setAlarm(this)

        checkInit()

        getSchedule()

        //setupNavigation()
    }

    /**
     * Check if first launch or first sync
     */
    private fun checkInit() {
        if (sp.getString("token", "") == "" || sp.getString("website", "") == "") {
            val i = Intent(this@MainActivity, InitActivity::class.java)
            finish()
            startActivity(i)
        } else if (sp.getBoolean("firstSync", true)) {
            syncSchedule()

            sp.edit().putBoolean("firstSync", false).apply()
        }
    }

    /**
     * Get schedule from storage
     */
    fun getSchedule() {
        runOnUiThread({
            schedule = ScheduleHandler.getSchedule(this@MainActivity)

            setupSchedule()
        })
    }

    /**
     * Show schedule in app
     */
    private fun setupSchedule() {
        schedulePagerAdapter = SchedulePagerAdapter(supportFragmentManager, schedule)
        scheduleViewPager = findViewById(R.id.scheduleViewPager)
        scheduleViewPager.adapter = schedulePagerAdapter
    }

    /**
     * Sync schedule with Zermelo
     */
    private fun syncSchedule() {
        syncing = true
        ZermeloSync().syncZermelo(application, true, false)
        Toast.makeText(application, "Rooster aan het synchroniseren...", Toast.LENGTH_LONG).show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.settings) {
            // Launch SettingsActivity
            val i = Intent(this@MainActivity, SettingsActivity::class.java)
            startActivity(i)

            return true
        } else if (item.itemId == R.id.zermelo_sync) {
            // Sync schedule
            syncSchedule()

            return true
        } else {
            return super.onOptionsItemSelected(item)
        }
    }

    override fun onPause() {
        Utils.updateWidgets(this)
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        setupSchedule()
    }

    class SchedulePagerAdapter(fragmentManager: FragmentManager, val schedule: Schedule) : FragmentStatePagerAdapter(fragmentManager) {
        override fun getItem(position: Int): Fragment {
            println("Getting item at $position")
            val fragment = ScheduleFragment()
            fragment.arguments = Bundle().apply {
                val scheduleDay = schedule[Date(System.currentTimeMillis() + position * 24 * 60 * 60 * 1000)]
                putParcelable("schedule", scheduleDay)
            }
            return fragment
        }

        override fun getCount(): Int {
            return 14
        }
    }

    class ScheduleFragment : Fragment() {
        @SuppressLint("SetTextI18n")
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            val rootView: View = inflater.inflate(R.layout.fragment_schedule, container, false)
            arguments?.takeIf { it.containsKey("schedule") }?.apply {
                val scheduleDay = getParcelable<ScheduleDay>("schedule")

                val dayFormatter = SimpleDateFormat("dd-MM", Locale.getDefault())
                rootView.findViewById<TextView>(R.id.dayText).text =
                        Utils.dayIntToStr(Calendar.getInstance().also { it.time = scheduleDay.day }.get(Calendar.DAY_OF_WEEK)) +
                        " " + dayFormatter.format(scheduleDay.day)

                rootView.findViewById<ListView>(R.id.dayListView).adapter =
                        ScheduleListAdapter(scheduleDay,
                                context!!.getSharedPreferences("Main", Context.MODE_PRIVATE),
                                context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
            }
            return rootView
        }
    }
}
