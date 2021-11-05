package com.ew.autofly.entity;

import java.io.Serializable;



public class FlightMonitorInfo implements Serializable {

    
     private String userName;

    
    private String deviceNo;
    
    private double longitude;
    
    private double latitude;

    
    private String droneModel;

    
    private int missionType;

    
    private String missionTypeName;

    
    private double height;

    
    private double verticalSpeed;

    
    private double horizontalSpeed;

    
    private int batteryRemain;

    
    private int batteryTemperature;

    
    private int angle;
    
    private double dronePitch;
    
    private double droneRoll;
    
    private int roll;
    
    private int yaw;
    
    private int pitch;

    
    private String uploadTime;

    
    private String createdTime;

    
    private String flightSerialNumber;

    
    private String batterySerialNumber;

    
    private int gpsCount;

    
    private String cameraModel;

    
    private double rudderVerticalLeft;
    
    private double rudderHorizontalLeft;
    
    private double rudderVerticalRight;
    
    private double rudderHorizontalRight;

    
    private String missionId;

    
    private long subMissionId;

    
    private long flightTime;

    
    private double flightDistance;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDeviceNo() {
        return deviceNo;
    }

    public void setDeviceNo(String deviceNo) {
        this.deviceNo = deviceNo;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getDroneModel() {
        return droneModel;
    }

    public void setDroneModel(String droneModel) {
        this.droneModel = droneModel;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getVerticalSpeed() {
        return verticalSpeed;
    }

    public void setVerticalSpeed(double verticalSpeed) {
        this.verticalSpeed = verticalSpeed;
    }

    public double getHorizontalSpeed() {
        return horizontalSpeed;
    }

    public void setHorizontalSpeed(double horizontalSpeed) {
        this.horizontalSpeed = horizontalSpeed;
    }

    public int getBatteryRemain() {
        return batteryRemain;
    }

    public void setBatteryRemain(int batteryRemain) {
        this.batteryRemain = batteryRemain;
    }

    public int getBatteryTemperature() {
        return batteryTemperature;
    }

    public void setBatteryTemperature(int batteryTemperature) {
        this.batteryTemperature = batteryTemperature;
    }

    public int getAngle() {
        return angle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }

    public int getRoll() {
        return roll;
    }

    public void setRoll(int roll) {
        this.roll = roll;
    }

    public int getYaw() {
        return yaw;
    }

    public void setYaw(int yaw) {
        this.yaw = yaw;
    }

    public int getPitch() {
        return pitch;
    }

    public void setPitch(int pitch) {
        this.pitch = pitch;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public int getMissionType() {
        return missionType;
    }

    public void setMissionType(int missionType) {
        this.missionType = missionType;
    }

    public String getMissionTypeName() {
        return missionTypeName;
    }

    public void setMissionTypeName(String missionTypeName) {
        this.missionTypeName = missionTypeName;
    }

    public String getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getFlightSerialNumber() {
        return flightSerialNumber;
    }

    public void setFlightSerialNumber(String flightSerialNumber) {
        this.flightSerialNumber = flightSerialNumber;
    }

    public String getBatterySerialNumber() {
        return batterySerialNumber;
    }

    public void setBatterySerialNumber(String batterySerialNumber) {
        this.batterySerialNumber = batterySerialNumber;
    }

    public int getGpsCount() {
        return gpsCount;
    }

    public void setGpsCount(int gpsCount) {
        this.gpsCount = gpsCount;
    }

    public String getCameraModel() {
        return cameraModel;
    }

    public void setCameraModel(String cameraModel) {
        this.cameraModel = cameraModel;
    }

    public double getRudderVerticalLeft() {
        return rudderVerticalLeft;
    }

    public void setRudderVerticalLeft(double rudderVerticalLeft) {
        this.rudderVerticalLeft = rudderVerticalLeft;
    }

    public double getRudderHorizontalLeft() {
        return rudderHorizontalLeft;
    }

    public void setRudderHorizontalLeft(double rudderHorizontalLeft) {
        this.rudderHorizontalLeft = rudderHorizontalLeft;
    }

    public double getRudderVerticalRight() {
        return rudderVerticalRight;
    }

    public void setRudderVerticalRight(double rudderVerticalRight) {
        this.rudderVerticalRight = rudderVerticalRight;
    }

    public double getRudderHorizontalRight() {
        return rudderHorizontalRight;
    }

    public void setRudderHorizontalRight(double rudderHorizontalRight) {
        this.rudderHorizontalRight = rudderHorizontalRight;
    }

    public String getMissionId() {
        return missionId;
    }

    public void setMissionId(String missionId) {
        this.missionId = missionId;
    }

    public double getDronePitch() {
        return dronePitch;
    }

    public void setDronePitch(double dronePitch) {
        this.dronePitch = dronePitch;
    }

    public double getDroneRoll() {
        return droneRoll;
    }

    public void setDroneRoll(double droneRoll) {
        this.droneRoll = droneRoll;
    }

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

    public long getSubMissionId() {
        return subMissionId;
    }

    public void setSubMissionId(long subMissionId) {
        this.subMissionId = subMissionId;
    }

    @Override
    public String toString() {
        return "Monitor{" +
                ", deviceNo='" + deviceNo + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", droneModel='" + droneModel + '\'' +
                ", height=" + height +
                ", verticalSpeed=" + verticalSpeed +
                ", horizontalSpeed=" + horizontalSpeed +
                ", batteryRemain=" + batteryRemain +
                ", batteryTemperature=" + batteryTemperature +
                ", angle=" + angle +
                ", roll=" + roll +
                ", yaw=" + yaw +
                ", pitch=" + pitch +
                ", createdTime=" + createdTime +
                '}';
    }
}
