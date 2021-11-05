package com.ew.autofly.utils;

import com.ew.autofly.entity.LineSegment;
import com.ew.autofly.entity.LineSlope;
import com.ew.autofly.entity.LatLngInfo;

import java.math.BigDecimal;
import java.util.ArrayList;


public class LineSlopeUtils {
    private static LineSlope calculateSingleSlope(LatLngInfo p1, LatLngInfo p2) {
        double a = p2.latitude - p1.latitude;
        double b = p1.longitude - p2.longitude;
        double c = p1.latitude * p2.longitude - p1.longitude * p2.latitude;
        return new LineSlope(a, b, c, p1, p2);
    }

    /**
     * 传入多边形的顶点，计算所有边的直线方程
     *
     * @param points
     * @return
     */
    public static ArrayList<LineSlope> calculateAllLineSlope(ArrayList<LatLngInfo> points) {
        ArrayList<LineSlope> slopeList = new ArrayList<>();
        for (int i = 0, size = points.size(); i < size; i++) {
            LineSlope lineSlope;
            if (i == size - 1)
                lineSlope = calculateSingleSlope(points.get(i), points.get(0));
            else
                lineSlope = calculateSingleSlope(points.get(i), points.get(i + 1));
            slopeList.add(lineSlope);
        }
        return slopeList;
    }

    /**
     * 获取平行线移动分割方向
     *
     * @param xielv0
     * @param xielv1
     * @param xielv2
     * @param distance
     * @return
     */
    private static int getDirection(LineSlope xielv0, LineSlope xielv1, LineSlope xielv2, double distance) {
        BigDecimal A = new BigDecimal(xielv1.getA()).multiply(new BigDecimal(xielv1.getA()));
        BigDecimal B = new BigDecimal(xielv1.getB()).multiply(new BigDecimal(xielv1.getB()));
        BigDecimal add = A.add(B);
        double c = new BigDecimal(xielv1.getC()).add(new BigDecimal(distance).multiply(new BigDecimal(1)).multiply(new BigDecimal(StrictMath.sqrt(add.doubleValue())))).doubleValue();
        double xPoint = (new BigDecimal(xielv2.getC()).multiply(new BigDecimal(xielv1.getB())).subtract(new BigDecimal(c).multiply(new BigDecimal(xielv2.getB()))))
                .divide((new BigDecimal(xielv1.getA()).multiply(new BigDecimal(xielv2.getB())).subtract(new BigDecimal(xielv1.getB()).multiply(new BigDecimal(xielv2.getA())))), 20, BigDecimal.ROUND_HALF_DOWN).doubleValue();
        double yPoint = (new BigDecimal(xielv1.getA()).multiply(new BigDecimal(xielv2.getC())).subtract(new BigDecimal(c).multiply(new BigDecimal(xielv2.getA()))))
                .divide((new BigDecimal(xielv1.getB()).multiply(new BigDecimal(xielv2.getA())).subtract(new BigDecimal(xielv1.getA()).multiply(new BigDecimal(xielv2.getB())))), 20, BigDecimal.ROUND_HALF_DOWN).doubleValue();
        LatLngInfo landPoint = new LatLngInfo(xPoint, yPoint);
        double a = xielv2.getPoint1().longitude > xielv2.getPoint2().longitude ? xielv2.getPoint1().longitude : xielv2.getPoint2().longitude;
        double a1 = xielv2.getPoint1().longitude < xielv2.getPoint2().longitude ? xielv2.getPoint1().longitude : xielv2.getPoint2().longitude;
        double b = xielv2.getPoint1().latitude > xielv2.getPoint2().latitude ? xielv2.getPoint1().latitude : xielv2.getPoint2().latitude;
        double b1 = xielv2.getPoint1().latitude < xielv2.getPoint2().latitude ? xielv2.getPoint1().latitude : xielv2.getPoint2().latitude;
        if (landPoint.longitude <= a && landPoint.longitude >= a1 && landPoint.latitude <= b && landPoint.latitude >= b1) {
            System.out.println(landPoint.longitude + "__Point__" + landPoint.latitude);
            return 1;
        } else {
            double c1 = new BigDecimal(xielv1.getC()).add(new BigDecimal(distance).multiply(new BigDecimal(1)).multiply(new BigDecimal(StrictMath.sqrt(new BigDecimal(xielv1.getA()).multiply(new BigDecimal(xielv1.getA())).add(new BigDecimal(xielv1.getB()).multiply(new BigDecimal(xielv1.getB()))).doubleValue())))).doubleValue();
            double xPoint1 = (new BigDecimal(xielv0.getC()).multiply(new BigDecimal(xielv1.getB())).subtract(new BigDecimal(c1).multiply(new BigDecimal(xielv0.getB()))))
                    .divide((new BigDecimal(xielv1.getA()).multiply(new BigDecimal(xielv0.getB())).subtract(new BigDecimal(xielv1.getB()).multiply(new BigDecimal(xielv0.getA())))), 20, BigDecimal.ROUND_HALF_DOWN).doubleValue();
            double yPoint1 = (new BigDecimal(xielv1.getA()).multiply(new BigDecimal(xielv0.getC())).subtract(new BigDecimal(c1).multiply(new BigDecimal(xielv0.getA()))))
                    .divide((new BigDecimal(xielv1.getB()).multiply(new BigDecimal(xielv0.getA())).subtract(new BigDecimal(xielv1.getA()).multiply(new BigDecimal(xielv0.getB())))), 20, BigDecimal.ROUND_HALF_DOWN).doubleValue();
            LatLngInfo landPoint1 = new LatLngInfo(xPoint1, yPoint1);
            double a11 = xielv0.getPoint1().longitude > xielv0.getPoint2().longitude ? xielv0.getPoint1().longitude : xielv0.getPoint2().longitude;
            double a12 = xielv0.getPoint1().longitude < xielv0.getPoint2().longitude ? xielv0.getPoint1().longitude : xielv0.getPoint2().longitude;
            double b11 = xielv0.getPoint1().latitude > xielv0.getPoint2().latitude ? xielv0.getPoint1().latitude : xielv0.getPoint2().latitude;
            double b12 = xielv0.getPoint1().latitude < xielv0.getPoint2().latitude ? xielv0.getPoint1().latitude : xielv0.getPoint2().latitude;
            if (landPoint1.longitude <= a11 && landPoint1.longitude >= a12 && landPoint1.latitude <= b11 && landPoint1.latitude >= b12) {
                return 1;
            } else
                return 0;
        }
    }

