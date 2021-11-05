package com.ew.autofly.interfaces;

import dji.common.battery.BatteryState;
import dji.common.flightcontroller.FlightControllerState;
import dji.common.gimbal.GimbalState;

public interface OnAircraftDialogClickListener {
    void onFlightControllerState(FlightControllerState flightControllerState);

    void onBatteryState(BatteryState batteryState);

    void onGimbalState(GimbalState gimbalState);
}