package com.ew.autofly.entity.sequoia.storage;



public class Storage {

    private String request;

    private String storage_selected;

    private StorageStatus internal;

    private StorageStatus sd;

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getStorage_selected() {
        return storage_selected;
    }

    public void setStorage_selected(String storage_selected) {
        this.storage_selected = storage_selected;
    }

    public StorageStatus getInternal() {
        return internal;
    }

    public void setInternal(StorageStatus internal) {
        this.internal = internal;
    }

    public StorageStatus getSd() {
        return sd;
    }

    public void setSd(StorageStatus sd) {
        this.sd = sd;
    }
}