    /**
     * 获取所有平行线段的端点
     * @param position 以多边形的哪条边为基准
     * @param lineSlopes 多边形的边（一般方程式）
     * @param distance 平行线的间距
     * @return
     */
    public static ArrayList<LineSegment> getAllLinePoints(int position, ArrayList<LineSlope> lineSlopes, double distance) {
        ArrayList<LineSegment> langPointArrayLists = new ArrayList<>();
        int direction;
        final double D = lineSlopes.get(position).getC();
        double C = lineSlopes.get(position).getC();
        int j = 1;
        boolean isComputing = true;
        if (position == lineSlopes.size() - 1)
            direction = getDirection(lineSlopes.get(position - 1), lineSlopes.get(position), lineSlopes.get(0), distance);
        else if (position == 0)
            direction = getDirection(lineSlopes.get(lineSlopes.size() - 1), lineSlopes.get(position), lineSlopes.get(position + 1), distance);
        else
            direction = getDirection(lineSlopes.get(position - 1), lineSlopes.get(position), lineSlopes.get(position + 1), distance);
        LineSlope lineSlopeOld = new LineSlope(lineSlopes.get(position).getA(), lineSlopes.get(position).getB(), lineSlopes.get(position).getC(), lineSlopes.get(position).getPoint1(), lineSlopes.get(position).getPoint2());
        lineSlopes.remove(position);
        while (isComputing) {
            LineSegment segment = new LineSegment();
            switch (direction) {
                case 0:
                    C = new BigDecimal(D).subtract(new BigDecimal(distance).multiply(new BigDecimal(j)).multiply(new BigDecimal(StrictMath.sqrt(new BigDecimal(lineSlopeOld.getA()).multiply(new BigDecimal(lineSlopeOld.getA())).add(new BigDecimal(lineSlopeOld.getB()).multiply(new BigDecimal(lineSlopeOld.getB()))).doubleValue())))).doubleValue();
                    break;
                case 1:
                    C = new BigDecimal(D).add(new BigDecimal(distance).multiply(new BigDecimal(j)).multiply(new BigDecimal(StrictMath.sqrt(new BigDecimal(lineSlopeOld.getA()).multiply(new BigDecimal(lineSlopeOld.getA())).add(new BigDecimal(lineSlopeOld.getB()).multiply(new BigDecimal(lineSlopeOld.getB()))).doubleValue())))).doubleValue();
                    break;
            }
            lineSlopeOld.setC(C);
            for (int i = 0; i < lineSlopes.size(); i++) {
                double xPoint = (new BigDecimal(lineSlopes.get(i).getC()).multiply(new BigDecimal(lineSlopeOld.getB())).subtract(new BigDecimal(lineSlopeOld.getC()).multiply(new BigDecimal(lineSlopes.get(i).getB())))).divide((new BigDecimal(lineSlopeOld.getA()).multiply(new BigDecimal(lineSlopes.get(i).getB())).subtract(new BigDecimal(lineSlopeOld.getB()).multiply(new BigDecimal(lineSlopes.get(i).getA())))), 20, BigDecimal.ROUND_HALF_DOWN).doubleValue();
                double yPoint = (new BigDecimal(lineSlopeOld.getA()).multiply(new BigDecimal(lineSlopes.get(i).getC())).subtract(new BigDecimal(lineSlopeOld.getC()).multiply(new BigDecimal(lineSlopes.get(i).getA())))).divide((new BigDecimal(lineSlopeOld.getB()).multiply(new BigDecimal(lineSlopes.get(i).getA())).subtract(new BigDecimal(lineSlopeOld.getA()).multiply(new BigDecimal(lineSlopes.get(i).getB())))), 20, BigDecimal.ROUND_HALF_DOWN).doubleValue();
                LatLngInfo latLngInfo = new LatLngInfo(yPoint, xPoint);
                double a = lineSlopes.get(i).getPoint1().longitude > lineSlopes.get(i).getPoint2().longitude ? lineSlopes.get(i).getPoint1().longitude : lineSlopes.get(i).getPoint2().longitude;
                double a1 = lineSlopes.get(i).getPoint1().longitude < lineSlopes.get(i).getPoint2().longitude ? lineSlopes.get(i).getPoint1().longitude : lineSlopes.get(i).getPoint2().longitude;
                double b = lineSlopes.get(i).getPoint1().latitude > lineSlopes.get(i).getPoint2().latitude ? lineSlopes.get(i).getPoint1().latitude : lineSlopes.get(i).getPoint2().latitude;
                double b1 = lineSlopes.get(i).getPoint1().latitude < lineSlopes.get(i).getPoint2().latitude ? lineSlopes.get(i).getPoint1().latitude : lineSlopes.get(i).getPoint2().latitude;
                if (latLngInfo.longitude <= a && latLngInfo.longitude >= a1 && latLngInfo.latitude <= b && latLngInfo.latitude >= b1) {
                    if (segment.getStartPoint() == null)
                        segment.setStartPoint(latLngInfo);
                    else
                        segment.setEndPoint(latLngInfo);
                }
            }
            if (segment.getStartPoint() == null || segment.getEndPoint() == null)
                isComputing = false;
            else
                langPointArrayLists.add(segment);
            j = j + 1;
        }
        return langPointArrayLists;
    }
}