package com.wilco375.roosternotification.activity;

import android.app.ActionBar;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;

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
    private void setupSettings() {
        sp = getSharedPreferences("Main", MODE_PRIVATE);

        final CheckBox group = findViewById(R.id.showGroupCheckbox);
        final CheckBox notify = findViewById(R.id.showNotificationCheckbox);
        final CheckBox notifyCancel = findViewById(R.id.showCancelledNotificationCheckbox);
        final CheckBox notifyDaySchedule = findViewById(R.id.showDayScheduleCheckbox);

        group.setChecked(sp.getBoolean("group", false));
        notify.setChecked(sp.getBoolean("notify", true));
        notifyCancel.setChecked(sp.getBoolean("notifyCancel", true));
        notifyDaySchedule.setChecked(sp.getBoolean("notifyDaySchedule", true));

        group.setOnCheckedChangeListener((buttonView, isChecked) -> sp.edit().putBoolean("group", isChecked).apply());

        notify.setOnCheckedChangeListener((buttonView, isChecked) -> sp.edit().putBoolean("notify", isChecked).apply());

        notifyCancel.setOnCheckedChangeListener((buttonView, isChecked) -> sp.edit().putBoolean("notifyCancel", isChecked).apply());

        notifyDaySchedule.setOnCheckedChangeListener((compoundButton, isChecked) -> sp.edit().putBoolean("notifyDaySchedule", isChecked).apply());

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
