package com.ew.autofly.entity.mission;

import java.io.File;


public class PatrolJobThumbnail {

  
    public final static int TYPE_VISIBLE = 0;
  
    public final static int TYPE_THERMAL = 1;

    
    private long worksheetId;

    
    private long missionId;

    
    private String originName;

    
    private String reName;

    
    private String photoTime;

    
    private String towerNum;

    
    private double lon;

    
    private double lat;

    
    private float alt;

    
    private int thumbType;

    
    private File thumbnail;

    public long getWorksheetId() {
        return worksheetId;
    }

    public void setWorksheetId(long worksheetId) {
        this.worksheetId = worksheetId;
    }

    public long getMissionId() {
        return missionId;
    }

    public void setMissionId(long missionId) {
        this.missionId = missionId;
    }

    public String getOriginName() {
        return originName;
    }

    public void setOriginName(String originName) {
        this.originName = originName;
    }

    public String getReName() {
        return reName;
    }

    public void setReName(String reName) {
        this.reName = reName;
    }

    public String getPhotoTime() {
        return photoTime;
    }

    public void setPhotoTime(String photoTime) {
        this.photoTime = photoTime;
    }

    public String getTowerNum() {
        return towerNum;
    }

    public void setTowerNum(String towerNum) {
        this.towerNum = towerNum;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public float getAlt() {
        return alt;
    }

    public void setAlt(float alt) {
        this.alt = alt;
    }

    public int getThumbType() {
        return thumbType;
    }

    public void setThumbType(int thumbType) {
        this.thumbType = thumbType;
    }

    public File getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(File thumbnail) {
        this.thumbnail = thumbnail;
    }
}
