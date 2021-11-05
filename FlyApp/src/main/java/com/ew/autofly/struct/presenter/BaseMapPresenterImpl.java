package com.ew.autofly.struct.presenter;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.esri.core.geometry.Point;
import com.ew.autofly.R;
import com.ew.autofly.application.EWApplication;
import com.ew.autofly.config.SharedConfig;
import com.ew.autofly.constant.AppConstant;
import com.ew.autofly.db.entity.MissionBase;
import com.ew.autofly.db.entity.MissionPhoto;
import com.ew.autofly.db.helper.MissionHelper;
import com.ew.autofly.db.helper.MissionPhotoDbHelper;
import com.ew.autofly.dialog.common.GoHomeDialog;
import com.ew.autofly.entity.AirRouteParameter;
import com.ew.autofly.entity.FlyCheckStatus;
import com.ew.autofly.entity.LocationCoordinate;
import com.ew.autofly.entity.WayPointInfo;
import com.ew.autofly.event.BatteryStateEvent;
import com.ew.autofly.event.flight.AircraftAttitudeEvent;
import com.ew.autofly.event.flight.LocationStateEvent;
import com.ew.autofly.internal.common.CheckError;
import com.ew.autofly.internal.key.base.FlyKeyManager;
import com.ew.autofly.key.cache.FlyFlightControllerKey;
import com.ew.autofly.model.AccessoryManager;
import com.ew.autofly.model.AircraftManager;
import com.ew.autofly.model.BatteryManager;
import com.ew.autofly.model.CameraManager;
import com.ew.autofly.model.FlightRecordManager;
import com.ew.autofly.model.GimbalManager;
import com.ew.autofly.model.RTKManager;
import com.ew.autofly.model.VideoRecordManager;
import com.ew.autofly.model.VisionManager;
import com.ew.autofly.model.WayPointManager;
import com.ew.autofly.model.mission.CheckConditionManager;
import com.ew.autofly.module.flightrecord.FlightPlatformManager;
import com.ew.autofly.entity.flightRecord.FlightUploadStates;
import com.ew.autofly.module.setting.cache.FlySettingKey;
import com.ew.autofly.struct.presenter.interfaces.IBaseCameraAction;
import com.ew.autofly.struct.presenter.interfaces.IBaseDroneStateCallback;
import com.ew.autofly.struct.presenter.interfaces.IBaseDroneStateCheck;
import com.ew.autofly.struct.presenter.interfaces.IBaseMapPresenter;
import com.ew.autofly.struct.presenter.interfaces.IBaseMissionCheck;
import com.ew.autofly.struct.view.IBaseMapView;
import com.ew.autofly.utils.IOUtils;
import com.ew.autofly.utils.LogUtilsOld;
import com.ew.autofly.utils.MyUtils;
import com.ew.autofly.utils.SoundPlayerUtil;
import com.flycloud.autofly.base.util.SysUtils;
import com.ew.autofly.utils.coordinate.LocationCoordinateUtils;
import com.flycloud.autofly.base.util.DateHelperUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.greendao.annotation.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import dji.common.accessory.AccessoryAggregationState;
import dji.common.battery.BatteryCellVoltageLevel;
import dji.common.battery.BatteryState;
import dji.common.camera.ExposureSettings;
import dji.common.camera.SettingsDefinitions;
import dji.common.camera.StorageState;
import dji.common.camera.SystemState;
import dji.common.camera.ThermalMeasurementMode;
import dji.common.error.DJIError;
import dji.common.flightcontroller.FlightControllerState;
import dji.common.flightcontroller.FlightMode;
import dji.common.flightcontroller.GoHomeAssessment;
import dji.common.flightcontroller.PositioningSolution;
import dji.common.flightcontroller.RTKState;
import dji.common.flightcontroller.SmartRTHState;
import dji.common.flightcontroller.simulator.InitializationData;
import dji.common.gimbal.GimbalMode;
import dji.common.gimbal.GimbalState;
import dji.common.mission.waypoint.Waypoint;
import dji.common.mission.waypoint.WaypointAction;
import dji.common.mission.waypoint.WaypointActionType;
import dji.common.mission.waypoint.WaypointExecutionProgress;
import dji.common.mission.waypoint.WaypointMission;
import dji.common.mission.waypoint.WaypointMissionDownloadEvent;
import dji.common.mission.waypoint.WaypointMissionExecuteState;
import dji.common.mission.waypoint.WaypointMissionExecutionEvent;
import dji.common.mission.waypoint.WaypointMissionState;
import dji.common.mission.waypoint.WaypointMissionUploadEvent;
import dji.common.mission.waypoint.WaypointUploadProgress;
import dji.common.model.LocationCoordinate2D;
import dji.common.product.Model;
import dji.common.remotecontroller.HardwareState;
import dji.common.util.CommonCallbacks;
import dji.keysdk.FlightControllerKey;
import dji.keysdk.KeyManager;
import dji.keysdk.callback.KeyListener;
import dji.sdk.camera.Camera;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.flightcontroller.Simulator;
import dji.sdk.gimbal.Gimbal;
import dji.sdk.media.MediaFile;
import dji.sdk.mission.MissionControl;
import dji.sdk.mission.waypoint.WaypointMissionOperator;
import dji.sdk.mission.waypoint.WaypointMissionOperatorListener;
import dji.sdk.products.Aircraft;
import dji.sdk.useraccount.UserAccountManager;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;


