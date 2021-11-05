package com.ew.autofly.entity.geometry;

import com.esri.arcgisruntime.geometry.Polyline;


public class GeoLine {

    private String gid = "";
    private Polyline polyline;
    private String name = "";

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public Polyline getPolyline() {
        return polyline;
    }

    public void setPolyline(Polyline polyline) {
        this.polyline = polyline;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
