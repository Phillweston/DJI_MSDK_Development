package com.ew.autofly.event;


public class AddWayPointEvent {

    public final static int ACTION_ADD=0;
    public final static int ACTION_INSERT=1;

    private int action;
    private Object param;

    public AddWayPointEvent(int action, Object param) {
        this.action = action;
        this.param = param;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public Object getParam() {
        return param;
    }

    public void setParam(Object param) {
        this.param = param;
    }
}
