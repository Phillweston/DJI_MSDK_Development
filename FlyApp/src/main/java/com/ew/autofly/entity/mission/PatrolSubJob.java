package com.ew.autofly.entity.mission;

import java.io.Serializable;
import java.util.List;


public class PatrolSubJob implements Serializable {

    private long id;

    private String name;

    private String filePath;

    private int status;

    private int towerStart;

    private int towerEnd;

    private List<PatrolSubJobTower> towerList;

    public long getId() {
        return id;
    }

    public String getIdString(){
        return String.valueOf(id);
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getTowerStart() {
        return towerStart;
    }

    public void setTowerStart(int towerStart) {
        this.towerStart = towerStart;
    }

    public int getTowerEnd() {
        return towerEnd;
    }

    public void setTowerEnd(int towerEnd) {
        this.towerEnd = towerEnd;
    }

    public List<PatrolSubJobTower> getTowerList() {
        return towerList;
    }

    public void setTowerList(List<PatrolSubJobTower> towerList) {
        this.towerList = towerList;
    }
}
