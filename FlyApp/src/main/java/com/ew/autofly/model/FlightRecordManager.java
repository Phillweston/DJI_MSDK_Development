package com.ew.autofly.model;

import android.text.TextUtils;

import com.ew.autofly.application.EWApplication;
import com.ew.autofly.db.entity.FlightRecord;
import com.ew.autofly.db.entity.FlightRecordDetail;
import com.ew.autofly.db.entity.MissionBase;
import com.ew.autofly.db.helper.FlightRecordDbHelper;
import com.ew.autofly.db.helper.FlightRecordDetailDbHelper;
import com.ew.autofly.entity.FlightMonitorInfo;
import com.ew.autofly.entity.LocationCoordinate;
import com.flycloud.autofly.base.util.SysUtils;
import com.ew.autofly.xflyer.utils.DateHelperUtils;
import com.ew.autofly.xflyer.utils.LatLngUtils;
import com.google.gson.Gson;
import com.flycloud.autofly.control.service.RouteService;

import java.util.Date;

import dji.common.battery.BatteryState;
import dji.common.error.DJIError;
import dji.common.flightcontroller.Attitude;
import dji.common.flightcontroller.FlightControllerState;
import dji.common.gimbal.GimbalState;
import dji.common.remotecontroller.HardwareState;
import dji.common.util.CommonCallbacks;
import dji.sdk.base.BaseProduct;
import dji.sdk.battery.Battery;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.products.Aircraft;



public class FlightRecordManager {

    private volatile boolean isRecordCreated = false;

    private FlightRecord mFlightRecord;
    private MissionBase mMissionBase;
    private double mTotalDistance;
    private LocationCoordinate mLastRecordLoc;

    public final static FlightRecordManager INSTANCE = new FlightRecordManager();
    private String mProductName;
    private String mFlightSerialNumber;
    private String mBatterySerialNumber;
    private String mDeviceNo;
    private String mCreateRecordTime;

  
    private long mSubMissionId;


    private long mCurrentRecordTime;
    private long mCurrentUploadTime;

    public static FlightRecordManager getInstance() {
        return INSTANCE;
    }

    public FlightRecordManager() {
        mDeviceNo = SysUtils.getFormatDeviceId(EWApplication.getInstance());
    }

    public synchronized void createNewRecord(LocationCoordinate airCraftLoc) {
        createNewRecord(airCraftLoc, null);
    }

    public void init() {

        Aircraft aircraft = AircraftManager.getAircraft();
        if (aircraft != null && aircraft.getModel() != null) {
            mProductName = aircraft.getModel().getDisplayName();
        }

        if (aircraft != null) {
            FlightController flightController = aircraft.getFlightController();
            if (flightController != null) {
                flightController.getSerialNumber(new CommonCallbacks.CompletionCallbackWith<String>() {
                    @Override
                    public void onSuccess(String s) {
                        mFlightSerialNumber = s;
                    }

                    @Override
                    public void onFailure(DJIError djiError) {

                    }
                });
            }

            Battery battery = aircraft.getBattery();
            if (battery != null) {
                battery.getSerialNumber(new CommonCallbacks.CompletionCallbackWith<String>() {
                    @Override
                    public void onSuccess(String s) {
                        mBatterySerialNumber = s;

                    }

                    @Override
                    public void onFailure(DJIError djiError) {

                    }
                });
            }

        }
    }


    public synchronized void createNewRecord(LocationCoordinate airCraftLoc,
                                             MissionBase missionBase) {

        if (missionBase != null) {
            mMissionBase = missionBase;
        }

        if (mFlightRecord == null && !isRecordCreated) {
            isRecordCreated = true;
            mFlightRecord = new FlightRecord();
            mFlightRecord.setId(SysUtils.newGUID());
            mFlightRecord.setCreatedTime(DateHelperUtils.getSystemTime());
            mFlightRecord.setStartTime(DateHelperUtils.getSystemTime());
            mFlightRecord.setLatitude(airCraftLoc.getLatitude());
            mFlightRecord.setLongitude(airCraftLoc.getLongitude());
            mCreateRecordTime = DateHelperUtils.format(new Date());
            mFlightRecord.setProductName(mProductName);
            mFlightRecord.setProductSerialNumber(mFlightSerialNumber);
            mFlightRecord.setBatterySerialNumber(mBatterySerialNumber);

            FlightRecordDbHelper.getInstance().save(mFlightRecord);

        }
    }

