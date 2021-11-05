package com.ew.autofly.entity;

import com.ew.autofly.mode.linepatrol.point.ui.model.FlyTask;

import java.io.Serializable;
import java.util.UUID;

/**
 * 后面新写的代码禁止再使用这个类，由于之前墨卡托坐标和WGS84/GCJ坐标混用这个类，整个项目过于混乱，分不清坐标是墨卡托还是WGS84/GCJ,请使用
 * {@link LocationCoordinate}和{@link LocationMercator}代替，对于老代码有空及时更换
 */
@Deprecated
public class LatLngInfo implements Serializable{

    public String id;
    public double latitude;
    public double longitude;

    public FlyTask flyTask;

    public LatLngInfo() {
        id = UUID.randomUUID().toString();
        flyTask = new FlyTask();
        flyTask.id = id;
    }

    public LatLngInfo(double latitude, double longitude) {
        this();
        this.latitude = latitude;
        this.longitude = longitude;
    }
}