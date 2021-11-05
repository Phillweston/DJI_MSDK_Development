package com.ew.autofly.utils.coordinate;

import com.ew.autofly.entity.LocationCoordinate;
import com.ew.autofly.entity.LocationMercator;
import com.flycloud.autofly.map.MapCoordinateSystem;
import com.flycloud.autofly.map.MapRegion;
import com.flycloud.autofly.map.MapServiceProvider;
import com.flycloud.autofly.map.util.MapUtils;


public class LocationCoordinateUtils {

    
    public static MapCoordinateSystem MAP_COORDINATE_SYSTEM = MapCoordinateSystem.GCJ02;

    public static double a = 6378245.0D;
    public static double ee = 0.006693421622965943D;
    static double Rc = 6378137.0D;
    static double Rj = 6356725.0D;

    /**
     * 转换坐标系统
     *
     * @param mapServiceProvider
     */
    public static void transformMapCoordinateType(MapServiceProvider mapServiceProvider, MapRegion region) {
        MAP_COORDINATE_SYSTEM = MapUtils.getMapCoordinateSystem(mapServiceProvider, region);
    }

    /**
     * 从Gps84经纬度转墨卡托到地图
     *
     * @param gps84
     * @return
     */
    public static LocationMercator gps84ToMapMercator(LocationCoordinate gps84) {
        return gps84ToMapMercator(gps84.longitude, gps84.latitude);
    }


    public static LocationMercator gps84ToMapMercator(double lon, double lat) {
        LocationCoordinate latLngInfo;
        switch (MAP_COORDINATE_SYSTEM) {
            case GCJ02:

                latLngInfo = LocationCoordinateUtils.gps84_To_Gcj02(lat, lon);
                return lonLatToMercator(latLngInfo.longitude, latLngInfo.latitude);
            case BD09:


                latLngInfo = LocationCoordinateUtils.gps84_To_Gcj02(lat, lon);
                LocationCoordinate coordinate = LocationCoordinateUtils.gcj02_To_Bd09(latLngInfo.latitude, latLngInfo.longitude);
                return lonLatToMercator(coordinate.longitude, coordinate.latitude);

            default:
                return lonLatToMercator(lon, lat);
        }
    }

    public static LocationCoordinate mapMercatorToGps84(LocationMercator mercator) {
        return mapMercatorToGps84(mercator.getX(), mercator.getY());
    }


    public static LocationCoordinate mapMercatorToGps84(double x, double y) {

        LocationCoordinate latLngInfo = mercatorToLonLat(x, y);
        switch (MAP_COORDINATE_SYSTEM) {
            case GCJ02:

                return gcj_To_Gps84(latLngInfo.latitude, latLngInfo.longitude);
            case BD09:
                return bd09_To_Gps84(latLngInfo.longitude, latLngInfo.latitude);
            default:

                return latLngInfo;
        }
    }

    public static LocationCoordinate gps84_To_Gcj02(LocationCoordinate gps) {
        return gps84_To_Gcj02(gps.getLatitude(), gps.getLongitude());
    }

    public static LocationCoordinate gps84_To_Gcj02(double lat, double lon) {
        if (outOfChina(lat, lon)) {
            return new LocationCoordinate(lat, lon);
        }
        double dLat = transformLat(lon - 105.0D, lat - 35.0D);
        double dLon = transformLon(lon - 105.0D, lat - 35.0D);
        double radLat = lat / 180.0D * 3.141592653589793D;
        double magic = Math.sin(radLat);
        magic = 1.0D - ee * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = dLat * 180.0D
                / (a * (1.0D - ee) / (magic * sqrtMagic) * 3.141592653589793D);
        dLon = dLon * 180.0D
                / (a / sqrtMagic * Math.cos(radLat) * 3.141592653589793D);
        double mgLat = lat + dLat;
        double mgLon = lon + dLon;
        return new LocationCoordinate(mgLat, mgLon);
    }

    public static LocationCoordinate gcj_To_Gps84(double lat, double lon) {
        LocationCoordinate gps = transform(lat, lon);
        double lontitude = lon * 2.0D - gps.longitude;
        double latitude = lat * 2.0D - gps.latitude;
        return new LocationCoordinate(latitude, lontitude);
    }

