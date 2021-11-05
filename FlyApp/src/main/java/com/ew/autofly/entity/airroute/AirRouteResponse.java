package com.ew.autofly.entity.airroute;

import java.io.Serializable;


public class AirRouteResponse implements Serializable {

    private int code;
    private String message;
    private AirRoute data;

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

    public AirRoute getData() {
        return data;
    }

    public void setData(AirRoute data) {
        this.data = data;
    }
}
