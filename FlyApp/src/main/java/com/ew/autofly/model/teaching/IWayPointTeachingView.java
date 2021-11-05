package com.ew.autofly.model.teaching;

import com.ew.autofly.db.entity.FinePatrolWayPointDetail;
import com.ew.autofly.entity.LocationCoordinate;
import com.ew.autofly.entity.mission.MissionPointType;

import java.util.List;


public interface IWayPointTeachingView {


    void addTeachMissionPoint(LocationCoordinate coordinate, MissionPointType missionPointType);

    
    void addTeachMissionPointAltitude(LocationCoordinate coordinate);


    void addTeachMissionLine(List<LocationCoordinate> coordinates, boolean showFlag);

    /**
     * 添加辅助航点
     * @param loc
     */
    void addAssistWayPoint(LocationCoordinate loc);

    /**
     * 添加拍照航点
     * @param loc
     */
    void addShotPhotoWayPoint(LocationCoordinate loc);

    /**
     * 获取航点坐标
     *
     * @return
     */
    List<FinePatrolWayPointDetail> getWaypointDetails();

}
