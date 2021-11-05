package com.ew.autofly.utils;

import com.esri.android.map.MapView;
import com.esri.core.geometry.Point;
import com.ew.autofly.constant.AppConstant;
import com.ew.autofly.entity.AirRouteParameter;
import com.ew.autofly.entity.LatLngInfo;
import com.ew.autofly.entity.LocationCoordinate;
import com.ew.autofly.entity.Tower;
import com.ew.autofly.xflyer.utils.ArcgisPointUtils;
import com.ew.autofly.xflyer.utils.CoordinateUtils;
import com.ew.autofly.xflyer.utils.LatLngUtils;

import java.util.ArrayList;
import java.util.List;



public class MyUtils {
    /**
     * 根据gps84坐标计算杆塔线路拐点坐标
     *
     * @param towerList
     * @param distance  等距线与杆塔连线的间距的1/2，为正则在杆塔连线的左侧；为负则在杆塔连线的右侧
     * @return 返回拐点 gps84坐标
     */
    public static List<LatLngInfo> getTurnCoordListByGps84(List<Tower> towerList, double distance) {

        List<LatLngInfo> llList = new ArrayList<>();

        int size = towerList.size();

        if (size < 3) {
            return llList;
        }

        for (int i = 0; i < size - 2; i++) {
            LatLngInfo pA = new LatLngInfo(towerList.get(i).getLatitude(), towerList.get(i).getLongitude());
            LatLngInfo pB = new LatLngInfo(towerList.get(i + 1).getLatitude(), towerList.get(i + 1).getLongitude());
            LatLngInfo pC = new LatLngInfo(towerList.get(i + 2).getLatitude(), towerList.get(i + 2).getLongitude());

            double routeAngle1 = LatLngUtils.getABAngle(pA.latitude, pA.longitude, pB.latitude, pB.longitude);
            double routeAngle2 = LatLngUtils.getABAngle(pB.latitude, pB.longitude, pC.latitude, pC.longitude);

            double turnAngle = (180 - Math.abs(routeAngle2 - routeAngle1)) / 2D;

            double dis = distance / Math.sin(turnAngle * 3.141592653589793D / 180.0D);

            double angle;

            if (routeAngle1 > routeAngle2) {
                angle = routeAngle1 + turnAngle;
            } else {
                angle = routeAngle2 + turnAngle;
            }

            LatLngInfo turnPoint = LatLngUtils.getLatlngByDA(pB.latitude, pB.longitude, dis, angle);
            llList.add(turnPoint);
        }

        return llList;
    }

    /**
     * 此方法计算航线飞行最大时间（待测试）
     *
     * @param homeGps84         home点坐标
     * @param pointGps84        航点集合
     * @param airRouteParameter
     * @param returnMode        0:直线返航   1:原路返回
     * @return
     */
    public static double calculateFlyTime(LatLngInfo homeGps84, List<LatLngInfo> pointGps84,
                                          AirRouteParameter airRouteParameter, int returnMode) {
        return calculateFlyTime(homeGps84, pointGps84, airRouteParameter, returnMode, false);
    }

    /**
     * 此方法计算航线飞行最大时间（待测试）
     *
     * @param homeGps84   home点坐标
     * @param pointGps84  航点集合
     * @param speed       速度
     * @param entryHeight 起飞高度
     * @param returnMode  0:直线返航   1:原路返回
     * @return
     */
    public static double calculateFlyTime(LatLngInfo homeGps84, List<LatLngInfo> pointGps84,
                                          double speed, int entryHeight, int returnMode) {
        double flyTime = 0.0D;
        int pointSize = pointGps84.size();
        if (pointSize < 1)
            return flyTime;

        flyTime = entryHeight * 2 / 2.9D;

        if (homeGps84 != null) {

            double distance1 = LatLngUtils.getDistance(homeGps84.latitude, homeGps84.longitude, pointGps84.get(0).latitude, pointGps84.get(0).longitude);

            double distance2 = LatLngUtils.getDistance(homeGps84.latitude, homeGps84.longitude, pointGps84.get(pointSize - 1).latitude, pointGps84.get(pointSize - 1).longitude);
            flyTime += (distance1 + distance2) / AppConstant.MAX_FLY_SPEED;
        }
        double dis = 0.0D;
        for (int i = 0; i < pointSize - 1; i++) {
            if (returnMode == 1) {
                dis += LatLngUtils.getDistance(pointGps84.get(i).latitude, pointGps84.get(i).longitude, pointGps84.get(i + 1).latitude, pointGps84.get(i + 1).longitude);
                flyTime += 10;
            }
            dis += LatLngUtils.getDistance(pointGps84.get(i).latitude, pointGps84.get(i).longitude, pointGps84.get(i + 1).latitude, pointGps84.get(i + 1).longitude);
            flyTime += 10;
        }
        flyTime += dis / speed;
        return flyTime / 60.0D;
    }


