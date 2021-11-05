package com.ew.autofly.event.ui.topbar;



public class MonitorInfoUpdateEvent {

    private String info;

    public MonitorInfoUpdateEvent(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }
}
