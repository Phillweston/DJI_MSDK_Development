package com.ew.autofly.entity;



public class LineSegment {
    private LatLngInfo startPoint;
    private LatLngInfo endPoint;

    public LatLngInfo getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(LatLngInfo startPoint) {
        this.startPoint = startPoint;
    }

    public LatLngInfo getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(LatLngInfo endPoint) {
        this.endPoint = endPoint;
    }
}
