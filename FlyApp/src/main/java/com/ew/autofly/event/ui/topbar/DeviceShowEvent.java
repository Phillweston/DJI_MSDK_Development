package com.ew.autofly.event.ui.topbar;


public class DeviceShowEvent {

    private boolean isShow;

    public DeviceShowEvent(boolean isShow) {
        this.isShow = isShow;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }
}
