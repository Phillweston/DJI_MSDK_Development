package com.ew.autofly.entity;

import java.io.Serializable;
import java.util.UUID;

import dji.common.mission.waypoint.Waypoint;

/**
 *gps84坐标航点任务
 */
@Deprecated
public class WayPointTask implements Serializable {

    private static final long serialVersionUID = 1L;

    private UUID id;
    private LatLngInfo position;
    private double altitude;
    private double headAngle;

    private boolean wayPointFlag;
    private int status;


    private Tower mTower;

    public Tower getTower() {
        return mTower;
    }

    public void setTower(Tower tower) {
        mTower = tower;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public LatLngInfo getPosition() {
        return position;
    }

    public void setPosition(LatLngInfo position) {
        this.position = position;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public double getHeadAngle() {
        return headAngle;
    }

    public void setHeadAngle(double headAngle) {
        this.headAngle = headAngle;
    }

    public boolean isWayPointFlag() {
        return wayPointFlag;
    }

    public void setWayPointFlag(boolean wayPointFlag) {
        this.wayPointFlag = wayPointFlag;
    }
}
