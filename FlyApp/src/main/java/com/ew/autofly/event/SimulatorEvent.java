package com.ew.autofly.event;


public class SimulatorEvent {

    private boolean enable;

    public SimulatorEvent(boolean enable) {
        this.enable = enable;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
