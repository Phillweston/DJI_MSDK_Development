package com.ew.autofly.entity.sequoia.config;



public class ConfigStatus {

    private String request;

    private String capture_mode;

    private String timelapse_param;

    private String gps_param;

    private String overlap_param;

    private String resolution_rgb;

    private String resolution_mono;

    private String bit_depth;

    private String sensors_mask;

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

    public String getTimelapse_param() {
        return timelapse_param;
    }

    public void setTimelapse_param(String timelapse_param) {
        this.timelapse_param = timelapse_param;
    }

    public String getGps_param() {
        return gps_param;
    }

    public void setGps_param(String gps_param) {
        this.gps_param = gps_param;
    }

    public String getOverlap_param() {
        return overlap_param;
    }

    public void setOverlap_param(String overlap_param) {
        this.overlap_param = overlap_param;
    }

    public String getResolution_rgb() {
        return resolution_rgb;
    }

    public void setResolution_rgb(String resolution_rgb) {
        this.resolution_rgb = resolution_rgb;
    }

    public String getResolution_mono() {
        return resolution_mono;
    }

    public void setResolution_mono(String resolution_mono) {
        this.resolution_mono = resolution_mono;
    }

    public String getBit_depth() {
        return bit_depth;
    }

    public void setBit_depth(String bit_depth) {
        this.bit_depth = bit_depth;
    }

    public String getSensors_mask() {
        return sensors_mask;
    }

    public void setSensors_mask(String sensors_mask) {
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

    public boolean checkOK(){
        return "Ok".equals(capture_mode)&&"Ok".equals(timelapse_param)
                &&"Ok".equals(gps_param)&&"Ok".equals(overlap_param)
                &&"Ok".equals(resolution_rgb)&&"Ok".equals(resolution_mono)
                &&"Ok".equals(bit_depth)&&"Ok".equals(sensors_mask)
                &&"Ok".equals(storage_selected)&&"Ok".equals(auto_select);
    }

    public String getError(){
        if(!"Ok".equals(capture_mode)){
            return "设置拍摄模式失败";
        }

        if(!"Ok".equals(timelapse_param)){
            return "设置拍摄时间间隔失败";
        }

        if(!"Ok".equals(gps_param)){
            return "设置拍摄GPS定距失败，请检查多光谱GPS模块信号";
        }

        if(!"Ok".equals(overlap_param)){
            return "设置航向重叠度失败";
        }

        if(!"Ok".equals(resolution_rgb)){
                return "设置RGB失败";
        }

        if(!"Ok".equals(resolution_mono)){
            return "设置单色光失败";
        }

        if(!"Ok".equals(bit_depth)){
            return "设置图片位深失败";
        }

        if(!"Ok".equals(sensors_mask)){
            return "选择图像传感器失败";
        }

        if(!"Ok".equals(storage_selected)){
            return "存储失败";
        }

        if(!"Ok".equals(storage_selected)){
            return "选择存储失败";
        }

        return null;
    }

    @Override
    public String toString() {
        return "ConfigStatus{" +
                "request='" + request + '\'' +
                ", capture_mode='" + capture_mode + '\'' +
                ", timelapse_param='" + timelapse_param + '\'' +
                ", gps_param='" + gps_param + '\'' +
                ", overlap_param='" + overlap_param + '\'' +
                ", resolution_rgb='" + resolution_rgb + '\'' +
                ", resolution_mono='" + resolution_mono + '\'' +
                ", bit_depth='" + bit_depth + '\'' +
                ", sensors_mask='" + sensors_mask + '\'' +
                ", storage_selected='" + storage_selected + '\'' +
                ", auto_select='" + auto_select + '\'' +
                '}';
    }
}
