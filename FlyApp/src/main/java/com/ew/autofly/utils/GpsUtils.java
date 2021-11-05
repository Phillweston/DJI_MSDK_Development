package com.ew.autofly.utils;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;

import java.util.ArrayList;


public class GpsUtils {
    private static GpsUtils mInstance = null;
    private Location mLocation;
    private LocationManager mLocationManager = null;

    public interface OnLocationChangedListener {
        void onChanged(Location location);
    }

    private ArrayList<OnLocationChangedListener> mChangedListener;

    public synchronized void addOnLocationChangedListener(OnLocationChangedListener listener) {
        if (listener == null)
            return;
        if (mChangedListener == null)
            mChangedListener = new ArrayList<>();
        mChangedListener.add(listener);
    }

    public synchronized void removeOnLocationChangedListener(OnLocationChangedListener listener) {
        if (listener == null)
            return;
        if (mChangedListener == null)
            return;
        if (mChangedListener.contains(listener))
            mChangedListener.remove(listener);
    }

    public synchronized void removeAllOnLocationChangedListener() {
        if (mChangedListener == null)
            return;
        mChangedListener.clear();
        mChangedListener = null;
    }

    private GpsUtils() {

    }

    public static GpsUtils getInstance() {
        if (mInstance == null) {
            synchronized (GpsUtils.class) {
                if (mInstance == null)
                    mInstance = new GpsUtils();
            }
        }
        return mInstance;
    }

    private LocationListener mGPSLocationListener = new LocationListener() {
        private boolean isRemove = false;

        @Override
        public void onLocationChanged(Location location) {
            mLocation = location;
          
            if (location != null && !isRemove) {
                if (location.getProvider() == LocationManager.GPS_PROVIDER) {
                    mLocationManager.removeUpdates(mNetworkLocationListener);
                    isRemove = true;
                    if (mChangedListener != null && mChangedListener.size() > 0) {
                        for (OnLocationChangedListener listener : mChangedListener) {
                            listener.onChanged(location);
                        }
                    }
                }
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            if (LocationProvider.OUT_OF_SERVICE == status) {
                try {
                    mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 0.0f, mNetworkLocationListener);
                } catch (SecurityException e) {

                }
            }
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    };

    private LocationListener mNetworkLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            mLocation = location;
            if (location != null) {
                if (mChangedListener != null && mChangedListener.size() > 0) {
                    for (OnLocationChangedListener listener : mChangedListener) {
                        listener.onChanged(location);
                    }
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
    };

    public void start(Context context) {
        startGps(context);
    }

    public void stop() {
        stopGps();
    }

    private void startGps(Context context) {
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            if (mLocationManager.getProvider("gps") == null &&
                    mLocationManager.getProvider("network") == null) {
                mLocationManager = null;
                return;
            }
            try {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 0.0f, mGPSLocationListener);
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 0.0f, mNetworkLocationListener);
            } catch (SecurityException e) {

            }
        }
    }

    private void stopGps() {
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(mGPSLocationListener);
            mLocationManager.removeUpdates(mNetworkLocationListener);
        }
        if(mChangedListener != null){
            mChangedListener.clear();
            mChangedListener = null;
        }
        mLocationManager = null;
    }

    public boolean isValid() {
        return mLocationManager != null && mLocation != null && mLocation.getLatitude() != 0.0;
    }

    public Location getLocation() {
        try {
            if (mLocationManager == null)
                return null;
            mLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (mLocation == null)
                mLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        } catch (SecurityException e) {

        }
        return mLocation;
    }

    public void destroy() {
        removeAllOnLocationChangedListener();
        stop();
        mInstance = null;
    }
}