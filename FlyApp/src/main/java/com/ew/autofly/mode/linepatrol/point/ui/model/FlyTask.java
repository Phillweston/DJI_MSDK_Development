package com.ew.autofly.mode.linepatrol.point.ui.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Transient;

import java.util.ArrayList;
import java.util.Date;

public class FlyTask {
    @Id
    public String id;
    @NotNull
    public String taskName;
    public String image;
    public Date createTime;
    public Date updateTime;

    public int completePointSize;
    public int currentStart;
    public int currentEnd;

    public int taskStatus;
    public String cameraId;
    public int cameraDirection;
    public int taskType;
    public int photoNum;
    public int photoModel;
    public int hover;
    public float speed;
    public float autoFlightSpeed;
    public float radius;
    public float diffHeight;
    public float flyHeight;
    public int GoHomeHeight;
    public float adjacentOverlapping;
    public float parallelOverlapping;
    public double airWayAngle;
    public int radiusAngle;
    public int finishAction;
    public int interval;
    public float gsd;
    public int areaASL;
    public int homeASL;
    public int pitch;
    public int pPitchSetting;
    public int yaw;
    public int pYawSetting;
    public int isThridCamera;
    public int isSrtm;
    public String srtmDataFile;
    public String pId;
    public String address;
    public boolean save;

    public boolean followSpeed;
    public boolean followHeight;

    public boolean isGimbalPitchRotationEnabled;

    public double latitude;
    public double longitude;

    public float basePlaneHeight;

    public int model;

    @Transient
    public FlyCamera cameraInfo;

    public float lineSpace;

    public float pointSpace;

    public double pointSort;

    public int circlePointCount;

    @Transient
    public ArrayList<FlyTask> flyTaskItemList = new ArrayList<>();

    @Transient
    public ArrayList<FlyAreaPoint> flyAreaPoints = new ArrayList<>();


    @Transient
    public ArrayList<FlyPointAction> flyPointActions = new ArrayList<>();


@Generated(hash = 226878336)
public FlyTask() {
}

@Generated(hash = 550135639)
public FlyTask(String id, @NotNull String taskName, String image,
               Date createTime, Date updateTime, int completePointSize,
               int currentStart, int currentEnd, int taskStatus, String cameraId,
               int cameraDirection, int taskType, int photoNum, int photoModel,
               int hover, float speed, float autoFlightSpeed, float radius,
               float diffHeight, float flyHeight, int GoHomeHeight,
               float adjacentOverlapping, float parallelOverlapping,
               double airWayAngle, int radiusAngle, int finishAction, int interval,
               float gsd, int areaASL, int homeASL, int pitch, int pPitchSetting,
               int yaw, int pYawSetting, int isThridCamera, int isSrtm,
               String srtmDataFile, String pId, String address, boolean save,
               boolean followSpeed, boolean followHeight,
               boolean isGimbalPitchRotationEnabled, double latitude, double longitude,
               float basePlaneHeight, int model, float lineSpace, float pointSpace,
               double pointSort, int circlePointCount) {
    this.id = id;
    this.taskName = taskName;
    this.image = image;
    this.createTime = createTime;
    this.updateTime = updateTime;
    this.completePointSize = completePointSize;
    this.currentStart = currentStart;
    this.currentEnd = currentEnd;
    this.taskStatus = taskStatus;
    this.cameraId = cameraId;
    this.cameraDirection = cameraDirection;
    this.taskType = taskType;
    this.photoNum = photoNum;
    this.photoModel = photoModel;
    this.hover = hover;
    this.speed = speed;
    this.autoFlightSpeed = autoFlightSpeed;
    this.radius = radius;
    this.diffHeight = diffHeight;
    this.flyHeight = flyHeight;
    this.GoHomeHeight = GoHomeHeight;
    this.adjacentOverlapping = adjacentOverlapping;
    this.parallelOverlapping = parallelOverlapping;
    this.airWayAngle = airWayAngle;
    this.radiusAngle = radiusAngle;
    this.finishAction = finishAction;
    this.interval = interval;
    this.gsd = gsd;
    this.areaASL = areaASL;
    this.homeASL = homeASL;
    this.pitch = pitch;
    this.pPitchSetting = pPitchSetting;
    this.yaw = yaw;
    this.pYawSetting = pYawSetting;
    this.isThridCamera = isThridCamera;
    this.isSrtm = isSrtm;
    this.srtmDataFile = srtmDataFile;
    this.pId = pId;
    this.address = address;
    this.save = save;
    this.followSpeed = followSpeed;
    this.followHeight = followHeight;
    this.isGimbalPitchRotationEnabled = isGimbalPitchRotationEnabled;
    this.latitude = latitude;
    this.longitude = longitude;
    this.basePlaneHeight = basePlaneHeight;
    this.model = model;
    this.lineSpace = lineSpace;
    this.pointSpace = pointSpace;
    this.pointSort = pointSort;
    this.circlePointCount = circlePointCount;
}

