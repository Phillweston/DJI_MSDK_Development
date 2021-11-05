package com.ew.autofly.entity;

import java.io.Serializable;



public class RiverCirclePoint implements Serializable {
    private double latitude;
    private double longitude;
    private double altitude;
    private double Nradius;
    private double Bradius;

    public RiverCirclePoint(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public double getNradius() {
        return Nradius;
    }

    public void setNradius(double nradius) {
        Nradius = nradius;
    }

    public double getBradius() {
        return Bradius;
    }

    public void setBradius(double bradius) {
        Bradius = bradius;
    }
}
