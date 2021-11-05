package com.ew.autofly.entity.airroute;

import com.ew.autofly.db.entity.FinePatrolWayPointDetail;

import java.io.Serializable;
import java.util.List;


public class BaseAirRoute implements Serializable {


    private boolean isConfirm=false;


    private String createdTime;

    private String creator;

    private String company;

    private String workgroup;

    private String aircraftName;
    
    private String cameraName;

    private int airRouteType;

    private double baseLocationLatitude;

    private double baseLocationLongitude;

    private double baseLocationAltitude;

    private float towerAltitude;

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getWorkgroup() {
        return workgroup;
    }

    public void setWorkgroup(String workgroup) {
        this.workgroup = workgroup;
    }

    public String getAircraftName() {
        return aircraftName;
    }

    public void setAircraftName(String aircraftName) {
        this.aircraftName = aircraftName;
    }

    public String getCameraName() {
        return cameraName;
    }

    public void setCameraName(String cameraName) {
        this.cameraName = cameraName;
    }

    public int getAirRouteType() {
        return airRouteType;
    }

    public void setAirRouteType(int airRouteType) {
        this.airRouteType = airRouteType;
    }

    public double getBaseLocationLatitude() {
        return baseLocationLatitude;
    }

    public void setBaseLocationLatitude(double baseLocationLatitude) {
        this.baseLocationLatitude = baseLocationLatitude;
    }

    public double getBaseLocationLongitude() {
        return baseLocationLongitude;
    }

    public void setBaseLocationLongitude(double baseLocationLongitude) {
        this.baseLocationLongitude = baseLocationLongitude;
    }

    public double getBaseLocationAltitude() {
        return baseLocationAltitude;
    }

    public void setBaseLocationAltitude(double baseLocationAltitude) {
        this.baseLocationAltitude = baseLocationAltitude;
    }

    public float getTowerAltitude() {
        return towerAltitude;
    }

    public void setTowerAltitude(float towerAltitude) {
        this.towerAltitude = towerAltitude;
    }

    public boolean isConfirm() {
        return isConfirm;
    }

    public void setConfirm(boolean confirm) {
        isConfirm = confirm;
    }
}

