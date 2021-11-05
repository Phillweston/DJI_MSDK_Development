package com.ew.autofly.widgets.CloudPointView2;

import com.ew.autofly.widgets.CloudPointView2.util.LatLngCloudPoint;

import java.util.ArrayList;



public class ThreeDimensionEntity {

    private ArrayList<Float> cloudPointLists;

    private ArrayList<LatLngCloudPoint> highLightPoint;

    private ArrayList<LatLngCloudPoint> airLinePoint;

    private ArrayList<LatLngCloudPoint> photoPoint;

    public ArrayList<Float> getCloudPointLists() {
        return cloudPointLists;
    }

    public void setCloudPointLists(ArrayList<Float> cloudPointLists) {
        this.cloudPointLists = cloudPointLists;
    }

    public ArrayList<LatLngCloudPoint> getHighLightPoint() {
        return highLightPoint;
    }

    public void setHighLightPoint(ArrayList<LatLngCloudPoint> highLightPoint) {
        this.highLightPoint = highLightPoint;
    }

    public ArrayList<LatLngCloudPoint> getAirLinePoint() {
        return airLinePoint;
    }

    public void setAirLinePoint(ArrayList<LatLngCloudPoint> airLinePoint) {
        this.airLinePoint = airLinePoint;
    }

    public ArrayList<LatLngCloudPoint> getPhotoPoint() {
        return photoPoint;
    }

    public void setPhotoPoint(ArrayList<LatLngCloudPoint> photoPoint) {
        this.photoPoint = photoPoint;
    }
}
