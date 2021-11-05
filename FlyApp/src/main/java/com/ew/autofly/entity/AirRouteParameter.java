package com.ew.autofly.entity;

import java.io.Serializable;

import dji.sdk.camera.Camera;

/**
 * Created by DAN on 2017/1/16.
 * 部分参数定义精度不合理，容易在精度转换时出错，请谨慎使用，特使时double和fload
 */
public class AirRouteParameter implements Serializable {
    private static final long serialVersionUID = 1L;


    protected final float MIN_SHOOT_TIME_INTERVAL = 2.0f;
    public static final int DEFAULT_FLY_HEIGHT = 115;

    private String cameraName = "UnKnow";

    /* 航向重叠度 (注意这个double->float类型转换是可能会出现精度丢失)*/
    private double routeOverlap = 0.7D;
    /* 旁向重叠度 (注意这个double->float类型转换是可能会出现精度丢失)*/
    private double sideOverlap = 0.6D;
    private double airZoneWidth = 200.0D;
    private double airZoneHeight = 200.0D;

    
    private double pixelSizeWidth = 1.58D;
    
    private double focalLength = 3.61D;
    
    private double sensorWidth = 6.32D;

    
    private double sensorHeight = 4.74D;


    /* 飞行速度 */
    private double flySpeed = 8.0D;


    private double resolution;

    private double gotoTaskDistance = 0.0D;
    /* 航线高度 */
    private int altitude = DEFAULT_FLY_HEIGHT;
    /* 进入测区高度 */
    private int entryHeight = DEFAULT_FLY_HEIGHT;

    private int returnHeight = DEFAULT_FLY_HEIGHT;
    /* 是否勾选进入测区高度 */
    private boolean checkEntryHeight = false;
    /* 云台角度 */
    private int gimbalAngle = -90;
    
    private boolean shortDirection = false;
    /* 是否固定航高 */
    private boolean fixedAltitude = true;
    private boolean planeDirection = false;
    private int baseLineHeight = 0;
    /* 最大航高 */
    private int maxFlyingHeight = DEFAULT_FLY_HEIGHT;
    /* 最小航高 */
    private int minFlyingHeight = 20;
    /* 飞行层数 */
    private int surroundLayer = 1;
    /* 快速测绘多边形旋转角度 */
    private int flyingAngle = 18;
    /* 全景采集单圈照片数量 */
    private int photoNumPerRow = 10;
    /* 可变航高的各点航高 */
    private String fixedAltitudeList = "";
    /* 缓冲区 */
    private int Buffer = 50;
    /* 正射倾斜航线起点 */
    private int startingPoint = 0;
    /* 正射倾斜航线终点 */
    private int endPoint = 0;
    /* 机头朝向（0-360度） */
    private int planeYaw = 0;
    /* 是否开启协调转弯 */
    private boolean isCircleLine = false;
    /* 是否反转航线 */
    private boolean isReverse = false;



    public int actionMode;

    public int returnMode;

    public int recodeMode;

    public AirRouteParameter(String cameraName) {
        this.cameraName = cameraName;
        initializeCameraParams(cameraName);
    }

