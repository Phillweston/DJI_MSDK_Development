package com.ew.autofly.entity.airrouteparameter;

import com.ew.autofly.entity.AirRouteParameter;



public class TreeObstacleAirRouteParameter extends AirRouteParameter {

    private float baseFlyAltitude;

    public TreeObstacleAirRouteParameter(String cameraName) {
        super(cameraName);
    }

    public float getBaseFlyAltitude() {
        return baseFlyAltitude;
    }

    public void setBaseFlyAltitude(float baseFlyAltitude) {
        this.baseFlyAltitude = baseFlyAltitude;
    }

    @Override
    public double getPhotoWidth() {
        return (this.baseFlyAltitude - getBaseLineHeight()) * getSensorWidth() / getFocalLength();
    }

    @Override
    public double getPhotoHeight() {
        return (this.baseFlyAltitude - getBaseLineHeight()) * getSensorHeight() / getFocalLength();
    }

    /**
     * 计算照片覆盖的宽度
     *
     * @return
     */
    public double getTrackSpacingCoverWidth() {
        return ((this.baseFlyAltitude - getBaseLineHeight()) * getSensorWidth() / getFocalLength()) * (2.0D - getSideOverlap());
    }

}
