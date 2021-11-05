package com.ew.autofly.entity;

import java.io.Serializable;


public class WayPointInfo implements Serializable {


    private LocationCoordinate position;


    private double heading;

    public LocationCoordinate getPosition() {
        return position;
    }

    public void setPosition(LocationCoordinate position) {
        this.position = position;
    }

    public double getHeading() {
        return heading;
    }

    public void setHeading(double heading) {
        this.heading = heading;
    }
}
