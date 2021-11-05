package com.ew.autofly.db.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;


@Entity(nameInDb = "t_Tower_Location_update")
public class TowerLocationUpdate {

    @Id(autoincrement = true)
    protected Long id;

    @Unique
    protected String towerId;


    private String fileUpdateTime;


    private String gridLineName;


    private String towerNo;

    @Generated(hash = 291119893)
    public TowerLocationUpdate(Long id, String towerId, String fileUpdateTime,
                               String gridLineName, String towerNo) {
        this.id = id;
        this.towerId = towerId;
        this.fileUpdateTime = fileUpdateTime;
        this.gridLineName = gridLineName;
        this.towerNo = towerNo;
    }

    @Generated(hash = 302959383)
    public TowerLocationUpdate() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTowerId() {
        return towerId;
    }

    public void setTowerId(String towerId) {
        this.towerId = towerId;
    }

    public String getFileUpdateTime() {
        return fileUpdateTime;
    }

    public void setFileUpdateTime(String fileUpdateTime) {
        this.fileUpdateTime = fileUpdateTime;
    }

    public String getGridLineName() {
        return gridLineName;
    }

    public void setGridLineName(String gridLineName) {
        this.gridLineName = gridLineName;
    }

    public String getTowerNo() {
        return towerNo;
    }

    public void setTowerNo(String towerNo) {
        this.towerNo = towerNo;
    }
}
