package com.ew.autofly.entity.sequoia.status;



public class Status {

    private String request;

    private Gps gps;

    private Instruments instruments;

    private Sunshine sunshine;

    private Temperature temperature;

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public Gps getGps() {
        return gps;
    }

    public void setGps(Gps gps) {
        this.gps = gps;
    }

    public Instruments getInstruments() {
        return instruments;
    }

    public void setInstruments(Instruments instruments) {
        this.instruments = instruments;
    }

    public Sunshine getSunshine() {
        return sunshine;
    }

    public void setSunshine(Sunshine sunshine) {
        this.sunshine = sunshine;
    }

    public Temperature getTemperature() {
        return temperature;
    }

    public void setTemperature(Temperature temperature) {
        this.temperature = temperature;
    }
}
