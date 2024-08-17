package org.lineageos.recorder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.lineageos.recorder.service.WidgetService;

public class WidgetReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("START_FOREGROUND_SERVICE".equals(intent.getAction())) {
            if (WidgetService.isServiceRunning) {
                Log.e("saalim", "WidgetService stopping");
                Intent serviceIntent = new Intent(context, WidgetService.class);
                serviceIntent.setAction("START_FOREGROUND_SERVICE");
                context.stopService(serviceIntent);
                WidgetService.isServiceRunning = false;
            } else {
                Log.e("saalim", "WidgetService Starting");
                Intent serviceIntent = new Intent(context, WidgetService.class);
                serviceIntent.setAction("START_FOREGROUND_SERVICE");
                context.startForegroundService(serviceIntent);
                WidgetService.isServiceRunning = true;
            }
        }
    }
}