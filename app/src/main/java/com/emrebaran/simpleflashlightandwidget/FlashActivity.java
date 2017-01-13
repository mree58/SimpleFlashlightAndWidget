package com.emrebaran.simpleflashlightandwidget;

import android.Manifest;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import java.lang.reflect.Field;

/**
 * Created by mree on 25.11.2016.
 */

public class FlashActivity extends AppCompatActivity {

    private boolean isLightOn = false;
    private Camera camera;

    DisplayMetrics metrics;

    private boolean doubleBackToExitPressedOnce;
    private Handler mHandler = new Handler();

    ImageButton btnFlash;

    String[] permissionsRequired = new String[]{Manifest.permission.CAMERA};

    Parameters p;

    @Override
    protected void onStop() {
        super.onStop();

        if (camera != null) {
            camera.release();
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_flash_activity);

        //for popup depending on resolution
        Display display = ((WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        metrics = new DisplayMetrics();
        display.getMetrics(metrics);


        Context context = this;
        PackageManager pm = context.getPackageManager();

        // if device support camera?
        if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Log.e("Error", "Device has no camera!");
            return;
        }


        if(ActivityCompat.checkSelfPermission(getApplicationContext(), permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED) {

            Intent myIntent = new Intent(FlashActivity.this,PermissionActivity.class);
            startActivity(myIntent);
        }
        else {

            camera = Camera.open();
            p = camera.getParameters();
        }

            btnFlash = (ImageButton) findViewById(R.id.btn_flash_light);
            btnFlash.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                    if(ActivityCompat.checkSelfPermission(getApplicationContext(), permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED) {

                        Intent myIntent = new Intent(FlashActivity.this,PermissionActivity.class);
                        startActivity(myIntent);
                    }
                    else {

                        camera = Camera.open();
                        p = camera.getParameters();

                        if (isLightOn) {

                            p.setFlashMode(Parameters.FLASH_MODE_OFF);
                            camera.setParameters(p);
                            camera.stopPreview();
                            isLightOn = false;

                            btnFlash.setImageResource(R.mipmap.off);

                        } else {

                            p.setFlashMode(Parameters.FLASH_MODE_TORCH);

                            camera.setParameters(p);
                            camera.startPreview();
                            isLightOn = true;

                            btnFlash.setImageResource(R.mipmap.ic_launcher);

                        }

                    }

                }
            });


            try {
                ViewConfiguration config = ViewConfiguration.get(this);
                Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
                if (menuKeyField != null) {
                    menuKeyField.setAccessible(true);
                    menuKeyField.setBoolean(config, false);
                }
            } catch (Exception ex) {
                // Ignore
            }

    }



    private void showPopupAbout() {
        try {

            final Dialog dialogAbout = new Dialog(FlashActivity.this);
            dialogAbout.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogAbout.setContentView(R.layout.layout_about);


            dialogAbout.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

            WindowManager.LayoutParams wmlp = dialogAbout.getWindow().getAttributes();

            wmlp.gravity = Gravity.TOP;
            wmlp.y = (int)(50*metrics.scaledDensity);

            dialogAbout.show();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_about) {

            showPopupAbout();

            return true;
        }
        if (id == R.id.action_rate) {

            Uri uri = Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            // To count with Play market backstack, After pressing back button,
            // to taken back to our application, we need to add following flags to intent.
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            try {
                startActivity(goToMarket);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName())));
            }

            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    //double click to exit
    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            doubleBackToExitPressedOnce = false;
        }
    };
    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        if (mHandler != null) { mHandler.removeCallbacks(mRunnable); }
    }
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, R.string.app_exit, Toast.LENGTH_SHORT).show();

        mHandler.postDelayed(mRunnable, 1500);
    }




}