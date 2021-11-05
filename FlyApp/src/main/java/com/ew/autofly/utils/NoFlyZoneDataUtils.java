package com.ew.autofly.utils;

import android.content.Context;

import com.ew.autofly.entity.LatLngInfo;
import com.ew.autofly.interfaces.OnGetNoFlyZoneDataListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dji.common.error.DJIError;
import dji.common.flightcontroller.flyzone.FlyZoneInformation;
import dji.common.flightcontroller.flyzone.SubFlyZoneInformation;
import dji.common.util.CommonCallbacks;
import dji.sdk.flightcontroller.FlyZoneManager;



public class NoFlyZoneDataUtils {

    private static NoFlyZoneDataUtils mInstance = null;
    private HashMap<Integer, List<LatLngInfo>> hashMap;

    public NoFlyZoneDataUtils() {
        hashMap = new HashMap<>();
    }

    public static NoFlyZoneDataUtils getInstance() {
        if (mInstance == null) {
            synchronized (NoFlyZoneDataUtils.class) {
                if (mInstance == null) {
                    mInstance = new NoFlyZoneDataUtils();
                }
            }
        }
        return mInstance;
    }

    public void getNoFlyZoneData(Context context ,final OnGetNoFlyZoneDataListener listener) {
        final ArrayList<SubFlyZoneInformation[]> subFlyZoneInformations = new ArrayList<>();
        FlyZoneManager manager = new FlyZoneManager(context);
        manager.getFlyZonesInSurroundingArea(new CommonCallbacks.CompletionCallbackWith<ArrayList<FlyZoneInformation>>() {
            @Override
            public void onSuccess(ArrayList<FlyZoneInformation> flyZoneInformations) {
                for (FlyZoneInformation flyZoneInformation : flyZoneInformations) {
                    SubFlyZoneInformation[] subFlyZones = flyZoneInformation.getSubFlyZones();
                    subFlyZoneInformations.add(subFlyZones);
                }
                listener.onGetNoFlyZoneSuccess(subFlyZoneInformations);
            }

            @Override
            public void onFailure(final DJIError djiError) {
                listener.onGetNoFlyZoneFailed(djiError.toString());
            }
        });
    }
}