    public static LocationCoordinate gcj02_To_Bd09(double gg_lat, double gg_lon) {
        double x = gg_lon;
        double y = gg_lat;
        double z = Math.sqrt(x * x + y * y) + 2.E-005D
                * Math.sin(y * 3.141592653589793D);
        double theta = Math.atan2(y, x) + 3.E-006D
                * Math.cos(x * 3.141592653589793D);
        double bd_lon = z * Math.cos(theta) + 0.0065D;
        double bd_lat = z * Math.sin(theta) + 0.006D;
        return new LocationCoordinate(bd_lat, bd_lon);
    }

    public static LocationCoordinate bd09_To_Gcj02(double bd_lat, double bd_lon) {
        double x = bd_lon - 0.0065D;
        double y = bd_lat - 0.006D;
        double z = Math.sqrt(x * x + y * y) - 2.E-005D
                * Math.sin(y * 3.141592653589793D);
        double theta = Math.atan2(y, x) - 3.E-006D
                * Math.cos(x * 3.141592653589793D);
        double gg_lon = z * Math.cos(theta);
        double gg_lat = z * Math.sin(theta);
        return new LocationCoordinate(gg_lat, gg_lon);
    }

    public static LocationCoordinate bd09_To_Gps84(double bd_lat, double bd_lon) {
        LocationCoordinate gcj02 = bd09_To_Gcj02(bd_lat, bd_lon);
        LocationCoordinate map84 = gcj_To_Gps84(gcj02.latitude, gcj02.longitude);

        return map84;
    }

    public static LocationCoordinate Gps84_To_bd09(double lat, double lon) {
        LocationCoordinate gcj02 = gps84_To_Gcj02(lat, lon);
        LocationCoordinate map84 = gcj02_To_Bd09(gcj02.latitude, gcj02.longitude);

        return map84;
    }

    public static boolean outOfChina(double lat, double lon) {
        if ((lon < 72.004000000000005D) || (lon > 137.8347D))
            return true;
        if ((lat < 0.8293D) || (lat > 55.827100000000002D))
            return true;
        return false;
    }

    public static LocationCoordinate transform(double lat, double lon) {
        if (outOfChina(lat, lon)) {
            return new LocationCoordinate(lat, lon);
        }
        double dLat = transformLat(lon - 105.0D, lat - 35.0D);
        double dLon = transformLon(lon - 105.0D, lat - 35.0D);
        double radLat = lat / 180.0D * 3.141592653589793D;
        double magic = Math.sin(radLat);
        magic = 1.0D - ee * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = dLat * 180.0D
                / (a * (1.0D - ee) / (magic * sqrtMagic) * 3.141592653589793D);
        dLon = dLon * 180.0D
                / (a / sqrtMagic * Math.cos(radLat) * 3.141592653589793D);
        double mgLat = lat + dLat;
        double mgLon = lon + dLon;
        return new LocationCoordinate(mgLat, mgLon);
    }

    public static double transformLat(double x, double y) {
        double ret = -100.0D + 2.0D * x + 3.0D * y + 0.2D * y * y + 0.1D * x
                * y + 0.2D * Math.sqrt(Math.abs(x));

        ret += (20.0D * Math.sin(6.0D * x * 3.141592653589793D) + 20.0D * Math
                .sin(2.0D * x * 3.141592653589793D)) * 2.0D / 3.0D;
        ret += (20.0D * Math.sin(y * 3.141592653589793D) + 40.0D * Math
                .sin(y / 3.0D * 3.141592653589793D)) * 2.0D / 3.0D;
        ret += (160.0D * Math.sin(y / 12.0D * 3.141592653589793D) + 320.0D * Math
                .sin(y * 3.141592653589793D / 30.0D)) * 2.0D / 3.0D;
        return ret;
    }

    public static double transformLon(double x, double y) {
        double ret = 300.0D + x + 2.0D * y + 0.1D * x * x + 0.1D * x * y + 0.1D
                * Math.sqrt(Math.abs(x));

        ret += (20.0D * Math.sin(6.0D * x * 3.141592653589793D) + 20.0D * Math
                .sin(2.0D * x * 3.141592653589793D)) * 2.0D / 3.0D;
        ret += (20.0D * Math.sin(x * 3.141592653589793D) + 40.0D * Math
                .sin(x / 3.0D * 3.141592653589793D)) * 2.0D / 3.0D;
        ret += (150.0D * Math.sin(x / 12.0D * 3.141592653589793D) + 300.0D * Math
                .sin(x / 30.0D * 3.141592653589793D)) * 2.0D / 3.0D;

        return ret;
    }


