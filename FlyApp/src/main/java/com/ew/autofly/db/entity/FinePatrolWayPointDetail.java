package com.ew.autofly.db.entity;

import com.ew.autofly.entity.LocationCoordinate;

import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;
import java.util.List;


public class FinePatrolWayPointDetail implements Serializable {

    @Transient
    private static final long serialVersionUID = 1L;


    private double aircraftLocationLatitude;
    private double aircraftLocationLongitude;
    private float aircraftLocationAltitude;


    private double aircraftYaw;


    private float gimbalPitch;

    
    private int focalLength;

    public int getFocalLength() {
        return focalLength;
    }

    public void setFocalLength(int focalLength) {
        this.focalLength = focalLength;
    }

    
    private int waypointType;

    private List<PhotoPosition> photoPositionList;

    public double getAircraftLocationLatitude() {
        return aircraftLocationLatitude;
    }

    public void setAircraftLocationLatitude(double aircraftLocationLatitude) {
        this.aircraftLocationLatitude = aircraftLocationLatitude;
    }

    public double getAircraftLocationLongitude() {
        return aircraftLocationLongitude;
    }

    public void setAircraftLocationLongitude(double aircraftLocationLongitude) {
        this.aircraftLocationLongitude = aircraftLocationLongitude;
    }

    public float getAircraftLocationAltitude() {
        return aircraftLocationAltitude;
    }

    public void setAircraftLocationAltitude(float aircraftLocationAltitude) {
        this.aircraftLocationAltitude = aircraftLocationAltitude;
    }

    public double getAircraftYaw() {
        return aircraftYaw;
    }

    public void setAircraftYaw(double aircraftYaw) {
        this.aircraftYaw = aircraftYaw;
    }

    public float getGimbalPitch() {
        return gimbalPitch;
    }

    /**
     * 大疆waypoint设置的云台角度为负数
     *
     * @return
     */
    public float getWaypointGimbalPitch() {
        return gimbalPitch - 90;
    }

    public void setGimbalPitch(float gimbalPitch) {
        this.gimbalPitch = gimbalPitch;
    }

    public int getWaypointType() {
        return waypointType;
    }

    public void setWaypointType(int waypointType) {
        this.waypointType = waypointType;
    }

    public List<PhotoPosition> getPhotoPositionList() {
        return photoPositionList;
    }

    public void setPhotoPositionList(List<PhotoPosition> photoPositionList) {
        this.photoPositionList = photoPositionList;
    }

    public LocationCoordinate getAirCraftLocation(){
        return new LocationCoordinate(aircraftLocationLatitude,aircraftLocationLongitude,aircraftLocationAltitude);
    }
}
