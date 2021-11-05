package com.ew.autofly.mode.linepatrol.point.ui.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Transient;

import java.util.ArrayList;

public class FlyAreaPoint {//存储任务飞行区域
    @Id
    public String id;
    @NotNull
    public String targetId;

    @NotNull
    public String rootTaskId;

    @NotNull
    public double latitude;
    @NotNull
    public double longitude;
    @NotNull
    public float altitude;
    @NotNull
    public double speed;

    @NotNull
    public int pitch;

    @NotNull
    public int yaw;

    public double createTime;

    @Transient
    public ArrayList<FlyPointAction> flyPointActions = new ArrayList<>();

    @Transient
    public int index;

    @Transient
    public boolean isSelected;

    @Transient
    public boolean isEight;

    @Generated(hash = 1463023451)
    public FlyAreaPoint() {
    }

    @Generated(hash = 1344513278)
    public FlyAreaPoint(String id, @NotNull String targetId,
                        @NotNull String rootTaskId, double latitude, double longitude,
                        float altitude, double speed, int pitch, int yaw, double createTime) {
        this.id = id;
        this.targetId = targetId;
        this.rootTaskId = rootTaskId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.speed = speed;
        this.pitch = pitch;
        this.yaw = yaw;
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "FlyAreaPoint{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", altitude=" + altitude +
                ", speed=" + speed +
                ", pitch=" + pitch +
                ", yaw=" + yaw +
                '}' + '\n';
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTargetId() {
        return this.targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getRootTaskId() {
        return this.rootTaskId;
    }

    public void setRootTaskId(String rootTaskId) {
        this.rootTaskId = rootTaskId;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public float getAltitude() {
        return this.altitude;
    }

    public void setAltitude(float altitude) {
        this.altitude = altitude;
    }

    public double getSpeed() {
        return this.speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public int getPitch() {
        return this.pitch;
    }

    public void setPitch(int pitch) {
        this.pitch = pitch;
    }

    public int getYaw() {
        return this.yaw;
    }

    public void setYaw(int yaw) {
        this.yaw = yaw;
    }

    public double getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(double createTime) {
        this.createTime = createTime;
    }


}
