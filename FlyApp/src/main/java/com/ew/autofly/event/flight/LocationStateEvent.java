package com.ew.autofly.event.flight;

import com.ew.autofly.entity.LocationCoordinate;


public class LocationStateEvent {

    private boolean isRtkCoordinate;
    private LocationCoordinate mAircraftCoordinate;
    private LocationCoordinate mBaseCoordinate;

    public LocationStateEvent(boolean isRtkCoordinate, LocationCoordinate aircraftCoordinate, LocationCoordinate baseCoordinate) {
        this.isRtkCoordinate = isRtkCoordinate;
        mAircraftCoordinate = aircraftCoordinate;
        mBaseCoordinate = baseCoordinate;
    }

    public LocationCoordinate getAircraftCoordinate() {
        return mAircraftCoordinate;
    }

    public void setAircraftCoordinate(LocationCoordinate aircraftCoordinate) {
        mAircraftCoordinate = aircraftCoordinate;
    }

    public LocationCoordinate getBaseCoordinate() {
        return mBaseCoordinate;
    }

    public void setBaseCoordinate(LocationCoordinate baseCoordinate) {
        mBaseCoordinate = baseCoordinate;
    }

    public boolean isRtkCoordinate() {
        return isRtkCoordinate;
    }

    public void setRtkCoordinate(boolean rtkCoordinate) {
        isRtkCoordinate = rtkCoordinate;
    }
}
