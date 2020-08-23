package com.lch.util;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;


/**
 * 系统原生api定位。
 */

public class SystemLocationUtil {

    private static final String TAG = "SystemLocationUtil";
    private static final long LOC_UPDATE_INTERVAL = 20_000L;//mils
    private static final int LOC_MIN_DISTANCE = 50;//meters
    private static Location lastLocation;
    private static Context context;

    public static void init(Context ctx) {
        context = ctx;
    }


    public static Location getLastLocation() {
        return lastLocation;
    }

    @SuppressWarnings("MissingPermission")
    public static void start() {
        try {

            if (!PermissionUtil.hasLocationPermission(context)) {
                return;
            }

            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            Location loc = null;
            try {
                loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (loc != null) {
                    return;
                }
                loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (loc != null) {
                    return;
                }
                loc = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            } finally {
                if (loc != null) {
                    lastLocation = loc;
                }
                String provider = LocationManager.NETWORK_PROVIDER;
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    provider = LocationManager.GPS_PROVIDER;
                } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    provider = LocationManager.NETWORK_PROVIDER;
                } else if (locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)) {
                    provider = LocationManager.PASSIVE_PROVIDER;
                }
                locationManager.requestLocationUpdates(provider, LOC_UPDATE_INTERVAL, LOC_MIN_DISTANCE, locationListener);
            }

        } catch (Throwable e) {
        }
    }

    @SuppressWarnings("MissingPermission")
    public static void stop() {
        try {
            if (!PermissionUtil.hasLocationPermission(context)) {
                return;
            }

            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            locationManager.removeUpdates(locationListener);

        } catch (Throwable e) {

        }
    }


    private static LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            lastLocation = location;
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {
            start();
        }

        @Override
        public void onProviderDisabled(String provider) {
            start();
        }
    };


}
