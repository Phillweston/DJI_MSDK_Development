package com.ew.autofly.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ew.autofly.application.EWApplication;
import com.ew.autofly.constant.AppConstant;
import com.ew.autofly.entity.LocationCoordinate;
import com.ew.autofly.event.flight.LocationStateEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;

import dji.common.flightcontroller.FlightControllerState;
import dji.common.flightcontroller.RTKState;
import dji.common.model.LocationCoordinate2D;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.flightcontroller.RTK;
import dji.sdk.products.Aircraft;

/**
 * 注：准备弃用，请不要继承此类
 * 杆塔相关采集基类
 */
@Deprecated
public class BaseTowerCollectFragment extends BaseCollectFragment implements Serializable {

    private boolean isRTKBeingUsed;
    private boolean isHeadingValid;



    private double mPlaneAltitude;
    private double mPlaneHeader;

    private double mRTKBaseLatitude;
    private double mRTKBaseLongitude;
    private float mRTKBaseAltitude;

    private boolean isRtkEnable = false;

    private int mPositioningSolution = 0;

    public double getPlaneLatitude() {
        return aircraftLocationLatitude;
    }

    public double getPlaneLongitude() {
        return aircraftLocationLongitude;
    }

    public double getPlaneAltitude() {
        return mPlaneAltitude;
    }

    public double getPlaneHeader() {
        int rotateRealAngle = (int) (mPlaneHeader + (hasRTKSignal() ? 90 : 0)) % 360;
        rotateRealAngle = (rotateRealAngle >= 180 ? rotateRealAngle - 360 : rotateRealAngle);
        return rotateRealAngle;
    }

    public double getRTKBaseLatitude() {
        return mRTKBaseLatitude;
    }

    public double getRTKBaseLongitude() {
        return mRTKBaseLongitude;
    }