    @Override
    public String toString() {
        return "FlyTask{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }

public String getId() {
    return this.id;
}

public void setId(String id) {
    this.id = id;
}

public String getTaskName() {
    return this.taskName;
}

public void setTaskName(String taskName) {
    this.taskName = taskName;
}

public String getImage() {
    return this.image;
}

public void setImage(String image) {
    this.image = image;
}

public Date getCreateTime() {
    return this.createTime;
}

public void setCreateTime(Date createTime) {
    this.createTime = createTime;
}

public Date getUpdateTime() {
    return this.updateTime;
}

public void setUpdateTime(Date updateTime) {
    this.updateTime = updateTime;
}

public int getCompletePointSize() {
    return this.completePointSize;
}

public void setCompletePointSize(int completePointSize) {
    this.completePointSize = completePointSize;
}

public int getCurrentStart() {
    return this.currentStart;
}

public void setCurrentStart(int currentStart) {
    this.currentStart = currentStart;
}

public int getCurrentEnd() {
    return this.currentEnd;
}

public void setCurrentEnd(int currentEnd) {
    this.currentEnd = currentEnd;
}

public int getTaskStatus() {
    return this.taskStatus;
}

public void setTaskStatus(int taskStatus) {
    this.taskStatus = taskStatus;
}

public String getCameraId() {
    return this.cameraId;
}

public void setCameraId(String cameraId) {
    this.cameraId = cameraId;
}

public int getCameraDirection() {
    return this.cameraDirection;
}

public void setCameraDirection(int cameraDirection) {
    this.cameraDirection = cameraDirection;
}

public int getTaskType() {
    return this.taskType;
}

public void setTaskType(int taskType) {
    this.taskType = taskType;
}

public int getPhotoNum() {
    return this.photoNum;
}

public void setPhotoNum(int photoNum) {
    this.photoNum = photoNum;
}

public int getPhotoModel() {
    return this.photoModel;
}

public void setPhotoModel(int photoModel) {
    this.photoModel = photoModel;
}

public int getHover() {
    return this.hover;
}

public void setHover(int hover) {
    this.hover = hover;
}

public float getSpeed() {
    return this.speed;
}

public void setSpeed(float speed) {
    this.speed = speed;
}

public float getAutoFlightSpeed() {
    return this.autoFlightSpeed;
}

public void setAutoFlightSpeed(float autoFlightSpeed) {
    this.autoFlightSpeed = autoFlightSpeed;
}

public float getRadius() {
    return this.radius;
}

public void setRadius(float radius) {
    this.radius = radius;
}

public float getDiffHeight() {
    return this.diffHeight;
}

public void setDiffHeight(float diffHeight) {
    this.diffHeight = diffHeight;
}

public float getFlyHeight() {
    return this.flyHeight;
}

public void setFlyHeight(float flyHeight) {
    this.flyHeight = flyHeight;
}

public int getGoHomeHeight() {
    return this.GoHomeHeight;
}

public void setGoHomeHeight(int GoHomeHeight) {
    this.GoHomeHeight = GoHomeHeight;
}

public float getAdjacentOverlapping() {
    return this.adjacentOverlapping;
}

public void setAdjacentOverlapping(float adjacentOverlapping) {
    this.adjacentOverlapping = adjacentOverlapping;
}

public float getParallelOverlapping() {
    return this.parallelOverlapping;
}

public void setParallelOverlapping(float parallelOverlapping) {
    this.parallelOverlapping = parallelOverlapping;
}

public double getAirWayAngle() {
    return this.airWayAngle;
}

public void setAirWayAngle(double airWayAngle) {
    this.airWayAngle = airWayAngle;
}

public int getRadiusAngle() {
    return this.radiusAngle;
}

public void setRadiusAngle(int radiusAngle) {
    this.radiusAngle = radiusAngle;
}

public int getFinishAction() {
    return this.finishAction;
}

public void setFinishAction(int finishAction) {
    this.finishAction = finishAction;
}

public int getInterval() {
    return this.interval;
}

public void setInterval(int interval) {
    this.interval = interval;
}

public float getGsd() {
    return this.gsd;
}

public void setGsd(float gsd) {
    this.gsd = gsd;
}

public int getAreaASL() {
    return this.areaASL;
}

public void setAreaASL(int areaASL) {
    this.areaASL = areaASL;
}

public int getHomeASL() {
    return this.homeASL;
}

public void setHomeASL(int homeASL) {
    this.homeASL = homeASL;
}

public int getPitch() {
    return this.pitch;
}

public void setPitch(int pitch) {
    this.pitch = pitch;
}

public int getPPitchSetting() {
    return this.pPitchSetting;
}

public void setPPitchSetting(int pPitchSetting) {
    this.pPitchSetting = pPitchSetting;
}

public int getYaw() {
    return this.yaw;
}

public void setYaw(int yaw) {
    this.yaw = yaw;
}

public int getPYawSetting() {
    return this.pYawSetting;
}

public void setPYawSetting(int pYawSetting) {
    this.pYawSetting = pYawSetting;
}

public int getIsThridCamera() {
    return this.isThridCamera;
}

public void setIsThridCamera(int isThridCamera) {
    this.isThridCamera = isThridCamera;
}

public int getIsSrtm() {
    return this.isSrtm;
}

public void setIsSrtm(int isSrtm) {
    this.isSrtm = isSrtm;
}

public String getSrtmDataFile() {
    return this.srtmDataFile;
}

public void setSrtmDataFile(String srtmDataFile) {
    this.srtmDataFile = srtmDataFile;
}

public String getPId() {
    return this.pId;
}

public void setPId(String pId) {
    this.pId = pId;
}

public String getAddress() {
    return this.address;
}

public void setAddress(String address) {
    this.address = address;
}

public boolean getSave() {
    return this.save;
}

public void setSave(boolean save) {
    this.save = save;
}

public boolean getFollowSpeed() {
    return this.followSpeed;
}

public void setFollowSpeed(boolean followSpeed) {
    this.followSpeed = followSpeed;
}

public boolean getFollowHeight() {
    return this.followHeight;
}

public void setFollowHeight(boolean followHeight) {
    this.followHeight = followHeight;
}

public boolean getIsGimbalPitchRotationEnabled() {
    return this.isGimbalPitchRotationEnabled;
}

public void setIsGimbalPitchRotationEnabled(
        boolean isGimbalPitchRotationEnabled) {
    this.isGimbalPitchRotationEnabled = isGimbalPitchRotationEnabled;
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

public float getBasePlaneHeight() {
    return this.basePlaneHeight;
}

public void setBasePlaneHeight(float basePlaneHeight) {
    this.basePlaneHeight = basePlaneHeight;
}

public int getModel() {
    return this.model;
}

public void setModel(int model) {
    this.model = model;
}

public float getLineSpace() {
    return this.lineSpace;
}

public void setLineSpace(float lineSpace) {
    this.lineSpace = lineSpace;
}

public float getPointSpace() {
    return this.pointSpace;
}

public void setPointSpace(float pointSpace) {
    this.pointSpace = pointSpace;
}

public double getPointSort() {
    return this.pointSort;
}

public void setPointSort(double pointSort) {
    this.pointSort = pointSort;
}

public int getCirclePointCount() {
    return this.circlePointCount;
}

public void setCirclePointCount(int circlePointCount) {
    this.circlePointCount = circlePointCount;
}

}
