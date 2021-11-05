package com.ew.autofly.mode.linepatrol.point.ui.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;

@Entity(indexes = {
        @Index(value = "pointId,createTime ASC")//建立索引
})
public class FlyPointAction {//存储任务飞行区域
    @Id
    public String id;

    @NotNull
    public String taskId;

    @NotNull
    public String pTaskId;

    @NotNull
    public String pointId;

    public int action;

    public String actionName;

    public int staySecond;

    public int rotateAircraft;

    public int gimbalPitch;

    public Double createTime;

    @Override
    public String toString() {
        return "FlyPointAction{" +
                "action=" + action +
                ", actionName='" + actionName + '\'' +
                ", staySecond=" + staySecond +
                ", rotateAircraft=" + rotateAircraft +
                ", gimbalPitch=" + gimbalPitch +
                '}';
    }

    @Generated(hash = 1777271790)
public FlyPointAction(String id, @NotNull String taskId,
                      @NotNull String pTaskId, @NotNull String pointId, int action,
                      String actionName, int staySecond, int rotateAircraft, int gimbalPitch,
                      Double createTime) {
    this.id = id;
    this.taskId = taskId;
    this.pTaskId = pTaskId;
    this.pointId = pointId;
    this.action = action;
    this.actionName = actionName;
    this.staySecond = staySecond;
    this.rotateAircraft = rotateAircraft;
    this.gimbalPitch = gimbalPitch;
    this.createTime = createTime;
}

@Generated(hash = 767299794)
public FlyPointAction() {
}

public String getId() {
    return this.id;
}

public void setId(String id) {
    this.id = id;
}

public String getTaskId() {
    return this.taskId;
}

public void setTaskId(String taskId) {
    this.taskId = taskId;
}

public String getPTaskId() {
    return this.pTaskId;
}

public void setPTaskId(String pTaskId) {
    this.pTaskId = pTaskId;
}

public String getPointId() {
    return this.pointId;
}

public void setPointId(String pointId) {
    this.pointId = pointId;
}

public int getAction() {
    return this.action;
}

public void setAction(int action) {
    this.action = action;
}

public String getActionName() {
    return this.actionName;
}

public void setActionName(String actionName) {
    this.actionName = actionName;
}

public int getStaySecond() {
    return this.staySecond;
}

public void setStaySecond(int staySecond) {
    this.staySecond = staySecond;
}

public int getRotateAircraft() {
    return this.rotateAircraft;
}

public void setRotateAircraft(int rotateAircraft) {
    this.rotateAircraft = rotateAircraft;
}

public int getGimbalPitch() {
    return this.gimbalPitch;
}

public void setGimbalPitch(int gimbalPitch) {
    this.gimbalPitch = gimbalPitch;
}

public Double getCreateTime() {
    return this.createTime;
}

public void setCreateTime(Double createTime) {
    this.createTime = createTime;
}


}
