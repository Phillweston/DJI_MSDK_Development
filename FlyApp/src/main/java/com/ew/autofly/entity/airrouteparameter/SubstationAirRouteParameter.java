package com.ew.autofly.entity.airrouteparameter;

import com.ew.autofly.entity.AirRouteParameter;


public class SubstationAirRouteParameter extends AirRouteParameter {

    public final static int MODE_PATROL = 0;
    public final static int MODE_STUDY = 1;


    private int mode;

    /* 巡检速度 */
    private float mPatrolSpeed = 0.0F;

    public SubstationAirRouteParameter(String cameraName) {
        super(cameraName);
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public float getPatrolSpeed() {
        return mPatrolSpeed;
    }

    public void setPatrolSpeed(float patrolSpeed) {
        mPatrolSpeed = patrolSpeed;
    }
}
