package com.ew.autofly.entity.sequoia.manualmode;



public class ManualMode {

    private String request;

    private RGB rgb;

    private Monochrome monochrome;

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public RGB getRgb() {
        return rgb;
    }

    public void setRgb(RGB rgb) {
        this.rgb = rgb;
    }

    public Monochrome getMonochrome() {
        return monochrome;
    }

    public void setMonochrome(Monochrome monochrome) {
        this.monochrome = monochrome;
    }
}
