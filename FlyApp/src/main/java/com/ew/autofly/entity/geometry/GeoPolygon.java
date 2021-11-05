package com.ew.autofly.entity.geometry;

import com.esri.arcgisruntime.geometry.Polygon;


public class GeoPolygon {

    private String gid = "";
    private Polygon polygon;
    private String name = "";

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public Polygon getPolygon() {
        return polygon;
    }

    public void setPolygon(Polygon polygon) {
        this.polygon = polygon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
