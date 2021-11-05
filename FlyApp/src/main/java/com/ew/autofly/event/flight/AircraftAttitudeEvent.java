package com.ew.autofly.event.flight;


public class AircraftAttitudeEvent {

    private double yaw;

    public AircraftAttitudeEvent() {
    }

    public double getYaw() {
        return yaw;
    }

    public void setYaw(double yaw) {
        this.yaw = yaw;
    }
}
