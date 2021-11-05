package com.ew.autofly.entity.mission;

import com.ew.autofly.entity.Tower;


public class PatrolSubJobTower extends Tower {

    private int towerNum;

    
    private String relative;

    public int getTowerNum() {
        return towerNum;
    }

    public void setTowerNum(int towerNum) {
        this.towerNum = towerNum;
    }

    public String getRelative() {
        return relative;
    }

    public void setRelative(String relative) {
        this.relative = relative;
    }
}
