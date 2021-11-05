package com.ew.autofly.db.entity;

import com.ew.autofly.db.converter.StringConverter;
import com.ew.autofly.entity.LatLngInfo;
import com.ew.autofly.module.weather.bean.realtime.info.Nearest;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;



@Entity(nameInDb = "t_Mission_TreeObstacle")
public class MissionTreeObstacle extends MissionBase{

    @Id(autoincrement = true)
    protected Long id;

    @Unique
    protected String missionId;
    protected String missionBatchId;
    private int missionType;
    protected String name;
    protected String snapshot;

    protected int status;

    protected Date createDate;

    protected Date startTime;

    protected Date endTime;

    private int startPhotoIndex;

    private int endPhotoIndex;

    protected int geometryType;

    @Convert(columnType = String.class, converter = StringConverter.class)
    protected List<String> geometryLatLngList;


    private int sideOverlap;

    private int routeOverlap;

    private double resolutionRate;

    private boolean isAltitudeFixed;

    private int altitude;

    private int flySpeed;

    private int baseLineHeight;

    private int entryHeight;

    @Transient
    private List<MissionTower> missionTowers;

    @Generated(hash = 1235744159)
    public MissionTreeObstacle(Long id, String missionId, String missionBatchId, int missionType, String name,
            String snapshot, int status, Date createDate, Date startTime, Date endTime, int startPhotoIndex,
            int endPhotoIndex, int geometryType, List<String> geometryLatLngList, int sideOverlap,
            int routeOverlap, double resolutionRate, boolean isAltitudeFixed, int altitude, int flySpeed,
            int baseLineHeight, int entryHeight) {
        this.id = id;
        this.missionId = missionId;
        this.missionBatchId = missionBatchId;
        this.missionType = missionType;
        this.name = name;
        this.snapshot = snapshot;
        this.status = status;
        this.createDate = createDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.startPhotoIndex = startPhotoIndex;
        this.endPhotoIndex = endPhotoIndex;
        this.geometryType = geometryType;
        this.geometryLatLngList = geometryLatLngList;
        this.sideOverlap = sideOverlap;
        this.routeOverlap = routeOverlap;
        this.resolutionRate = resolutionRate;
        this.isAltitudeFixed = isAltitudeFixed;
        this.altitude = altitude;
        this.flySpeed = flySpeed;
        this.baseLineHeight = baseLineHeight;
        this.entryHeight = entryHeight;
    }

    @Generated(hash = 999771731)
    public MissionTreeObstacle() {
    }

    public List<MissionTower> getMissionTowers() {
        return missionTowers;
    }

    public void setMissionTowers(List<MissionTower> missionTowers) {
        this.missionTowers = missionTowers;
    }

    public int getEntryHeight() {
        return this.entryHeight;
    }

    public void setEntryHeight(int entryHeight) {
        this.entryHeight = entryHeight;
    }

    public int getBaseLineHeight() {
        return this.baseLineHeight;
    }

    public void setBaseLineHeight(int baseLineHeight) {
        this.baseLineHeight = baseLineHeight;
    }

    public int getFlySpeed() {
        return this.flySpeed;
    }

    public void setFlySpeed(int flySpeed) {
        this.flySpeed = flySpeed;
    }

    public int getAltitude() {
        return this.altitude;
    }

    public void setAltitude(int altitude) {
        this.altitude = altitude;
    }

    public boolean getIsAltitudeFixed() {
        return this.isAltitudeFixed;
    }

    public void setIsAltitudeFixed(boolean isAltitudeFixed) {
        this.isAltitudeFixed = isAltitudeFixed;
    }

    public double getResolutionRate() {
        return this.resolutionRate;
    }

    public void setResolutionRate(double resolutionRate) {
        this.resolutionRate = resolutionRate;
    }

    public int getRouteOverlap() {
        return this.routeOverlap;
    }

    public void setRouteOverlap(int routeOverlap) {
        this.routeOverlap = routeOverlap;
    }

    public int getSideOverlap() {
        return this.sideOverlap;
    }

    public void setSideOverlap(int sideOverlap) {
        this.sideOverlap = sideOverlap;
    }

    public Date getEndTime() {
        return this.endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getStartTime() {
        return this.startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getCreateDate() {
        return this.createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getSnapshot() {
        return this.snapshot;
    }

    public void setSnapshot(String snapshot) {
        this.snapshot = snapshot;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMissionBatchId() {
        return this.missionBatchId;
    }

    public void setMissionBatchId(String missionBatchId) {
        this.missionBatchId = missionBatchId;
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

    public List<String> getGeometryLatLngList() {
        return this.geometryLatLngList;
    }

    public void setGeometryLatLngList(List<String> geometryLatLngList) {
        this.geometryLatLngList = geometryLatLngList;
    }

    public int getGeometryType() {
        return this.geometryType;
    }

    public void setGeometryType(int geometryType) {
        this.geometryType = geometryType;
    }

    public int getMissionType() {
        return this.missionType;
    }

    public void setMissionType(int missionType) {
        this.missionType = missionType;
    }

    public int getStartPhotoIndex() {
        return this.startPhotoIndex;
    }
    public void setStartPhotoIndex(int startPhotoIndex) {
        this.startPhotoIndex = startPhotoIndex;
    }
    public int getEndPhotoIndex() {
        return this.endPhotoIndex;
    }
    public void setEndPhotoIndex(int endPhotoIndex) {
        this.endPhotoIndex = endPhotoIndex;
    }
    
}
