package com.ew.autofly.model;

import android.text.TextUtils;

import com.ew.autofly.internal.key.base.FlyKeyManager;
import com.ew.autofly.key.config.NTRIPConfigKey;
import com.ew.autofly.key.config.RTKConfigKey;

import dji.common.flightcontroller.rtk.CoordinateSystem;
import dji.common.flightcontroller.rtk.NetworkServiceSettings;
import dji.common.flightcontroller.rtk.ReferenceStationSource;


public class DataManager {

    public static CoordinateSystem getRtkCoordinateSystem(){
        Object object = FlyKeyManager.getInstance().getValue(RTKConfigKey
                .create(RTKConfigKey.RTK_COORDINATE_SYSTEM));
        int source = (int) object;
        CoordinateSystem coordinateSystem = CoordinateSystem.WGS84;
        switch (source) {
            case 0:
                coordinateSystem =CoordinateSystem.WGS84;
                break;
            case 1:
                coordinateSystem =CoordinateSystem.CGCS2000;
                break;
        }
        return coordinateSystem;
    }

    public static void setRtkCoordinateSystem(CoordinateSystem coordinateSystem) {
        int source = 0;
        switch (coordinateSystem) {
            case WGS84:
                source = 0;
                break;
            case CGCS2000:
                source = 1;
                break;
        }
        FlyKeyManager.getInstance().setValue(RTKConfigKey
                .create(RTKConfigKey.RTK_COORDINATE_SYSTEM), source);
    }

    public static ReferenceStationSource getRtkReferenceStationSource() {
        Object object = FlyKeyManager.getInstance().getValue(RTKConfigKey
                .create(RTKConfigKey.RTK_REFERENCE_STATION_SOURCE));
        int source = (int) object;
        ReferenceStationSource referenceStationSource = ReferenceStationSource.NETWORK_RTK;
        switch (source) {
            case 0:
                referenceStationSource = ReferenceStationSource.NETWORK_RTK;
                break;
            case 1:
                referenceStationSource = ReferenceStationSource.CUSTOM_NETWORK_SERVICE;
                break;
            case 2:
                referenceStationSource = ReferenceStationSource.NONE;
                break;
        }
        return referenceStationSource;
    }

    public static void setRtkReferenceStationSource(ReferenceStationSource referenceStationSource) {
        int source = 0;
        switch (referenceStationSource) {
            case NETWORK_RTK:
                source = 0;
                break;
            case CUSTOM_NETWORK_SERVICE:
                source = 1;
                break;
            case NONE:
                source = 2;
                break;
        }
        FlyKeyManager.getInstance().setValue(RTKConfigKey
                .create(RTKConfigKey.RTK_REFERENCE_STATION_SOURCE), source);
    }

    public static NetworkServiceSettings getNetworkServiceSettings() {
        String address = getNTRIPConfigKeyValue(NTRIPConfigKey.RTK_NETWORK_SERVICE_ADDRESS);
        if (TextUtils.isEmpty(address)) {
            return null;
        }
        String port = getNTRIPConfigKeyValue(NTRIPConfigKey.RTK_NETWORK_SERVICE_PORT);
        if (TextUtils.isEmpty(port)) {
            return null;
        }

        String mountPoint = getNTRIPConfigKeyValue(NTRIPConfigKey.RTK_NETWORK_SERVICE_MOUNTPOINT);
        if (TextUtils.isEmpty(mountPoint)) {
            return null;
        }

        String account = getNTRIPConfigKeyValue(NTRIPConfigKey.RTK_NETWORK_SERVICE_ACCOUNT);
        if (TextUtils.isEmpty(account)) {
            return null;
        }

        String password = getNTRIPConfigKeyValue(NTRIPConfigKey.RTK_NETWORK_SERVICE_PASSWORD);
        if (TextUtils.isEmpty(password)) {
            return null;
        }

        return new NetworkServiceSettings.Builder()
                .ip(address)
                .port(Integer.valueOf(port))
                .mountPoint(mountPoint)
                .userName(account)
                .password(password)
                .build();
    }

    private static String getNTRIPConfigKeyValue(String key) {
        Object object = FlyKeyManager.getInstance().getValue(NTRIPConfigKey
                .create(key));
        if (object instanceof String) {
            return (String) object;
        }
        return null;
    }
}
