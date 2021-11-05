package com.ew.autofly.entity;



public class LineSlope {
    private double A;
    private double B;
    private double C;
    private LatLngInfo point1;
    private LatLngInfo point2;

    public LineSlope(double a, double b, double c, LatLngInfo point1, LatLngInfo point2) {
        A = a;
        B = b;
        C = c;
        this.point1 = point1;
        this.point2 = point2;
    }

    public double getA() {
        return A;
    }

    public void setA(double a) {
        A = a;
    }

    public double getB() {
        return B;
    }

    public void setB(double b) {
        B = b;
    }

    public double getC() {
        return C;
    }

    public void setC(double c) {
        C = c;
    }

    public LatLngInfo getPoint1() {
        return point1;
    }

    public void setPoint1(LatLngInfo point1) {
        this.point1 = point1;
    }

    public LatLngInfo getPoint2() {
        return point2;
    }

    public void setPoint2(LatLngInfo point2) {
        this.point2 = point2;
    }
}