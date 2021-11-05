package com.ew.autofly.utils;

import androidx.annotation.NonNull;
import android.util.Log;

import com.ew.autofly.entity.LatLngInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;



public class DouglasPeuckerUtils {

    public static ArrayList<LatLngInfo> douglasPointList(ArrayList<LatLngInfo> infoArrayList, double thresholdValue) {
        ArrayList<LPoint> pInit = new ArrayList<>();
        ArrayList<LPoint> pFilter = new ArrayList<>();
        ArrayList<LatLngInfo> pReturn = new ArrayList<>();

        int size = infoArrayList.size();


        for (int i = 0; i < size; i++) {
            LPoint lPoint = new LPoint();
            lPoint.setId(i);
            lPoint.setLat(infoArrayList.get(i).latitude);
            lPoint.setLon(infoArrayList.get(i).longitude);
            pInit.add(lPoint);
        }


        pFilter.add(pInit.get(0));
        pFilter.add(pInit.get(pInit.size() - 1));


        LPoint[] enpInit = new LPoint[pInit.size()];
        Iterator<LPoint> iInit = pInit.iterator();
        int jj = 0;
        while (iInit.hasNext()) {
            enpInit[jj] = iInit.next();
            jj++;
        }

        Log.i("Ronny", "------Listsize before:" + pInit.size());

        TrajCompressC(enpInit, pFilter, 0, pInit.size() - 1, thresholdValue);

        Log.i("Ronny", "------Listsize after:" + pFilter.size());


        LPoint[] enpFilter = new LPoint[pFilter.size()];
        Iterator<LPoint> iF = pFilter.iterator();
        int i = 0;
        while (iF.hasNext()) {
            enpFilter[i] = iF.next();
            i++;
        }
        Arrays.sort(enpFilter);
        for (int j = 0; j < enpFilter.length; j++) {
            pReturn.add(new LatLngInfo(enpFilter[j].getLat(), enpFilter[j].getLon()));
        }
        return pReturn;
    }

    /**
     * 道格拉斯排序
     * @param enpInit 原始数组
     * @param enpArrayFilter 处理后数组
     * @param start 起始位
     * @param end 结束位
     * @param DMax 幅度
     */
    private static void TrajCompressC(LPoint[] enpInit, ArrayList<LPoint> enpArrayFilter, int start, int end, double DMax) {
        if (start < end) {
            double maxDist = 0;
            int cur_pt = 0;
            for (int i = start + 1; i < end; i++) {
                double curDist = getDistentForMaxPoint(enpInit[start], enpInit[end], enpInit[i]);
                if (curDist > maxDist) {
                    maxDist = curDist;
                    cur_pt = i;
                }
            }

            if (maxDist >= DMax) {
                enpArrayFilter.add(enpInit[cur_pt]);

                TrajCompressC(enpInit, enpArrayFilter, start, cur_pt, DMax);
                TrajCompressC(enpInit, enpArrayFilter, cur_pt, end, DMax);
            }
        }

    }

    /**
     * 点距离AB线段的最大距离
     * @param pA
     * @param pB
     * @param pX
     * @return
     */
    private static double getDistentForMaxPoint(LPoint pA, LPoint pB, LPoint pX) {
        double a = Math.abs(getDistance(pA, pB));
        double b = Math.abs(getDistance(pA, pX));
        double c = Math.abs(getDistance(pB, pX));
        double p = (a + b + c) / 2.0;
        double s = Math.sqrt(Math.abs(p * (p - a) * (p - b) * (p - c)));
        double d = s * 2.0 / a;
        return d;
    }

    public static double getDistance(LPoint pA, LPoint pB) {
        double radLat1 = Rad(pA.lon);
        double radLat2 = Rad(pB.lon);
        double delta_lon = Rad(pB.lat - pA.lat);
        double top_1 = Math.cos(radLat2) * Math.sin(delta_lon);
        double top_2 = Math.cos(radLat1) * Math.sin(radLat2)
                - Math.sin(radLat1) * Math.cos(radLat2) * Math.cos(delta_lon);
        double top = Math.sqrt(top_1 * top_1 + top_2 * top_2);
        double bottom = Math.sin(radLat1) * Math.sin(radLat2)
                + Math.cos(radLat1) * Math.cos(radLat2) * Math.cos(delta_lon);
        double delta_sigma = Math.atan2(top, bottom);
        double distance = delta_sigma * 6378137.0;
        return distance;
    }

    public static double Rad(double d) {
        return d * Math.PI / 180.0;
    }


    public static List<DPoint> compress(List<DPoint> points, int thresholdValue) {

        double maxH = 0;
        int index = 0;
        int end = points.size();
        for (int i = 1; i < end - 1; i++) {
            double h = H(points.get(i), points.get(0), points.get(end - 1));
            if (h > maxH) {
                maxH = h;
                index = i;
            }
        }


        List<DPoint> result = new ArrayList<>();
        if (maxH > thresholdValue) {
            List<DPoint> leftPoints = new ArrayList<>();
            List<DPoint> rightPoints = new ArrayList<>();

            for (int i = 0; i < end; i++) {
                if (i <= index) {
                    leftPoints.add(points.get(i));
                    if (i == index)
                        rightPoints.add(points.get(i));
                } else {
                    rightPoints.add(points.get(i));
                }
            }


            List<DPoint> leftResult = new ArrayList<>();
            List<DPoint> rightResult = new ArrayList<>();
            leftResult = compress(leftPoints, thresholdValue);
            rightResult = compress(rightPoints, thresholdValue);


            rightResult.remove(0);
            leftResult.addAll(rightResult);
            result = leftResult;
        } else {// 如果不存在最大阈值点则返回当前遍历的子曲线的起始点
            result.add(points.get(0));
            result.add(points.get(end - 1));
        }
        return result;
    }

    /**
     * 计算点到直线的距离
     *
     * @param p
     * @param s
     * @param e
     * @return
     */
    public static double H(DPoint p, DPoint s, DPoint e) {
        double AB = distance(s, e);
        double CB = distance(p, s);
        double CA = distance(p, e);

        double S = helen(CB, CA, AB);
        double H = 2 * S / AB;

        return H;
    }


    public static double distance(DPoint p1, DPoint p2) {
        double x1 = p1.x;
        double y1 = p1.y;

        double x2 = p2.x;
        double y2 = p2.y;

        double xy = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
        return xy;
    }

    /**
     * 海伦公式，已知三边求三角形面积
     *
     * @return 面积
     */
    public static double helen(double CB, double CA, double AB) {
        double p = (CB + CA + AB) / 2;
        double S = Math.sqrt(p * (p - CB) * (p - CA) * (p - AB));
        return S;
    }

    public static class DPoint {
        double x;
        double y;
        private int index;

        public DPoint(double x, double y, int index) {
            this.x = x;
            this.y = y;
            this.index = index;
        }

        public double getX() {
            return x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public double getY() {
            return y;
        }

        public void setY(double y) {
            this.y = y;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }
    }

}


class LPoint implements Comparable<LPoint> {

    private int id;
    public double lat;
    public double lon;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    @Override
    public int compareTo(@NonNull LPoint other) {
        if (this.id < other.id) return -1;
        else if (this.id > other.id) return 1;
        else
            return 0;
    }
}