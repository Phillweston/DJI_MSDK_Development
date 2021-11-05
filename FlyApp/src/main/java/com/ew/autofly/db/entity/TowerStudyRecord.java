package com.ew.autofly.db.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;


@Entity(nameInDb = "t_Tower_StudyRecord")
public class TowerStudyRecord {

    @Id(autoincrement = true)
    private Long id;


    private String towerId;


    private int missionType;


    private int missionMode;


    private int recordStatus;

    public int getRecordStatus() {
        return this.recordStatus;
    }

    public void setRecordStatus(int recordStatus) {
        this.recordStatus = recordStatus;
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

    @Generated(hash = 1912995610)
    public TowerStudyRecord(Long id, String towerId, int missionType,
            int missionMode, int recordStatus) {
        this.id = id;
        this.towerId = towerId;
        this.missionType = missionType;
        this.missionMode = missionMode;
        this.recordStatus = recordStatus;
    }

    @Generated(hash = 1133260640)
    public TowerStudyRecord() {
    }

}
