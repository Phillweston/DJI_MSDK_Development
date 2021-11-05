package com.ew.autofly.entity;


import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class MissionBatch2 implements Serializable {
    private static final long serialVersionUID = 1L;
  
    private String id;
    private String name;
    private int status;
    private String snapShot;
    private int sideOverlap;
    private int routeOverlap;
    private Date createDate;
    private int altitude;
    private double resolutionRate;
  
    private int flightNum;
    private String workMode;
    private boolean fixedAltitude;
    private int buffer;
    private List<Mission> missionList;
    private int createUserId = 0;
    private boolean isCloud;

    public int getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(int createUserId) {
        this.createUserId = createUserId;
    }

    public boolean isCloud() {
        return isCloud;
    }

    public void setCloud(boolean cloud) {
        isCloud = cloud;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getSnapShot() {
        return snapShot;
    }

    public void setSnapShot(String snapShot) {
        this.snapShot = snapShot;
    }

    public int getSideOverlap() {
        return sideOverlap;
    }

    public void setSideOverlap(int sideOverlap) {
        this.sideOverlap = sideOverlap;
    }

    public int getRouteOverlap() {
        return routeOverlap;
    }

    public void setRouteOverlap(int routeOverlap) {
        this.routeOverlap = routeOverlap;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public int getAltitude() {
        return altitude;
    }

    public void setAltitude(int altitude) {
        this.altitude = altitude;
    }

    public double getResolutionRate() {
        return resolutionRate;
    }

    public void setResolutionRate(double resolutionRate) {
        this.resolutionRate = resolutionRate;
    }

    public int getFlightNum() {
        return flightNum;
    }

    public void setFlightNum(int flightNum) {
        this.flightNum = flightNum;
    }

    public String getWorkMode() {
        return workMode;
    }

    public void setWorkMode(String workMode) {
        this.workMode = workMode;
    }

    public boolean isFixedAltitude() {
        return fixedAltitude;
    }

    public void setFixedAltitude(boolean fixedAltitude) {
        this.fixedAltitude = fixedAltitude;
    }

    public List<Mission> getMissionList() {
        return missionList;
    }

    public void setMissionList(List<Mission> missionList) {
        this.missionList = missionList;
    }

    public int getBuffer() {
        return buffer;
    }

    public void setBuffer(int buffer) {
        this.buffer = buffer;
    }
}