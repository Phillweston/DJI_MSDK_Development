package com.ew.autofly.db.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;



@Entity(nameInDb = "flight_record_detail")
public class FlightRecordDetail {

    @Id(autoincrement = true)
    @Property(nameInDb = "id")
    private Long id;

    @Property(nameInDb = "record_id")
    private String recordId;

    @Property(nameInDb = "latitude")
    private double latitude;

    @Property(nameInDb = "longitude")
    private double longitude;

    @Property(nameInDb = "altitude")
    private double altitude;

    @Property(nameInDb = "distance")
    private double distance;

    @Property(nameInDb = "horizontal_speed")
    private int horizontalSpeed;

    @Property(nameInDb = "vertical_speed")
    private int verticalSpeed;

    @Property(nameInDb = "battery_level")
    private double batteryLevel;

    @Property(nameInDb = "gps_mode")
    private String GPSMode;

    @Property(nameInDb = "angle")
    private double angle;

    @Property(nameInDb = "left_stick_vertical_pos")
    private double leftStickVerticalPosition;

    @Property(nameInDb = "left_stick_horizontal_pos")
    private double leftStickHorizontalPosition;

    @Property(nameInDb = "right_stick_vertical_pos")
    private double rightStickVerticalPosition;

    @Property(nameInDb = "right_stick_horizontal_pos")
    private double rightStickHorizontalPosition;

    @Property(nameInDb = "created_time")
    private String createdTime;

    @Property(nameInDb = "gps_satellite_count")
    private int GPSSatelliteCount;

    @Property(nameInDb = "mission_id")
    private String missionId;

    @Generated(hash = 1219473948)
    public FlightRecordDetail(Long id, String recordId, double latitude, double longitude,
            double altitude, double distance, int horizontalSpeed, int verticalSpeed,
            double batteryLevel, String GPSMode, double angle,
            double leftStickVerticalPosition, double leftStickHorizontalPosition,
            double rightStickVerticalPosition, double rightStickHorizontalPosition,
            String createdTime, int GPSSatelliteCount, String missionId) {
        this.id = id;
        this.recordId = recordId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.distance = distance;
        this.horizontalSpeed = horizontalSpeed;
        this.verticalSpeed = verticalSpeed;
        this.batteryLevel = batteryLevel;
        this.GPSMode = GPSMode;
        this.angle = angle;
        this.leftStickVerticalPosition = leftStickVerticalPosition;
        this.leftStickHorizontalPosition = leftStickHorizontalPosition;
        this.rightStickVerticalPosition = rightStickVerticalPosition;
        this.rightStickHorizontalPosition = rightStickHorizontalPosition;
        this.createdTime = createdTime;
        this.GPSSatelliteCount = GPSSatelliteCount;
        this.missionId = missionId;
    }

    @Generated(hash = 2129222552)
    public FlightRecordDetail() {
    }

    public String getMissionId() {
        return this.missionId;
    }

    public void setMissionId(String missionId) {
        this.missionId = missionId;
    }

    public int getGPSSatelliteCount() {
        return this.GPSSatelliteCount;
    }

    public void setGPSSatelliteCount(int GPSSatelliteCount) {
        this.GPSSatelliteCount = GPSSatelliteCount;
    }

    public String getCreatedTime() {
        return this.createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public double getRightStickHorizontalPosition() {
        return this.rightStickHorizontalPosition;
    }

    public void setRightStickHorizontalPosition(double rightStickHorizontalPosition) {
        this.rightStickHorizontalPosition = rightStickHorizontalPosition;
    }

    public double getRightStickVerticalPosition() {
        return this.rightStickVerticalPosition;
    }

    public void setRightStickVerticalPosition(double rightStickVerticalPosition) {
        this.rightStickVerticalPosition = rightStickVerticalPosition;
    }

    public double getLeftStickHorizontalPosition() {
        return this.leftStickHorizontalPosition;
    }

    public void setLeftStickHorizontalPosition(double leftStickHorizontalPosition) {
        this.leftStickHorizontalPosition = leftStickHorizontalPosition;
    }

    public double getLeftStickVerticalPosition() {
        return this.leftStickVerticalPosition;
    }

    public void setLeftStickVerticalPosition(double leftStickVerticalPosition) {
        this.leftStickVerticalPosition = leftStickVerticalPosition;
    }

    public double getAngle() {
        return this.angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public String getGPSMode() {
        return this.GPSMode;
    }

    public void setGPSMode(String GPSMode) {
        this.GPSMode = GPSMode;
    }

    public double getBatteryLevel() {
        return this.batteryLevel;
    }

    public void setBatteryLevel(double batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public double getDistance() {
        return this.distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getAltitude() {
        return this.altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getRecordId() {
        return this.recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getVerticalSpeed() {
        return this.verticalSpeed;
    }

    public void setVerticalSpeed(int verticalSpeed) {
        this.verticalSpeed = verticalSpeed;
    }

    public int getHorizontalSpeed() {
        return this.horizontalSpeed;
    }

    public void setHorizontalSpeed(int horizontalSpeed) {
        this.horizontalSpeed = horizontalSpeed;
    }
    
}
