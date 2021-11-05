package com.ew.autofly.utils.business;

import com.ew.autofly.entity.LocationCoordinate;
import com.ew.autofly.entity.Tower;
import com.ew.autofly.utils.coordinate.LocationCoordinateUtils;

import java.util.ArrayList;
import java.util.List;


public class AirRouteUtils {

    /**
     * 获取起点坐标
     *
     * @param latitude
     * @param longitude
     * @param width
     * @param height
     * @param angle
     * @param angleB
     * @return
     */
    public static LocationCoordinate getStartPoint(double latitude, double longitude, double width, double height, double angle, double angleB) {
        LocationCoordinate pointB = LocationCoordinateUtils.getPositionClockwiseToNorth(latitude, longitude, width, angleB);
        LocationCoordinate pointC = LocationCoordinateUtils.getPositionClockwiseToNorth(latitude, longitude, height, angle);

        return new LocationCoordinate(pointB.latitude + pointC.latitude - latitude, pointB.longitude + pointC.longitude - longitude);
    }


    /**
     * 根据gps84坐标计算杆塔线路拐点坐标
     *
     * @param towerList
     * @param distance  等距线与杆塔连线的间距的1/2，为正则在杆塔连线的左侧；为负则在杆塔连线的右侧
     * @return 返回拐点 gps84坐标
     */
    public static List<LocationCoordinate> getTurnCoordinateList(List<Tower> towerList, double distance) {

        List<LocationCoordinate> llList = new ArrayList<>();

        int size = towerList.size();

        if (size < 3) {
            return llList;
        }

        for (int i = 0; i < size - 2; i++) {
            LocationCoordinate pA = new LocationCoordinate(towerList.get(i).getLatitude(), towerList.get(i).getLongitude());
            LocationCoordinate pB = new LocationCoordinate(towerList.get(i + 1).getLatitude(), towerList.get(i + 1).getLongitude());
            LocationCoordinate pC = new LocationCoordinate(towerList.get(i + 2).getLatitude(), towerList.get(i + 2).getLongitude());

            double routeAngle1 = LocationCoordinateUtils.getAngleClockwiseToNorth(pB.latitude, pB.longitude, pA.latitude, pA.longitude);
            double routeAngle2 = LocationCoordinateUtils.getAngleClockwiseToNorth(pC.latitude, pC.longitude, pB.latitude, pB.longitude);

            double turnAngle = (180 - Math.abs(routeAngle2 - routeAngle1)) / 2D;

            double dis = distance / Math.sin(turnAngle * 3.141592653589793D / 180.0D);

            double angle;

            if (routeAngle1 > routeAngle2) {
                angle = routeAngle1 + turnAngle;
            } else {
                angle = routeAngle2 + turnAngle;
            }

            LocationCoordinate turnPoint = LocationCoordinateUtils.getPositionClockwiseToNorth(pB.latitude, pB.longitude, dis, angle);
            turnPoint.setAltitude(towerList.get(i + 1).getFlyAltitude());
            llList.add(turnPoint);
        }

        return llList;
    }
}
