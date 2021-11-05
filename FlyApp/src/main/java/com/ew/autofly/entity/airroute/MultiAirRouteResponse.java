package com.ew.autofly.entity.airroute;

import java.io.Serializable;


public class MultiAirRouteResponse implements Serializable {

    private int code;
    private String message;
    private MultiAirRoute data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public MultiAirRoute getData() {
        return data;
    }

    public void setData(MultiAirRoute data) {
        this.data = data;
    }
}
