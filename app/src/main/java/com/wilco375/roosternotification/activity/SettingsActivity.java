package com.wilco375.roosternotification.activity;

import android.content.SharedPreferences;
import android.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.wilco375.roosternotification.R;

public class SettingsActivity extends AppCompatActivity {

    SharedPreferences sp;
    //int timesClicked = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Show back button
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setupSettings();
    }

    /**
     * Save settings to SharedPreferences when changed
     */
    private void setupSettings(){
        sp = getSharedPreferences("Main",MODE_PRIVATE);

        final CheckBox group = (CheckBox) findViewById(R.id.showGroupCheckbox);
        final CheckBox notify = (CheckBox) findViewById(R.id.showNotificationCheckbox);
        final CheckBox notifyCancel = (CheckBox) findViewById(R.id.showCancelledNotificationCheckbox);
        final CheckBox notifyDaySchedule = (CheckBox) findViewById(R.id.showDayScheduleCheckbox);

        group.setChecked(sp.getBoolean("group",false));
        notify.setChecked(sp.getBoolean("notify",true));
        notifyCancel.setChecked(sp.getBoolean("notifyCancel",true));
        notifyDaySchedule.setChecked(sp.getBoolean("notifyDaySchedule", true));

        group.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sp.edit().putBoolean("group", isChecked).apply();
            }
        });

        notify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sp.edit().putBoolean("notify", isChecked).apply();
            }
        });

        notifyCancel.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sp.edit().putBoolean("notifyCancel", isChecked).apply();
            }
        });

        notifyDaySchedule.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                sp.edit().putBoolean("notifyDaySchedule", isChecked).apply();
            }
        });

        /*findViewById(R.id.scheduleTitle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timesClicked += 1;
                if (timesClicked >= 3) {
                    //Copy JSON to clipboard
                    new ZermeloSync().syncZermelo(getApplication(), SettingsActivity.this, false, true);
                }
            }
        });*/
    }
}
