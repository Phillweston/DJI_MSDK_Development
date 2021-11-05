package com.ew.autofly.entity;

import com.esri.core.geometry.Polygon;

import java.io.Serializable;
import java.util.Date;

public class MissionBatch implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String missionId;
    private int batch;
    private int startPhotoIndex;
    private int endPhotoIndex;
    private int status;
    private Date startTime;
    private Date endTime;
    private Polygon polygon;

    private Mission mission;

    public Mission getMission() {
        return mission;
    }

    public void setMission(Mission mission) {
        this.mission = mission;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMissionId() {
        return missionId;
    }

    public void setMissionId(String missionId) {
        this.missionId = missionId;
    }

    public int getBatch() {
        return batch;
    }

    public void setBatch(int batch) {
        this.batch = batch;
    }

    public int getStartPhotoIndex() {
        return startPhotoIndex;
    }

    public void setStartPhotoIndex(int startPhotoIndex) {
        this.startPhotoIndex = startPhotoIndex;
    }

    public int getEndPhotoIndex() {
        return endPhotoIndex;
    }

    public void setEndPhotoIndex(int endPhotoIndex) {
        this.endPhotoIndex = endPhotoIndex;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Polygon getPolygon() {
        return polygon;
    }

    public void setPolygon(Polygon polygon) {
        this.polygon = polygon;
    }
}
