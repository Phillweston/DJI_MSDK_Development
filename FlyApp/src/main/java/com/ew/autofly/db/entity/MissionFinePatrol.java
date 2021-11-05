package com.ew.autofly.db.entity;

import com.ew.autofly.db.converter.StringConverter;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

import java.util.Date;
import java.util.List;
import org.greenrobot.greendao.annotation.Generated;


@Entity(nameInDb = "t_Mission_FinePatrol")
public class MissionFinePatrol extends MissionBase{

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


    
    private boolean isReverse;

    /* 巡检速度(绕塔速度) */
    private float patrolSpeed ;

  
    private int flySpeed;

    @Generated(hash = 1121065050)
    public MissionFinePatrol(Long id, String missionId, String missionBatchId,
            int missionType, String name, String snapshot, int status,
            Date createDate, Date startTime, Date endTime, int startPhotoIndex,
            int endPhotoIndex, int geometryType, List<String> geometryLatLngList,
            boolean isReverse, float patrolSpeed, int flySpeed) {
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
        this.isReverse = isReverse;
        this.patrolSpeed = patrolSpeed;
        this.flySpeed = flySpeed;
    }

    @Generated(hash = 848566828)
    public MissionFinePatrol() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMissionId() {
        return this.missionId;
    }

    public void setMissionId(String missionId) {
        this.missionId = missionId;
    }

    public String getMissionBatchId() {
        return this.missionBatchId;
    }

    public void setMissionBatchId(String missionBatchId) {
        this.missionBatchId = missionBatchId;
    }

    public int getMissionType() {
        return this.missionType;
    }

    public void setMissionType(int missionType) {
        this.missionType = missionType;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSnapshot() {
        return this.snapshot;
    }

    public void setSnapshot(String snapshot) {
        this.snapshot = snapshot;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getCreateDate() {
        return this.createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getStartTime() {
        return this.startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return this.endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
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

    public int getGeometryType() {
        return this.geometryType;
    }

    public void setGeometryType(int geometryType) {
        this.geometryType = geometryType;
    }

    public List<String> getGeometryLatLngList() {
        return this.geometryLatLngList;
    }

    public void setGeometryLatLngList(List<String> geometryLatLngList) {
        this.geometryLatLngList = geometryLatLngList;
    }

    public boolean getIsReverse() {
        return this.isReverse;
    }

    public void setIsReverse(boolean isReverse) {
        this.isReverse = isReverse;
    }

    public float getPatrolSpeed() {
        return this.patrolSpeed;
    }

    public void setPatrolSpeed(float patrolSpeed) {
        this.patrolSpeed = patrolSpeed;
    }

    public int getFlySpeed() {
        return this.flySpeed;
    }

    public void setFlySpeed(int flySpeed) {
        this.flySpeed = flySpeed;
    }

}
