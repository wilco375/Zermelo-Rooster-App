package com.wilco375.roosternotification.activity

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.StrictMode
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.textfield.TextInputEditText
import com.wilco375.roosternotification.R
import com.wilco375.roosternotification.R.layout.activity_main
import com.wilco375.roosternotification.`object`.Schedule
import com.wilco375.roosternotification.`object`.ScheduleDay
import com.wilco375.roosternotification.general.Config
import com.wilco375.roosternotification.general.ScheduleListAdapter
import com.wilco375.roosternotification.general.Utils
import com.wilco375.roosternotification.online.ZermeloSync
import io.multimoon.colorful.CAppCompatActivity
import io.multimoon.colorful.Colorful
import io.multimoon.colorful.ThemeColor
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : CAppCompatActivity() {
    private var syncing = false
    private var username = "~me"

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
        sp.edit().putString("token", "u35aav1qu355jhbtcbeghv538b").putString("website", "jfc.zportal.nl").apply() // STOPSHIP
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
            schedule = Schedule.getInstance(this@MainActivity, username)

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
        scheduleViewPager.currentItem = Config.SYNC_WINDOW
        today.hide()
        today.setOnClickListener {
            scheduleViewPager.currentItem = Config.SYNC_WINDOW
        }
        scheduleViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                if (position != Config.SYNC_WINDOW) {
                    today.show()
                } else {
                    today.hide()
                }
            }
        })
        schedulePagerTitleStrip.setOnClickListener {
            val now = Calendar.getInstance()
            val datePicker = DatePickerDialog(this, DatePickerDialog.OnDateSetListener {
                _, selectedYear, selectedMonth, selectedDay ->
                run {
                    val calendar = Calendar.getInstance()
                    calendar.set(selectedYear, selectedMonth, selectedDay)

                    val today = Utils.currentScheduleDate()
                    scheduleViewPager.currentItem = ((calendar.time.time - today.time) / (24 * 3600 * 1000)).toInt() + Config.SYNC_WINDOW
                }
            }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH))
            datePicker.show()
        }
    }

    private fun showUserDialog() {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_user, LinearLayout(this))
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle(R.string.user)
        dialog.setView(view)
        dialog.setPositiveButton(android.R.string.ok) { _, _ ->
            val user = view.findViewById<TextInputEditText>(R.id.userId).text.toString()
            this.username = user
            syncSchedule()
            getSchedule()
        }
        dialog.setNegativeButton(R.string.me) { _, _ ->
            this.username = "~me"
            getSchedule()
        }
        dialog.show()
    }

    /**
     * Sync schedule with Zermelo
     */
    private fun syncSchedule() {
        syncing = true
        ZermeloSync().syncZermelo(this, true, username)
        Toast.makeText(this, "Rooster aan het synchroniseren...", Toast.LENGTH_LONG).show()
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
        } else if(item.itemId == R.id.user) {
            showUserDialog()

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
            val fragment = ScheduleFragment()
            fragment.arguments = Bundle().apply {
                val scheduleDay = schedule[Date(Utils.currentScheduleDate().time + (position - Config.SYNC_WINDOW) * 24 * 60 * 60 * 1000)]
                putParcelable("schedule", scheduleDay)
            }
            return fragment
        }

        override fun getCount(): Int {
            return 60
        }

        override fun getPageTitle(position: Int): CharSequence? {
            val date = Date(Utils.currentScheduleDate().time + (position - Config.SYNC_WINDOW) * 24 * 60 * 60 * 1000)
            return dateToText(date)
        }
    }

    class ScheduleFragment : Fragment() {
        @SuppressLint("SetTextI18n")
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            val rootView: View = inflater.inflate(R.layout.fragment_schedule, container, false)
            arguments?.takeIf { it.containsKey("schedule") }?.apply {
                val scheduleDay = getParcelable<ScheduleDay>("schedule")

                if (!scheduleDay.getItems().isEmpty()) {
                    rootView.findViewById<TextView>(R.id.noLessons).visibility = View.GONE

                    rootView.findViewById<ListView>(R.id.dayListView).adapter =
                            ScheduleListAdapter(scheduleDay,
                                    context!!.getSharedPreferences("Main", Context.MODE_PRIVATE),
                                    context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
                }
            }
            return rootView
        }
    }

    companion object {
        private fun dateToText(date: Date): String {
            val dayFormatter = SimpleDateFormat("dd-MM", Locale.getDefault())
            return Utils.dayIntToStr(Calendar.getInstance().also { it.time = date }.get(Calendar.DAY_OF_WEEK)) +
                    " " + dayFormatter.format(date)
        }
    }
}
