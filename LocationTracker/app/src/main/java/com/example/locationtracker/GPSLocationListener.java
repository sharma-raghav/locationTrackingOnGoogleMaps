package com.example.locationtracker;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class GPSLocationListener implements LocationListener {

    public Activity activity;

    public Integer val;

    public List<Double> latitudes;

    public List<Double> longitudes;

    public List<Float> speeds;

    GPSLocationListener(Activity activity, Integer valForLogging) {
        this.activity = activity;
        this.val = valForLogging;
        latitudes = new ArrayList<>();
        longitudes = new ArrayList<>();
        speeds = new ArrayList<>();
    }

    @Override
    public void onLocationChanged(Location location) {

        TextView latitude =  activity.findViewById(R.id.latitude);
        TextView longitude = activity.findViewById(R.id.longitude);
        TextView speed = activity.findViewById(R.id.speed);

        if (location != null) {
            latitude.setText(String.valueOf(location.getLatitude()));
            longitude.setText(String.valueOf(location.getLongitude()));
            speed.setText(String.valueOf(location.getSpeed()));
            if (val == 1) {
                latitudes.add(location.getLatitude());
                longitudes.add(location.getLongitude());
                speeds.add(location.getSpeed());
            }

        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
