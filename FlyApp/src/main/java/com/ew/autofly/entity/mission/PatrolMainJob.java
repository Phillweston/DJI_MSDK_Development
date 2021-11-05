package com.ew.autofly.entity.mission;

import java.io.Serializable;
import java.util.ArrayList;


public class PatrolMainJob implements Serializable {


    private long id;

    private String worksheetNum;

    private String lineName;

    private String worksheetStart;

    private String worksheetEnd;



    private int inspectRate;

    public long getId() {
        return id;
    }

    public String getIdString(){
        return String.valueOf(id);
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getWorksheetNum() {
        return worksheetNum;
    }

    public void setWorksheetNum(String worksheetNum) {
        this.worksheetNum = worksheetNum;
    }

    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }

    public String getWorksheetStart() {
        return worksheetStart;
    }

    public void setWorksheetStart(String worksheetStart) {
        this.worksheetStart = worksheetStart;
    }

    public String getWorksheetEnd() {
        return worksheetEnd;
    }

    public void setWorksheetEnd(String worksheetEnd) {
        this.worksheetEnd = worksheetEnd;
    }

 /*   public ArrayList<PatrolSubJob> getMissionList() {
        return missionList;
    }

    public void setMissionList(ArrayList<PatrolSubJob> missionList) {
        this.missionList = missionList;
    }*/

    public int getInspectRate() {
        return inspectRate;
    }

    public void setInspectRate(int inspectRate) {
        this.inspectRate = inspectRate;
    }
}
