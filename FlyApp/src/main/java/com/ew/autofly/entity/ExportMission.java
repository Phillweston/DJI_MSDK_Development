package com.ew.autofly.entity;

import com.ew.autofly.utils.MissionUtil;

import java.io.Serializable;
import java.util.Date;



public class ExportMission implements Serializable{

    private Mission2 mission;

    private String geometry;

    public Mission2 getMission() {
        return mission;
    }

    public void setMission(Mission2 mission) {
        this.mission = mission;
        geometry= MissionUtil.convertToGeometry(mission);
    }

    public String getGeometry() {
        return geometry;
    }

    public void setGeometry(String geometry) {
        this.geometry = geometry;
    }
}
