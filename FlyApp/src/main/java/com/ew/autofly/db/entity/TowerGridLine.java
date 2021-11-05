package com.ew.autofly.db.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

/**
 *杆塔线路
 */

@Entity(nameInDb = "t_TowerGridLine")
public class TowerGridLine {

    @Id(autoincrement = true)
    @Property(nameInDb = "Id")
    private Long id;


    @Property(nameInDb = "lineName")
    private String lineName;


    @Property(nameInDb = "voltage")
    private String voltage;


    @Property(nameInDb = "groupName")
    private String groupName;

    @Generated(hash = 374401335)
    public TowerGridLine(Long id, String lineName, String voltage, String groupName) {
        this.id = id;
        this.lineName = lineName;
        this.voltage = voltage;
        this.groupName = groupName;
    }

    @Generated(hash = 1905935242)
    public TowerGridLine() {
    }

    public String getGroupName() {
        return this.groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getVoltage() {
        return this.voltage;
    }

    public void setVoltage(String voltage) {
        this.voltage = voltage;
    }

    public String getLineName() {
        return this.lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (o instanceof TowerGridLine) {
            if (((TowerGridLine) o).getId() == this.getId()) {
                return true;
            }
        }

        return false;
    }


}