public abstract class BaseMapPresenterImpl<V extends IBaseMapView> extends BaseFlightPresenterImpl<V>
        implements IBaseMapPresenter, IBaseCameraAction, IBaseDroneStateCallback, IBaseDroneStateCheck,
        IBaseMissionCheck, WaypointMissionOperatorListener {

    protected CompositeDisposable mRxDisposable;

    private AtomicBoolean isAlreadyUpdateMissionStart = new AtomicBoolean(false);
    private AtomicBoolean isAlreadyUpdateMissionEnd = new AtomicBoolean(false);

    private boolean isRtkEnable;
    private boolean isRtkSignalReady;
    private boolean isHeadingValid;
    private boolean isRtkBeingUsed;
    private boolean isRTkLocationValid;
    private boolean isShootingPhoto;

    protected PositioningSolution mPositioningSolution;

  
    public AppConstant.OperateAction operateAction = AppConstant.OperateAction.Unknown;

    protected double homeLocationLatitude;
    protected double homeLocationLongitude;
    protected float homeLocationAltitude;
    protected double aircraftLocationLatitude;
    protected double aircraftLocationLongitude;
    protected float aircraftLocationAltitude;
    protected float aircraftRelativeAltitude;
    protected double aircraftYaw = 0;

    protected double mRTKBaseLatitude;
    protected double mRTKBaseLongitude;
    protected float mRTKBaseAltitude;

  
    protected int mTotalFlyTaskPhotoNumber = 0;
  
    protected double mTotalFlyTime = 0;

    protected double mDistanceX;
    protected double mHorizontalSpeed;
    protected double mVerticalSpeed;
    protected long mRunTime;

    private int mSatelliteCount = 0;

    protected String mFlightModeString = "";
    public double mRemainPower = 0.0;
    private BatteryCellVoltageLevel mCellVoltageLevel;
    protected float mBatteryTemperature;
    protected long mSdcardRemainSize = 0;
    protected long mExposureNum = 0;
    protected long startTimeMillisecond = 0;

    private float mThermalTemperature;
    private boolean isThermalCamera;
    private ThermalMeasurementMode mThermalMeasurementMode;
    private String mCameraMode = "";
    private String mCameraExposureCompensation = "";
    private int mCameraOpticalZoomFocalLength = 0;

  
    protected int mCurrentPhotoIndex = 0;

    protected SharedConfig config;
    protected AirRouteParameter mBaseAirRoutePara;
    protected MissionBase mMissionBase;

  
    protected SystemState mCameraState = null;
    protected GimbalState mGimbalState = null;
    protected HardwareState mHardwareState = null;
    protected BatteryState mBatteryState = null;

    protected WaypointMissionOperator mWaypointMissionOperator;
    protected WaypointMission mDJIWayPointMission;
    protected List<Waypoint> mWaypointList = new ArrayList<>();
    protected List<WayPointInfo> mWayPointTaskList = new ArrayList<>();

    protected LogUtilsOld log;

    private VideoRecordManager mVideoRecordManager;

    private float mMaxMissionHeight;

    public BaseMapPresenterImpl() {
        log = LogUtilsOld.getInstance(getAppContext());
        config = new SharedConfig(getAppContext());
        mBaseAirRoutePara = initAirRouterParameter(config.getCameraName());
    }

    @Override
    public void attachView(V view) {
        super.attachView(view);
        VisionManager.getInstance().init();
        RTKManager.getInstance().init();
        BatteryManager.getInstance().init();
        mVideoRecordManager = new VideoRecordManager();
        mRxDisposable = new CompositeDisposable();

        FlightPlatformManager.getInstance().init();
        FlightPlatformManager.getInstance().startService();
    }

    @Override
    public void detachView() {
        super.detachView();
        VisionManager.getInstance().release();
        RTKManager.getInstance().release();
        BatteryManager.getInstance().release();
        if (mRxDisposable != null) {
            mRxDisposable.clear();
        }

        FlightPlatformManager.getInstance().destroy();
    }

    public void addRxDisposable(Disposable disposable) {
        if (mRxDisposable != null) {
            mRxDisposable.add(disposable);
        }
    }

    @Override
    public void initAirCraftStateCallback() {
        super.initAirCraftStateCallback();
        initWayPointMissionOperator();
        RTKManager.initNetWorkRtkService();
        updateAircraftParams();
        FlightRecordManager.getInstance().init();
        installAccessoryAggregation();
    }

    @Override
    public void removeAircraftStateCallback() {
        super.removeAircraftStateCallback();
        AccessoryManager.destroyAccessoryAggregation();
    }

    @Override
    public abstract AirRouteParameter initAirRouterParameter(String cameraName);

    protected void updateAircraftParams() {

        Aircraft aircraft = AircraftManager.getAircraft();

      
        if (aircraft != null) {
            Model model = aircraft.getModel();
            if (model != null) {
                String modelName = model.getDisplayName();
                Camera camera = CameraManager.getCamera();

                String cameraName = "";

                if (camera != null) {
                    cameraName = camera.getDisplayName();
                }

                if (mBaseAirRoutePara == null) {
                    mBaseAirRoutePara = new AirRouteParameter(cameraName);
                } else {
                    mBaseAirRoutePara.initializeCameraParams(cameraName);
                }
                config.setCameraName(cameraName);
                config.setAirDraftModel(modelName);
            }
        }
    }

    private void initWayPointMissionOperator() {
        if (isAirCraftConnect() && MissionControl.getInstance() != null) {
            mWaypointMissionOperator = MissionControl.getInstance().getWaypointMissionOperator();
            if (mWaypointMissionOperator != null) {
                mWaypointMissionOperator.removeListener(this);
                mWaypointMissionOperator.addListener(this);
            }
        }
    }

    private void installAccessoryAggregation() {
        AccessoryManager.initAccessoryAggregation(new AccessoryAggregationState.Callback() {
            @Override
            public void onUpdate(AccessoryAggregationState accessoryAggregationState) {
                mUIThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        updateAccessoryAggregationView(accessoryAggregationState);
                    }
                });
            }
        });

        updateAccessoryAggregationView(AccessoryManager.getAccessoryAggregationState());
    }

    private void updateAccessoryAggregationView(AccessoryAggregationState accessoryAggregationState) {
        if (getView() != null) {
            if (accessoryAggregationState == null) {
                getView().dismissAccessoryAggregationView();
                return;
            }

            getView().showSpotlight(accessoryAggregationState.isSpotlightConnected());
            getView().showSpeaker(accessoryAggregationState.isSpeakerConnected());
            getView().showBeacon(accessoryAggregationState.isBeaconConnected());
        }
    }

    @Override
    public AirRouteParameter getAirRouteParameter() {
        return mBaseAirRoutePara;
    }

    public boolean isRtkEnable() {
        return isRtkEnable;
    }

    public boolean isRtkSignalReady() {
        return isRtkSignalReady;
    }

    public boolean isHeadingValid() {
        return isHeadingValid;
    }

    public boolean isRTkLocationValid() {
        return isRTkLocationValid;
    }

    public void setRtkSignalReady(boolean rtkSignalReady) {
        isRtkSignalReady = rtkSignalReady;
    }

    public float getMaxMissionHeight() {
        return mMaxMissionHeight;
    }

    public void setMaxMissionHeight(float maxMissionHeight) {
        mMaxMissionHeight = maxMissionHeight;
    }

    public String getCameraModeString() {
        return mCameraMode;
    }

    @Override
    public CheckError checkMission() {

        CheckError error = null;

        mTotalFlyTime = prepareMission();

        int i = 0;
        mMaxMissionHeight = 0;
        if (mWaypointList != null && !mWaypointList.isEmpty()) {
            for (Waypoint waypoint : mWaypointList) {
                if (i == 0) {
                    mMaxMissionHeight = waypoint.altitude;
                } else if (mMaxMissionHeight < waypoint.altitude) {
                    mMaxMissionHeight = waypoint.altitude;
                }
                i++;
            }
        } else {
            error = new CheckError("任务航点列表为空");
        }

        if (isViewAttached()) {
            getView().updateBottomMissionInfo(mTotalFlyTime);
        }

        return error;

    }

    @Override
    public void uploadMission() {

        if (mWaypointMissionOperator.getCurrentState().equals(WaypointMissionState.READY_TO_UPLOAD) ||
                mWaypointMissionOperator.getCurrentState().equals(WaypointMissionState.READY_TO_RETRY_UPLOAD) ||
                mWaypointMissionOperator.getCurrentState().equals(WaypointMissionState.READY_TO_EXECUTE)) {

          
            if (mDJIWayPointMission != null && mDJIWayPointMission.getWaypointList() != null) {
                mDJIWayPointMission.getWaypointList().clear();
            }

            buildMission();

            addResetGimbalAction();

            setGimbalMode(GimbalMode.YAW_FOLLOW);

            if (mWaypointMissionOperator == null)
                mWaypointMissionOperator = MissionControl.getInstance().getWaypointMissionOperator();

            mWaypointMissionOperator.clearMission();

            DJIError djiError = mDJIWayPointMission.checkParameters();
            if (djiError != null) {
                showToastDialog(djiError.getDescription());
                sendUploadErrorBroadcast("检查任务参数错误：" + djiError.getDescription());
                return;
            }

            DJIError djiError2 = mWaypointMissionOperator.loadMission(mDJIWayPointMission);
            if (djiError2 != null) {
                showToastDialog(djiError2.getDescription());
                sendUploadErrorBroadcast("加载任务错误：" + djiError2.getDescription());
                return;
            }

            mWaypointMissionOperator.uploadMission(new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(final DJIError djiError) {
                    if (djiError != null) {
                        sendUploadErrorBroadcast(djiError.getDescription());
                    }
                }
            });
        } else {
            showToast("航点正在上传中……");
        }
    }

    @Override
    public void cancelUploadMission() {
        if (mWaypointMissionOperator.getCurrentState().equals(WaypointMissionState.UPLOADING)) {
            if (mWaypointMissionOperator == null)
                mWaypointMissionOperator = MissionControl.getInstance().getWaypointMissionOperator();

            mWaypointMissionOperator.cancelUploadingMission(new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError != null) {
                        showToast("取消上传航点失败" + djiError.getDescription());
                    }
                }
            });
        }
    }

    private void sendUploadErrorBroadcast(String errorMsg) {
        Intent intent = new Intent(AppConstant.BROADCAST_UPLOAD_MISSION);
        intent.putExtra(AppConstant.BROADCAST_UPLOAD_MISSION_ERROR, errorMsg);
        getAppContext().sendBroadcast(intent);
    }


    private void addResetGimbalAction() {
        Waypoint waypoint = mDJIWayPointMission.getWaypointList().get(mDJIWayPointMission.getWaypointCount() - 1);
        waypoint.addAction(new WaypointAction(WaypointActionType.GIMBAL_PITCH, 0));
    }

    @Override
    public void startMission() {
        mWaypointMissionOperator.startMission(new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError result) {
                if (result != null) {
                    operateAction = AppConstant.OperateAction.Unknown;
                    String resultStr = "起飞的返回值为：" + result.getDescription();
                    showToastDialog(resultStr);
                } else {
                    operateAction = AppConstant.OperateAction.ExecuteTask;
                    startTimeMillisecond = new Date().getTime();
                    mExposureNum = 0;
                    isAlreadyUpdateMissionStart.set(false);
                    isAlreadyUpdateMissionEnd.set(false);
                    refreshFlightButton(AppConstant.FlightAction.StartTask);
                    if (isViewAttached()) {
                        mUIThreadHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                getView().showFlightSuccess();
                                onStartMissionSuccess();
                            }
                        });
                    }
                }
            }
        });
    }

    protected void onStartMissionSuccess() {
        FlightRecordManager.getInstance().endRecord();
        FlightRecordManager.getInstance().createNewRecord(getAirCraftLocation(), mMissionBase);
    }

    @Override
    public void pauseMission() {
        if (mWaypointMissionOperator == null)
            mWaypointMissionOperator = MissionControl.getInstance().getWaypointMissionOperator();
        mWaypointMissionOperator.pauseMission(new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                if (djiError == null) {
                    showToast("暂停任务成功");

                    refreshFlightButton(AppConstant.FlightAction.PauseTask);

                } else {
                    String resultString = "暂停任务失败，失败原因：" + djiError.getDescription();
                    showToastDialog(resultString);
                }
            }
        });
    }

    @Override
    public void resumeMission() {
        if (mWaypointMissionOperator == null)
            mWaypointMissionOperator = MissionControl.getInstance().getWaypointMissionOperator();
        mWaypointMissionOperator.resumeMission(new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                if (djiError == null) {
                    showToast("继续任务成功");

                    refreshFlightButton(AppConstant.FlightAction.ResumeTask);

                } else {
                    String resultString = "继续任务失败：" + djiError.getDescription();
                    showToastDialog(resultString);
                }
            }
        });
    }

    @Override
    public void finishMission() {
        startTimeMillisecond = 0;
        stopTakePhoto();
        stopRecord();
    }

    @Override
    public void goHome() {

        if (isGoingHome()) {
            return;
        }
        FlightController flightController = AircraftManager.getFlightController();
        if (flightController != null) {
            flightController.startGoHome(new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError result) {
                    if (result == null) {
                        operateAction = AppConstant.OperateAction.GoHome;
                        showToast("一键返航成功");
                        refreshFlightButton(AppConstant.FlightAction.GoHomeTask);
                        finishMission();
                    } else {
                        String resultString = "一键返航失败，失败原因：" + result.getDescription();
                        showToastDialog(resultString);
                    }
                }
            });
        }
    }

    @Override
    public boolean checkAirCraftConnect() {

        if (!isAirCraftConnect()) {
            showToast("请连接飞机");
            return false;
        }
        return true;
    }

    @Override
    public boolean checkCompassOk() {
        Aircraft mProduct = EWApplication.getAircraftInstance();
        return (mProduct != null && mProduct.getFlightController() != null
                && !mProduct.getFlightController().getCompass().hasError());
    }

    @Override
    public boolean checkTaskFree() {
        return checkTaskFree(true);
    }

    @Override
    public boolean checkTaskFree(boolean showMsg) {
        if (operateAction == AppConstant.OperateAction.UploadingWayPoint) {
            if (showMsg) {
                showToast("正在上传航点，请稍候");
            }
            return false;
        } else if (operateAction != AppConstant.OperateAction.Unknown
                && operateAction != AppConstant.OperateAction.FinishTask) {
            if (showMsg) {
                showToast("飞机正在执行任务");
            }
            return false;
        }
        return true;
    }

    @Override
    public CheckError checkWaypointParams() {

        CheckError checkError = null;

        if (mWaypointList != null) {
            Iterator<Waypoint> waypointIterator = mWaypointList.iterator();
            Waypoint tempWp = null;
            int i = 0;
            while (waypointIterator.hasNext()) {

                String indexError = "(航点id为" + i + ")";

                Waypoint next = waypointIterator.next();
                if (next != null) {

                    if (i == 0) {

                        if (next.altitude <= 0) {
                            checkError = new CheckError(getString(R.string.error_waypoint_first_altitude_less_than_zero));
                            return checkError;
                        }
                    }

                    if (tempWp != null) {
                        if (!WayPointManager.checkSuitableDistance(next, tempWp)) {
                            StringBuilder wpSb = new StringBuilder();
                            wpSb.append("[航点坐标：").append(next.coordinate.getLatitude()).append(",")
                                    .append(next.coordinate.getLongitude()).append(",")
                                    .append(next.altitude).append("]");
                            checkError = new CheckError(getString(R.string.error_waypoint_distance_too_close)
                                    + indexError + wpSb.toString());
                            return checkError;
                        }
                    }

                    tempWp = next;

                    if (next.waypointActions != null) {
                        Iterator<WaypointAction> actionIterator = next.waypointActions.iterator();
                        while (actionIterator.hasNext()) {
                            WaypointAction waypointAction = (WaypointAction) actionIterator.next();
                            if ((waypointAction.actionType == WaypointActionType.ROTATE_AIRCRAFT)
                                    && ((waypointAction.actionParam > Waypoint.MAX_HEADING)
                                    || (waypointAction.actionParam < Waypoint.MIN_HEADING))) {
                                checkError = new CheckError(getString(R.string.error_waypoint_rotate_aircraft_action_not_valid)
                                        + indexError);
                                return checkError;
                            } else if (waypointAction.actionType == WaypointActionType.GIMBAL_PITCH &&
                                    !GimbalManager.isAppropriateWaypointPitch(waypointAction.actionParam)) {
                                checkError = new CheckError(getString(R.string.error_waypoint_gimbal_pitch_not_valid)
                                        + indexError);
                                return checkError;
                            } else if (waypointAction.actionType == WaypointActionType.CAMERA_ZOOM) {
                                if (!CameraManager.isZoomCamera()) {
                                    checkError = new CheckError(getString(R.string
                                            .error_waypoint_camera_zoom_not_support)
                                            + indexError);
                                } else if (!CameraManager.isAppropriateCameraZoom(waypointAction.actionParam)) {
                                    checkError = new CheckError(getString(R.string
                                            .error_waypoint_camera_zoom_not_vaild)
                                            + indexError);
                                }

                                return checkError;
                            }
                        }
                    }
                }

                i++;
            }
        }

        return null;
    }

    @Override
    public void checkPitchRangeExtension(@NonNull CheckConditionManager checkConditionManager) {

        if (AircraftManager.isP4R()) {

            Gimbal gimbal = GimbalManager.getGimbal();
            if (gimbal != null) {

                boolean needToCheckPitchRangeExtension = false;
                if (mWaypointList != null) {
                    for (Waypoint waypoint : mWaypointList) {
                        if (waypoint.waypointActions != null) {
                            for (WaypointAction waypointAction : waypoint.waypointActions) {
                                if (waypointAction.actionType == WaypointActionType.GIMBAL_PITCH
                                        && waypointAction.actionParam > GimbalManager.GIMBAL_PITCH_MAX) {
                                    needToCheckPitchRangeExtension = true;
                                    break;
                                }
                            }
                        }
                    }
                }

                if (needToCheckPitchRangeExtension) {

                    ObservableOnSubscribe<Boolean> subscribe = new ObservableOnSubscribe<Boolean>() {
                        @Override
                        public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {

                            gimbal.getPitchRangeExtensionEnabled(new CommonCallbacks.CompletionCallbackWith<Boolean>() {
                                @Override
                                public void onSuccess(Boolean aBoolean) {

                                    mUIThreadHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (aBoolean) {
                                                emitter.onComplete();
                                            } else {
                                                CheckError checkError = new CheckError(
                                                        getString(R.string.error_waypoint_gimbal_pitch_beyond_range_extension));
                                                checkConditionManager.showCheckResult(checkError, emitter);

                                            }
                                        }
                                    });
                                }

                                @Override
                                public void onFailure(DJIError djiError) {
                                    mUIThreadHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            emitter.onComplete();
                                        }
                                    });
                                }
                            });
                        }
                    };

                    checkConditionManager.addCheckConditionSubscribe(subscribe);
                }
            }
        }
    }


    @Override
    public CheckError checkGetHomePoint() {
        CheckError checkError = null;
        if (!LocationCoordinateUtils.checkGpsCoordinate(homeLocationLatitude, homeLocationLongitude)) {
            SoundPlayerUtil.getInstance(getAppContext()).playSound(R.raw.no_home_point, 0);
            checkError = new CheckError("未定位到Home点位置");
        }

        return checkError;
    }

    @Override
    public CheckError checkDJIAccountState() {

        CheckError checkError = null;
        switch (UserAccountManager.getInstance().getUserAccountState()) {
            case AUTHORIZED:
            case NOT_AUTHORIZED:
                break;
            case TOKEN_OUT_OF_DATE:
                checkError = new CheckError("登录过期，请重新登录大疆账号");
                break;
            case NOT_LOGGED_IN:
                checkError = new CheckError("请先登录大疆账号");
                break;
            case UNKNOWN:
                checkError = new CheckError("请先登录大疆账号");
                break;
        }

        return checkError;
    }

    @Override
    public CheckError checkMaxFlyHeight() {

        CheckError error = null;

        int maxHeight;
        Object value = KeyManager.getInstance().getValue(FlightControllerKey.create(FlightControllerKey.MAX_FLIGHT_HEIGHT));
        if (value instanceof Integer) {
            maxHeight = (int) value;
        } else {
            error = new CheckError(getString(R.string.error_get_max_flight_height));
            return error;
        }

        float maxFlyHeight = getMaxMissionHeight();

        if (maxFlyHeight > 500) {
            error = new CheckError(getString(R.string.beyond_mission_flight_height_limit));
        } else if (maxFlyHeight > maxHeight) {
            error = new CheckError(String.format(getString(R.string.check_max_flight_height_failed), maxFlyHeight, maxHeight));
        }

        return error;
    }


    @Override
    public CheckError checkGoHomeFlyHeight() {

        CheckError error = null;

        float maxFlyHeight = getMaxMissionHeight();

        int goHomeHeight;

        Object value = KeyManager.getInstance().getValue(FlightControllerKey.create(FlightControllerKey.GO_HOME_HEIGHT_IN_METERS));

        if (value instanceof Integer) {
            goHomeHeight = (int) value;
        } else {
            error = new CheckError(getString(R.string.error_get_go_home_height));
            return error;
        }

        if (maxFlyHeight > goHomeHeight) {
            error = new CheckError(String.format(getString(R.string.check_go_home_height), maxFlyHeight, goHomeHeight));
            error.setIgnoreError(true);
        }

        return error;
    }

    @Override
    public CheckError checkEnoughFlyTimeAndBattery() {

        CheckError checkError = null;

        if (mTotalFlyTime == 0) {
            checkError = new CheckError("预计飞行时长为0分钟，不能执行此任务");
            return checkError;
        }

        float maxTime = BatteryManager.getBatteryLifeTime();
        if (mTotalFlyTime > maxTime) {
            checkError = new CheckError("预计飞行时长超过" + (int) maxTime + "分钟，不能执行此任务");
            return checkError;
        }






        if (this.mRemainPower < BatteryManager.MIN_MISSION_POWER) {
            checkError = new CheckError("剩余电量低于" + BatteryManager.MIN_MISSION_POWER + "%，请更换电池");
            return checkError;
        }
        return checkError;
    }

    @Override
    public boolean isHasCamera() {
        return CameraManager.getCamera() != null;
    }

    @Override
    public boolean checkHasCamera() {
        /*if (!isHasCamera()) {
            showToastDialog("未检测到云台相机");
            return false;
        }*/
        return true;
    }

    @Override
    public boolean checkMultiCamera() {
        Aircraft aircraft = AircraftManager.getAircraft();
        if (aircraft != null && aircraft.getCameras() != null) {
            int cameraSize = aircraft.getCameras().size();
            return cameraSize > 1;
        }
        return false;
    }

    @NonNull
    @Override
    public LocationCoordinate getAirCraftHomeLocation() {
        return new LocationCoordinate(homeLocationLatitude, homeLocationLongitude, homeLocationAltitude);
    }

    @NonNull
    @Override
    public LocationCoordinate getAirCraftLocation() {
        return new LocationCoordinate(aircraftLocationLatitude, aircraftLocationLongitude, aircraftLocationAltitude);
    }

    @NonNull
    @Override
    public LocationCoordinate getRTKBaseLocation() {
        return new LocationCoordinate(mRTKBaseLatitude, mRTKBaseLongitude, mRTKBaseAltitude);
    }

    @Override
    public FlyCheckStatus getFlyCheckStatus() {
        FlyCheckStatus flyCheckStatus = new FlyCheckStatus();
        flyCheckStatus.setConnected(isAirCraftConnect());
        flyCheckStatus.setCompassOk(checkCompassOk());
        flyCheckStatus.setFlightModeString(mFlightModeString);
        flyCheckStatus.setSatelliteCount(mSatelliteCount);
        flyCheckStatus.setHandsetStorage(IOUtils.getAvailaleSize());
        flyCheckStatus.setCellVoltageLevel(mCellVoltageLevel);
        flyCheckStatus.setRemainPower(mRemainPower);
        flyCheckStatus.setSdcardRemainSize(mSdcardRemainSize);
        flyCheckStatus.setTotalFlyTaskPhotoNumber(mTotalFlyTaskPhotoNumber);
        return flyCheckStatus;
    }

    private Simulator getSimulator() {
        if (checkAirCraftConnect()) {
            Aircraft aircraft = EWApplication.getAircraftInstance();
            if (aircraft != null && aircraft.getFlightController() != null) {
                return aircraft.getFlightController().getSimulator();
            }
        }
        return null;
    }

    @Override
    public void startSimulate(@NotNull LocationCoordinate locationCoordinate) {

        final Simulator simulator = getSimulator();
        if (simulator != null) {
            final LocationCoordinate2D simulateLoc = new LocationCoordinate2D(locationCoordinate.latitude, locationCoordinate.longitude);
            final InitializationData data = InitializationData.createInstance(simulateLoc, 10, 16);
            showLoading(true, "开始模拟");
            if (simulator.isSimulatorActive()) {
                simulator.stop(new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        showLoading(false, null);
                        if (djiError != null) {
                            showToast("开始模拟失败" + djiError.getDescription());
                        } else {
                            startSimulate(simulator, data);
                        }
                    }
                });
            } else {
                startSimulate(simulator, data);
            }
        }
    }

    private void startSimulate(Simulator simulator, InitializationData data) {
        simulator.start(data, new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                showLoading(false, null);
                if (djiError != null) {
                    showToast("开始模拟失败" + djiError.getDescription());
                } else {
                    showToast("开始模拟成功");
                }
            }
        });
    }

    @Override
    public void stopSimulate() {
        final Simulator simulator = getSimulator();
        if (simulator != null) {
            simulator.stop(new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    showLoading(false, null);
                    if (djiError != null) {
                        showToast("停止模拟失败" + djiError.getDescription());
                    } else {
                        showToast("停止模拟成功");
                    }
                }
            });
        }
    }

    @Override
    public void onFlightControllerStateCallback(FlightControllerState state) {
        super.onFlightControllerStateCallback(state);
        if (!isRtkSignalReady()) {
            aircraftLocationLatitude = state.getAircraftLocation().getLatitude();
            aircraftLocationLongitude = state.getAircraftLocation().getLongitude();
        }
        if (!LocationCoordinateUtils.checkGpsCoordinate(aircraftLocationLatitude, aircraftLocationLongitude)
                || !LocationCoordinateUtils.checkGpsCoordinate(homeLocationLatitude, homeLocationLongitude)) {
            homeLocationLatitude = state.getHomeLocation().getLatitude();
            homeLocationLongitude = state.getHomeLocation().getLongitude();
          
            updateHomeLocation();
            return;
        }

        if (!state.areMotorsOn() && !state.isFlying()) {
            homeLocationLatitude = aircraftLocationLatitude;
            homeLocationLongitude = aircraftLocationLongitude;
            homeLocationAltitude = aircraftLocationAltitude;
            updateHomeLocation();
            FlightRecordManager.getInstance().endRecord();
        }

        aircraftRelativeAltitude = state.getAircraftLocation().getAltitude();

      
        if (AircraftManager.isP4R()) {
            aircraftYaw = state.getAttitude().yaw;
        }

        if (!isRtkSignalReady()) {
            aircraftLocationAltitude = state.getAircraftLocation().getAltitude();
            aircraftYaw = state.getAttitude().yaw;
        }

        mSatelliteCount = state.getSatelliteCount();
      
        mFlightModeString = state.getFlightMode().toString();

        mDistanceX = LocationCoordinateUtils.getDistance(aircraftLocationLatitude, aircraftLocationLongitude, homeLocationLatitude, homeLocationLongitude);
        if (startTimeMillisecond > 0) {
            long now = new Date().getTime();
            mRunTime = (now - startTimeMillisecond) / 1000;
        }
        final float velocityX = state.getVelocityX();
        final float velocityY = state.getVelocityY();
        final float velocityZ = state.getVelocityZ();
      
        mHorizontalSpeed = Math.sqrt(Math.pow(velocityX, 2) + Math.pow(velocityY, 2));
      
        mVerticalSpeed = Math.abs(velocityZ);
      
        mUIThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                updateView();
            }
        });

        onFlightModeChange(state.getFlightMode());

        FlyKeyManager.getInstance().setValue(mFlightControllerKey, getAirCraftLocation());

        EventBus.getDefault().post(new LocationStateEvent(isRtkSignalReady(), getAirCraftLocation(), getRTKBaseLocation()));

        AircraftAttitudeEvent aircraftAttitudeEvent = new AircraftAttitudeEvent();
        aircraftAttitudeEvent.setYaw(aircraftYaw);
        EventBus.getDefault().post(aircraftAttitudeEvent);

        FlightRecordManager.getInstance().saveDetailRecord(getAirCraftLocation(), getAirCraftHomeLocation(), state, mHardwareState, mBatteryState);
      /*  FlightRecordManager.getInstance().uploadFlightMonitorInfo(RouteService.getUserName(), getAirCraftLocation(),
                mRunTime, mDistanceX, state, mHardwareState, mBatteryState, mGimbalState);*/

        FlightUploadStates uploadStates = new FlightUploadStates();
        uploadStates.setFlightTime(mRunTime);
        uploadStates.setFlightDistance(mDistanceX);
        uploadStates.setFlightControllerState(state);
        uploadStates.setBatteryState(mBatteryState);
        uploadStates.setGimbalState(mGimbalState);
        uploadStates.setHardwareState(mHardwareState);
        uploadStates.setAircraftLocation(new LocationCoordinate(aircraftLocationLatitude,aircraftLocationLongitude,aircraftRelativeAltitude));
        uploadStates.setAircraftSerialNumber(mAirCraftSerialNumber);
        uploadStates.setBatterySerialNumber(mBatterySerialNumber);

        FlightPlatformManager.getInstance().senData(uploadStates.getFlightUploadData());

    }

    protected void updateView() {
        if (isViewAttached()) {
            getView().updateDroneLocation(getAirCraftLocation(), (float) aircraftYaw);

            if (LocationCoordinateUtils.checkGpsCoordinate(getAirCraftHomeLocation())) {
                getView().updateHomeLocation(getAirCraftHomeLocation());
                getView().updateLineBetweenDroneAndHome(getAirCraftHomeLocation(), getAirCraftLocation());
            }

            getView().updateMonitorInfo(getMonitorFlightInfo());
        }
    }

    private void updateHomeLocation() {
        mUIThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (isViewAttached()) {
                    if (LocationCoordinateUtils.checkGpsCoordinate(getAirCraftHomeLocation())) {
                        getView().updateHomeLocation(getAirCraftHomeLocation());
                    }
                }

            }
        });
    }


    @Override
    public String getMonitorFlightInfo() {

        StringBuffer sbMonitorInfo = new StringBuffer();

        if (AircraftManager.isRtkAircraft()) {
            sbMonitorInfo.append("RTK定位: ").append(getRTKPositioningSolution()).append("\n");
        }

      
      
        sbMonitorInfo.append("云台角度: ").append(String.format("%.0f°", getGimbalPitch())).append("\n");
        sbMonitorInfo.append("飞行高度: ").append(String.format("%.2fm", aircraftRelativeAltitude)).append("\n");
        sbMonitorInfo.append("水平距离: ").append(String.format("%.2fm", mDistanceX)).append("\n");
        sbMonitorInfo.append("水平速度: ").append(String.format("%.2fm/s", mHorizontalSpeed)).append("\n");
        sbMonitorInfo.append("垂直速度: ").append(String.format("%.2fm/s", mVerticalSpeed)).append("\n");
        sbMonitorInfo.append("飞行时长: ").append(String.format("%.2f", mRunTime / 60.0f)).append("min\n");
        if (mBaseAirRoutePara.getActionMode() == AppConstant.ACTION_MODE_PHOTO) {
            sbMonitorInfo.append("曝光数量: ").append(mExposureNum).append("\n");
        }
        if (CameraManager.isSupportCameraOpticalZoom()) {
            if (mCameraOpticalZoomFocalLength == 0) {
                sbMonitorInfo.append("光学焦距: ").append("N/A").append("\n");
            } else {
                sbMonitorInfo.append("光学焦距: ").append(
                        String.format("%.2f", mCameraOpticalZoomFocalLength / 10.0f) + "mm").append("\n");
            }
        }
        sbMonitorInfo.append("曝光补偿: ").append(mCameraExposureCompensation).append("\n");
        sbMonitorInfo.append("红外相机: ").append(isThermalCamera?"是":"否").append("\n");
        sbMonitorInfo.append("测温模式: ").append(mThermalMeasurementMode==null?"未知":mThermalMeasurementMode.toString()).append("\n");
        sbMonitorInfo.append("红外温度: ").append(String.format("%.2f°C", mThermalTemperature)).append("\n");

        sbMonitorInfo.append("相机模式: ").append(mCameraMode);

        return sbMonitorInfo.toString();
    }

    protected String getRTKPositioningSolution() {
        String ps = "无定位";

        if (isRtkSignalReady()) {
            if (mPositioningSolution != null) {
                switch (mPositioningSolution) {
                    case NONE:
                        ps = "无定位";
                        break;
                    case SINGLE_POINT:
                        ps = "单点解";
                        break;
                    case FLOAT:
                        ps = "浮点解";
                        break;
                    case FIXED_POINT:
                        ps = "固定解";
                        break;
                    default:
                        ps = "未知";
                        break;
                }
            }
        } else {
            if (!isRtkBeingUsed()) {
                ps = "未使用";
            } else if (!isRTkLocationValid) {
                ps = "无定位";
            }
        }

        return ps;
    }

    protected String getRTKMonitorFlightInfoAll() {
        StringBuffer sbMonitorInfo = new StringBuffer();

        sbMonitorInfo.append("RTK正常: ").append(isRtkSignalReady() ? "是" : "否").append("\n");
      
        if (AircraftManager.isDualAntennaRtkAircraft()) {
            sbMonitorInfo.append("RTK朝向: ").append(isHeadingValid ? "是" : "否" + "").append("\n");
        }
        sbMonitorInfo.append("RTK定位: ").append(getRTKPositioningSolution());
        if (AircraftManager.isRtkAircraft() && isRtkSignalReady()) {
            sbMonitorInfo.append("\n");
            sbMonitorInfo.append("飞机海拔: ").append(String.format("%.2fm", aircraftLocationAltitude)).append("\n");
            sbMonitorInfo.append("home海拔: ").append(String.format("%.2fm", homeLocationAltitude));
        }

        return sbMonitorInfo.toString();
    }

    protected void onFlightModeChange(FlightMode flightMode) {
        switch (flightMode) {
            case MANUAL:
                break;
            case ATTI:
            case GPS_SPORT:
                operateAction = AppConstant.OperateAction.Unknown;
                refreshFlightButton(AppConstant.FlightAction.Unknown);
                FlightRecordManager.getInstance().endRecord();
                break;
            case AUTO_TAKEOFF:// 起飞
                mExposureNum = 0;
                FlightRecordManager.getInstance().createNewRecord(getAirCraftLocation());
                break;
            case ASSISTED_TAKEOFF:
                FlightRecordManager.getInstance().createNewRecord(getAirCraftLocation());
                break;
            case AUTO_LANDING:// 降落
                startTimeMillisecond = 0;
                operateAction = AppConstant.OperateAction.Unknown;
                break;
            case GO_HOME:
                operateAction = AppConstant.OperateAction.GoHome;
                refreshFlightButton(AppConstant.FlightAction.GoHomeTask);
                break;
        }
    }


    protected void getCameraOpticalZoomFocalLength() {

        if (CameraManager.isSupportCameraOpticalZoom()) {
            if (isHasCamera()) {
                Camera camera = CameraManager.getCamera();
                camera.getOpticalZoomFocalLength(new CommonCallbacks.CompletionCallbackWith<Integer>() {
                    @Override
                    public void onSuccess(Integer focalLength) {
                        mCameraOpticalZoomFocalLength = focalLength;
                    }

                    @Override
                    public void onFailure(DJIError djiError) {

                    }
                });
            }
        }
    }

    public boolean isRtkBeingUsed() {
        return isRtkBeingUsed;
    }

    @Override
    public void onRTKStateCallback(RTKState djiRtkState) {
        super.onRTKStateCallback(djiRtkState);
        LocationCoordinate2D rtkPlaneLocation = djiRtkState.getMobileStationLocation();
        LocationCoordinate2D rtkBaseLocation = djiRtkState.getBaseStationLocation();

      

        mRTKBaseLatitude = rtkBaseLocation.getLatitude();
        mRTKBaseLongitude = rtkBaseLocation.getLongitude();
        mRTKBaseAltitude = djiRtkState.getBaseStationAltitude();


        isRTkLocationValid = rtkPlaneLocation != null
                && rtkPlaneLocation.getLongitude() != 0
                && rtkPlaneLocation.getLatitude() != 0
                && mRTKBaseLatitude != 0
                && mRTKBaseLongitude != 0;

      
        if (isRTkLocationValid) {

            isRtkBeingUsed = true;

            setRtkSignalReady(true);

            aircraftLocationLatitude = rtkPlaneLocation.getLatitude();
            aircraftLocationLongitude = rtkPlaneLocation.getLongitude();
            aircraftLocationAltitude = djiRtkState.getMobileStationAltitude();
            float heading = djiRtkState.getHeading();
            float rotateRealAngle = (heading + 90) % 360;

            if (AircraftManager.isDualAntennaRtkAircraft()) {
                aircraftYaw = (rotateRealAngle >= 180 ? rotateRealAngle - 360 : rotateRealAngle);
            }

        } else {
            isRtkBeingUsed = false;
            setRtkSignalReady(false);
        }


        isHeadingValid = djiRtkState.isHeadingValid();
        mPositioningSolution = djiRtkState.getPositioningSolution();
    }

    @Override
    public void onHardwareStateCallback(@NonNull HardwareState hardwareState) {
        super.onHardwareStateCallback(hardwareState);
        this.mHardwareState = hardwareState;
        CameraManager.shootPhotoAndRecordByRC(hardwareState, mCameraState);
    }

    private FlySettingKey mGimbalModeKey = FlySettingKey.create(FlySettingKey.GIMBAL_MODE);

    @Override
    public void onGimbalStateCallback(@NonNull GimbalState gimbalState) {
        super.onGimbalStateCallback(gimbalState);
        this.mGimbalState = gimbalState;
        if (gimbalState != null && gimbalState.getMode() != null) {
            GimbalMode gimbalMode = gimbalState.getMode();
            /*Object value = YFKeyManager.getInstance().getValue(mGimbalModeKey);
            if (value instanceof GimbalMode) {
                if (!((GimbalMode)value)._equals(gimbalMode.value())){
                    YFKeyManager.getInstance().setValue(mGimbalModeKey, gimbalMode);
                }
            }*/
            FlyKeyManager.getInstance().setValue(mGimbalModeKey, gimbalMode);
        }
    }

    @Override
    public void onBatteryStateStateCallback(BatteryState batteryState) {
        super.onBatteryStateStateCallback(batteryState);
        this.mBatteryState = batteryState;
        mCellVoltageLevel = batteryState.getCellVoltageLevel();
        mRemainPower = batteryState.getChargeRemainingInPercent();
        mBatteryTemperature = batteryState.getTemperature();
        EventBus.getDefault().post(new BatteryStateEvent(batteryState));
    }

    @Override
    public void onCameraSystemStateCallback(@NonNull SystemState systemState) {
        super.onCameraSystemStateCallback(systemState);
        mCameraState = systemState;
        if (systemState.getMode() == SettingsDefinitions.CameraMode.SHOOT_PHOTO) {
            if (systemState.isShootingSinglePhoto()) {
                mCameraMode = "正在拍照";
            } else if (systemState.isShootingBurstPhoto())
                mCameraMode = "正在连拍";
            else if (systemState.isShootingIntervalPhoto())
                mCameraMode = "定时拍照";
            else
                mCameraMode = "拍照模式";
        } else if (systemState.getMode() == SettingsDefinitions.CameraMode.RECORD_VIDEO) {
            if (systemState.isRecording())
                mCameraMode = "正在录像";
            else
                mCameraMode = "视频模式";

            if (mBaseAirRoutePara.getActionMode() == AppConstant.ACTION_MODE_VIDEO) {
                if (systemState.isRecording()) {
                    mVideoRecordManager.startRecordVideoPoint();
                } else if (!systemState.isRecording()) {
                    mVideoRecordManager.finishRecordVideoPoint();
                }
            }
        }
        getCameraOpticalZoomFocalLength();

        if (CameraManager.isNoCameraMediaCallback()) {
            if (systemState.getMode() == SettingsDefinitions.CameraMode.SHOOT_PHOTO) {
                if (systemState.isShootingSinglePhoto()) {
                    if (!isShootingPhoto) {
                        isShootingPhoto = true;
                        handleNoCameraMediaCallbackShootAction();
                    }
                } else {
                    isShootingPhoto = false;
                }
            }
        }

    }

    @Override
    public void onCameraExposureSettingsCallback(@NonNull ExposureSettings exposureSettings) {
        super.onCameraExposureSettingsCallback(exposureSettings);
        mCameraExposureCompensation = MyUtils.getExposureValue(exposureSettings.getExposureCompensation().value());

    }

    @Override
    public void onCameraStorageCallBack(@NonNull StorageState storageState) {
        super.onCameraStorageCallBack(storageState);
        if (mSdcardRemainSize != storageState.getRemainingSpaceInMB()) {
            mSdcardRemainSize = storageState.getRemainingSpaceInMB();
            config.setCameraStorageLocation(storageState.getStorageLocation().name());
          
        }
    }


    @Override
    public void onCameraMediaFileCallback(@NonNull MediaFile mediaFile) {
        super.onCameraMediaFileCallback(mediaFile);
        if (mediaFile.getMediaType() == MediaFile.MediaType.JPEG ||
                mediaFile.getMediaType() == MediaFile.MediaType.RAW_DNG) {
            mCurrentPhotoIndex = mediaFile.getIndex();
            mExposureNum++;
            insertMissionPhoto(mediaFile);

        } else if (mediaFile.getMediaType() == MediaFile.MediaType.MP4
                || mediaFile.getMediaType() == MediaFile.MediaType.MOV) {
            if (mBaseAirRoutePara.getActionMode() == AppConstant.ACTION_MODE_VIDEO) {
                String name = mediaFile.getFileName();
                mVideoRecordManager.writeVideoDataIntoFile(name.substring(0, name.indexOf(".")));
            }
        }
        updateMissionStart();
    }

    @Override
    public void onDownloadUpdate(@NonNull WaypointMissionDownloadEvent waypointMissionDownloadEvent) {

    }

    @Override
    public void onUploadUpdate(@NonNull WaypointMissionUploadEvent waypointMissionUploadEvent) {
        WaypointUploadProgress uploadProgress = waypointMissionUploadEvent.getProgress();
        if (uploadProgress != null) {
            int uploaded = uploadProgress.uploadedWaypointIndex + 1;
            int total = uploadProgress.totalWaypointCount;
            int percent = uploaded * 100 / total;
            Intent intent = new Intent(AppConstant.BROADCAST_UPLOAD_MISSION);
            intent.putExtra(AppConstant.BROADCAST_UPLOAD_MISSION_PROGRESS, percent);
            getAppContext().sendBroadcast(intent);
        }
    }

    @Override
    public void onExecutionUpdate(@NonNull WaypointMissionExecutionEvent waypointMissionExecutionEvent) {
        WaypointExecutionProgress progress = waypointMissionExecutionEvent.getProgress();
        if (progress != null) {
            if (progress.executeState == WaypointMissionExecuteState.RETURN_TO_FIRST_WAYPOINT) {
                updateMissionEnd();
            }
        }
    }

    @Override
    public void onExecutionStart() {

    }

    @Override
    public void onExecutionFinish(DJIError djiError) {
        if (djiError == null) {
            if (isGoingHome() || operateAction == AppConstant.OperateAction.GoHome) {
                return;
            }
            operateAction = AppConstant.OperateAction.FinishTask;
            mUIThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (isViewAttached()) {
                        getView().showToast("航点任务执行完成");
                        getView().showAutoReturnDialog();
                    }
                }
            });

            finishMission();
        }
    }

    public boolean isGoingHome() {
        return AircraftManager.getFlightController() != null
                && AircraftManager.getFlightController().getState() != null && AircraftManager.getFlightController().getState().isGoingHome();
    }

    public boolean isWaypointMissionExecuting() {

        boolean flag = false;

        if (mWaypointMissionOperator != null) {
            WaypointMissionState currentState = mWaypointMissionOperator.getCurrentState();
            if (currentState != null) {
                if (currentState == WaypointMissionState.EXECUTING) {
                    flag = true;
                }
            }
        }
        return flag;
    }


    /**
     * 刷新航飞任务的几个按钮
     *
     * @param action
     */
    protected void refreshFlightButton(final AppConstant.FlightAction action) {

        mUIThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (isViewAttached()) {
                    switch (action) {
                        case StartTask:
                            getView().updateFlightStart();
                            break;
                        case ResumeTask:
                            getView().updateFlightResume();
                            break;
                        case PauseTask:
                            getView().updateFlightPause();
                            break;
                        case GoHomeTask:
                            getView().updateFlightGoHome();
                            break;
                        case Unknown:
                            getView().updateFlightUnknown();
                            break;
                    }
                    getView().updateFlightButtons(action);
                }
            }
        });
    }


    @Override
    public void startTakePhoto() {
        if (isHasCamera())
            CameraManager.getCamera().startShootPhoto(new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError == null) {
                      
                    } else {
                        String result = "startTakePhoto::errorDescription =" + djiError.getDescription();
                        log.e(result);
                    }
                }
            });
    }

    @Override
    public void stopTakePhoto() {

        if (isHasCamera()) {
            CameraManager.getCamera().stopShootPhoto(new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError == null) {
                      
                    } else {
                        String result = "stopTakePhoto::errorDescription = " + djiError.getDescription();
                        log.e(result);
                    }
                }
            });
        }
    }

    @Override
    public void startRecord() {
        if (isHasCamera()) {
            CameraManager.getCamera().startRecordVideo(new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError == null) {
                      
                    } else {
                        String result = "startRecord::errorDescription =" + djiError.getDescription();
                        log.e(result);
                    }
                }
            });
        }
    }

    @Override
    public void stopRecord() {

        if (isHasCamera()) {
            CameraManager.getCamera().stopRecordVideo(new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError == null) {
                      
                        log.i("停止录像成功");
                    } else {
                        String result = "stopRecord::errorDescription =" + djiError.getDescription();
                        log.e(result);
                    }
                }
            });
        }
    }

    @Override
    public void setCameraShootPhoto() {
        if (isHasCamera()) {
            stopRecord();
            CameraManager.getCamera().setMode(SettingsDefinitions.CameraMode.SHOOT_PHOTO, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError == null)
                        mCameraMode = "拍照模式";
                }
            });
        }
    }

    @Override
    public void setCameraRecordVideo() {
        setCameraRecordVideo(false);
    }

    @Override
    public void setCameraRecordVideo(final boolean startRecordImmediately) {
        if (isHasCamera()) {
            stopTakePhoto();
            CameraManager.getCamera().setMode(SettingsDefinitions.CameraMode.RECORD_VIDEO, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError == null) {
                        mCameraMode = "视频模式";
                        if (startRecordImmediately) {
                            startRecord();
                        }
                    }
                }
            });
        }
    }


    @Override
    public void setPhotoAspectRatio() {
        if (isHasCamera()) {

            Camera camera = CameraManager.getCamera();
            camera.setPhotoAspectRatio(SettingsDefinitions.PhotoAspectRatio.RATIO_3_2, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError != null) {
                        camera.setPhotoAspectRatio(SettingsDefinitions.PhotoAspectRatio.RATIO_4_3, new CommonCallbacks.CompletionCallback() {
                            @Override
                            public void onResult(DJIError djiError) {
                                if (djiError != null) {
                                    camera.setPhotoAspectRatio(SettingsDefinitions.PhotoAspectRatio.RATIO_16_9, null);
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    /**
     * 设置云台模式
     *
     * @param gimbalMode
     */
    @Override
    public void setGimbalMode(GimbalMode gimbalMode) {
        if (isAirCraftConnect()) {

            Gimbal gimbal = GimbalManager.getGimbal();
            if (gimbal != null) {
                gimbal.setMode(gimbalMode, new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        /*if (djiError != null) {
                            showToastDialog(djiError.getDescription());
                        }else {
                            showToastDialog("设置跟随成功");
                        }*/
                    }
                });
            }
        }
    }


    @Override
    public void resetGimbal() {
        if (isAirCraftConnect()) {

            Gimbal gimbal = GimbalManager.getGimbal();
            if (gimbal != null) {
                gimbal.reset(new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        if (djiError != null) {
                            showToastDialog(djiError.getDescription());
                        }
                    }
                });
            }
        }
    }

    @Override
    public float getGimbalPitch() {
        float pitch = 0.0f;
        if (mGimbalState != null) {
            pitch = mGimbalState.getAttitudeInDegrees().getPitch() + 90;
        }
        return pitch;
    }

    protected void updateMissionStart() {
        if (operateAction == AppConstant.OperateAction.ExecuteTask
                && mMissionBase != null && isAlreadyUpdateMissionStart.compareAndSet(false, true)) {
            MissionHelper.updateMissionStart(config.getMode(), mMissionBase.getMissionId(), new Date(), mCurrentPhotoIndex);
        }
    }

    protected void updateMissionEnd() {
        if (mMissionBase != null && isAlreadyUpdateMissionEnd.compareAndSet(false, true)) {
            MissionHelper.updateMissionEnd(config.getMode(), mMissionBase.getMissionId(), new Date(), mCurrentPhotoIndex);
            MissionHelper.updateMissionBathFinish(mMissionBase.getMissionBatchId());
        }
    }

    protected void updateMissionPhotoIndex(int photoIndex) {
        if (mMissionBase != null) {
            MissionHelper.updateMissionPhotoIndex(config.getMode(), mMissionBase.getMissionId(), photoIndex);
        }
    }


    protected void insertMissionPhoto(MediaFile mediaFile) {

        if (operateAction == AppConstant.OperateAction.ExecuteTask
                && mMissionBase != null) {
            MissionPhoto photo = new MissionPhoto();
            photo.setId(SysUtils.newGUID());
            photo.setMissionId(mMissionBase.getMissionId());
            photo.setCreateDate(DateHelperUtils.string2DateTime(mediaFile.getDateCreated()));
            photo.setPhotoPath(mediaFile.getFileName());
            photo.setPhotoIndex(mediaFile.getIndex());
            photo.setPoint(new Point(aircraftLocationLongitude, aircraftLocationLatitude));
            MissionPhotoDbHelper.getInstance().save(photo);

            updateMissionPhotoIndex(mediaFile.getIndex());
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        FlightRecordManager.getInstance().onDestroy();
        mVideoRecordManager.destroy();
        if (mWaypointMissionOperator != null) {
          
            mWaypointMissionOperator.removeListener(this);
        }

    }


  

    private FlyFlightControllerKey mFlightControllerKey = FlyFlightControllerKey.create(FlyFlightControllerKey.AIRCRAFT_LOCATION);

    @Override
    public void setUpKeys() {
        KeyManager.getInstance().addListener(FlightControllerKey.create(FlightControllerKey.GO_HOME_ASSESSMENT), mGoHomeAssessmentListener);

    }

    @Override
    public void tearDownKeys() {
        KeyManager.getInstance().removeListener(mGoHomeAssessmentListener);
    }


    private KeyListener mGoHomeAssessmentListener = new KeyListener() {
        @Override
        public void onValueChange(@Nullable Object oldValue, @Nullable Object newValue) {
            if (newValue instanceof GoHomeAssessment) {
                GoHomeAssessment goHomeAssessment = (GoHomeAssessment) newValue;
                SmartRTHState smartRTHState = goHomeAssessment.getSmartRTHState();
                final int smartRTHCountdown = goHomeAssessment.getSmartRTHCountdown();
                if (smartRTHState != null) {
                    if (smartRTHState == SmartRTHState.COUNTING_DOWN) {
                        if (isViewAttached()) {
                            mUIThreadHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (smartRTHCountdown >= 9) {
                                        getView().showSmartReturnDialog(mSmartGoHomeListener);
                                    }
                                    getView().showSmartReturnTimeCount(smartRTHCountdown);
                                }
                            });

                        }
                    }
                }
            }
        }
    };

    private GoHomeDialog.GoHomeListener mSmartGoHomeListener = new GoHomeDialog.GoHomeListener() {
        @Override
        public void confirm() {
            confirmSmartReturnToHomeRequest(true);
        }

        @Override
        public void cancel() {
            confirmSmartReturnToHomeRequest(false);
        }
    };

    private void confirmSmartReturnToHomeRequest(boolean confirm) {

        FlightController flightController = AircraftManager.getFlightController();
        if (flightController != null) {
            flightController.confirmSmartReturnToHomeRequest(confirm, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError != null) {
                        showToast("智能返航请求失败:" + djiError.getDescription());
                    }
                }
            });
        }

    }


    protected void handleNoCameraMediaCallbackShootAction() {
        if (mBaseAirRoutePara.getActionMode() == AppConstant.ACTION_MODE_PHOTO) {
            mExposureNum++;
        }
    }

}
