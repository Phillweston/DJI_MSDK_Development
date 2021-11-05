package com.ew.autofly.entity.sequoia.status;



public class Temperature {

    private float body_p7;

    private float body_p7mu;

    private float body_ddr3;

    private float body_wifi;

    private float body_imu;

    private float sunshine_imu;

    private String status;

    public float getBody_p7() {
        return body_p7;
    }

    public void setBody_p7(float body_p7) {
        this.body_p7 = body_p7;
    }

    public float getBody_p7mu() {
        return body_p7mu;
    }

    public void setBody_p7mu(float body_p7mu) {
        this.body_p7mu = body_p7mu;
    }

    public float getBody_ddr3() {
        return body_ddr3;
    }

    public void setBody_ddr3(float body_ddr3) {
        this.body_ddr3 = body_ddr3;
    }

    public float getBody_wifi() {
        return body_wifi;
    }

    public void setBody_wifi(float body_wifi) {
        this.body_wifi = body_wifi;
    }

    public float getBody_imu() {
        return body_imu;
    }

    public void setBody_imu(float body_imu) {
        this.body_imu = body_imu;
    }

    public float getSunshine_imu() {
        return sunshine_imu;
    }

    public void setSunshine_imu(float sunshine_imu) {
        this.sunshine_imu = sunshine_imu;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
