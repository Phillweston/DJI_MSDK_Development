package com.ew.autofly.model;

import com.ew.autofly.logger.Logger;

import dji.common.accessory.AccessoryAggregationState;
import dji.common.error.DJIError;
import dji.common.util.CommonCallbacks;
import dji.sdk.accessory.AccessoryAggregation;
import dji.sdk.accessory.beacon.Beacon;
import dji.sdk.products.Aircraft;


public class AccessoryManager {

    public static AccessoryAggregation getAccessoryAggregation() {
        Aircraft aircraft = AircraftManager.getAircraft();
        if (aircraft != null && null != aircraft.getAccessoryAggregation()) {
            return aircraft.getAccessoryAggregation();
        }
        return null;
    }

    public static AccessoryAggregationState getAccessoryAggregationState() {
        AccessoryAggregation accessoryAggregation = getAccessoryAggregation();
        if (accessoryAggregation != null) {
            return accessoryAggregation.getAccessoryAggregationState();
        }
        return null;
    }

    public static void initAccessoryAggregation(AccessoryAggregationState.Callback callback) {

        AccessoryAggregation accessoryAggregation = getAccessoryAggregation();
        if (accessoryAggregation != null) {
            accessoryAggregation.setStateCallback(callback);
        }
    }

    public static void destroyAccessoryAggregation() {
        AccessoryAggregation accessoryAggregation = getAccessoryAggregation();
        if (accessoryAggregation != null) {
            accessoryAggregation.setStateCallback(null);
        }
    }

    public static void toggleBeacon() {
        Aircraft aircraft = AircraftManager.getAircraft();
        if (aircraft != null) {
            final AccessoryAggregation accessoryAggregation = aircraft.getAccessoryAggregation();
            if (accessoryAggregation != null) {
                final Beacon beacon = accessoryAggregation.getBeacon();

                if (beacon == null)
                    return;

                beacon.getEnabled(new CommonCallbacks.CompletionCallbackWith<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        beacon.setEnabled(!aBoolean, null);
                    }

                    @Override
                    public void onFailure(DJIError djiError) {
                        if (djiError != null) {
                            Logger.i(djiError.toString());
                        }
                    }
                });
            }
        }
    }
}
