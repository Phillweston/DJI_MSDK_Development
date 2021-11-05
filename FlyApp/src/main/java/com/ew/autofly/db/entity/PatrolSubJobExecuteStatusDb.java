package com.ew.autofly.db.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;


@Entity(nameInDb = "patrol_subjob_execute_status")
public class PatrolSubJobExecuteStatusDb {

    @Id(autoincrement = true)
    private Long id;


    
    private long missionId;

    
    private int status;

    @Generated(hash = 628504696)
    public PatrolSubJobExecuteStatusDb(Long id, long missionId, int status) {
        this.id = id;
        this.missionId = missionId;
        this.status = status;
    }

    @Generated(hash = 2051591824)
    public PatrolSubJobExecuteStatusDb() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getMissionId() {
        return this.missionId;
    }

    public void setMissionId(long missionId) {
        this.missionId = missionId;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }



}
