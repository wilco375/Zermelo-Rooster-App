package com.wilco375.roosternotification;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends Activity {
    boolean syncing = false;
    SharedPreferences sp;
    int timesClicked = 0;
    Activity activity;
    Context context;
    Schedule[][] scheduleArray;
    int day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sendBroadcast(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME));
        setContentView(R.layout.activity_main);

        //Hide keyboard
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //Allow internet
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());

        activity = this;
        context = getApplicationContext();

        //Get SharedPreferences
        sp = getSharedPreferences("Main",MODE_PRIVATE);

        //Display warning on first Zermelo sync
        if(sp.getBoolean("firstSync",false)) displayWarning();

        //Set alarm
        Utils.setAlarm(this);

        handleZermeloSync();

        handleSettings();

        getSchedule();
    }

    public void getSchedule(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Schedule[] schedule = ScheduleHandler.getSchedule(context);

                List<Schedule> monday = new ArrayList<>();
                List<Schedule> tuesday = new ArrayList<>();
                List<Schedule> wednesday = new ArrayList<>();
                List<Schedule> thursday = new ArrayList<>();
                List<Schedule> friday = new ArrayList<>();

                //Add schedule to appropriate day
                for(Schedule lesson : schedule){
                    switch (lesson.getDay()){
                        case Calendar.MONDAY:
                            monday.add(lesson);
                            break;
                        case Calendar.TUESDAY:
                            tuesday.add(lesson);
                            break;
                        case Calendar.WEDNESDAY:
                            wednesday.add(lesson);
                            break;
                        case Calendar.THURSDAY:
                            thursday.add(lesson);
                            break;
                        case Calendar.FRIDAY:
                            friday.add(lesson);
                            break;
                    }
                }

                Schedule[][] tempScheduleArray = {
                        {},
                        {},
                        Utils.scheduleListToArray(monday),
                        Utils.scheduleListToArray(tuesday),
                        Utils.scheduleListToArray(wednesday),
                        Utils.scheduleListToArray(thursday),
                        Utils.scheduleListToArray(friday),
                };

                scheduleArray = tempScheduleArray;

                Calendar calendar = Calendar.getInstance();
                day = calendar.get(Calendar.DAY_OF_WEEK);
                if(calendar.get(Calendar.HOUR_OF_DAY)>17) day += 1;
                if(day == Calendar.SATURDAY || day == Calendar.SUNDAY || (day == Calendar.MONDAY && calendar.get(Calendar.HOUR_OF_DAY) > 17)) day = Calendar.MONDAY;

                showSchedule();
            }
        });
    }

    private void showSchedule(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Schedule [] daySchedule = scheduleArray[day];
                Arrays.sort(daySchedule, new Schedule.ScheduleComparator());

                //Set text to day
                TextView dayText = (TextView) findViewById(R.id.dayText);
                dayText.setText(Utils.dayIntToStr(day));

                String[] timeslots = new String[daySchedule.length];
                String[] infos = new String[daySchedule.length];
                String[] times = new String[daySchedule.length];
                boolean[] cancelled = new boolean[daySchedule.length];

                //Loop through lessons and add to arrays
                for(int i=0;i<daySchedule.length;i++) {

                    if(daySchedule[i].getTimeslot() < 1) timeslots[i] = "-";
                    else timeslots[i] = String.valueOf(daySchedule[i].getTimeslot());

                    String infoStr = "";
                    if(!daySchedule[i].getSubject().equals("")) infoStr = daySchedule[i].getSubjectAndGroup(sp);
                    if(!daySchedule[i].getType().equals("Les")) infoStr += " ("+daySchedule[i].getType()+")";
                    if(!daySchedule[i].getLocation().equals("")) infoStr += " - "+daySchedule[i].getLocation();
                    infos[i] = infoStr;

                    times[i] = daySchedule[i].getStart()+" - "+daySchedule[i].getEnd();

                    cancelled[i] = daySchedule[i].getCancelled();
                }

                ListView listView = (ListView) findViewById(R.id.dayListView);
                listView.setAdapter(new ScheduleListAdapter(context, timeslots, infos, times,cancelled));
                setListViewHeight(listView);
            }
        });
    }

    private void setListViewHeight(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) return;

        int totalHeight = listView.getPaddingTop() + listView.getPaddingBottom();
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            if (listItem instanceof ViewGroup) {
                listItem.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.WRAP_CONTENT, AbsListView.LayoutParams.WRAP_CONTENT));
            }
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    private void displayWarning(){
        sp.edit().putBoolean("firstSync",false).apply();
        new AlertDialog.Builder(this)
                .setTitle("LET OP!")
                .setMessage("Aan meldingen van uitvallende uren zijn geen rechten verbonden. Controleer dus altijd op Zermelo of een uur inderdaad uitvalt. Dit is dus ook geen reden waarom je niet naar een les gegaan bent. Het JFC is niet verantwoordelijk voor deze app")
                .setCancelable(true)
                .setNeutralButton("Oké", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }).create().show();
    }

    private void handleZermeloSync(){
        if(sp.getString("token","").equals("")) {
            findViewById(R.id.zermeloConfirm).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ZermeloSync.authenticate(((EditText) findViewById(R.id.zermeloCode)).getText().toString().replaceAll(" ", ""), getBaseContext(), sp)) {
                        syncing = true;
                        sp.edit().putBoolean("firstSync", true).putBoolean("zermeloSync", true).apply();
                        new ZermeloSync().syncZermelo(getApplication(), activity, true, false);
                        Toast.makeText(getApplication(), "Rooster aan het synchroniseren...", Toast.LENGTH_LONG).show();
                    }
                }
            });

            Button syncZermelo = (Button) findViewById(R.id.zermeloForceSync);
            syncZermelo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    syncing = true;
                    new ZermeloSync().syncZermelo(getApplication(), activity, true, false);
                    Toast.makeText(getApplication(), "Rooster aan het synchroniseren...", Toast.LENGTH_LONG).show();
                }
            });

            TextView zermeloHelp = (TextView) findViewById(R.id.zermeloCodeHelp);
            zermeloHelp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getApplication(), ZermeloHelp.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplication().startActivity(i);
                }
            });
        }else{
            findViewById(R.id.zermeloSyncLayout).setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 0));
            Button syncZermelo = (Button) findViewById(R.id.zermeloForceSync);
            syncZermelo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    syncing = true;
                    new ZermeloSync().syncZermelo(getApplication(), activity, true, false);
                    Toast.makeText(getApplication(), "Rooster aan het synchroniseren...", Toast.LENGTH_LONG).show();
                }
            });
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.addRule(RelativeLayout.BELOW,R.id.zermeloTitle);
            findViewById(R.id.zermeloForceSync).setLayoutParams(lp);
        }
    }

    private void handleSettings(){
        final CheckBox group = (CheckBox) findViewById(R.id.showGroupCheckbox);
        final CheckBox notify = (CheckBox) findViewById(R.id.showNotificationCheckbox);
        final CheckBox notifyCancel = (CheckBox) findViewById(R.id.showCancelledNotificationCheckbox);

        group.setChecked(sp.getBoolean("group",false));
        notify.setChecked(sp.getBoolean("notify",true));
        notifyCancel.setChecked(sp.getBoolean("notifyCancel",true));

        findViewById(R.id.showNotificationText).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify.setChecked(!notify.isChecked());
            }
        });
        findViewById(R.id.showCancelledNotificationText).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyCancel.setChecked(!notifyCancel.isChecked());
            }
        });
        findViewById(R.id.showGroupText).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                group.setChecked(!group.isChecked());
            }
        });

        group.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sp.edit().putBoolean("group", isChecked).apply();
                showSchedule();
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

        findViewById(R.id.scheduleTitle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timesClicked += 1;
                if (timesClicked >= 3) {
                    //Copy JSON to clipboard
                    new ZermeloSync().syncZermelo(getApplication(), activity, false, true);
                }
            }
        });

        findViewById(R.id.prevDay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (day - 1 >= Calendar.MONDAY) {
                    day -= 1;
                    showSchedule();
                } else {
                    Toast.makeText(context, "Je kunt niet verder terug", Toast.LENGTH_LONG).show();
                }
            }
        });

        findViewById(R.id.nextDay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(day+1<=Calendar.FRIDAY){
                    day += 1;
                    showSchedule();
                }else{
                    Toast.makeText(context,"Je kunt niet verder",Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    protected void onPause() {
        Utils.updateWidgets(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
