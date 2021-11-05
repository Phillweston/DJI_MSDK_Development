package com.ew.autofly.struct.presenter.interfaces;

import androidx.annotation.NonNull;

import com.ew.autofly.entity.AirRouteParameter;
import com.ew.autofly.entity.FlyCheckStatus;
import com.ew.autofly.entity.LocationCoordinate;
import com.ew.autofly.internal.common.CheckError;

import org.greenrobot.greendao.annotation.NotNull;



public interface IBaseMapPresenter {

    
    AirRouteParameter initAirRouterParameter(String cameraName);

    
    AirRouteParameter getAirRouteParameter();

    /**
     * 生成任务
     * @return 任务时间 (返回-1代表不成功)
     */
    double prepareMission();

    
    CheckError checkMission();

    
    void buildMission();

    
    void uploadMission();

    
    void cancelUploadMission();

    
    void startMission();

    
    void pauseMission();

    
    void resumeMission();

    
    void finishMission();

    /**
     * 保存任务
     *
     * @param isAutoSave        是否自动保存任务
     * @param name
     * @param snapShot          截图
     */
    void saveMission(boolean isAutoSave, String name, String snapShot);

    
    double calculateMissionFlyTime();

    
    void goHome();



    /**
     * 是否有云台相机
     *
     * @return
     */
    boolean isHasCamera();

    /**
     * 启动模拟飞行
     *
     * @return
     * @param locationCoordinate
     */
    void startSimulate(@NotNull LocationCoordinate locationCoordinate);

    
    void stopSimulate();

    /**
     * 获取飞机的Home点坐标
     *
     * @return
     */
    LocationCoordinate getAirCraftHomeLocation();

    /**
     * 获取飞机的三维坐标
     *
     * @return
     */
    @NonNull LocationCoordinate getAirCraftLocation();

    /**
     * 获取
     * @return
     */
    LocationCoordinate getRTKBaseLocation();

    
    String getMonitorFlightInfo();

    FlyCheckStatus getFlyCheckStatus();


}
