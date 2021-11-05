package com.ew.autofly.event;



public class DJIRegistrationEvent {

    private boolean isSuccess;

    public DJIRegistrationEvent(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public boolean isSuccess() {
        return isSuccess;
    }
}
