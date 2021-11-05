package com.ew.autofly.entity;

import java.io.Serializable;

public class LatLngCloudPoint implements Serializable{
    public double latitude;
    public double longitude;
    public double altitude;

    public LatLngCloudPoint() {
    }

    public LatLngCloudPoint(double latitude, double longitude, double altitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
    }
}