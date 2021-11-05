package com.ew.autofly.db.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.util.Date;



@Entity(nameInDb = "t_Mission_Batch")
public class MissionBatch {

    @Id(autoincrement = true)
    private Long id;

    private String missionBatchId;

    private int missionType;

    private String name;

    private int status;
    private String snapShot;
    private Date createDate;

    @Generated(hash = 1832625898)
    public MissionBatch(Long id, String missionBatchId, int missionType,
            String name, int status, String snapShot, Date createDate) {
        this.id = id;
        this.missionBatchId = missionBatchId;
        this.missionType = missionType;
        this.name = name;
        this.status = status;
        this.snapShot = snapShot;
        this.createDate = createDate;
    }

    @Generated(hash = 268541026)
    public MissionBatch() {
    }

    public String getSnapShot() {
        return this.snapShot;
    }

    public void setSnapShot(String snapShot) {
        this.snapShot = snapShot;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
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

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreateDate() {
        return this.createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public int getMissionType() {
        return this.missionType;
    }

    public void setMissionType(int missionType) {
        this.missionType = missionType;
    }


}
