package com.ew.autofly.db.entity;

import org.greenrobot.greendao.annotation.Entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;


@Entity(nameInDb = "t_Tower_StudyComponent")
public class TowerStudyComponent implements Serializable {

    @Transient
    private static final long serialVersionUID = 1L;

    @Id(autoincrement = true)
    private Long id;

    private String towerId;

    private String towerNo;


    private String gridLineName;

    private String componentName;

    @Transient
    private String imageFileName;

    public String getComponentName() {
        return this.componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
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

    public String getImageFileName() {
        return this.imageFileName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    @Generated(hash = 1300772675)
    public TowerStudyComponent(Long id, String towerId, String towerNo,
            String gridLineName, String componentName) {
        this.id = id;
        this.towerId = towerId;
        this.towerNo = towerNo;
        this.gridLineName = gridLineName;
        this.componentName = componentName;
    }

    @Generated(hash = 1382298353)
    public TowerStudyComponent() {
    }

    
}