    public static LocationCoordinate mercatorToLonLat(double x, double y) {
        double toX = x / 20037508.34D * 180.0D;
        double toY = y / 20037508.34D * 180.0D;
        toY = 57.295779513082323D * (2.0D * Math.atan(Math
                .exp(toY * 3.141592653589793D / 180.0D)) - 1.570796326794897D);
        return new LocationCoordinate(toY, toX);
    }


    public static LocationMercator lonLatToMercator(double lon, double lat) {
        double toX = lon * 20037508.34D / 180.0D;
        double toY = Math.log(Math
                .tan((90.0D + lat) * 3.141592653589793D / 360.0D)) / 0.0174532925199433D;
        toY = toY * 20037508.34D / 180.0D;

        return new LocationMercator(toX, toY);
    }

    public static String decimalToDMS(double coord) {
        double mod = coord % 1.0D;
        int intPart = (int) coord;

        String degrees = String.valueOf(intPart);
        coord = mod * 60.0D;
        mod = coord % 1.0D;
        intPart = (int) coord;
        if (intPart < 0) {
            intPart *= -1;
        }
        String minutes = String.valueOf(intPart);
        coord = mod * 60.0D;
        intPart = (int) coord;
        if (intPart < 0) {
            intPart *= -1;
        }
        String seconds = String.valueOf(intPart);
        String output = degrees + "/1," + minutes + "/1," + seconds + "/1";
        return output;
    }

    public static boolean checkGpsCoordinate(double latitude, double longitude) {
        return (latitude > -90.0D) && (latitude < 90.0D)
                && (longitude > -180.0D) && (longitude < 180.0D)
                && (latitude != 0.0D) && (longitude != 0.0D)
                && (!Double.isNaN(latitude)) && (!Double.isNaN(longitude))
                && (!Double.isInfinite(latitude)) && (!Double.isInfinite(longitude));
    }

    public static boolean checkGpsCoordinate(LocationCoordinate latLng) {
        return latLng != null && checkGpsCoordinate(latLng.latitude, latLng.longitude);
    }

    public static double getDistance(LocationCoordinate a, LocationCoordinate b) {
        return getDistance(a.latitude, a.longitude, b.latitude, b.longitude);
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

    /**
     * 获取与正北方向夹角（顺时针）*A到B方向
     *
     * @param aLatLng
     * @param bLatLng
     * @return 返回0-360度
     */
    public static double getAngleClockwiseToNorth(LocationCoordinate aLatLng, LocationCoordinate bLatLng) {
        return getAngleClockwiseToNorth(aLatLng.latitude, aLatLng.longitude, bLatLng.latitude, bLatLng.longitude);
    }

    /**
     * 获取与正北方向夹角（顺时针）*A到B方向
     *
     * @param aLatitude
     * @param aLongitude
     * @param bLatitude
     * @param bLongitude
     * @return 返回0-360度
     */
    public static double getAngleClockwiseToNorth(double aLatitude, double aLongitude,
                                                  double bLatitude, double bLongitude
    ) {
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

    /**
     * 获取与正北方向的夹角（顺时针）的坐标
     *
     * @param coordinate
     * @param distance
     * @param angle
     * @return
     */
    public static LocationCoordinate getPositionClockwiseToNorth(LocationCoordinate coordinate,
                                                                 double distance, double angle) {
        return getPositionClockwiseToNorth(coordinate.latitude, coordinate.longitude, distance, angle);
    }

    /**
     * 获取与正北方向的夹角（顺时针）的坐标
     *
     * @param latitude
     * @param longitude
     * @param distance  距离
     * @param angle     与正北方向的夹角（顺时针）
     * @return
     */
    public static LocationCoordinate getPositionClockwiseToNorth(double latitude, double longitude,
                                                                 double distance, double angle) {
        double dx = distance * Math.sin(angle * 3.141592653589793D / 180.0D);
        double dy = distance * Math.cos(angle * 3.141592653589793D / 180.0D);

        double BJD = (dx / getEd(latitude) + getRadLongitude(longitude)) * 180.0D / 3.141592653589793D;
        double BWD = (dy / getEc(latitude) + getRadLatitude(latitude)) * 180.0D / 3.141592653589793D;

        return new LocationCoordinate(BWD, BJD);
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
}