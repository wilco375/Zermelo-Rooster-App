package com.wilco375.roosternotification;

import android.content.Context;
import android.content.BroadcastReceiver;
import android.content.Intent;

public class BootComplete extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent serviceIntent = new Intent(context, AutoStartUp.class);
            context.startService(serviceIntent);
        }
    }
}
