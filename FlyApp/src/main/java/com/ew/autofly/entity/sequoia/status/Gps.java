package com.ew.autofly.entity.sequoia.status;



public class Gps {

    private int satellite_number;

    private double precision;

    private float speed;

    private int fix;

    private String status;

    public int getSatellite_number() {
        return satellite_number;
    }

    public void setSatellite_number(int satellite_number) {
        this.satellite_number = satellite_number;
    }

    public double getPrecision() {
        return precision;
    }

    public void setPrecision(double precision) {
        this.precision = precision;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public int getFix() {
        return fix;
    }

    public void setFix(int fix) {
        this.fix = fix;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
