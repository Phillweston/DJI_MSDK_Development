package com.ew.autofly.entity;

import java.util.ArrayList;
import java.util.List;



public class TowerComponent {

    private String towerId;

    private String towerNo;


    private String gridLineName;

    private String componentName;

    private List<String> imageList=new ArrayList<>();

    public void addImage(String fileName){
        imageList.add(fileName);
    }

    public String getTowerId() {
        return towerId;
    }

    public void setTowerId(String towerId) {
        this.towerId = towerId;
    }

    public String getTowerNo() {
        return towerNo;
    }

    public void setTowerNo(String towerNo) {
        this.towerNo = towerNo;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public List<String> getImageList() {
        return imageList;
    }

    public String getGridLineName() {
        return gridLineName;
    }

    public void setGridLineName(String gridLineName) {
        this.gridLineName = gridLineName;
    }
}
