package com.ew.autofly.utils.arcgis;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.ew.autofly.entity.LocationCoordinate;
import com.ew.autofly.entity.LocationMercator;
import com.ew.autofly.utils.coordinate.LocationCoordinateUtils;

import java.util.List;


public class ArcGisUtil {

    /**
     * 坐标转arcgis 墨卡托point
     *
     * @param locationCoordinate
     * @return
     */
    public static Point locationToMercatorPoint(LocationCoordinate locationCoordinate) {
        LocationMercator mercator = LocationCoordinateUtils.gps84ToMapMercator(locationCoordinate);
        return new Point(mercator.x, mercator.y, SpatialReferences.getWebMercator());
    }

    public static PointCollection locationListToMercatorPointCollection(List<LocationCoordinate> coordinates) {
        PointCollection collection = new PointCollection(SpatialReferences.getWebMercator());
        if (coordinates != null) {
            for (LocationCoordinate coordinate : coordinates) {
                Point point = locationToMercatorPoint(coordinate);
                collection.add(point);
            }
        }
        return collection;
    }

    /**
     * 坐标转arcgis gps84 point
     *
     * @param locationCoordinate
     * @return
     */
    public static Point locationToWgs84Point(LocationCoordinate locationCoordinate) {
        return new Point(locationCoordinate.longitude, locationCoordinate.latitude,
                locationCoordinate.altitude, SpatialReferences.getWgs84());
    }

    /**
     * 坐标集转arcgis 墨卡托PointCollection
     *
     * @param locationCoordinates
     * @return
     */
    public static PointCollection locationToMercatorPointCollection(List<LocationCoordinate> locationCoordinates) {
        PointCollection collection = new PointCollection(SpatialReferences.getWebMercator());
        for (LocationCoordinate locationCoordinate : locationCoordinates) {
            collection.add(locationToMercatorPoint(locationCoordinate));
        }
        return collection;
    }

    /**
     * 坐标集转arcgis Wgs84 PointCollection
     *
     * @param locationCoordinates
     * @return
     */
    public static PointCollection locationToWgs84PointCollection(List<LocationCoordinate> locationCoordinates) {
        PointCollection collection = new PointCollection(SpatialReferences.getWgs84());
        for (LocationCoordinate locationCoordinate : locationCoordinates) {
            collection.add(locationToWgs84Point(locationCoordinate));
        }
        return collection;
    }



    public static Point getScreenLocationPoint(MapView mapView, float screenX, float screenY) {
        android.graphics.Point point = new android.graphics.Point(Math.round(screenX), Math.round(screenY));
        return mapView.screenToLocation(point);
    }
}
