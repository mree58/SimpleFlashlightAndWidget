package com.emrebaran.simpleflashlightandwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

/**
 * Created by mree on 25.11.2016.
 */

public class FlashlightWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {

        Intent receiver = new Intent(context, FlashLightWidgetReceiver.class);
        receiver.setAction("COM_FLASHLIGHT");
        receiver.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, receiver, 0);

        RemoteViews views = new RemoteViews(context.getPackageName(),
                R.layout.widget_layout_flash_1_1);
        views.setOnClickPendingIntent(R.id.button, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetIds, views);

    }
}