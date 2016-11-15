package com.wilco375.roosternotification.receiver;

import android.content.Context;
import android.content.BroadcastReceiver;
import android.content.Intent;

import com.wilco375.roosternotification.general.Utils;

public class BootComplete extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        //System.out.println("Intent message is "+intent.getAction());
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()) || "START_ALARM".equals(intent.getAction())) {
            Utils.setAlarm(context);
        }
    }
}
