package com.emrebaran.simpleflashlightandwidget;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.widget.RemoteViews;
import android.widget.Toast;
import android.hardware.Camera.Parameters;

/**
 * Created by mree on 25.11.2016.
 */

public class FlashLightWidgetReceiver extends BroadcastReceiver {
    private static boolean isLightOn = false;
    private static Camera camera;

    @Override
    public void onReceive(Context context, Intent intent) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout_flash_1_1);

        if (isLightOn) {
            views.setImageViewResource(R.id.button, R.mipmap.off);
        } else {
            views.setImageViewResource(R.id.button, R.mipmap.ic_launcher);
        }

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        appWidgetManager.updateAppWidget(new ComponentName(context, FlashlightWidgetProvider.class),
                views);

        if (isLightOn) {
            if (camera != null) {
                camera.stopPreview();
                camera.release();
                camera = null;
                isLightOn = false;
            }

        } else {
            // Open the default i.e. the first rear facing camera.
            camera = Camera.open();

            if (camera == null) {
                Toast.makeText(context, R.string.no_camera, Toast.LENGTH_SHORT).show();
            } else {
                // Set the torch flash mode
                Parameters param = camera.getParameters();
                param.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                try {
                    camera.setParameters(param);
                    camera.startPreview();
                    isLightOn = true;
                } catch (Exception e) {
                    Toast.makeText(context, R.string.no_flash, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}