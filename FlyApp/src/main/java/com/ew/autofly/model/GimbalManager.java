package com.ew.autofly.model;

import com.ew.autofly.application.EWApplication;

import dji.sdk.base.BaseProduct;
import dji.sdk.gimbal.Gimbal;


public class GimbalManager {


    public static final float GIMBAL_PITCH_MAX = 0.0F;
    public static final float GIMBAL_PITCH_MAX_P4R = 30F;
    public static final float GIMBAL_PITCH_MIN = -90.0F;

    public static Gimbal getGimbal() {
        BaseProduct productInstance = EWApplication.getProductInstance();
        if (productInstance != null) {
            return productInstance.getGimbal();
        }

        return null;
    }

    public static int getAppropriateGimbalPitch(float pitch) {
        int waypointPicth;

        int max = (int) GIMBAL_PITCH_MAX;
        if (AircraftManager.isP4R()) {
            max = (int) GIMBAL_PITCH_MAX_P4R;
        }
        int min = (int) GIMBAL_PITCH_MIN;

        if (pitch > max) {
            waypointPicth = max;
        } else if (pitch < min) {
            waypointPicth = min;
        } else {
            waypointPicth = Math.round(pitch);
        }
        return waypointPicth;
    }

    public static boolean isAppropriateWaypointPitch(float pitch) {

        int[] range = getWaypointPitchRange();

        return pitch >= range[0] && pitch <= range[1];

    }

    /**
     * 获取航点云台角度范围
     *
     * @return int[2]数值，0为最小值，1为最大值
     */
    public static int[] getWaypointPitchRange() {
        int max = (int) GIMBAL_PITCH_MAX;
        if (AircraftManager.isP4R()) {
            max = (int) GIMBAL_PITCH_MAX_P4R;
        }
        int min = (int) GIMBAL_PITCH_MIN;
        int[] range = new int[2];
        range[0] = min;
        range[1] = max;
        return range;
    }
}
