package com.ew.autofly.struct.view;

import androidx.annotation.Nullable;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.ew.autofly.constant.AppConstant;
import com.ew.autofly.dialog.common.GoHomeDialog;
import com.ew.autofly.entity.LocationCoordinate;
import com.ew.autofly.entity.WayPointInfo;

import java.util.List;



public interface IBaseMapView extends IBaseFlightView {

    /**
     * 获取手机定位
     *
     * @return
     */
    @Nullable
    LocationCoordinate getMobileLocation();


    
    void clickFlightStart();

    
    void showFlightCheck();

    
    void showFlightSuccess();

    void updateFlightEnable(boolean enable);

    /**
     * 刷新按钮状态
     *
     * @param action
     */
    void updateFlightButtons(AppConstant.FlightAction action);

    void updateFlightStart();

    void updateFlightPause();

    void updateFlightResume();

    void updateFlightGoHome();

    void updateFlightUnknown();

    
    void showKmlDialog();

    
    void showSettingDialog();

    
    void showLoadTaskDialog();

    
    void showSimulateDialog();

    
    void showAutoReturnDialog();

    
    void showLowPowerReturnDialog();

    
    void showSmartReturnDialog(GoHomeDialog.GoHomeListener goHomeListener);

    /**
     * 显示智能返航倒计时
     *
     * @param countdown
     */
    void showSmartReturnTimeCount(int countdown);

    
    void addMissionPoint(Point point);

    /**
     * 添加多个任务航点
     * @param pointList
     */
    void addMultiMissionPoint(List<Point> pointList);

    
    void addMissionPointAltitude(Point point, String altitudeTxt);

    
    void addMissionLine(PointCollection points);

    /**
     * 添加任务航线
     *
     * @param showFlag 是否显示起/终点
     */
    void addMissionLine(PointCollection points, boolean showFlag);


    void addMissionLine(PointCollection points, boolean showStartFlag, boolean showEndFlag);

    
    void clearMission();

    
    void clearMissionLayer();

    
    void showMission();

    
    void showLoadMission();

    /**
     * 绘制任务航线轨迹
     *
     * @param isLoadMission 是否载入任务
     */
    void drawMissionPath(boolean isLoadMission);

    /**
     * 将航线换算任务航点
     *
     * @return
     */
    List<? extends WayPointInfo> convertWaypointTasks();

    /**
     * //航线经纬度坐标点（纬度,经度），格式：23.111,113.1234|23.4545,113.5555
     *
     * @return
     */
    List<String> convertGeometryLatLngList();

    
    void updateMonitorInfo(String info);

    
    void showMonitorInfo();

    /**
     * 实时更新飞机的位置信息
     *
     * @param angle 飞机机头的方位
     */
    void updateDroneLocation(LocationCoordinate locationCoordinate, float angle);


    
    void updateHomeLocation(LocationCoordinate locationCoordinate);

    
    void updateLineBetweenDroneAndHome(LocationCoordinate homeLoc, LocationCoordinate droneLoc);

    /**
     * 刷新底部任务信息
     *
     * @param missionFlyTime 单位：分钟
     */
    void updateBottomMissionInfo(double missionFlyTime);

    
    void saveMission();

    /**
     * 载入任务
     *
     * @param missionId
     */
    void loadMission(String missionId);

    
    void reviewMission(List<String> selectedIdList, int missionType);


  
    void onPsdkDataReceive(String s);

    void onPsdkConnected();

    /**
     * 是否显示照明灯（只针对御2行业版）
     *
     * @param isVisible
     */
    void showSpotlight(boolean isVisible);

    /**
     * 是否显示喇叭（只针对御2行业版）
     *
     * @param isVisible
     */
    void showSpeaker(boolean isVisible);

    /**
     * 是否显示航向灯（只针对御2行业版）
     *
     * @param isVisible
     */
    void showBeacon(boolean isVisible);

    
    void dismissAccessoryAggregationView();

}
