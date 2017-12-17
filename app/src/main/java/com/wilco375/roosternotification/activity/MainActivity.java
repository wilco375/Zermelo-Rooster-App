package com.wilco375.roosternotification.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wilco375.roosternotification.R;
import com.wilco375.roosternotification.Schedule;
import com.wilco375.roosternotification.general.ScheduleHandler;
import com.wilco375.roosternotification.general.ScheduleListAdapter;
import com.wilco375.roosternotification.general.Utils;
import com.wilco375.roosternotification.online.ZermeloSync;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    boolean syncing = false;
    SharedPreferences sp;
    List<ArrayList<Schedule>> scheduleList;
    int day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sendBroadcast(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME));
        setContentView(R.layout.activity_main);

        //Allow internet
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());

        //Get SharedPreferences
        sp = getSharedPreferences("Main",MODE_PRIVATE);

        //Set alarm
        Utils.setAlarm(this);

        checkInit();

        getSchedule();

        setupNavigation();
    }

    /**
     * Check if first launch or first sync
     */
    private void checkInit(){
        if (sp.getString("token", "").equals("")) {
            Intent i = new Intent(MainActivity.this, InitActivity.class);
            finish();
            startActivity(i);
        }else if(sp.getBoolean("firstSync",true)){
            syncSchedule();

            sp.edit().putBoolean("firstSync", false).apply();
        }
    }

    /**
     * Get schedule from storage
     */
    public void getSchedule(){
        runOnUiThread(() -> {
            Schedule[] schedule = ScheduleHandler.getSchedule(MainActivity.this);
            int DAY_MAX = Calendar.FRIDAY + 7;

            scheduleList = new ArrayList<>();
            // Add empty ArrayLists to scheduleList for each day of the week
            for(int i = 0; i <= DAY_MAX; i++){
                scheduleList.add(i, new ArrayList<>());
            }
            for(Schedule lesson : schedule){
                scheduleList.get(lesson.getDay()).add(lesson);
            }

            day = Utils.currentDay();

            setupSchedule();
        });
    }

    /**
     * Show schedule in app
     */
    private void setupSchedule(){
        runOnUiThread(() -> {
            List<Schedule> daySchedule = scheduleList.get(day);
            Collections.sort(daySchedule, new Schedule.ScheduleComparator());

            //Set text to day
            TextView dayText = findViewById(R.id.dayText);
            dayText.setText(Utils.dayIntToStr(day));

            ListView listView = findViewById(R.id.dayListView);
            listView.setAdapter(
                    new ScheduleListAdapter(daySchedule, sp, (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
            );
        });
    }

    /**
     * Setup navigation to go to previous/next day
     */
    private void setupNavigation(){
        findViewById(R.id.prevDay).setOnClickListener(v -> {
            if (day - 1 >= Calendar.MONDAY) {
                if(day-1 == Calendar.SUNDAY+7) day = Calendar.FRIDAY;
                else day -= 1;
                setupSchedule();
            } else {
                Toast.makeText(MainActivity.this, "Je kunt niet verder terug", Toast.LENGTH_LONG).show();
            }
        });

        findViewById(R.id.nextDay).setOnClickListener(v -> {
            if(day+1 <= Calendar.FRIDAY+7){
                if(day+1 == Calendar.SATURDAY) day = Calendar.MONDAY + 7;
                else day += 1;
                setupSchedule();
            }else{
                Toast.makeText(MainActivity.this, "Je kunt niet verder", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Sync schedule with Zermelo
     */
    private void syncSchedule(){
        syncing = true;
        new ZermeloSync().syncZermelo(getApplication(), MainActivity.this, true, false);
        Toast.makeText(getApplication(), "Rooster aan het synchroniseren...", Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Launch SettingsActivity
        if(item.getItemId() == R.id.settings){
            Intent i = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(i);

            return true;
        }
        // Sync schedule
        else if(item.getItemId() == R.id.zermelo_sync){
            syncSchedule();

            return true;
        }
        else{
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPause() {
        Utils.updateWidgets(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupSchedule();
    }
}
