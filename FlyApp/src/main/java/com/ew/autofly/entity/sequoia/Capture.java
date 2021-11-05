package com.ew.autofly.entity.sequoia;



public class Capture {

    private String request;

    private String status;

    private String path;

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "Capture{" +
                "request='" + request + '\'' +
                ", status='" + status + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