    public double getRTKBaseAltitude() {
        return mRTKBaseAltitude;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initDroneEvent() {
        super.initDroneEvent();
        initRtk();
    }

    /**
     * RTK是否开启
     *
     * @return
     */
    protected boolean isRTKEnable() {
        return isRtkEnable;
    }

    
    private void initRtk() {

        Aircraft djiAircraft = EWApplication.getAircraftInstance();
        if (djiAircraft != null) {
            FlightController flightController = djiAircraft.getFlightController();
            if (flightController != null) {
                RTK djiRtk = flightController.getRTK();

                if (djiRtk != null) {
                    djiRtk.setStateCallback(new RTKState.Callback() {
                        @Override
                        public void onUpdate(@NonNull RTKState djiRtkState) {

                            LocationCoordinate2D rtkPlaneLocation = djiRtkState.getMobileStationLocation();
                            LocationCoordinate2D rtkBaseLocation = djiRtkState.getBaseStationLocation();

                            isRTKBeingUsed = djiRtkState.isRTKBeingUsed();
                            isHeadingValid = djiRtkState.isHeadingValid();

                            mRTKBaseLatitude = rtkBaseLocation.getLatitude();
                            mRTKBaseLongitude = rtkBaseLocation.getLongitude();
                            mRTKBaseAltitude = djiRtkState.getBaseStationAltitude();

                            if (
                                    djiRtkState.isRTKBeingUsed()
                                            && djiRtkState.isHeadingValid()
                                            && rtkPlaneLocation != null
                                            && rtkPlaneLocation.getLongitude() != 0
                                            && rtkPlaneLocation.getLatitude() != 0
                                            && mRTKBaseLatitude != 0
                                            && mRTKBaseLongitude != 0) {

                                setHasRTk(true);

                                aircraftLocationLatitude = rtkPlaneLocation.getLatitude();
                                aircraftLocationLongitude = rtkPlaneLocation.getLongitude();
                                mPlaneAltitude = djiRtkState.getMobileStationAltitude();
                                mPlaneHeader = djiRtkState.getHeading();



                                mPositioningSolution = djiRtkState.getPositioningSolution().value();

                            } else {
                                setHasRTk(false);
                            }

                        }
                    });
                } else {
                    setHasRTk(false);
                }
            }
        }

       /* if (imgBtnGPSSignal != null) {
            imgBtnGPSSignal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showRTKStateDialog();
                }
            });
        }*/
    }

    @Override
    public void onFlightControllerStateCallback(FlightControllerState state) {
        super.onFlightControllerStateCallback(state);
        if (!hasRTKSignal()) {
            mPlaneHeader = state.getAttitude().yaw;
            mPlaneAltitude = state.getAircraftLocation().getAltitude();
        }
        EventBus.getDefault().post(new LocationStateEvent(hasRTKSignal(), getAirCraftLocation(), getRTKBaseLocation()));
    }

    @Override
   /* protected void showMonitorText() {
        StringBuffer sbMonitorInfo = new StringBuffer();
        sbMonitorInfo.append("rtk是否有信号: ").append(hasRTKSignal() ? "是" : "否").append("\n");
        sbMonitorInfo.append("PositioningSolution: ").append(mPositioningSolution + "").append("\n");
        sbMonitorInfo.append("isRTKEnable: ").append(isRtkEnable ? "是" : "否" + "").append("\n");
        sbMonitorInfo.append("isRTKBeingUsed: ").append(isRTKBeingUsed ? "是" : "否" + "").append("\n");
        sbMonitorInfo.append("isHeadingValid: ").append(isHeadingValid ? "是" : "否" + "").append("\n");
        sbMonitorInfo.append("飞机高度: ").append(String.format("%.2fm", getPlaneAltitude())).append("\n");
        sbMonitorInfo.append("飞机纬度: ").append(String.format("%.6f", getPlaneLatitude())).append("\n");
        sbMonitorInfo.append("飞机经度: ").append(String.format("%.6f", getPlaneLongitude())).append("\n");
        sbMonitorInfo.append("飞机朝向: ").append(String.format("%.2f°", getPlaneHeader())).append("\n");
        sbMonitorInfo.append("基站高度: ").append(String.format("%.2fm", getRTKBaseAltitude())).append("\n");
        sbMonitorInfo.append("基站纬度: ").append(String.format("%.6f", getRTKBaseLatitude())).append("\n");
        sbMonitorInfo.append("基站经度: ").append(String.format("%.6f", getRTKBaseLongitude())).append("\n");
        sbMonitorInfo.append("水平距离: ").append(String.format("%.2fm", mDistanceX)).append("\n");
        sbMonitorInfo.append("水平速度: ").append(String.format("%.2fm/s", mHorizontalSpeed)).append("\n");
        sbMonitorInfo.append("垂直速度: ").append(String.format("%.2fm/s", mVerticalSpeed)).append("\n");
        sbMonitorInfo.append("云台角度: ").append(String.format("%.0f°", mGimbalAngle)).append("\n");
        sbMonitorInfo.append("飞行时长: ").append(String.format("%.2f", mRunTime / 60.0f)).append("min\n");
        if (actionMode == AppConstant.ACTION_MODE_PHOTO) {
            sbMonitorInfo.append("曝光数量: ").append(mExposureNum).append("\n");
        }
        sbMonitorInfo.append("曝光补偿: ").append(cameraExposureCompensation).append("\n");
        sbMonitorInfo.append("相机模式: ").append(cameraMode);

        mcuState = sbMonitorInfo.toString();
    }*/

    protected void showMonitorText() {

        StringBuffer sbMonitorInfo = new StringBuffer();

        sbMonitorInfo.append("云台角度: ").append(String.format("%.0f°", mGimbalAngle)).append("\n");
        sbMonitorInfo.append("飞行高度: ").append(String.format("%.2fm", getPlaneAltitude())).append("\n");
        sbMonitorInfo.append("水平距离: ").append(String.format("%.2fm", mDistanceX)).append("\n");
        sbMonitorInfo.append("水平速度: ").append(String.format("%.2fm/s", mHorizontalSpeed)).append("\n");
        sbMonitorInfo.append("垂直速度: ").append(String.format("%.2fm/s", mVerticalSpeed)).append("\n");
        sbMonitorInfo.append("飞行时长: ").append(String.format("%.2f", mRunTime / 60.0f)).append("min\n");
        if (actionMode == AppConstant.ACTION_MODE_PHOTO) {
            sbMonitorInfo.append("曝光数量: ").append(exposureNum).append("\n");
        }
        sbMonitorInfo.append("曝光补偿: ").append(cameraExposureCompensation).append("\n");
        sbMonitorInfo.append("相机模式: ").append(cameraMode);

        mcuState = sbMonitorInfo.toString();
    }

   /* protected void showRTKStateDialog() {
        RTKStateDialog rtkStateDialog = new RTKStateDialog();
        Bundle args = new Bundle();
        args.putBoolean(RTKStateDialog.PARAMS_RTK_ENABLED_FLAG, isRTKEnable());
        rtkStateDialog.setArguments(args);
        rtkStateDialog.show(getFragmentManager(), "RTKStateDialog");
    }*/

    public LocationCoordinate getRTKBaseLocation() {
        return new LocationCoordinate(mRTKBaseLatitude, mRTKBaseLongitude, mRTKBaseAltitude);
    }
}
