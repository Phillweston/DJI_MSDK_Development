package com.ew.autofly.db.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;


@Entity
public class EPoint {
    @Id (autoincrement = true)
    Long id;
    String missionId;
    double longitude;
    double latitude;
    double homeLongitude;
    double homeLatitude;
    double altitude;
    double heading = 0;
    float gimbalPitch = -90.0f;
    float gimbalRoll;
    float gimbalYaw;
    double speed = 15.0D;
    boolean hasAction;
    int actionType;
    public int getActionType() {
        return this.actionType;
    }
    public void setActionType(int actionType) {
        this.actionType = actionType;
    }
    public boolean getHasAction() {
        return this.hasAction;
    }
    public void setHasAction(boolean hasAction) {
        this.hasAction = hasAction;
    }
    public double getSpeed() {
        return this.speed;
    }
    public void setSpeed(double speed) {
        this.speed = speed;
    }
    public float getGimbalYaw() {
        return this.gimbalYaw;
    }
    public void setGimbalYaw(float gimbalYaw) {
        this.gimbalYaw = gimbalYaw;
    }
    public float getGimbalRoll() {
        return this.gimbalRoll;
    }
    public void setGimbalRoll(float gimbalRoll) {
        this.gimbalRoll = gimbalRoll;
    }
    public float getGimbalPitch() {
        return this.gimbalPitch;
    }
    public void setGimbalPitch(float gimbalPitch) {
        this.gimbalPitch = gimbalPitch;
    }
    public double getHeading() {
        return this.heading;
    }
    public void setHeading(double heading) {
        this.heading = heading;
    }
    public double getAltitude() {
        return this.altitude;
    }
    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }
    public double getHomeLatitude() {
        return this.homeLatitude;
    }
    public void setHomeLatitude(double homeLatitude) {
        this.homeLatitude = homeLatitude;
    }
    public double getHomeLongitude() {
        return this.homeLongitude;
    }
    public void setHomeLongitude(double homeLongitude) {
        this.homeLongitude = homeLongitude;
    }
    public double getLatitude() {
        return this.latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public double getLongitude() {
        return this.longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    public String getMissionId() {
        return this.missionId;
    }
    public void setMissionId(String missionId) {
        this.missionId = missionId;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    @Generated(hash = 1952086655)
    public EPoint(Long id, String missionId, double longitude, double latitude,
            double homeLongitude, double homeLatitude, double altitude,
            double heading, float gimbalPitch, float gimbalRoll, float gimbalYaw,
            double speed, boolean hasAction, int actionType) {
        this.id = id;
        this.missionId = missionId;
        this.longitude = longitude;
        this.latitude = latitude;
        this.homeLongitude = homeLongitude;
        this.homeLatitude = homeLatitude;
        this.altitude = altitude;
        this.heading = heading;
        this.gimbalPitch = gimbalPitch;
        this.gimbalRoll = gimbalRoll;
        this.gimbalYaw = gimbalYaw;
        this.speed = speed;
        this.hasAction = hasAction;
        this.actionType = actionType;
    }
    @Generated(hash = 1759290670)
    public EPoint() {
    }
}