    /**
     * 此方法计算航线飞行最大时间（待测试）
     *
     * @param homeGps84         home点坐标
     * @param pointGps84        航点集合
     * @param airRouteParameter
     * @param returnMode        0:直线返航   1:原路返回
     * @param isVideo           是否视频模式
     * @return
     */
    public static double calculateFlyTime(LatLngInfo homeGps84, List<LatLngInfo> pointGps84,
                                          AirRouteParameter airRouteParameter, int returnMode, boolean isVideo) {
        double flyTime = 0.0D;
        int pointSize = pointGps84.size();
        if (pointSize < 1)
            return flyTime;
        double speed;
        if (isVideo) {
            speed = airRouteParameter.getFlySpeed();
        } else {
            speed = airRouteParameter.getActualSpeed();
        }







        flyTime = airRouteParameter.getAltitude() > airRouteParameter.getEntryHeight() ? airRouteParameter.getAltitude() : airRouteParameter.getEntryHeight() * 2 / 2.9D;


        if (homeGps84 != null) {

            double distance1 = LatLngUtils.getDistance(homeGps84.latitude, homeGps84.longitude, pointGps84.get(0).latitude, pointGps84.get(0).longitude);

            double distance2 = LatLngUtils.getDistance(homeGps84.latitude, homeGps84.longitude, pointGps84.get(pointSize - 1).latitude, pointGps84.get(pointSize - 1).longitude);
            flyTime += (distance1 + distance2) / AppConstant.MAX_FLY_SPEED;
        }
        double dis = 0.0D;
        for (int i = 0; i < pointSize - 1; i++) {
            if (returnMode == 1) {
                dis += LatLngUtils.getDistance(pointGps84.get(i).latitude, pointGps84.get(i).longitude, pointGps84.get(i + 1).latitude, pointGps84.get(i + 1).longitude);
                flyTime += 10;
            }
            dis += LatLngUtils.getDistance(pointGps84.get(i).latitude, pointGps84.get(i).longitude, pointGps84.get(i + 1).latitude, pointGps84.get(i + 1).longitude);
            flyTime += 10;
        }
        flyTime += dis / speed;
        return flyTime / 60.0D;
    }


    /**
     * 计算全景时间
     *
     * @param homePoint
     * @param merList   mer坐标
     * @param altitude
     * @return
     */
    public static double calculatePanoramaFlyTime(LatLngInfo homePoint, List<LatLngInfo> merList, int altitude) {
        if ((merList == null) || (merList.size() == 0))
            return 0.0D;
        int i = 0;
        int speed = 14;
        double dFlyTime = altitude * 2 / 3;
        LatLngInfo p1;
        LatLngInfo p2 = null;
        for (LatLngInfo gcjLatLngInfo : merList) {
            LatLngInfo gp84LatLngInfo = CoordinateUtils.mercatorToLonLat(gcjLatLngInfo.longitude, gcjLatLngInfo.latitude);
            if (i == 0) {
                p2 = gp84LatLngInfo;
                if (homePoint != null) {
                    double gotoTaskDistance = LatLngUtils.getDistance(homePoint.latitude, homePoint.longitude, gp84LatLngInfo.latitude, gp84LatLngInfo.longitude);
                    dFlyTime += gotoTaskDistance / speed;
                }
            } else {
                p1 = p2;
                p2 = gp84LatLngInfo;
                double distance = LatLngUtils.getDistance(p1.latitude, p1.longitude, p2.latitude, p2.longitude);
                dFlyTime += distance / speed;
            }
            i++;
        }
        if ((p2 != null) && (homePoint != null)) {
            double returnFirst = LatLngUtils.getDistance(p2.latitude, p2.longitude, homePoint.latitude, homePoint.longitude);
            dFlyTime += returnFirst / speed;
        }
        return (dFlyTime + merList.size() * 240) / 60.0D;
    }

