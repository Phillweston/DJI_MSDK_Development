package com.ew.autofly.entity.sequoia.storage;



public class StorageStatus {

    private double total;

    private double free;

    private String path;

    private String read_only;

    private String corrupted;

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getFree() {
        return free;
    }

    public void setFree(double free) {
        this.free = free;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getRead_only() {
        return read_only;
    }

    public void setRead_only(String read_only) {
        this.read_only = read_only;
    }

    public String getCorrupted() {
        return corrupted;
    }

    public void setCorrupted(String corrupted) {
        this.corrupted = corrupted;
    }
}
