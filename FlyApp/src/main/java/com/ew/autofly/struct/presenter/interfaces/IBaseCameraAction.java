package com.ew.autofly.struct.presenter.interfaces;

import dji.common.gimbal.GimbalMode;



public interface IBaseCameraAction{

    
    void startTakePhoto();

    
    void stopTakePhoto();

    
    void startRecord();

    
    void stopRecord();

    
    void setCameraShootPhoto();

    
    void setCameraRecordVideo();

    /**
     * 设置相机录像模式
     * @param startRecordImmediately 设置后立即启动相机
     */
    void setCameraRecordVideo(boolean startRecordImmediately);

    
    void setPhotoAspectRatio();

    
    void resetGimbal();

    /**
     * 设置云台模式
     * @param gimbalMode
     */
    void setGimbalMode(GimbalMode gimbalMode);

    /**
     * 获取云台俯仰角度
     * @return
     */
    float getGimbalPitch();

}
