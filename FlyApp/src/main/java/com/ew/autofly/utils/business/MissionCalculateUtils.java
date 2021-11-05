package com.ew.autofly.utils.business;

import com.ew.autofly.entity.LocationCoordinate;
import com.ew.autofly.entity.Tower;
import com.ew.autofly.entity.WayPointInfo;
import com.ew.autofly.utils.coordinate.LocationCoordinateUtils;
import com.ew.autofly.xflyer.utils.LatLngUtils;

import java.util.List;

import dji.common.mission.waypoint.Waypoint;
import dji.common.mission.waypoint.WaypointAction;


public class MissionCalculateUtils {

    /**
     * 根据杆塔坐标计算线路长度
     *
     * @param towerList 杆塔集（gps84坐标）
     * @return
     */
    public static double calculateDistanceByTower(List<Tower> towerList) {
        double dis = 0.0D;
        for (int i = 0, size = towerList.size(); i < size - 1; i++) {
            Tower gps1 = towerList.get(i);
            Tower gps2 = towerList.get(i + 1);
            dis += LocationCoordinateUtils.getDistance(gps1.getLatitude(), gps1.getLongitude(),
                    gps2.getLatitude(), gps2.getLongitude());
        }
        return dis;
    }


    /**
     * 根据gps84坐标计算线路长度
     *
     * @param wayPointTaskList gps84坐标
     * @return
     */
    public static double calculateDistanceByWayPointInfo(List<WayPointInfo> wayPointTaskList) {
        double dis = 0.0D;
        if (wayPointTaskList != null && wayPointTaskList.size() >= 2) {
            int size = wayPointTaskList.size();
            for (int i = 0; i < size - 1; i++) {
                LocationCoordinate gps1 = wayPointTaskList.get(i).getPosition();
                LocationCoordinate gps2 = wayPointTaskList.get(i + 1).getPosition();
                dis += LocationCoordinateUtils.getDistance(gps1.latitude, gps1.longitude, gps2.latitude, gps2.longitude);
            }
        }
        return dis;
    }

    /**
     * 根据gps84坐标计算线路长度
     *
     * @param gps84List gps84坐标
     * @return
     */
    public static double calculateDistanceByCoordinate(List<LocationCoordinate> gps84List) {
        double dis = 0.0D;
        for (int i = 0, size = gps84List.size(); i < size - 1; i++) {
            LocationCoordinate gps1 = gps84List.get(i);
            LocationCoordinate gps2 = gps84List.get(i + 1);
            dis += LocationCoordinateUtils.getDistance(gps1.latitude, gps1.longitude, gps2.latitude, gps2.longitude);
        }
        return dis;
    }


    /**
     * 根据dji waypoint坐标计算线路时间
     *
     * @param waypointList dji waypoint
     * @param speed        当waypoint.speed没有设置时候的给定的统一速度
     * @return 分钟
     */
    public static double calculateTimeByWayPoint(List<Waypoint> waypointList, double speed) {

        double time = 0.0D;
        if (waypointList == null || waypointList.size() < 2) {
            return 0;
        }


        Waypoint firstPoint = waypointList.get(0);
        double altitude = firstPoint.altitude;
        time = altitude * 2 / 2D;

        Waypoint secondPoint = null;

        int size = waypointList.size();
        Waypoint temp = firstPoint;
        for (int i = 1; i < size; i++) {
            Waypoint gps = waypointList.get(i);
            if (gps.coordinate.getLatitude() != firstPoint.coordinate.getLatitude()
                    && gps.coordinate.getLongitude() != firstPoint.coordinate.getLongitude()) {
                secondPoint = gps;
            }
            double dis = LocationCoordinateUtils.getDistance(temp.coordinate.getLatitude(), temp.coordinate.getLongitude(),
                    gps.coordinate.getLatitude(), gps.coordinate.getLongitude());
            time = time + (dis / (temp.speed == 0 ? speed : temp.speed));
            temp = gps;
        }

        if (secondPoint != null) {

            double goToDis = LocationCoordinateUtils.getDistance(firstPoint.coordinate.getLatitude(), firstPoint.coordinate.getLongitude(),
                    secondPoint.coordinate.getLatitude(), secondPoint.coordinate.getLongitude());
            time = time + (goToDis / (firstPoint.speed == 0 ? speed : firstPoint.speed));
        }


        Waypoint lastPoint = waypointList.get(waypointList.size() - 1);

        double goBackDis = LocationCoordinateUtils.getDistance(firstPoint.coordinate.getLatitude(), firstPoint.coordinate.getLongitude(),
                lastPoint.coordinate.getLatitude(), lastPoint.coordinate.getLongitude());
        time = time + (goBackDis / (lastPoint.speed == 0 ? speed : lastPoint.speed));

        return time / 60;
    }

    /**
     * 计算所用航点执行动作的时间
     * @param waypointList
     * @return 分钟
     */
    public static double calculateWaypointActionTime(List<Waypoint> waypointList){
        int actionCount = 0;
        for (Waypoint waypoint : waypointList) {
            List<WaypointAction> actionList = waypoint.waypointActions;
            if (actionList != null) {
                actionCount += actionList.size();
            }
        }

        return (actionCount * 3.0d)/ 60.0d;
    }

}