    /**
     * 初始化相机参数
     *
     * @param cameraName
     */
    public void initializeCameraParams(String cameraName) {

        if (cameraName == null) {
            return;
        }

        if (cameraName.equals(Camera.DisplayNamePhantom3AdvancedCamera)
                || cameraName.equals(Camera.DisplayNamePhantom3ProfessionalCamera)
                || cameraName.equals(Camera.DisplayNamePhantom3StandardCamera)
                || cameraName.equals(Camera.DisplayNamePhantom34KCamera)) {
            this.focalLength = 3.700D;
            this.sensorWidth = 6.400D;
            this.sensorHeight = 4.800D;
            this.pixelSizeWidth = 1.613D;

        } else if (cameraName.equals(Camera.DisplaynamePhantom4AdvancedCamera)
                || cameraName.equals(Camera.DisplaynamePhantom4ProCamera)
                || cameraName.equals(Camera.DisplaynamePhantom4PV2Camera)
                || cameraName.equals(Camera.DisplaynamePhantom4RTKCamera)) {
            this.focalLength = 8.800D;
            this.sensorWidth = 13.200D;
            this.sensorHeight = 8.800D;
            this.pixelSizeWidth = 2.412D;

        } else if (cameraName.equals(Camera.DisplayNamePhantom4Camera)) {
            this.focalLength = 3.700D;
            this.sensorWidth = 6.400D;
            this.sensorHeight = 4.800D;
            this.pixelSizeWidth = 1.600D;

        } else if (cameraName.equals(Camera.DisplayNameMavicProCamera) ||
                cameraName.equals(Camera.DisplayNameMavic2ProCamera) ||
                cameraName.equals(Camera.DisplayNameMavic2ZoomCamera)) {
            this.focalLength = 5.000D;
            this.sensorWidth = 6.400D;
            this.sensorHeight = 4.800D;
            this.pixelSizeWidth = 1.600D;

        } else if (cameraName.equals(Camera.DisplayNameSparkCamera)) {
            this.focalLength = 4.500D;
            this.sensorWidth = 6.400D;
            this.sensorHeight = 4.800D;
            this.pixelSizeWidth = 1.613D;

        } else if (cameraName.equals(Camera.DisplayNameX5)
                || cameraName.equals(Camera.DisplayNameX5R)
                || cameraName.equals(Camera.DisplayNameX5S)) {
            this.focalLength = 15.000D;
            this.sensorWidth = 17.300D;
            this.sensorHeight = 13.000D;
            this.pixelSizeWidth = 3.277D;

        } else if (cameraName.equals(Camera.DisplayNameX4S)) {
            this.focalLength = 8.800D;
            this.sensorWidth = 13.200D;
            this.sensorHeight = 8.800D;
            this.pixelSizeWidth = 2.412D;

        } else if (cameraName.equals(Camera.DisplayNameZ3)) {
            this.focalLength = 4.300D;
            this.sensorWidth = 6.200D;
            this.sensorHeight = 4.650D;
            this.pixelSizeWidth = 1.550D;

        } else if (cameraName.equals(Camera.DisplayNameZ30)) {
            this.focalLength = 4.000D;
            this.sensorWidth = 4.620D;
            this.sensorHeight = 3.465D;
            this.pixelSizeWidth = 1.550D;

        } else if (cameraName.equals(Camera.DisplayNameX7)) {
            this.focalLength = 35.00D;
            this.sensorWidth = 23.500D;
            this.sensorHeight = 15.700D;
            this.pixelSizeWidth = 3.91;

        } else if (cameraName.equals("Sequoia mono")) {
            this.focalLength = 3.98;
            this.sensorWidth = 4.8;
            this.sensorHeight = 3.6;
            this.pixelSizeWidth = 3.75;

        } else if (cameraName.equals("Sequoia RGB")) {
            this.focalLength = 4.88;
            this.sensorWidth = 6.17472;
            this.sensorHeight = 4.63104;
            this.pixelSizeWidth = 1.34;

        } else {
            this.focalLength = 3.61D;
            this.sensorWidth = 6.32D;
            this.sensorHeight = 4.74D;
            this.pixelSizeWidth = 1.58D;

        }
    }

    public double getSensorHeight() {
        return sensorHeight;
    }

    public int getMaxFlyingHeight() {
        return this.maxFlyingHeight;
    }

    public void setMaxFlyingHeight(int maxFlyingHeight) {
        this.maxFlyingHeight = maxFlyingHeight;
    }

    public int getMinFlyingHeight() {
        return this.minFlyingHeight;
    }

    public void setMinFlyingHeight(int MinFlyingHeight) {
        this.minFlyingHeight = MinFlyingHeight;
    }

    public int getFlyingLayer() {
        return this.surroundLayer;
    }

    public void setFlyingLayer(int surroundLayer) {
        this.surroundLayer = surroundLayer;
    }

    public int getFlyingAngle() {
        return this.flyingAngle;
    }

    public void setFlyingAngle(int FlyingAngle) {
        this.flyingAngle = FlyingAngle;
    }

    public int getGimbalAngle() {
        return this.gimbalAngle;
    }

    public void setGimbalAngle(int gimbalAngle) {
        this.gimbalAngle = gimbalAngle;
    }

    public void setAltitude(int altitude) {
        this.altitude = altitude;
    }

    public int getAltitude() {
        return this.altitude;
    }

