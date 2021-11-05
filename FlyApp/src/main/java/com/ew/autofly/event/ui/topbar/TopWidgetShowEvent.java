package com.ew.autofly.event.ui.topbar;


public class TopWidgetShowEvent {

    public static final int SHOW_DEVICE = 0;

    private int showWitch;
    private boolean isShow;

    public TopWidgetShowEvent(int showWitch, boolean isShow) {
        this.showWitch = showWitch;
        this.isShow = isShow;
    }

    public int getShowWitch() {
        return showWitch;
    }

    public boolean isShow() {
        return isShow;
    }
}
