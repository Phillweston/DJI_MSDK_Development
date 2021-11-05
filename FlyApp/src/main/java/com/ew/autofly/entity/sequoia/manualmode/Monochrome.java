package com.ew.autofly.entity.sequoia.manualmode;



public class Monochrome {

    private String mode;

    private CameraParameters green;

    private CameraParameters red;

    private CameraParameters red_edge;

    private CameraParameters near_infrared;

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public CameraParameters getGreen() {
        return green;
    }

    public void setGreen(CameraParameters green) {
        this.green = green;
    }

    public CameraParameters getRed() {
        return red;
    }

    public void setRed(CameraParameters red) {
        this.red = red;
    }

    public CameraParameters getRed_edge() {
        return red_edge;
    }

    public void setRed_edge(CameraParameters red_edge) {
        this.red_edge = red_edge;
    }

    public CameraParameters getNear_infrared() {
        return near_infrared;
    }

    public void setNear_infrared(CameraParameters near_infrared) {
        this.near_infrared = near_infrared;
    }
}
