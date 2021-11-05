package com.ew.autofly.db.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;



@Entity(nameInDb = "t_Mission_Tower")
public class MissionTower {

    @Id(autoincrement = true)
    private Long id;

    private String missionId;


    private String towerNo;


    private String gridLineName;


    private double latitude;
    private double longitude;

    private float flyAltitude;
    public float getFlyAltitude() {
        return this.flyAltitude;
    }
    public void setFlyAltitude(float flyAltitude) {
        this.flyAltitude = flyAltitude;
    }
    public double getLongitude() {
        return this.longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    public double getLatitude() {
        return this.latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public String getGridLineName() {
        return this.gridLineName;
    }
    public void setGridLineName(String gridLineName) {
        this.gridLineName = gridLineName;
    }
    public String getTowerNo() {
        return this.towerNo;
    }
    public void setTowerNo(String towerNo) {
        this.towerNo = towerNo;
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
    @Generated(hash = 2104765036)
    public MissionTower(Long id, String missionId, String towerNo,
            String gridLineName, double latitude, double longitude,
            float flyAltitude) {
        this.id = id;
        this.missionId = missionId;
        this.towerNo = towerNo;
        this.gridLineName = gridLineName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.flyAltitude = flyAltitude;
    }
    @Generated(hash = 1713679660)
    public MissionTower() {
    }

}
