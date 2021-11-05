package com.ew.autofly.module.flightrecord.view;

import com.esri.android.map.GraphicsLayer;
import com.esri.core.geometry.Point;
import com.ew.autofly.base.IBaseMvpView;
import com.ew.autofly.entity.LocationCoordinate;

import java.util.List;

public interface IPlaybackView extends IBaseMvpView{


    void setProductInfo(String productName);


    void setLocationCity(String city);


    void setCreatedTime(String createdTime);


    void setTotalTime(String startTime,String endTime);


    void setTotalDistance(double distance);


    void setMaxHeight(double maxHeight);


    void showAirRouteInMap(List<Point> pointList);


    void addPlane(Point point ,double angle);


    void updatePlane(Point point ,double angle);


    void updateRealTimeFlightInfo(String height, String distance, String vSpeed,
                                  String hSpeed, String battery, String GPSMode);

    void updateRealTimeDroneLocation(LocationCoordinate locationCoordinate);


    void updateControllerState(int leftStickHorizontalPos, int leftStickVerticalPos, int rightStickHorizontalPos, int rightStickVerticalPos);


    void updateProgress(int progress);


    void setProgressMax(int max);

}
