package com.ew.autofly.entity.flightRecord;

import com.ew.autofly.entity.LocationCoordinate;
import com.ew.autofly.model.AircraftManager;
import com.ew.autofly.model.CameraManager;

import dji.common.battery.BatteryState;
import dji.common.flightcontroller.FlightControllerState;
import dji.common.gimbal.Attitude;
import dji.common.gimbal.GimbalState;
import dji.common.product.Model;
import dji.common.remotecontroller.HardwareState;
import dji.sdk.camera.Camera;


public class FlightUploadStates {

    private long flightTime;
    private double flightDistance;

    private String aircraftSerialNumber;

    private String batterySerialNumber;

    private LocationCoordinate aircraftLocation;

    private FlightControllerState flightControllerState;

    private HardwareState hardwareState;

    private BatteryState batteryState;

    private GimbalState gimbalState;

    public long getFlightTime() {
        return flightTime;
    }

    public void setFlightTime(long flightTime) {
        this.flightTime = flightTime;
    }

    public double getFlightDistance() {
        return flightDistance;
    }

    public void setFlightDistance(double flightDistance) {
        this.flightDistance = flightDistance;
    }

    public LocationCoordinate getAircraftLocation() {
        return aircraftLocation;
    }

    public void setAircraftLocation(LocationCoordinate aircraftLocation) {
        this.aircraftLocation = aircraftLocation;
    }

    public FlightControllerState getFlightControllerState() {
        return flightControllerState;
    }

    public void setFlightControllerState(FlightControllerState flightControllerState) {
        this.flightControllerState = flightControllerState;
    }

    public HardwareState getHardwareState() {
        return hardwareState;
    }

    public void setHardwareState(HardwareState hardwareState) {
        this.hardwareState = hardwareState;
    }

    public BatteryState getBatteryState() {
        return batteryState;
    }

    public void setBatteryState(BatteryState batteryState) {
        this.batteryState = batteryState;
    }

    public GimbalState getGimbalState() {
        return gimbalState;
    }

    public void setGimbalState(GimbalState gimbalState) {
        this.gimbalState = gimbalState;
    }

    public String getAircraftSerialNumber() {
        return aircraftSerialNumber;
    }

    public void setAircraftSerialNumber(String aircraftSerialNumber) {
        this.aircraftSerialNumber = aircraftSerialNumber;
    }

    public String getBatterySerialNumber() {
        return batterySerialNumber;
    }

    public void setBatterySerialNumber(String batterySerialNumber) {
        this.batterySerialNumber = batterySerialNumber;
    }

    public FlightInfo getFlightUploadData() {

        FlightInfo flightInfo = new FlightInfo();

        if (flightControllerState != null) {
            float velocityX = flightControllerState.getVelocityX();
            float velocityY = flightControllerState.getVelocityY();
            float velocityZ = flightControllerState.getVelocityZ();

            flightInfo.setVspeed(velocityZ);
            flightInfo.setHspeed(Math.sqrt(Math.pow(velocityX, 2) + Math.pow(velocityY, 2)));

            dji.common.flightcontroller.Attitude attitude = flightControllerState.getAttitude();

            flightInfo.setAircraftYaw(attitude.yaw);
            flightInfo.setAircraftPitch(attitude.pitch);
            flightInfo.setAircraftRoll(attitude.roll);

            flightInfo.setGpsCount(flightControllerState.getSatelliteCount());
        }

        if (aircraftLocation != null) {
            flightInfo.setLongitude(aircraftLocation.getLongitude());
            flightInfo.setLatitude(aircraftLocation.getLatitude());
            flightInfo.setAltitude(aircraftLocation.getAltitude());
        }

        if (batteryState != null) {
            flightInfo.setBatteryRemain(batteryState.getChargeRemainingInPercent());
            flightInfo.setBatteryTemperature(batteryState.getTemperature());
        }

        if (gimbalState != null) {
            Attitude attitudeInDegrees = gimbalState.getAttitudeInDegrees();
            if (attitudeInDegrees != null) {
                flightInfo.setGimbalYaw(attitudeInDegrees.getYaw());
                flightInfo.setGimbalPitch(attitudeInDegrees.getPitch());
                flightInfo.setGimbalRoll(attitudeInDegrees.getRoll());
            }
        }

        Model model = AircraftManager.getModel();
        if (model != null) {
            String displayName = model.getDisplayName();
            flightInfo.setAircraftModel(displayName);
        }

        Camera camera = CameraManager.getCamera();
        if (camera != null) {
            String displayName = camera.getDisplayName();
            flightInfo.setCameraModel(displayName);
        }

        flightInfo.setFlightDistance(flightDistance);
        flightInfo.setFlightTime(flightTime);

        flightInfo.setAircraftSerialNumber(aircraftSerialNumber);
        flightInfo.setBatterySerialNumber(batterySerialNumber);

        return flightInfo;
    }
}
