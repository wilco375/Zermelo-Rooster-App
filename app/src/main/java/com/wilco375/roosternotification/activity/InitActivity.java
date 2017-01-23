package com.wilco375.roosternotification.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.wilco375.roosternotification.R;
import com.wilco375.roosternotification.online.ZermeloSync;

public class InitActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);

        setupInit();
    }

    /**
     * Launch MainActivity after entering code or launch HelpActivity on button click
     */
    private void setupInit(){
        findViewById(R.id.zermeloConfirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = ((EditText) findViewById(R.id.zermeloCode)).getText().toString().replaceAll(" ", "");
                SharedPreferences sp = getSharedPreferences("Main", MODE_PRIVATE);
                if (ZermeloSync.authenticate(code, getBaseContext(), sp)) {
                    new AlertDialog.Builder(InitActivity.this)
                            .setTitle(R.string.first_sync_title)
                            .setMessage(R.string.first_sync_message)
                            .setCancelable(true)
                            .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent i = new Intent(InitActivity.this, MainActivity.class);
                                    finish();
                                    startActivity(i);
                                }
                            })
                            .show();
                }
            }
        });

        TextView zermeloHelp = (TextView) findViewById(R.id.zermeloCodeHelp);
        zermeloHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplication(), HelpActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        });
    }
}
