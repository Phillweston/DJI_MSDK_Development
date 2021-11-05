package com.ew.autofly.entity.sequoia.status;



public class Instruments {

    private double body_yaw;

    private double body_pitch;

    private double body_roll;

    private double sunshine_yaw;

    private double sunshine_pitch;

    private double sunshine_roll;

    private String status;

    public double getBody_yaw() {
        return body_yaw;
    }

    public void setBody_yaw(double body_yaw) {
        this.body_yaw = body_yaw;
    }

    public double getBody_pitch() {
        return body_pitch;
    }

    public void setBody_pitch(double body_pitch) {
        this.body_pitch = body_pitch;
    }

    public double getBody_roll() {
        return body_roll;
    }

    public void setBody_roll(double body_roll) {
        this.body_roll = body_roll;
    }

    public double getSunshine_yaw() {
        return sunshine_yaw;
    }

    public void setSunshine_yaw(double sunshine_yaw) {
        this.sunshine_yaw = sunshine_yaw;
    }

    public double getSunshine_pitch() {
        return sunshine_pitch;
    }

    public void setSunshine_pitch(double sunshine_pitch) {
        this.sunshine_pitch = sunshine_pitch;
    }

    public double getSunshine_roll() {
        return sunshine_roll;
    }

    public void setSunshine_roll(double sunshine_roll) {
        this.sunshine_roll = sunshine_roll;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
