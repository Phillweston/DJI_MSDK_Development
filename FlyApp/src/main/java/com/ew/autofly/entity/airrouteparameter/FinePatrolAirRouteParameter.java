package com.ew.autofly.entity.airrouteparameter;

import com.ew.autofly.entity.AirRouteParameter;



public class FinePatrolAirRouteParameter extends AirRouteParameter {

    /* 巡检速度 */
    private float mPatrolSpeed = 0.0F;

    
    private int mMissionType;
    
    private int mMissionMode;
    
    private int mSettingMode;
    
    private int mStudyStatus;
    private boolean isRecordPictureIndex;
    private boolean isStudyAirRoute;
    private boolean isReverse = false;
    private boolean enableManualMode=true;

    
    private int flyStrategy;

    
    private int cameraZoomMode;

    public FinePatrolAirRouteParameter(String cameraName) {
        super(cameraName);
    }

    public int getMissionType() {
        return mMissionType;
    }

    public void setMissionType(int missionType) {
        mMissionType = missionType;
    }

    public int getMissionMode() {
        return mMissionMode;
    }

    public void setMissionMode(int missionMode) {
        mMissionMode = missionMode;
    }

    public int getSettingMode() {
        return mSettingMode;
    }

    public void setSettingMode(int settingMode) {
        mSettingMode = settingMode;
    }

    public int getStudyStatus() {
        return mStudyStatus;
    }

    public void setStudyStatus(int studyStatus) {
        mStudyStatus = studyStatus;
    }

    public boolean isRecordPictureIndex() {
        return isRecordPictureIndex;
    }

    public void setRecordPictureIndex(boolean recordPictureIndex) {
        isRecordPictureIndex = recordPictureIndex;
    }

    public boolean isStudyAirRoute() {
        return isStudyAirRoute;
    }

    public void setStudyAirRoute(boolean studyAirRoute) {
        isStudyAirRoute = studyAirRoute;
    }

    public float getPatrolSpeed() {
        return mPatrolSpeed;
    }

    public void setPatrolSpeed(float patrolSpeed) {
        mPatrolSpeed = patrolSpeed;
    }

    public boolean isReverse() {
        return isReverse;
    }

    public void setReverse(boolean reverse) {
        isReverse = reverse;
    }

    public boolean isEnableManualMode() {
        return enableManualMode;
    }

    public void setEnableManualMode(boolean enableManualMode) {
        this.enableManualMode = enableManualMode;
    }

    public int getFlyStrategy() {
        return flyStrategy;
    }

    public void setFlyStrategy(int flyStrategy) {
        this.flyStrategy = flyStrategy;
    }

    public int getCameraZoomMode() {
        return cameraZoomMode;
    }

    public void setCameraZoomMode(int cameraZoomMode) {
        this.cameraZoomMode = cameraZoomMode;
    }
}
