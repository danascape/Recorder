package org.lineageos.recorder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.lineageos.recorder.service.SoundRecorderService;

public class WidgetReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("START_RECORDING".equals(intent.getAction())) {
            Log.e("saalim", "Starting record");
            Intent serviceIntent = new Intent(context, SoundRecorderService.class);
            serviceIntent.setAction(SoundRecorderService.ACTION_START);
            context.stopService(serviceIntent);
        }
    }
}