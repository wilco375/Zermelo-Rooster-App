package com.wilco375.roosternotification;

import android.app.Activity;
import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import cz.msebera.android.httpclient.message.BasicNameValuePair;

public class MainActivity extends Activity {
    CheckBox[] checkboxArray;
    EditText[] edittextArray;

    CheckBox notify;
    CheckBox zermeloSync;
    CheckBox fourtyMinuteSchedule;
    CheckBox notifyCancel;
    CheckBox group;
    //AdRequest adRequest;
    boolean paused = false;
    boolean syncing = false;
    SharedPreferences sp;
    Intent autoStartUp;
    int timesClicked = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sendBroadcast(new Intent(Intent.ACTION_MAIN)
                .addCategory(Intent.CATEGORY_HOME));
        setContentView(R.layout.activity_main);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());

        paused = false;

        sp = getSharedPreferences("Main",MODE_PRIVATE);

        System.out.println("zermeloSync is "+sp.getBoolean("zermeloSync",false));

        if(sp.getBoolean("firstSync",false)){
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

        if(sp.getBoolean("exam",false)){
            new AlertDialog.Builder(this)
                    .setTitle("Toetsweek")
                    .setMessage("Er is een toetsweek gedetecteerd. Kijk voor je toetsrooster op Zermelo")
                    .setCancelable(true)
                    .setNeutralButton("Oké", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    }).create().show();
        }

        setAlarm();

        //AdView mAdView = (AdView) findViewById(R.id.adView);
        //adRequest = new AdRequest.Builder().addTestDevice(getResources().getString(R.string.test_device_id)).build();
        //mAdView.loadAd(adRequest);

        final Activity activity = this;

        Button zermeloButton = (Button) findViewById(R.id.zermeloconfirm);
        final EditText zermeloCode = (EditText) findViewById(R.id.zermelocode);
        zermeloButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(doAuth(zermeloCode.getText().toString().replaceAll(" ",""))){
                    syncing = true;
                    sp.edit().putBoolean("firstSync",true).putBoolean("zermeloSync",true).apply();
                    new ZermeloSync().syncZermelo(getApplication(),activity, true, false);
                    Toast.makeText(getApplication(), "Rooster aan het synchroniseren...", Toast.LENGTH_LONG).show();
                    //System.out.println("zermelo confirm button clicked");
                }
            }
        });

        Button syncZermelo = (Button) findViewById(R.id.zermelosync);
        syncZermelo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                syncing = true;
                new ZermeloSync().syncZermelo(getApplication(),activity, true, false);
                Toast.makeText(getApplication(), "Rooster aan het synchroniseren...", Toast.LENGTH_LONG).show();
                //System.out.println("zeremlo sync button clicked");
            }
        });

        TextView zermeloHelp = (TextView) findViewById(R.id.zermelocodehelp);
        zermeloHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplication(), ZermeloHelp.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplication().startActivity(i);
            }
        });

        TextView roosterTitle = (TextView) findViewById(R.id.roosterTitle);
        roosterTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timesClicked += 1;
                if(timesClicked >= 3){
                    new ZermeloSync().syncZermelo(getApplication(), activity, false, true);
                }
            }
        });

        group = (CheckBox) findViewById(R.id.showGroupCheckbox);

        CheckBox[] tempCheckboxArray = {(CheckBox) findViewById(R.id.a12),(CheckBox) findViewById(R.id.b12),(CheckBox) findViewById(R.id.c12),(CheckBox) findViewById(R.id.d12),(CheckBox) findViewById(R.id.e12),(CheckBox) findViewById(R.id.a22),(CheckBox) findViewById(R.id.b22),(CheckBox) findViewById(R.id.c22),(CheckBox) findViewById(R.id.d22),(CheckBox) findViewById(R.id.e22),(CheckBox) findViewById(R.id.a32),(CheckBox) findViewById(R.id.b32),(CheckBox) findViewById(R.id.c32),(CheckBox) findViewById(R.id.d32),(CheckBox) findViewById(R.id.e32),(CheckBox) findViewById(R.id.a42),(CheckBox) findViewById(R.id.b42),(CheckBox) findViewById(R.id.c42),(CheckBox) findViewById(R.id.d42),(CheckBox) findViewById(R.id.e42),(CheckBox) findViewById(R.id.a52),(CheckBox) findViewById(R.id.b52),(CheckBox) findViewById(R.id.c52),(CheckBox) findViewById(R.id.d52),(CheckBox) findViewById(R.id.e52),(CheckBox) findViewById(R.id.a62),(CheckBox) findViewById(R.id.b62),(CheckBox) findViewById(R.id.c62),(CheckBox) findViewById(R.id.d62),(CheckBox) findViewById(R.id.e62),(CheckBox) findViewById(R.id.a72),(CheckBox) findViewById(R.id.b72),(CheckBox) findViewById(R.id.c72),(CheckBox) findViewById(R.id.d72),(CheckBox) findViewById(R.id.e72),(CheckBox) findViewById(R.id.a82),(CheckBox) findViewById(R.id.b82),(CheckBox) findViewById(R.id.c82),(CheckBox) findViewById(R.id.d82),(CheckBox) findViewById(R.id.e82),(CheckBox) findViewById(R.id.a92),(CheckBox) findViewById(R.id.b92),(CheckBox) findViewById(R.id.c92),(CheckBox) findViewById(R.id.d92),(CheckBox) findViewById(R.id.e92)};
        checkboxArray = tempCheckboxArray;

        EditText[] tempEdittextArray = {(EditText) findViewById(R.id.a13),(EditText) findViewById(R.id.b13),(EditText) findViewById(R.id.c13),(EditText) findViewById(R.id.d13),(EditText) findViewById(R.id.e13),(EditText) findViewById(R.id.a23),(EditText) findViewById(R.id.b23),(EditText) findViewById(R.id.c23),(EditText) findViewById(R.id.d23),(EditText) findViewById(R.id.e23),(EditText) findViewById(R.id.a33),(EditText) findViewById(R.id.b33),(EditText) findViewById(R.id.c33),(EditText) findViewById(R.id.d33),(EditText) findViewById(R.id.e33),(EditText) findViewById(R.id.a43),(EditText) findViewById(R.id.b43),(EditText) findViewById(R.id.c43),(EditText) findViewById(R.id.d43),(EditText) findViewById(R.id.e43),(EditText) findViewById(R.id.a53),(EditText) findViewById(R.id.b53),(EditText) findViewById(R.id.c53),(EditText) findViewById(R.id.d53),(EditText) findViewById(R.id.e53),(EditText) findViewById(R.id.a63),(EditText) findViewById(R.id.b63),(EditText) findViewById(R.id.c63),(EditText) findViewById(R.id.d63),(EditText) findViewById(R.id.e63),(EditText) findViewById(R.id.a73),(EditText) findViewById(R.id.b73),(EditText) findViewById(R.id.c73),(EditText) findViewById(R.id.d73),(EditText) findViewById(R.id.e73),(EditText) findViewById(R.id.a83),(EditText) findViewById(R.id.b83),(EditText) findViewById(R.id.c83),(EditText) findViewById(R.id.d83),(EditText) findViewById(R.id.e83),(EditText) findViewById(R.id.a93),(EditText) findViewById(R.id.b93),(EditText) findViewById(R.id.c93),(EditText) findViewById(R.id.d93),(EditText) findViewById(R.id.e93),(EditText) findViewById(R.id.a14),(EditText) findViewById(R.id.b14),(EditText) findViewById(R.id.c14),(EditText) findViewById(R.id.d14),(EditText) findViewById(R.id.e14),(EditText) findViewById(R.id.a24),(EditText) findViewById(R.id.b24),(EditText) findViewById(R.id.c24),(EditText) findViewById(R.id.d24),(EditText) findViewById(R.id.e24),(EditText) findViewById(R.id.a34),(EditText) findViewById(R.id.b34),(EditText) findViewById(R.id.c34),(EditText) findViewById(R.id.d34),(EditText) findViewById(R.id.e34),(EditText) findViewById(R.id.a44),(EditText) findViewById(R.id.b44),(EditText) findViewById(R.id.c44),(EditText) findViewById(R.id.d44),(EditText) findViewById(R.id.e44),(EditText) findViewById(R.id.a54),(EditText) findViewById(R.id.b54),(EditText) findViewById(R.id.c54),(EditText) findViewById(R.id.d54),(EditText) findViewById(R.id.e54),(EditText) findViewById(R.id.a64),(EditText) findViewById(R.id.b64),(EditText) findViewById(R.id.c64),(EditText) findViewById(R.id.d64),(EditText) findViewById(R.id.e64),(EditText) findViewById(R.id.a74),(EditText) findViewById(R.id.b74),(EditText) findViewById(R.id.c74),(EditText) findViewById(R.id.d74),(EditText) findViewById(R.id.e74),(EditText) findViewById(R.id.a84),(EditText) findViewById(R.id.b84),(EditText) findViewById(R.id.c84),(EditText) findViewById(R.id.d84),(EditText) findViewById(R.id.e84),(EditText) findViewById(R.id.a94),(EditText) findViewById(R.id.b94),(EditText) findViewById(R.id.c94),(EditText) findViewById(R.id.d94),(EditText) findViewById(R.id.e94)};
        edittextArray = tempEdittextArray;

        notify = (CheckBox) findViewById(R.id.showNotificationCheckbox);
        zermeloSync = (CheckBox) findViewById(R.id.syncZermeloCheckbox);
        fourtyMinuteSchedule = (CheckBox) findViewById(R.id.fourtyMinuteCheckbox);
        notifyCancel = (CheckBox) findViewById(R.id.showCancelledNotificationCheckbox);

        TextView notifyText = (TextView) findViewById(R.id.showNotificationText);
        final TextView zermeloSyncText = (TextView) findViewById(R.id.syncZermeloText);
        TextView fourtyMinuteScheduleText = (TextView) findViewById(R.id.fourtyMinuteText);
        final TextView notifyCancelText = (TextView) findViewById(R.id.showCancelledNotificationText);
        final TextView groupText = (TextView) findViewById(R.id.showGroupText);

        notifyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify.setChecked(!notify.isChecked());
            }
        });
        zermeloSyncText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zermeloSync.setChecked(!zermeloSync.isChecked());
            }
        });
        fourtyMinuteScheduleText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fourtyMinuteSchedule.setChecked(!fourtyMinuteSchedule.isChecked());
            }
        });
        notifyCancelText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyCancel.setChecked(!notifyCancel.isChecked());
            }
        });
        groupText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                group.setChecked(!group.isChecked());
            }
        });


        if(sp.getBoolean("RoosterSaved",false)){
            fillEditTextFields();
        }


        group.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                syncing = true;
                sp.edit().putBoolean("group", group.isChecked()).apply();
                new ZermeloSync().syncZermelo(getApplication(), activity, true, false);
                Toast.makeText(getApplication(), "Rooster aan het synchroniseren...", Toast.LENGTH_LONG).show();
            }
        });

        zermeloSync.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor spe = sp.edit();
                if(zermeloSync.isChecked()) {
                    syncing = true;
                    spe.putBoolean("zermeloSync", true);
                    spe.apply();
                    new ZermeloSync().syncZermelo(getApplication(), activity, true, false);
                    Toast.makeText(getApplication(), "Rooster aan het synchroniseren...", Toast.LENGTH_LONG).show();
                } else {
                    spe.putBoolean("zermeloSync", false);
                    spe.apply();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                }
            }
        });
    }

    public void setAlarm(){
        System.out.println("setting alarm");
        autoStartUp = new Intent(this, AutoStartUp.class);
        startService(autoStartUp);
    }

    public boolean doAuth(String code) {
        if (code.equals("")) {
            Toast.makeText(this, R.string.invalid_code, Toast.LENGTH_LONG).show();
            return false;
        }

        if (!isConnected()) {
            Toast.makeText(this, R.string.no_connection, Toast.LENGTH_LONG).show();
            return false;
        }

        try {
            HttpClient client = HttpClientBuilder.create().build();

            HttpPost post = new HttpPost("https://jfc.zportal.nl/api/v2/oauth/token?");

            List<NameValuePair> nameValuePair = new ArrayList<>(2);
            nameValuePair.add(new BasicNameValuePair("grant_type", "authorization_code"));
            nameValuePair.add(new BasicNameValuePair("code", code));

            post.setEntity(new UrlEncodedFormEntity(nameValuePair));
            HttpResponse response = client.execute(post);
            System.out.println("response: "+response.toString());
            if(response.getStatusLine().getStatusCode() != 200) return false;

            JSONObject tokenJson = new JSONObject(new BufferedReader(new InputStreamReader((response.getEntity().getContent()))).readLine());
            String token = tokenJson.getString("access_token");
            System.out.println("token: "+token);

            if (token == null) {
                Toast.makeText(this, R.string.invalid_code, Toast.LENGTH_LONG).show();
                return false;
            }
            if (token.equals("")) {
                Toast.makeText(this, R.string.invalid_code, Toast.LENGTH_LONG).show();
                return false;
            }
            Toast.makeText(this, R.string.auth_success, Toast.LENGTH_LONG).show();

            SharedPreferences.Editor spe = sp.edit();
            spe.putString("token", token);
            spe.putBoolean("zermeloSync", true);
            spe.apply();
            return true;
        }catch(JSONException e){
            e.printStackTrace();
            return false;
        }catch(IOException e){
            e.printStackTrace();
            return false;
        }
    }

    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    public void fillEditTextFields(){
        System.out.println("filling edittextfields");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (EditText e : edittextArray) {
                    //System.out.println("Name: " + getResources().getResourceName(e.getId()).replace("com.wilco375.roosternotification:id/", ""));
                    e.setText(sp.getString(getResources().getResourceName(e.getId()).replace("com.wilco375.roosternotification:id/", ""), ""));
                }

                for (CheckBox c : checkboxArray) {
                    c.setChecked(sp.getBoolean(getResources().getResourceName(c.getId()).replace("com.wilco375.roosternotification:id/", ""), false));
                }

                if(sp.getBoolean("zermeloSync",false)&&!sp.getString("token","").equals("")){
                    for(EditText e : edittextArray){
                        e.setFocusable(false);
                    }
                    for(CheckBox c : checkboxArray){
                        c.setClickable(false);
                    }
                }

                System.out.println("zermeloSync2: "+sp.getBoolean("zermeloSync",false));

                notify.setChecked(sp.getBoolean("notify", true));
                System.out.println(sp.getBoolean("zermeloSync", false));
                zermeloSync.setChecked(sp.getBoolean("zermeloSync", false));
                fourtyMinuteSchedule.setChecked(sp.getBoolean("fourtyMinuteSchedule", false));
                notifyCancel.setChecked(sp.getBoolean("notifyCancel", true));
                group.setChecked(sp.getBoolean("group", false));
            }
        });
    }

    private void saveEditTextFields(){
        SharedPreferences.Editor spe = sp.edit();

        if(!zermeloSync.isChecked()) {
            for (EditText e : edittextArray) {
                spe.putString(getResources().getResourceName(e.getId()).replace("com.wilco375.roosternotification:id/", ""), e.getText().toString());
            }

            for (CheckBox c : checkboxArray) {
                spe.putBoolean(getResources().getResourceName(c.getId()).replace("com.wilco375.roosternotification:id/", ""), c.isChecked());
            }
        }

        spe.putBoolean("notify",notify.isChecked());
        spe.putBoolean("fourtyMinuteSchedule", fourtyMinuteSchedule.isChecked());
        spe.putBoolean("notifyCancel",notifyCancel.isChecked());
        spe.putBoolean("syncZermelo", zermeloSync.isChecked());

        if(!sp.getBoolean("RoosterSaved",false)){
            spe.putBoolean("RoosterSaved",true);
        }

        spe.apply();

        Toast.makeText(this,getResources().getString(R.string.saved),Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        if(!syncing) {
            saveEditTextFields();

            int[] ids = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), LesdagWidgetProvider.class));
            LesdagWidgetProvider lesdagWidget = new LesdagWidgetProvider();
            lesdagWidget.onUpdate(this, AppWidgetManager.getInstance(this), ids);

            int[] ids2 = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), LesuurWidgetProvider.class));
            LesuurWidgetProvider lesuurWidget = new LesuurWidgetProvider();
            lesuurWidget.onUpdate(this, AppWidgetManager.getInstance(this), ids2);
        }else{
            syncing = false;
        }
        paused = true;
        super.onPause();
    }

    @Override
    protected void onResume() {
        System.out.println("resume, ma,4e uur: "+sp.getString("a43",""));
        sp = getSharedPreferences("Main",MODE_PRIVATE);
        fillEditTextFields();
        super.onResume();
    }


}