    public int getEntryHeight() {
        return entryHeight;
    }

    public void setEntryHeight(int entryHeight) {
        this.entryHeight = entryHeight;
    }

    public boolean isCheckEntryHeight() {
        return checkEntryHeight;
    }

    public void setCheckEntryHeight(boolean checkEntryHeight) {
        this.checkEntryHeight = checkEntryHeight;
    }

    public double getSideOverlap() {
        return this.sideOverlap;
    }

    public void setSideOverlap(double lineOverlap) {
        this.sideOverlap = lineOverlap;
    }

    public double getRouteOverlap() {
        return this.routeOverlap;
    }

    public void setRouteOverlap(double routeOverlap) {
        this.routeOverlap = routeOverlap;
    }

    public double getFlySpeed() {
        return this.flySpeed;
    }

    public int getActualSpeed() {
        int iSpeed = Double.valueOf(this.getAirPointSpacing() / 2.0D).intValue();
        if (iSpeed > 15)
            iSpeed = 15;
        if (iSpeed < 3)
            iSpeed = 3;
        return iSpeed;
    }

    /* 获取照片拍摄间隔 */
    public int getPhotoInterval() {
        int iInterval = Double.valueOf(this.getAirPointSpacing() / (double) this.getActualSpeed()).intValue();
        if (iInterval < 2)
            iInterval = 2;
        return iInterval;
    }

    public void setFlySpeed(double flySpeed) {
        if (Math.abs(flySpeed) > 15.0D)
            flySpeed = 15.0D * Math.abs(flySpeed) / flySpeed;
        this.flySpeed = flySpeed;
    }

    public void setAirZoneWidth(double airZoneWidth) {
        this.airZoneWidth = airZoneWidth;
    }

    public void setAirZoneHeight(double airZoneHeight) {
        this.airZoneHeight = airZoneHeight;
    }

    public void setGotoTaskDistance(double gotoTaskDistance) {
        this.gotoTaskDistance = gotoTaskDistance;
    }

    /**
     * 根据航高计算分辨率
     *
     * @return 返回分辨率单位为厘米（cm）
     */
    public double getResolutionRateByAltitude() {
        return (this.altitude - baseLineHeight) * this.pixelSizeWidth / this.focalLength / 10.000D;
    }

    /* 计算有多少条航线 */
    public int getRouteLine() {
        double dRouteLine = this.airZoneHeight / this.getTrackSpacing();
        int routeLine = Double.valueOf(dRouteLine).intValue();
        if (dRouteLine > (double) routeLine)
            ++routeLine;
        return routeLine;
    }

    public double getPhotoWidth() {
        return (this.altitude - this.baseLineHeight) * this.sensorWidth / this.focalLength;
    }

    public double getPhotoHeight() {
        return (this.altitude - this.baseLineHeight) * this.sensorHeight / this.focalLength;
    }


    /* 计算两个航点之间的距离 */
    public double getAirPointSpacing() {
        return this.getPhotoHeight() * (1.0D - this.routeOverlap);
    }

    /* 计算航线间距 */
    public double getTrackSpacing() {
        return this.getPhotoWidth() * (1.0D - this.sideOverlap);
    }


    public double calculateMaxFlySpeed(int relativeAltitude, double routeOverlap) {
        return (1 - routeOverlap) * relativeAltitude * this.sensorHeight / this.focalLength / MIN_SHOOT_TIME_INTERVAL;
    }

    /**
     * 计算分辨率（单位为cm）
     *
     * @param relativeAltitude
     * @return
     */
    public double calculateResolution(int relativeAltitude) {
        return (relativeAltitude * this.pixelSizeWidth) / (10 * this.focalLength);
    }

    /* 计算单条航线航点数 */
    public int getSingleFlightNum() {
        double flightNum = this.airZoneWidth / this.getAirPointSpacing();
        int singleNum = Double.valueOf(flightNum).intValue();
        if (flightNum > (double) singleNum)
            ++singleNum;
        return singleNum;
    }

    public double getAvailableWidth() {
        return (this.airZoneWidth - this.getAirPointSpacing() * (double) (this.getSingleFlightNum() - 1)) / 2.0D;
    }

