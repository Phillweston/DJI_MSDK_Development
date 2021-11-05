package com.ew.autofly.struct.proxy;

import com.esri.arcgisruntime.mapping.view.SceneView;
import com.ew.autofly.entity.LocationCoordinate;
import com.ew.autofly.entity.mission.MissionPointType;

import java.util.List;


public interface IBaseSceneViewProxy {

    String MODEL_TYPE_MSPK="mspk";
    String MODEL_TYPE_SLPK="slpk";

    /**
     * 绑定SceneView
     *
     * @param sceneView
     */
    void attach(SceneView sceneView);

    
    void detach();

    void initGraphicsLayers();

    void initSymbols();

    void initGraphics();


    void addMissionPoint(LocationCoordinate coordinate, MissionPointType missionPointType);

    
    void addMissionPointAltitude(LocationCoordinate coordinate);

    /**
     * 添加任务航线
     *
     * @param coordinates
     * @param showStartFlag 是否显示起点
     * @param showEndFlag   是否显示终点
     */
    void addMissionLine(List<LocationCoordinate> coordinates, boolean showStartFlag, boolean showEndFlag);

    
    void clearMission();

    /**
     * 实时更新飞机的位置信息
     *
     * @param angle      飞机机头的方位
     */
    void updateDroneLocation(LocationCoordinate locationCoordinate, float angle);

    
    void updateHomeLocation(LocationCoordinate locationCoordinate);

    /**
     * 定位到该坐标
     * @param locationCoordinate
     */
    void centerAtLocation(LocationCoordinate locationCoordinate);

}
