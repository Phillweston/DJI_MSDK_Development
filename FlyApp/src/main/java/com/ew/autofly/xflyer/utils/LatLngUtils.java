package com.ew.autofly.xflyer.utils;

import com.ew.autofly.entity.LatLngInfo;

@Deprecated
public class LatLngUtils {
    static double Rc = 6378137.0D;
    static double Rj = 6356725.0D;

    public static double getDistance(LatLngInfo aLatLng,LatLngInfo bLatLng){
        return getDistance(aLatLng.latitude ,aLatLng.longitude,bLatLng.latitude,bLatLng.longitude);
    }

    public static double getDistance(double aLatitude, double aLongitude,
                                     double bLatitude, double bLongitude) {
        double x = (bLongitude - aLongitude)
                * 3.141592653589793D
                * Rc
                * Math.cos((aLatitude + bLatitude) / 2.0D * 3.141592653589793D / 180.0D)
                / 180.0D;

        double y = (bLatitude - aLatitude) * 3.141592653589793D * Rc / 180.0D;
        double distance = Math.hypot(x, y);
        return distance;
    }

    public static LatLngInfo getLatlngByDA(LatLngInfo latLngInfo,
                                           double distance, double angle) {

        return getLatlngByDA(latLngInfo.latitude, latLngInfo.longitude, distance, angle);
    }

    /**
     *
     * @param latitude
     * @param longitude
     * @param distance
     * @param angle 与正北方向的夹角（0-360）
     * @return
     */
    public static LatLngInfo getLatlngByDA(double latitude, double longitude,
                                           double distance, double angle) {
        double dx = distance * Math.sin(angle * 3.141592653589793D / 180.0D);
        double dy = distance * Math.cos(angle * 3.141592653589793D / 180.0D);

        double BJD = (dx / getEd(latitude) + getRadLongitude(longitude)) * 180.0D / 3.141592653589793D;
        double BWD = (dy / getEc(latitude) + getRadLatitude(latitude)) * 180.0D / 3.141592653589793D;
        LatLngInfo B = new LatLngInfo(BWD, BJD);
        return B;
    }

    /**
     * 获取与正北方向夹角（顺时针）*A到B方向
     *
     * @param aLatLng
     * @param bLatLng
     * @return 返回0-360度
     */
    public static double getABAngle(LatLngInfo aLatLng,LatLngInfo bLatLng){
        return getABAngle(bLatLng.latitude ,bLatLng.longitude,aLatLng.latitude,aLatLng.longitude);
    }

    /**
     * 获取与正北方向夹角（顺时针）*A到B方向
     *
     * @param bLatitude
     * @param bLongitude
     * @param aLatitude
     * @param aLongitude
     * @return 返回0-360度
     */
    public static double getABAngle(double bLatitude, double bLongitude,
                                    double aLatitude, double aLongitude) {
        double dx = (getRadLongitude(bLongitude) - getRadLongitude(aLongitude))
                * getEd(aLatitude);
        double dy = (getRadLatitude(bLatitude) - getRadLatitude(aLatitude))
                * getEc(aLatitude);

        double angle = Math.atan(Math.abs(dx / dy)) * 180.0D / 3.141592653589793D;

        double dLo = bLongitude - aLongitude;
        double dLa = bLatitude - aLatitude;
        if ((dLo > 0.0D) && (dLa <= 0.0D)) {
            angle = 90.0D - angle + 90.0D;
        } else if ((dLo <= 0.0D) && (dLa < 0.0D))
            angle += 180.0D;
        else if ((dLo < 0.0D) && (dLa >= 0.0D)) {
            angle = 90.0D - angle + 270.0D;
        }
        return angle;
    }

    private static double getRadLongitude(double longitude) {
        return longitude * 3.141592653589793D / 180.0D;
    }

    private static double getRadLatitude(double latitude) {
        return latitude * 3.141592653589793D / 180.0D;
    }

    private static double getEc(double latitude) {
        return Rj + (Rc - Rj) * (90.0D - latitude) / 90.0D;
    }

    private static double getEd(double latitude) {
        return getEc(latitude) * Math.cos(getRadLatitude(latitude));
    }

    public static LatLngInfo GetBPoint(double aLatitude, double aLogitude,
                                       double cLatitude, double cLogitude, double angle) {
        double oldAngle = getABAngle(aLatitude, aLogitude, cLatitude, cLogitude);
        double newAngle = angle + oldAngle;
        if (newAngle > 360.0D) {
            newAngle -= 360.0D;
        }
        double distance = getDistance(aLatitude, aLogitude, cLatitude,
                cLogitude);

        return getLatlngByDA(cLatitude, cLogitude, distance, newAngle);
    }

    public static LatLngInfo getStartPoint(double latitude, double longitude, double width, double height, double angle, double angleB) {
        LatLngInfo pointB = getLatlngByDA(latitude, longitude, width, angleB);
        LatLngInfo pointC = getLatlngByDA(latitude, longitude, height, angle);

        return new LatLngInfo(pointB.latitude + pointC.latitude - latitude, pointB.longitude + pointC.longitude - longitude);
    }
}