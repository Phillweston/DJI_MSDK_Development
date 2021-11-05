package com.ew.autofly.event.ui.topbar;



public class DeviceEnableEvent {

    private boolean enable=false;

    public DeviceEnableEvent(boolean enable) {
        this.enable = enable;
    }

    public boolean isEnable() {
        return enable;
    }
}