    /**
     * 预计线状调查的飞行时长
     *
     * @param homeGps84
     * @param pointList
     * @param param
     * @param returnMode
     * @return
     */
    public static double calculateLinePatrolFlyTime(LatLngInfo homeGps84, List<Point> pointList, AirRouteParameter param, int returnMode) {
        double flyTime = 0.0D;
        if (null != homeGps84) {
            int size = pointList.size();
            double speed = param.getFlySpeed();
            flyTime = param.getAltitude() > param.getEntryHeight() ? param.getAltitude() : param.getEntryHeight() * 2 / 2.9D;
            double dis = calculateLinePatrolDistance(pointList);
            LatLngInfo firstGps = CoordinateUtils.mapMercatorToGps84(pointList.get(0).getX(), pointList.get(0).getY());
            LatLngInfo lastGps = CoordinateUtils.mapMercatorToGps84(pointList.get(size - 1).getX(), pointList.get(size - 1).getY());
            if (returnMode == AppConstant.RETURN_MODE_ORIGIN) {
                dis = dis * 2;
                dis = dis + (2 * LatLngUtils.getDistance(homeGps84.latitude, homeGps84.longitude, firstGps.latitude, firstGps.longitude));
            } else if (returnMode == AppConstant.RETURN_MODE_STRAIGHT) {
                dis += LatLngUtils.getDistance(homeGps84.latitude, homeGps84.longitude, firstGps.latitude, firstGps.longitude);
                dis += LatLngUtils.getDistance(homeGps84.latitude, homeGps84.longitude, lastGps.latitude, lastGps.longitude);
            }
            flyTime += (dis / (speed * 5 / 6));
        }
        return (flyTime) / 60.0D;
    }

    /**
     * @param pointMerList
     * @return
     */
    public static double calculateLinePatrolDistance(List<Point> pointMerList) {
        double dis = 0.0D;
        for (int i = 0, size = pointMerList.size(); i < size - 1; i++) {
            LatLngInfo gcj1 = CoordinateUtils.mercatorToLonLat(pointMerList.get(i).getX(), pointMerList.get(i).getY());
            LatLngInfo gps1 = CoordinateUtils.gcj_To_Gps84(gcj1.latitude, gcj1.longitude);
            LatLngInfo gcj2 = CoordinateUtils.mercatorToLonLat(pointMerList.get(i + 1).getX(), pointMerList.get(i + 1).getY());
            LatLngInfo gps2 = CoordinateUtils.gcj_To_Gps84(gcj2.latitude, gcj2.longitude);
            dis += LatLngUtils.getDistance(gps1.latitude, gps1.longitude, gps2.latitude, gps2.longitude);
        }
        return dis;
    }

    /**
     * 根据gps84坐标计算线路长度
     *
     * @param gps84List gps84坐标
     * @return
     */
    public static double calculateDistance(List<LatLngInfo> gps84List) {
        double dis = 0.0D;
        for (int i = 0, size = gps84List.size(); i < size - 1; i++) {
            LatLngInfo gps1 = gps84List.get(i);
            LatLngInfo gps2 = gps84List.get(i + 1);
            dis += LatLngUtils.getDistance(gps1.latitude, gps1.longitude, gps2.latitude, gps2.longitude);
        }
        return dis;
    }


