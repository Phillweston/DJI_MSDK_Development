package com.ew.autofly.event;

import dji.common.battery.BatteryState;



public class BatteryStateEvent {

    private BatteryState batteryState;

    public BatteryStateEvent(BatteryState batteryState) {
        this.batteryState = batteryState;
    }

    public BatteryState getBatteryState() {
        return batteryState;
    }
}
