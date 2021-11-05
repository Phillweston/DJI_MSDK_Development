package com.ew.autofly.entity;


public class Bound {
    private double mMinX = -999;
    private double mMinY = -999;
    private double mMaxX = 999;
    private double mMaxY = 999;

    public Bound(double minX, double minY, double maxX, double maxY) {
        this.mMinX = minX;
        this.mMinY = minY;
        this.mMaxX = maxX;
        this.mMaxY = maxY;
    }

    public double getMinX() {
        return mMinX;
    }

    public void setMinX(double mMinX) {
        this.mMinX = mMinX;
    }

    public double getMinY() {
        return mMinY;
    }

    public void setMinY(double mMinY) {
        this.mMinY = mMinY;
    }

    public double getMaxX() {
        return mMaxX;
    }

    public void setMaxX(double mMaxX) {
        this.mMaxX = mMaxX;
    }

    public double getMaxY() {
        return mMaxY;
    }

    public void setMaxY(double mMaxY) {
        this.mMaxY = mMaxY;
    }

    public boolean contain(LatLngInfo p) {
        return (p.latitude > mMinY
                && p.latitude < mMaxY
                && p.longitude > mMinX
                && p.longitude < mMaxX);
    }
}
