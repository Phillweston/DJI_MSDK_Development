package com.ew.autofly.entity.sequoia.config;




public class Config {

    private String request;

    private String capture_mode;

    private double timelapse_param;

    private double gps_param;

    private double overlap_param;

    private int resolution_rgb;

    private double resolution_mono;

    private int bit_depth;

    private int sensors_mask;

    private String storage_selected;

    private String auto_select;

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getCapture_mode() {
        return capture_mode;
    }

    public void setCapture_mode(String capture_mode) {
        this.capture_mode = capture_mode;
    }

    public double getTimelapse_param() {
        return timelapse_param;
    }

    public void setTimelapse_param(double timelapse_param) {
        this.timelapse_param = timelapse_param;
    }

    public double getGps_param() {
        return gps_param;
    }

    public void setGps_param(double gps_param) {
        this.gps_param = gps_param;
    }

    public double getOverlap_param() {
        return overlap_param;
    }

    public void setOverlap_param(double overlap_param) {
        this.overlap_param = overlap_param;
    }

    public int getResolution_rgb() {
        return resolution_rgb;
    }

    public void setResolution_rgb(int resolution_rgb) {
        this.resolution_rgb = resolution_rgb;
    }

    public double getResolution_mono() {
        return resolution_mono;
    }

    public void setResolution_mono(double resolution_mono) {
        this.resolution_mono = resolution_mono;
    }

    public int getBit_depth() {
        return bit_depth;
    }

    public void setBit_depth(int bit_depth) {
        this.bit_depth = bit_depth;
    }

    public int getSensors_mask() {
        return sensors_mask;
    }

    public void setSensors_mask(int sensors_mask) {
        this.sensors_mask = sensors_mask;
    }

    public String getStorage_selected() {
        return storage_selected;
    }

    public void setStorage_selected(String storage_selected) {
        this.storage_selected = storage_selected;
    }

    public String getAuto_select() {
        return auto_select;
    }

    public void setAuto_select(String auto_select) {
        this.auto_select = auto_select;
    }
}
