package com.example.fos.gamedemo.location;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

/**
 * Created by fos on 15.12.2015 г..
 */
public class LocationReciever extends WakefulBroadcastReceiver {

    public static final String ACTION_NEW_LOCATION = "UNIQUE_BROADCAST_ACTION_STRING_HERE";
    private static final String TAG = "LocationReciever";


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "Something recieved");
        startWakefulService(context, new Intent(context, LocationService.class));
    }
}
