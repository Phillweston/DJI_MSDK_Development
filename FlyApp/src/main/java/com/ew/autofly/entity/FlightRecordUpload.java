package com.ew.autofly.entity;

import java.io.Serializable;
import java.util.List;



public class FlightRecordUpload implements Serializable{

    private String userId;

    private String userName;

    private double longitude;

    private double latitude;

    private String droneModel;

    private double distance;

    private double flightTime;

    private double maxAltitude;

    private String startTime;

    private String endTime;

    private String flightSerialNumber;

    private String batterySerialNumber;

    private String createdTime;

    private String startTimeStart;

    private String startTimeEnd;

    private List<LatLngInfo> flightRecordDetailList;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getFlightTime() {
        return flightTime;
    }

    public void setFlightTime(double flightTime) {
        this.flightTime = flightTime;
    }

    public double getMaxAltitude() {
        return maxAltitude;
    }

    public void setMaxAltitude(double maxAltitude) {
        this.maxAltitude = maxAltitude;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
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

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getStartTimeStart() {
        return startTimeStart;
    }

    public void setStartTimeStart(String startTimeStart) {
        this.startTimeStart = startTimeStart;
    }

    public String getStartTimeEnd() {
        return startTimeEnd;
    }

    public void setStartTimeEnd(String startTimeEnd) {
        this.startTimeEnd = startTimeEnd;
    }

    public List<LatLngInfo> getFlightRecordDetailList() {
        return flightRecordDetailList;
    }

    public void setFlightRecordDetailList(List<LatLngInfo> flightRecordDetailList) {
        this.flightRecordDetailList = flightRecordDetailList;
    }
}
