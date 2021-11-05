package com.ew.autofly.utils;

import android.content.Context;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

/**
 * GpsUtils2
 *
 * @author Ronny
 * @date 2018/1/22 14:11
 */

public class GpsUtils2 {

    private static AMapLocationClient mlocationClient;
    public static AMapLocationClientOption mLocationOption = null;

    public interface LocationListner {
        void result(AMapLocation location);
    }

    public static void init(Context context) {
        mlocationClient = new AMapLocationClient(context);
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setInterval(2000);
        mLocationOption.setLocationCacheEnable(false);
        mLocationOption.setNeedAddress(false);
        mLocationOption.setWifiScan(false);
        mLocationOption.setHttpTimeOut(5000);
        mlocationClient.setLocationOption(mLocationOption);
    }

    public static AMapLocationClient getLocationClient() {
        return mlocationClient;
    }

    public static AMapLocation getLastKnowLocaation() {
        if (mlocationClient == null)
            return null;
        return mlocationClient.getLastKnownLocation();
    }

    public static void getCurrentLocation(final LocationListner listner) {
        if (mlocationClient == null)
            return;
        mlocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation != null) {
                    listner.result(aMapLocation);
                    mlocationClient.unRegisterLocationListener(this);
                }
            }
        });
        mlocationClient.startLocation();
    }

    public static void stopLocationClient(boolean isdestroy) {
        if (mlocationClient == null)
            return;
        mlocationClient.stopLocation();
        if (isdestroy)
            destroy();
    }

    private static void destroy() {
        mlocationClient.onDestroy();
    }
}