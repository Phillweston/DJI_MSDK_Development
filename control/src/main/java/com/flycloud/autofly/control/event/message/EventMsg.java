package com.flycloud.autofly.control.event.message;



public class EventMsg {

    private int code = Integer.MIN_VALUE;

    private String message;

    public EventMsg(int code, String message) {
        this.code = code;
        this.message = message;
    }

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

  
    public boolean codeEqual(EventMsg eventMsg) {
        return eventMsg != null && eventMsg.code == code;
    }
}
