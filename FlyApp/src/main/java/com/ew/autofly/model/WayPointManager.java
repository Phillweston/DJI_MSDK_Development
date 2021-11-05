package com.ew.autofly.model;

import com.ew.autofly.constant.AppConstant;
import com.ew.autofly.entity.LocationCoordinate;
import com.ew.autofly.utils.coordinate.LocationCoordinateUtils;

import java.util.ArrayList;
import java.util.List;

import dji.common.mission.waypoint.Waypoint;
import dji.common.mission.waypoint.WaypointTurnMode;


public class WayPointManager {

  
    public static final float WAYPOINT_DISTANCE_TOLERANCE = 0.6f;

    /**
     * 添加空白过度点
     *
     * @param Lat
     * @param Lon
     * @param altitude
     * @return
     */
    public static Waypoint addBlankWaypoint(double Lat, double Lon, float altitude) {
        return addBlankWaypoint(false, Lat, Lon, altitude, 0.0f);
    }

    /**
     * 添加空白过度点
     *
     * @param Lat
     * @param Lon
     * @param altitude
     * @param headingAngle
     * @return 空白waypoint
     */
    public static Waypoint addBlankWaypoint(boolean hasHeading, double Lat, double Lon, float altitude, double headingAngle) {
        Waypoint waypoint = new Waypoint(Lat, Lon, altitude);
        if (hasHeading)
            waypoint.heading = Double.valueOf(headingAngle).shortValue();
        waypoint.actionTimeoutInSeconds = AppConstant.MAX_ACTION_TIMEOUT;
        waypoint.turnMode = WaypointTurnMode.CLOCKWISE;
        return waypoint;
    }


    public static LocationCoordinate getPointDistance2KM(LocationCoordinate currentPoint, LocationCoordinate nextPoint) {
        double distance = LocationCoordinateUtils.getDistance(currentPoint.latitude, currentPoint.longitude, nextPoint.latitude, nextPoint.longitude);
        if (distance > 2000) {
            double latAmong = (currentPoint.latitude + nextPoint.latitude) / 2;
            double lonAmong = (nextPoint.longitude + nextPoint.longitude) / 2;
            return new LocationCoordinate(latAmong, lonAmong);
        }
        return null;
    }


    public static boolean checkSuitableDistance(LocationCoordinate current, LocationCoordinate next) {
        double distance = LocationCoordinateUtils.getDistance(current.latitude, current.longitude, next.latitude, next.longitude);
        return (distance > WAYPOINT_DISTANCE_TOLERANCE || Math.abs(current.altitude - next.altitude) > WAYPOINT_DISTANCE_TOLERANCE);
    }

    public static boolean checkSuitableDistance(Waypoint current, Waypoint next) {
        double distance = LocationCoordinateUtils.getDistance(current.coordinate.getLatitude(), current.coordinate.getLongitude(), next.coordinate.getLatitude(), next.coordinate.getLongitude());
        return (distance > WAYPOINT_DISTANCE_TOLERANCE || Math.abs(current.altitude - next.altitude) > WAYPOINT_DISTANCE_TOLERANCE);
    }

    /**
     * 检查两个相同经纬度航点高差是否合适
     * @param current
     * @param next
     * @return
     */
    public static boolean checkSuitableAltitudeDifference(float current,float next){
        return Math.abs(current - next) > WAYPOINT_DISTANCE_TOLERANCE;
    }

    /**
     * 从上一个航点到下一个航点的指向
     *
     * @param thisLatitude
     * @param thisLongitude
     * @param toLatitude
     * @param toLongitude
     * @return 以正北方向顺时针（-180°~180°）
     */
    public static double getToNextPointDirectionAngle(double thisLatitude, double thisLongitude,
                                                      double toLatitude, double toLongitude) {
        double rotateAngle = LocationCoordinateUtils.getAngleClockwiseToNorth(
                thisLatitude, thisLongitude, toLatitude, toLongitude);
        double rotateRealAngle = rotateAngle % 360;
        rotateRealAngle = (rotateRealAngle >= 180 ? rotateRealAngle - 360 : rotateRealAngle);
        return rotateRealAngle;
    }

    
    public static void removeDuplicateWaypoint(List<Waypoint> waypointList) {
        if (waypointList != null && waypointList.size() > 1) {
            List<Waypoint> removeList = new ArrayList<>();
            Waypoint tempWaypoint = waypointList.get(0);
            for (int i = 1; i < waypointList.size(); i++) {
                Waypoint waypoint = waypointList.get(i);
               /* if (waypoint.coordinate.getLatitude() == tempWaypoint.coordinate.getLatitude() &&
                        waypoint.coordinate.getLongitude() == tempWaypoint.coordinate.getLongitude()
                        && waypoint.altitude == tempWaypoint.altitude) {
                    removeList.add(waypoint);
                }*/

                if (WayPointManager.checkSuitableDistance(waypoint,tempWaypoint)) {
                    tempWaypoint = waypoint;
                }else {
                    removeList.add(waypoint);
                }
            }
            if (!removeList.isEmpty()) {
                waypointList.removeAll(removeList);
            }
        }
    }
}