    /**
     * 带上工单巡检信息（如果有）
     *
     * @param missionId
     */
    public void setPatrolInfo(long missionId) {
        this.mSubMissionId = missionId;
    }

    
    public synchronized void saveDetailRecord(LocationCoordinate airCraftLoc, LocationCoordinate homePoint,
                                              FlightControllerState flightControllerState,
                                              HardwareState hardwareState, BatteryState batteryState) {

        if (mFlightRecord == null) {
            return;
        }

        long timeMillis = System.currentTimeMillis();
        if (mCurrentRecordTime == 0) {
            mCurrentRecordTime = timeMillis;
        }

      
        if (timeMillis - mCurrentRecordTime > 1000) {

            mCurrentRecordTime = timeMillis;

            FlightRecordDetail detail = new FlightRecordDetail();
            detail.setCreatedTime(DateHelperUtils.getSystemTime());
            detail.setRecordId(mFlightRecord.getId());

            double latitude = airCraftLoc.getLatitude();
            double longitude = airCraftLoc.getLongitude();
            double altitude = airCraftLoc.getAltitude();

            detail.setLatitude(latitude);
            detail.setLongitude(longitude);
            detail.setAltitude(altitude);

          
            double homeDistance = LatLngUtils.getDistance(latitude, longitude, homePoint.latitude, homePoint.longitude);
            detail.setDistance(homeDistance);

            if (altitude > mFlightRecord.getMaxHeight()) {
                mFlightRecord.setMaxHeight((int) altitude);
            }

            if (mLastRecordLoc != null) {
                double distance = LatLngUtils.getDistance(latitude, longitude, mLastRecordLoc.getLatitude(), mLastRecordLoc.getLongitude());
                mTotalDistance += distance;
            }

            mLastRecordLoc = airCraftLoc;

            if (flightControllerState != null) {

                float velocityX = flightControllerState.getVelocityX();
                float velocityY = flightControllerState.getVelocityY();
                float velocityZ = flightControllerState.getVelocityZ();
                String flightModeString = flightControllerState.getFlightModeString();
                Attitude attitude = flightControllerState.getAttitude();
                int satelliteCount = flightControllerState.getSatelliteCount();

                detail.setVerticalSpeed((int) velocityZ);
                detail.setHorizontalSpeed((int) Math.sqrt(Math.pow(velocityX, 2) + Math.pow(velocityY, 2)));
                detail.setGPSSatelliteCount(satelliteCount);
                detail.setGPSMode(flightModeString);
                detail.setAngle(attitude.yaw);
            }

            if (batteryState != null) {
                int remainPower = batteryState.getChargeRemainingInPercent();
                detail.setBatteryLevel(remainPower);
            }

            if (hardwareState != null) {
              
                int leftStickHorizontalPosition = hardwareState.getLeftStick().getHorizontalPosition();
                int leftStickVerticalPosition = hardwareState.getLeftStick().getVerticalPosition();
                int rightStickHorizontalPosition = hardwareState.getRightStick().getHorizontalPosition();
                int rightStickVerticalPosition = hardwareState.getRightStick().getVerticalPosition();

                detail.setLeftStickHorizontalPosition(leftStickHorizontalPosition);
                detail.setLeftStickVerticalPosition(leftStickVerticalPosition);
                detail.setRightStickHorizontalPosition(rightStickHorizontalPosition);
                detail.setRightStickVerticalPosition(rightStickVerticalPosition);

            }

            FlightRecordDetailDbHelper.getInstance().save(detail);
        }
    }

    
    public synchronized void endRecord() {
        mCurrentRecordTime = 0;
        mMissionBase = null;
        if (mFlightRecord != null && isRecordCreated) {
            isRecordCreated = false;
            mFlightRecord.setEndTime(DateHelperUtils.getSystemTime());
            mFlightRecord.setDistance(mTotalDistance);
            FlightRecordDbHelper.getInstance().update(mFlightRecord);
            mFlightRecord = null;
            mLastRecordLoc = null;
            mTotalDistance = 0;
        }
    }

