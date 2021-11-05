package com.ew.autofly.entity.airrouteparameter;

import com.ew.autofly.entity.AirRouteParameter;


public class RiverAutoPatrolAirRouteParameter extends AirRouteParameter {

    public final static int MODE_PATROL = 0;
    public final static int MODE_STUDY = 1;

    public final static int AIRROUTE_MODE_CURVE = 0;
    public final static int AIRROUTE_MODE_POINT_TO_POINT = 1;


    private int mode;


    private int airRouteMode;

    public RiverAutoPatrolAirRouteParameter(String cameraName) {
        super(cameraName);
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getAirRouteMode() {
        return airRouteMode;
    }

    public void setAirRouteMode(int airRouteMode) {
        this.airRouteMode = airRouteMode;
    }
}
