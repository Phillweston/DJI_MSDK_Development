package com.ew.autofly.event.ui.topbar;



public class MonitorInfoShowEvent {

    private boolean isShow;

    public MonitorInfoShowEvent(boolean isShow) {
        this.isShow = isShow;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }
}
