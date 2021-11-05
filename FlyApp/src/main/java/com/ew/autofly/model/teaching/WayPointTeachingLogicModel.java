package com.ew.autofly.model.teaching;

import com.ew.autofly.db.entity.FinePatrolWayPointDetail;
import com.ew.autofly.entity.LocationCoordinate;
import com.ew.autofly.entity.mission.MissionPointType;

import java.util.ArrayList;
import java.util.List;


public class WayPointTeachingLogicModel {

    private List<LocationCoordinate> mTeachingPointList = new ArrayList<>();

    private IWayPointTeachingView mControlView;

    public WayPointTeachingLogicModel(IWayPointTeachingView controlView) {
        mControlView = controlView;
    }

    
    public void reSet() {
        mTeachingPointList.clear();
    }

    
    public void addAssistWayPoint(LocationCoordinate coordinate) {
        mControlView.addTeachMissionPoint(coordinate, MissionPointType.ASSIST);
        mControlView.addTeachMissionPointAltitude(coordinate);
        mTeachingPointList.add(coordinate);
        drawLineContinue(coordinate);
    }

    
    public void addShotPhotoWayPoint(LocationCoordinate coordinate) {
        mControlView.addTeachMissionPoint(coordinate, MissionPointType.SHOT_PHOTO);
        mControlView.addTeachMissionPointAltitude(coordinate);
        mTeachingPointList.add(coordinate);
        drawLineContinue(coordinate);
    }


    public void reviewPath(List<FinePatrolWayPointDetail> detailList, boolean showFlag) {
        List<LocationCoordinate> coordinates = new ArrayList<>();
        for (FinePatrolWayPointDetail detail : detailList) {
            LocationCoordinate coordinate = detail.getAirCraftLocation();
            mControlView.addTeachMissionPoint(coordinate, MissionPointType.find(detail.getWaypointType()));
            coordinates.add(coordinate);
        }
        drawLine(coordinates, showFlag);
    }

    private void drawLineContinue(LocationCoordinate point) {
        drawLine(getLastPoint(), point);
    }

    
    private void drawLine(LocationCoordinate point1, LocationCoordinate point2) {
        List<LocationCoordinate> points = new ArrayList<>();
        points.add(point1);
        points.add(point2);
        drawLine(points, false);
    }

    private void drawLine(List<LocationCoordinate> points, boolean showFlag) {
        mControlView.addTeachMissionLine(points, showFlag);
    }

    private LocationCoordinate getLastPoint() {
        int index = getPointSize() > 1 ? getPointSize() - 2 : 0;
        return mTeachingPointList.get(index);
    }

    private int getPointSize() {
        return mTeachingPointList.size();
    }
}
