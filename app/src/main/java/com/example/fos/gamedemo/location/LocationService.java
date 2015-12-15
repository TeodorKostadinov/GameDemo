package com.example.fos.gamedemo.location;

import android.Manifest;
import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by fos on 15.12.2015 г..
 */
public class LocationService extends Service {
    private static final String TAG = "LocationService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "SErive started");
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        long minTime = 1000;     //This should probably be fairly long
        float minDistance = 100; //This should probably be fairly big
        String provider = LocationManager.NETWORK_PROVIDER;   //GPS_PROVIDER or NETWORK_PROVIDER

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                stopSelf();
                return;
            }
        }

        manager.requestLocationUpdates(provider, minTime, minDistance, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.e(TAG, "Service location:" + location.getLatitude() + ", " + location.getLongitude());
                //TODO send notifications
                stopSelf();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.e(TAG, "Service status changed");
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.e(TAG, "Service provider changed");
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.e(TAG, "Service provider changed");
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }
}
