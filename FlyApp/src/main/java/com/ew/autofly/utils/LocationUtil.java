package com.ew.autofly.utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.ew.autofly.entity.LocationCoordinate;
import com.ew.autofly.utils.coordinate.LocationCoordinateUtils;

import static android.content.Context.SENSOR_SERVICE;


public class LocationUtil {

    private Context mContext;

  
    public AMapLocationClient mLocationClient = null;

  
    public LocationListener mLocationListener = null;


    private static LocationUtil instance;

    public static synchronized LocationUtil getInstance() {
        if (null == instance) {
            instance = new LocationUtil();
        }
        return instance;
    }

    public LocationUtil() {

    }

    public LocationUtil init(final Context context) {
      
        mContext=context.getApplicationContext();
        mLocationClient = new AMapLocationClient(mContext);
        mLocationClient.setLocationOption(initOption());
        mLocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation amapLocation) {
                if (amapLocation != null) {
                    int code = amapLocation.getErrorCode();
                    if (mLocationListener != null) {
                        if (code == 0) {
                            LocationCoordinate locationCoordinate = LocationCoordinateUtils.gcj_To_Gps84(
                                    amapLocation.getLatitude(), amapLocation.getLongitude());
                            mLocationListener.result(locationCoordinate.getLongitude(),
                                    locationCoordinate.getLatitude());
                        } else {
                            mLocationListener.error(code);
                        }
                    }
                 
                }
            }
        });
        initSensorManager();
        return this;
    }

    private AMapLocationClientOption initOption() {

      
        AMapLocationClientOption locationOption = new AMapLocationClientOption();
        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        return locationOption;
    }

    public LocationUtil addLocationListener(LocationListener locationListener) {
        mLocationListener = locationListener;
        return this;
    }

    public void destroy(){
        removeLocationListener();
        mLocationClient.onDestroy();
    }

    public SensorManager initSensorManager() {
        SensorManager sensorManager = (SensorManager)mContext.getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(mSensorEventListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
        return sensorManager;
    }


    private SensorEventListener mSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
          
            float degree = event.values[0];
            if (mLocationListener != null) {
                mLocationListener.onRotate(degree);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    public void removeLocationListener(){
        mLocationListener=null;
    }

    public interface LocationListener {
        void result(double longitude, double latitude);

        void error(int errorCode);

        void onRotate(float degree);
    }

    public AMapLocationClient getLocationClient() {
        return mLocationClient;
    }


}