    /**
     * 获取面积
     *
     * @param latLngMerLists 墨卡托多边形点
     * @return
     */
    public static double getAreaFormMerList(MapView mapView, ArrayList<LatLngInfo> latLngMerLists) {
        if (latLngMerLists.size() < 2)
            return 0;
        Point[] psArea = new Point[latLngMerLists.size()];
        int i = 0;
        for (LatLngInfo lngInfo : latLngMerLists) {
            psArea[i] = mapView.toScreenPoint(new Point(lngInfo.longitude, lngInfo.latitude));
            i++;
        }
        LatLngInfo lt = CoordinateUtils.mercatorToLonLat(latLngMerLists.get(0).longitude, latLngMerLists.get(0).latitude);
        LatLngInfo rt = LatLngUtils.getLatlngByDA(lt.latitude, lt.longitude, 200, 90);
        LatLngInfo rtMer = CoordinateUtils.lonLatToMercator(rt.longitude, rt.latitude);

        Point p1 = mapView.toScreenPoint(new Point(latLngMerLists.get(0).longitude, latLngMerLists.get(0).latitude));
        Point p2 = mapView.toScreenPoint(new Point(rtMer.longitude, rtMer.latitude));
        double diffPoint = p1.getX() - p2.getX();

        double res = 200 / diffPoint;

        double area = Math.abs(ArcgisPointUtils.calculatePolygonArea(res, psArea));
        return area;
    }

    /**
     * 查找离给定位置最近的杆塔
     *
     * @param location
     * @return
     */
    public static int findNearestTowerPositionByLocation(List<Tower> towerList, LocationCoordinate location) {
        int position = 0;
        double minDistance = 0;
        int i = 0;
        for (Tower tower : towerList) {
            double distance = LatLngUtils.getDistance(tower.getLatitude(), tower.getLongitude(),
                    location.getLatitude(), location.getLongitude());
            if (i == 0) {
                minDistance = distance;
            } else {
                if (distance < minDistance) {
                    minDistance = distance;
                    position = i;
                }
            }
            i++;
        }
        return position;
    }

    /**
     * 获取曝光补偿数值
     *
     * @param value
     * @return
     */
    public static String getExposureValue(int value) {
        String result = "Unknown";
        switch (value) {
            case 1:
                result = "-5.0eV";
                break;
            case 2:
                result = "-4.7eV";
                break;
            case 3:
                result = "-4.3eV";
                break;
            case 4:
                result = "-4.0eV";
                break;
            case 5:
                result = "-3.7eV";
                break;
            case 6:
                result = "-3.3eV";
                break;
            case 7:
                result = "-3.0eV";
                break;
            case 8:
                result = "-2.7eV";
                break;
            case 9:
                result = "-2.3eV";
                break;
            case 10:
                result = "-2.0eV";
                break;
            case 11:
                result = "-1.7eV";
                break;
            case 12:
                result = "-1.3eV";
                break;
            case 13:
                result = "-1.0eV";
                break;
            case 14:
                result = "-0.7eV";
                break;
            case 15:
                result = "-0.3eV";
                break;
            case 16:
                result = "0.0eV";
                break;
            case 17:
                result = "+0.3eV";
                break;
            case 18:
                result = "+0.7eV";
                break;
            case 19:
                result = "+1.0eV";
                break;
            case 20:
                result = "+1.3eV";
                break;
            case 21:
                result = "+1.7eV";
                break;
            case 22:
                result = "+2.0eV";
                break;
            case 23:
                result = "+2.3eV";
                break;
            case 24:
                result = "+2.7eV";
                break;
            case 25:
                result = "+3.0eV";
                break;
            case 26:
                result = "+3.3eV";
                break;
            case 27:
                result = "+3.7eV";
                break;
            case 28:
                result = "+4.0eV";
                break;
            case 29:
                result = "+4.3eV";
                break;
            case 30:
                result = "+4.7eV";
                break;
            case 31:
                result = "+5.0eV";
                break;
            case 255:
                result = "Unknown";
                break;
        }
        return result;
    }

}