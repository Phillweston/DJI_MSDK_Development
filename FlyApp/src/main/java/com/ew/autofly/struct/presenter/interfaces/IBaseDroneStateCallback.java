package com.ew.autofly.struct.presenter.interfaces;

import androidx.annotation.NonNull;

import dji.common.battery.BatteryState;
import dji.common.camera.ExposureSettings;
import dji.common.camera.StorageState;
import dji.common.camera.SystemState;
import dji.common.flightcontroller.FlightControllerState;
import dji.common.flightcontroller.RTKState;
import dji.common.flightcontroller.VisionControlState;
import dji.common.flightcontroller.VisionDetectionState;
import dji.common.gimbal.GimbalState;
import dji.common.remotecontroller.HardwareState;
import dji.sdk.media.MediaFile;



public interface IBaseDroneStateCallback {

    /**
     * 飞行监控状态回调
     * @param state
     */
    void onFlightControllerStateCallback(FlightControllerState state);

    /**
     * RTK状态回调
     * @param rtkState
     */
    void onRTKStateCallback(RTKState rtkState);

    /**
     * 视觉控制状态回调
     * @param visionControlState
     */
    void onVisionControlStateCallback(VisionControlState visionControlState);

    /**
     * 视觉检测状态回调
     * @param visionDetectionState
     */
    void onVisionDetectionStateCallBack(VisionDetectionState visionDetectionState);

    /**
     * 遥控器硬件状态回调
     * @param hardwareState
     */
    void onHardwareStateCallback(@NonNull HardwareState hardwareState);

    
    void onGimbalStateCallback(@NonNull GimbalState gimbalState);

    
    void onBatteryStateStateCallback(BatteryState batteryState);

    
    void onCameraSystemStateCallback(@NonNull SystemState systemState);

    
    void onCameraExposureSettingsCallback(@NonNull ExposureSettings exposureSettings);

    
    void onCameraStorageCallBack(@NonNull StorageState sdCardState);

    
    void onCameraMediaFileCallback(@NonNull MediaFile mediaFile);
}