    public synchronized void uploadFlightMonitorInfo(String userName, LocationCoordinate airCraftLoc, long flightTime, double flightDistance, FlightControllerState flightControllerState,
                                                     HardwareState hardwareState, BatteryState batteryState,
                                                     GimbalState gimbalState) {

        if (TextUtils.isEmpty(RouteService.getUserToken())) {
            return;
        }

        long timeMillis = System.currentTimeMillis();
        if (mCurrentUploadTime == 0) {
            mCurrentUploadTime = timeMillis;
        }

        if (flightControllerState != null) {

          
            if (timeMillis - mCurrentUploadTime >= 5000) {
                mCurrentUploadTime = timeMillis;

                FlightMonitorInfo flightMonitorInfo = new FlightMonitorInfo();

                BaseProduct product = EWApplication.getProductInstance();
                if (product != null && product.getModel() != null) {
                    String displayName = product.getModel().getDisplayName();
                    flightMonitorInfo.setDroneModel(displayName);
                }

                if (product != null && product.getCamera() != null) {
                    flightMonitorInfo.setCameraModel(product.getCamera().getDisplayName());
                }

                if (mMissionBase != null) {
                    flightMonitorInfo.setMissionId(mMissionBase.getMissionId());
                    flightMonitorInfo.setMissionType(mMissionBase.getMissionType());
                }

                flightMonitorInfo.setSubMissionId(mSubMissionId);

                flightMonitorInfo.setDeviceNo(mDeviceNo);


                double latitude = airCraftLoc.getLatitude();
                double longitude = airCraftLoc.getLongitude();
                double altitude = airCraftLoc.getAltitude();

                flightMonitorInfo.setLongitude(longitude);
                flightMonitorInfo.setLatitude(latitude);
                flightMonitorInfo.setHeight(altitude);


                float velocityX = flightControllerState.getVelocityX();
                float velocityY = flightControllerState.getVelocityY();
                float velocityZ = flightControllerState.getVelocityZ();

                Attitude attitude = flightControllerState.getAttitude();
                int satelliteCount = flightControllerState.getSatelliteCount();

                flightMonitorInfo.setVerticalSpeed(velocityZ);
                flightMonitorInfo.setHorizontalSpeed(Math.sqrt(Math.pow(velocityX, 2) + Math.pow(velocityY, 2)));
                flightMonitorInfo.setGpsCount(satelliteCount);
                flightMonitorInfo.setAngle((int) attitude.yaw);
                flightMonitorInfo.setDronePitch((int) attitude.pitch);
                flightMonitorInfo.setDroneRoll((int) attitude.roll);


                if (batteryState != null) {
                    int remainPower = batteryState.getChargeRemainingInPercent();
                    flightMonitorInfo.setBatteryRemain(remainPower);
                }

                if (hardwareState != null) {
                  
                    int leftStickHorizontalPosition = hardwareState.getLeftStick().getHorizontalPosition();
                    int leftStickVerticalPosition = hardwareState.getLeftStick().getVerticalPosition();
                    int rightStickHorizontalPosition = hardwareState.getRightStick().getHorizontalPosition();
                    int rightStickVerticalPosition = hardwareState.getRightStick().getVerticalPosition();

                    flightMonitorInfo.setRudderHorizontalLeft(leftStickHorizontalPosition);
                    flightMonitorInfo.setRudderHorizontalRight(rightStickHorizontalPosition);
                    flightMonitorInfo.setRudderVerticalLeft(leftStickVerticalPosition);
                    flightMonitorInfo.setRudderVerticalRight(rightStickVerticalPosition);

                }

                if (gimbalState != null) {
                    flightMonitorInfo.setPitch((int) gimbalState.getAttitudeInDegrees().getPitch());
                    flightMonitorInfo.setRoll((int) gimbalState.getAttitudeInDegrees().getRoll());
                    flightMonitorInfo.setYaw((int) gimbalState.getAttitudeInDegrees().getYaw());
                }

                flightMonitorInfo.setFlightSerialNumber(mFlightSerialNumber);
                flightMonitorInfo.setBatterySerialNumber(mBatterySerialNumber);

                String uploadTime = DateHelperUtils.format(new Date());
                flightMonitorInfo.setCreatedTime(mCreateRecordTime);
                flightMonitorInfo.setUploadTime(uploadTime);
                flightMonitorInfo.setFlightTime(flightTime);
                flightMonitorInfo.setFlightDistance(flightDistance);
                flightMonitorInfo.setUserName(userName);
                String monitorInfo = new Gson().toJson(flightMonitorInfo);
              
                RouteService.uploadFlightMonitor(monitorInfo);


            }
        }
    }

    
    public synchronized void onDestroy() {
        endRecord();
        mCurrentUploadTime = 0;
        clearPatrolJobInfo();
    }

    
    private void clearPatrolJobInfo() {
        mSubMissionId = 0;
    }
}
