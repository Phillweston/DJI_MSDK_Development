package com.ew.autofly.event.ui.topbar;


public class MainMenuShowEvent {

    private boolean isShow;

    public MainMenuShowEvent(boolean isShow) {
        this.isShow = isShow;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }
}
