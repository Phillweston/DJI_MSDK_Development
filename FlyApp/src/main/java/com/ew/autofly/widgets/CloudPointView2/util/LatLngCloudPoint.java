package com.ew.autofly.widgets.CloudPointView2.util;

import java.io.Serializable;

public class LatLngCloudPoint implements Serializable{
    private double latitude;
    private double longitude;
    private double altitude;

    private double pointSize = 0.0;

    private float red;
    private float green;
    private float blue;

    public LatLngCloudPoint(double latitude, double longitude, double altitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
    }

    public LatLngCloudPoint(double latitude, double longitude, double altitude,
                            float red, float green, float blue) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public LatLngCloudPoint(double latitude, double longitude, double altitude, double pointSize,
                            float red, float green, float blue) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.pointSize = pointSize;
        this.red = red;
        this.green = green;
        this.blue = blue;
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

    public double getPointSize() {
        return pointSize;
    }

    public void setPointSize(double pointSize) {
        this.pointSize = pointSize;
    }

    public float getRed() {
        return red;
    }

    public void setRed(float red) {
        this.red = red;
    }

    public float getGreen() {
        return green;
    }

    public void setGreen(float green) {
        this.green = green;
    }

    public float getBlue() {
        return blue;
    }

    public void setBlue(float blue) {
        this.blue = blue;
    }
}