    public double getAvailableHeight() {
        return (this.airZoneHeight - this.getTrackSpacing() * (double) (this.getRouteLine() - 1)) / 2.0D;
    }

    
    public double getFlyTime() {
        double flyTime = this.airZoneWidth * (double) this.getRouteLine() / (double) (this.getActualSpeed() - 1)
                + this.airZoneHeight / (double) (this.getActualSpeed() - 1)
                + (double) (3 * this.getRouteLine())
                + Math.sqrt(Math.pow(this.airZoneWidth - this.getAvailableWidth() * 2.0D, 2.0D)
                + Math.pow(this.airZoneHeight - this.getAvailableHeight() * 2.0D, 2.0D)) / (double) (this.getActualSpeed() - 1)
                + this.gotoTaskDistance * 2.0D / 2.9D + (double) (this.getAltitude() * 2) / 2.9D;
        return flyTime / 60.0D;
    }

    public boolean isShortDirection() {
        return this.shortDirection;
    }

    public void setShortDirection(boolean shortDirection) {
        this.shortDirection = shortDirection;
    }

    public boolean isFixedAltitude() {
        return this.fixedAltitude;
    }

    public void setFixedAltitude(boolean fixedAltitude) {
        this.fixedAltitude = fixedAltitude;
    }

    public boolean isPlaneDirection() {
        return this.planeDirection;
    }

    public void setPlaneDirection(boolean planeDirection) {
        this.planeDirection = planeDirection;
    }

    public void setBaseLineHeight(int baseLineHeight) {
        this.baseLineHeight = baseLineHeight;
    }

    public int getBaseLineHeight() {
        return this.baseLineHeight;
    }

    public double getPixelSizeWidth() {
        return pixelSizeWidth;
    }

    public double getFocalLength() {
        return focalLength;
    }

    public int getPhotoNumPerRow() {
        return photoNumPerRow;
    }

    public void setPhotoNumPerRow(int photoNumPerRow) {
        this.photoNumPerRow = photoNumPerRow;
    }

    public String getFixedAltitudeList() {
        return fixedAltitudeList;
    }

    public void setFixedAltitudeList(String fixedAltitudeList) {
        this.fixedAltitudeList = fixedAltitudeList;
    }

    public int getBuffer() {
        return Buffer;
    }

    public void setBuffer(int buffer) {
        Buffer = buffer;
    }

    public int getStartingPoint() {
        return startingPoint;
    }

    public void setStartingPoint(int startingPoint) {
        this.startingPoint = startingPoint;
    }

    public int getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(int endPoint) {
        this.endPoint = endPoint;
    }

    public int getPlaneYaw() {
        return planeYaw;
    }

    public void setPlaneYaw(int planeYaw) {
        this.planeYaw = planeYaw;
    }

    public void setCircleLine(boolean isCircleLine) {
        this.isCircleLine = isCircleLine;
    }

    public boolean isCircleLine() {
        return isCircleLine;
    }

    public void setReverse(boolean isReverse) {
        this.isReverse = isReverse;
    }

    public boolean isReverse() {
        return isReverse;
    }

    public double getResolution() {
        return resolution;
    }

    public void setResolution(double resolution) {
        this.resolution = resolution;
    }

    public void setFocalLength(double focalLength) {
        this.focalLength = focalLength;
    }

    public double getSensorWidth() {
        return sensorWidth;
    }

    public void setSensorWidth(double sensorWidth) {
        this.sensorWidth = sensorWidth;
    }

    public void setSensorHeight(double sensorHeight) {
        this.sensorHeight = sensorHeight;
    }

    public int getActionMode() {
        return actionMode;
    }

    public void setActionMode(int actionMode) {
        this.actionMode = actionMode;
    }

    public int getReturnMode() {
        return returnMode;
    }

    public void setReturnMode(int returnMode) {
        this.returnMode = returnMode;
    }

    public int getRecodeMode() {
        return recodeMode;
    }

    public void setRecodeMode(int recodeMode) {
        this.recodeMode = recodeMode;
    }

    public int getReturnHeight() {
        return returnHeight;
    }

    public void setReturnHeight(int returnHeight) {
        this.returnHeight = returnHeight;
    }

    public String getCameraName() {
        return cameraName;
    }

    public void setCameraName(String cameraName) {
        this.cameraName = cameraName;
    }
}