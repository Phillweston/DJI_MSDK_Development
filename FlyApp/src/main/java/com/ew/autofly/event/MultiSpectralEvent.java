package com.ew.autofly.event;



public class MultiSpectralEvent {

    public static final String CHECK_GPS_STATUS="CHECK_GPS_STATUS";
    public static final String GET_STORAGE="GET_STORAGE";
    public static final String GET_CAPTURE_STATE="GET_CAPTURE_STATE";
    public static final String SET_CONFIG="SET_CONFIG";
    public static final String START_CAPTURE="START_CAPTURE";
    public static final String CHANGE_MISSION_TYPE="CHANGE_MISSION_TYPE";


    private String key;

    private boolean ok;

    private Object object;

    public MultiSpectralEvent(String key, boolean ok, Object object) {
        this.key = key;
        this.ok = ok;
        this.object = object;
    }

    public String getKey() {
        return key;
    }

    public boolean isOk() {
        return ok;
    }

    public Object getObject() {
        return object;
    }
}
