package com.ew.autofly.entity.mission;

import java.io.Serializable;


public class PatrolJobExecuteUpload implements Serializable {

    
    private long worksheetId;

    
    private long missionId;

  
    private String worksheetNum;

    
    
    private String lineName;



    private int status;

    
    private float progress;

    
    private int deviceId;

    
    private String flightSerialNumber;

    
    private int videoCall = 0;

    
    private int liveStatus=0;

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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public String getFlightSerialNumber() {
        return flightSerialNumber;
    }

    public void setFlightSerialNumber(String flightSerialNumber) {
        this.flightSerialNumber = flightSerialNumber;
    }

    public int getVideoCall() {
        return videoCall;
    }

    public void setVideoCall(int videoCall) {
        this.videoCall = videoCall;
    }

    public String getWorksheetNum() {
        return worksheetNum;
    }

    public void setWorksheetNum(String worksheetNum) {
        this.worksheetNum = worksheetNum;
    }

    public int getLiveStatus() {
        return liveStatus;
    }

    public void setLiveStatus(int liveStatus) {
        this.liveStatus = liveStatus;
    }

    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }
}
