package com.ew.autofly.mode.linepatrol.point.ui.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;

import java.util.Date;

public class FlyCamera {
    @Id
    public String id;
    public Date createTime;
    @NotNull
    public float focalLength;
    @NotNull
    public String name;
    public int iso;
    public int aperture;
    public int exposureCompensation;
    public int shutterSpeed;
    public int imageWidth;
    public int imageHeight;
    public float opticalFormat;
    public float opticalFormatHeight;
    public String imgType;
    public float sensorHeight;
    public float sensorWidth;
    public float minInterval;
    public boolean isCustom;
    public boolean isCustomSelected;
@Generated(hash = 1007203268)
public FlyCamera(String id, Date createTime, float focalLength,
                 @NotNull String name, int iso, int aperture, int exposureCompensation,
                 int shutterSpeed, int imageWidth, int imageHeight, float opticalFormat,
                 float opticalFormatHeight, String imgType, float sensorHeight,
                 float sensorWidth, float minInterval, boolean isCustom,
                 boolean isCustomSelected) {
    this.id = id;
    this.createTime = createTime;
    this.focalLength = focalLength;
    this.name = name;
    this.iso = iso;
    this.aperture = aperture;
    this.exposureCompensation = exposureCompensation;
    this.shutterSpeed = shutterSpeed;
    this.imageWidth = imageWidth;
    this.imageHeight = imageHeight;
    this.opticalFormat = opticalFormat;
    this.opticalFormatHeight = opticalFormatHeight;
    this.imgType = imgType;
    this.sensorHeight = sensorHeight;
    this.sensorWidth = sensorWidth;
    this.minInterval = minInterval;
    this.isCustom = isCustom;
    this.isCustomSelected = isCustomSelected;
}
@Generated(hash = 165716801)
public FlyCamera() {
}
public String getId() {
    return this.id;
}
public void setId(String id) {
    this.id = id;
}
public Date getCreateTime() {
    return this.createTime;
}
public void setCreateTime(Date createTime) {
    this.createTime = createTime;
}
public float getFocalLength() {
    return this.focalLength;
}
public void setFocalLength(float focalLength) {
    this.focalLength = focalLength;
}
public String getName() {
    return this.name;
}
public void setName(String name) {
    this.name = name;
}
public int getIso() {
    return this.iso;
}
public void setIso(int iso) {
    this.iso = iso;
}
public int getAperture() {
    return this.aperture;
}
public void setAperture(int aperture) {
    this.aperture = aperture;
}
public int getExposureCompensation() {
    return this.exposureCompensation;
}
public void setExposureCompensation(int exposureCompensation) {
    this.exposureCompensation = exposureCompensation;
}
public int getShutterSpeed() {
    return this.shutterSpeed;
}
public void setShutterSpeed(int shutterSpeed) {
    this.shutterSpeed = shutterSpeed;
}
public int getImageWidth() {
    return this.imageWidth;
}
public void setImageWidth(int imageWidth) {
    this.imageWidth = imageWidth;
}
public int getImageHeight() {
    return this.imageHeight;
}
public void setImageHeight(int imageHeight) {
    this.imageHeight = imageHeight;
}
public float getOpticalFormat() {
    return this.opticalFormat;
}
public void setOpticalFormat(float opticalFormat) {
    this.opticalFormat = opticalFormat;
}
public float getOpticalFormatHeight() {
    return this.opticalFormatHeight;
}
public void setOpticalFormatHeight(float opticalFormatHeight) {
    this.opticalFormatHeight = opticalFormatHeight;
}
public String getImgType() {
    return this.imgType;
}
public void setImgType(String imgType) {
    this.imgType = imgType;
}
public float getSensorHeight() {
    return this.sensorHeight;
}
public void setSensorHeight(float sensorHeight) {
    this.sensorHeight = sensorHeight;
}
public float getSensorWidth() {
    return this.sensorWidth;
}
public void setSensorWidth(float sensorWidth) {
    this.sensorWidth = sensorWidth;
}
public float getMinInterval() {
    return this.minInterval;
}
public void setMinInterval(float minInterval) {
    this.minInterval = minInterval;
}
public boolean getIsCustom() {
    return this.isCustom;
}
public void setIsCustom(boolean isCustom) {
    this.isCustom = isCustom;
}
public boolean getIsCustomSelected() {
    return this.isCustomSelected;
}
public void setIsCustomSelected(boolean isCustomSelected) {
    this.isCustomSelected = isCustomSelected;
}

}
