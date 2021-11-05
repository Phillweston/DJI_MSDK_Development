package com.ew.autofly.event;


public class RiverPatrolModeEvent {

    public final static int MODE_GPS = 0;
    public final static int MODE_RTK = 1;

    private int mode;

    public RiverPatrolModeEvent(int mode) {
        this.mode = mode;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }
}
