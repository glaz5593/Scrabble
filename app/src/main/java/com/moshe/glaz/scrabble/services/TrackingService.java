package com.moshe.glaz.scrabble.services;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.moshe.glaz.scrabble.app.AppBase;
import com.moshe.glaz.scrabble.managers.TrackingManager;

public class TrackingService extends Service {
    static boolean isRunning;
    LocationManager locationManager;
    LocationListener listener;

    public TrackingService() {

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        super.onCreate();
        isRunning = true;

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        listener = getListener();

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 240000, 300, listener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,60000,0,listener);
    }

    private LocationListener getListener() {
        return new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                TrackingManager.getInstance().addLocation(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning=false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public static void start(){
        if(isRunning){
            return;
        }

        AppBase.getContext().startService(new Intent(AppBase.getContext(), TrackingService.class) );
    }

}