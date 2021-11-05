package com.ew.autofly.db.entity;

import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Transient;



@Entity(nameInDb = "t_Tower_StudyDetail_v2")
public class TowerStudyDetailV2 implements Serializable{

    @Transient
    private static final long serialVersionUID = 1L;

    @Id(autoincrement = true)
    private Long id;


    private String towerId;


    private double homeLocationLatitude;
    private double homeLocationLongitude;


    private double aircraftLocationLatitude;
    private double aircraftLocationLongitude;
    private float aircraftLocationAltitude;


    private double baseLocationLatitude;
    private double baseLocationLongitude;
    private float baseLocationAltitude;


    private double aircraftYaw;

    private float aircraftPitch;

    private float aircraftRoll;


    private double gimbalYaw;

    private float gimbalPitch;


    private float gimbalRoll;


    private int missionType;


    private int missionMode;


    private int waypointType;


    private String cameraName;


    private String aircraftName;


    private Date createdTime;

    @Transient
    private List<PhotoPosition> photoPositionList;

    public List<PhotoPosition> getPhotoPositionList() {
        return photoPositionList;
    }

    public void setPhotoPositionList(List<PhotoPosition> photoPositionList) {
        this.photoPositionList = photoPositionList;
    }

    public Date getCreatedTime() {
        return this.createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public String getAircraftName() {
        return this.aircraftName;
    }

    public void setAircraftName(String aircraftName) {
        this.aircraftName = aircraftName;
    }

    public String getCameraName() {
        return this.cameraName;
    }

    public void setCameraName(String cameraName) {
        this.cameraName = cameraName;
    }

    public int getWaypointType() {
        return this.waypointType;
    }

    public void setWaypointType(int waypointType) {
        this.waypointType = waypointType;
    }

    public int getMissionMode() {
        return this.missionMode;
    }

    public void setMissionMode(int missionMode) {
        this.missionMode = missionMode;
    }

    public int getMissionType() {
        return this.missionType;
    }

    public void setMissionType(int missionType) {
        this.missionType = missionType;
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

    public double getGimbalYaw() {
        return this.gimbalYaw;
    }

    public void setGimbalYaw(double gimbalYaw) {
        this.gimbalYaw = gimbalYaw;
    }

    public float getAircraftRoll() {
        return this.aircraftRoll;
    }

    public void setAircraftRoll(float aircraftRoll) {
        this.aircraftRoll = aircraftRoll;
    }

    public float getAircraftPitch() {
        return this.aircraftPitch;
    }

    public void setAircraftPitch(float aircraftPitch) {
        this.aircraftPitch = aircraftPitch;
    }

    public double getAircraftYaw() {
        return this.aircraftYaw;
    }

    public void setAircraftYaw(double aircraftYaw) {
        this.aircraftYaw = aircraftYaw;
    }

    public float getBaseLocationAltitude() {
        return this.baseLocationAltitude;
    }

    public void setBaseLocationAltitude(float baseLocationAltitude) {
        this.baseLocationAltitude = baseLocationAltitude;
    }

    public double getBaseLocationLongitude() {
        return this.baseLocationLongitude;
    }

    public void setBaseLocationLongitude(double baseLocationLongitude) {
        this.baseLocationLongitude = baseLocationLongitude;
    }

    public double getBaseLocationLatitude() {
        return this.baseLocationLatitude;
    }

    public void setBaseLocationLatitude(double baseLocationLatitude) {
        this.baseLocationLatitude = baseLocationLatitude;
    }

    public float getAircraftLocationAltitude() {
        return this.aircraftLocationAltitude;
    }

    public void setAircraftLocationAltitude(float aircraftLocationAltitude) {
        this.aircraftLocationAltitude = aircraftLocationAltitude;
    }

    public double getAircraftLocationLongitude() {
        return this.aircraftLocationLongitude;
    }

    public void setAircraftLocationLongitude(double aircraftLocationLongitude) {
        this.aircraftLocationLongitude = aircraftLocationLongitude;
    }

    public double getAircraftLocationLatitude() {
        return this.aircraftLocationLatitude;
    }

    public void setAircraftLocationLatitude(double aircraftLocationLatitude) {
        this.aircraftLocationLatitude = aircraftLocationLatitude;
    }

    public double getHomeLocationLongitude() {
        return this.homeLocationLongitude;
    }

    public void setHomeLocationLongitude(double homeLocationLongitude) {
        this.homeLocationLongitude = homeLocationLongitude;
    }

    public double getHomeLocationLatitude() {
        return this.homeLocationLatitude;
    }

    public void setHomeLocationLatitude(double homeLocationLatitude) {
        this.homeLocationLatitude = homeLocationLatitude;
    }

    public String getTowerId() {
        return this.towerId;
    }

    public void setTowerId(String towerId) {
        this.towerId = towerId;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Generated(hash = 1768047150)
    public TowerStudyDetailV2(Long id, String towerId, double homeLocationLatitude,
            double homeLocationLongitude, double aircraftLocationLatitude,
            double aircraftLocationLongitude, float aircraftLocationAltitude,
            double baseLocationLatitude, double baseLocationLongitude,
            float baseLocationAltitude, double aircraftYaw, float aircraftPitch,
            float aircraftRoll, double gimbalYaw, float gimbalPitch,
            float gimbalRoll, int missionType, int missionMode, int waypointType,
            String cameraName, String aircraftName, Date createdTime) {
        this.id = id;
        this.towerId = towerId;
        this.homeLocationLatitude = homeLocationLatitude;
        this.homeLocationLongitude = homeLocationLongitude;
        this.aircraftLocationLatitude = aircraftLocationLatitude;
        this.aircraftLocationLongitude = aircraftLocationLongitude;
        this.aircraftLocationAltitude = aircraftLocationAltitude;
        this.baseLocationLatitude = baseLocationLatitude;
        this.baseLocationLongitude = baseLocationLongitude;
        this.baseLocationAltitude = baseLocationAltitude;
        this.aircraftYaw = aircraftYaw;
        this.aircraftPitch = aircraftPitch;
        this.aircraftRoll = aircraftRoll;
        this.gimbalYaw = gimbalYaw;
        this.gimbalPitch = gimbalPitch;
        this.gimbalRoll = gimbalRoll;
        this.missionType = missionType;
        this.missionMode = missionMode;
        this.waypointType = waypointType;
        this.cameraName = cameraName;
        this.aircraftName = aircraftName;
        this.createdTime = createdTime;
    }

    @Generated(hash = 1633513468)
    public TowerStudyDetailV2() {
    }


}
