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
            Log.e("saalim", "WidgetService");
            Intent serviceIntent = new Intent(context, WidgetService.class);
            serviceIntent.setAction("START_FOREGROUND_SERVICE");
            context.startForegroundService(serviceIntent);
        }
    }
}