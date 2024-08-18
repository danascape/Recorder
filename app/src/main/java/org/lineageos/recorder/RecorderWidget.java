package org.lineageos.recorder;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.HashMap;
import java.util.Map;

public class RecorderWidget extends AppWidgetProvider {

    private static final String TAG = "RecorderWidget";

    public enum ACTIONS {
        BACK(R.id.back),
        REFRESH(R.id.refresh),
        NEXT(R.id.next);

        public final int resid;

        ACTIONS(int resid) {
            this.resid = resid;
        }

        public static final Map<String, ACTIONS> fromNames = new HashMap<String, ACTIONS>() {{
            for (ACTIONS a : ACTIONS.values()) put(a.name(), a);
        }};
    }

//    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
//                                int appWidgetId) {
//
//        // Construct the RemoteViews object
//        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recorder_widget_base);
//
//        Intent intent = new Intent(context.getApplicationContext(), WidgetReceiver.class);
//        intent.setAction("START_FOREGROUND_SERVICE");
//
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
//
//        views.setOnClickPendingIntent(R.id.sound_fab, pendingIntent);
//
//        // Instruct the widget manager to update the widget
//        appWidgetManager.updateAppWidget(appWidgetId, views);
//    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.recorder_widget_base);

            // Specify the service to provide data for the collection widget.
            // Note that we need to
            // embed the appWidgetId via the data otherwise it will be ignored.
            Intent intent = new Intent(context, RecorderWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            rv.setRemoteAdapter(R.id.page_flipper, intent);

            for (ACTIONS a : ACTIONS.values()) {
                Intent wIntent = new Intent(context, RecorderWidget.class);
                wIntent.setAction(a.name());
                wIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                PendingIntent pendingIntent = PendingIntent
                        .getBroadcast(context, 0, wIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                rv.setOnClickPendingIntent(a.resid, pendingIntent);
            }

            appWidgetManager.updateAppWidget(appWidgetId, rv);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) {
            Log.e(TAG, "onReceive: action is null");
            return;
        }

        ACTIONS a = ACTIONS.fromNames.get(action);

        if (a != null)
            switch (a) {
                case REFRESH:
                    AppWidgetManager mgr = AppWidgetManager.getInstance(context);
                    ComponentName cn = new ComponentName(context,
                            RecorderWidget.class);
                    mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn),
                            R.id.page_flipper);
                    break;
                case NEXT:
                case BACK:
                    RemoteViews rv = new RemoteViews(context.getPackageName(),
                            R.layout.recorder_widget_base);

                    if (a == ACTIONS.NEXT) rv.showNext(R.id.page_flipper);
                    else rv.showPrevious(R.id.page_flipper);

                    AppWidgetManager.getInstance(context).updateAppWidget(
                            intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                                    AppWidgetManager.INVALID_APPWIDGET_ID), rv);
                    break;
            }

        super.onReceive(context, intent);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }
}