package com.ew.autofly.event.ui.topbar;


public class MoreMenuShowEvent {
    private boolean isShow;

    public MoreMenuShowEvent(boolean isShow) {
        this.isShow = isShow;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }
}
