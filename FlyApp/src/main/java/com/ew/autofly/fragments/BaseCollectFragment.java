package com.ew.autofly.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.esri.core.geometry.Point;
import com.ew.autofly.R;
import com.ew.autofly.application.EWApplication;
import com.ew.autofly.config.SharedConfig;
import com.ew.autofly.constant.AppConstant;
import com.ew.autofly.db.entity.MissionPhoto;
import com.ew.autofly.db.helper.MissionPhotoDbHelper;
import com.ew.autofly.dialog.AircraftStateDialog;
import com.ew.autofly.dialog.CustomDialog;
import com.ew.autofly.dialog.common.GoHomeDialog;
import com.ew.autofly.dialog.tool.SimulatorDialog;
import com.ew.autofly.entity.AirRouteParameter;
import com.ew.autofly.entity.LatLngInfo;
import com.ew.autofly.entity.LocationCoordinate;
import com.ew.autofly.entity.Mission2;
import com.ew.autofly.entity.MissionBatch2;
import com.ew.autofly.entity.WayPointTask;
import com.ew.autofly.entity.flightRecord.FlightUploadStates;
import com.ew.autofly.event.SimulatorEvent;
import com.ew.autofly.interfaces.FlyCheckingFragmentListener;
import com.ew.autofly.interfaces.IConfirmListener;
import com.ew.autofly.interfaces.OnFlightLandingListener;
import com.ew.autofly.interfaces.OnTopLeftMenuClickListener;
import com.ew.autofly.interfaces.TopFragmentClickListener;
import com.ew.autofly.internal.common.CheckError;
import com.ew.autofly.internal.key.base.FlyKeyManager;
import com.ew.autofly.key.cache.FlyFlightControllerKey;
import com.ew.autofly.logger.Logger;
import com.ew.autofly.model.AccessoryManager;
import com.ew.autofly.model.AircraftManager;
import com.ew.autofly.model.BatteryManager;
import com.ew.autofly.model.CameraManager;
import com.ew.autofly.model.ConnectManager;
import com.ew.autofly.model.FlightRecordManager;
import com.ew.autofly.model.RTKManager;
import com.ew.autofly.model.UserManager;
import com.ew.autofly.model.VisionManager;
import com.ew.autofly.model.mission.CheckConditionManager;
import com.ew.autofly.module.flightrecord.FlightPlatformManager;
import com.ew.autofly.module.setting.cache.FlySettingKey;
import com.ew.autofly.module.setting.fragment.MainSettingDialog;
import com.ew.autofly.struct.presenter.interfaces.IBaseCameraAction;
import com.ew.autofly.struct.presenter.interfaces.IBaseDroneStateCallback;
import com.ew.autofly.struct.presenter.interfaces.IBaseDroneStateCheck;
import com.ew.autofly.struct.presenter.interfaces.IBaseMissionCheck;
import com.ew.autofly.utils.DataBaseUtils;
import com.ew.autofly.utils.LogUtilsOld;
import com.ew.autofly.utils.MyUtils;
import com.ew.autofly.utils.SoundPlayerUtil;
import com.flycloud.autofly.base.util.SysUtils;
import com.ew.autofly.utils.ToastUtil;
import com.ew.autofly.utils.coordinate.LocationCoordinateUtils;
import com.ew.autofly.xflyer.utils.CommonConstants;
import com.ew.autofly.xflyer.utils.CoordinateUtils;
import com.ew.autofly.xflyer.utils.DateHelperUtils;
import com.ew.autofly.xflyer.utils.LatLngUtils;
import com.flycloud.autofly.base.framework.aop.annotation.SingleClick;
import com.flycloud.autofly.base.framework.rx.RxManager;
import com.flycloud.autofly.base.widgets.dialog.BaseProgressDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.greenrobot.greendao.annotation.NotNull;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import dji.common.accessory.AccessoryAggregationState;
import dji.common.battery.BatteryCellVoltageLevel;
import dji.common.battery.BatteryState;
import dji.common.camera.ExposureSettings;
import dji.common.camera.SettingsDefinitions;
import dji.common.camera.StorageState;
import dji.common.camera.SystemState;
import dji.common.error.DJIError;
import dji.common.flightcontroller.FlightControllerState;
import dji.common.flightcontroller.GoHomeAssessment;
import dji.common.flightcontroller.RTKState;
import dji.common.flightcontroller.SmartRTHState;
import dji.common.flightcontroller.VisionControlState;
import dji.common.flightcontroller.VisionDetectionState;
import dji.common.flightcontroller.simulator.InitializationData;
import dji.common.gimbal.GimbalMode;
import dji.common.gimbal.GimbalState;
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
import dji.common.useraccount.UserAccountState;
import dji.common.util.CommonCallbacks;
import dji.keysdk.FlightControllerKey;
import dji.keysdk.KeyManager;
import dji.keysdk.callback.KeyListener;
import dji.sdk.base.BaseComponent;
import dji.sdk.base.BaseProduct;
import dji.sdk.battery.Battery;
import dji.sdk.camera.Camera;
import dji.sdk.flightcontroller.FlightAssistant;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.flightcontroller.Simulator;
import dji.sdk.gimbal.Gimbal;
import dji.sdk.media.MediaFile;
import dji.sdk.mission.MissionControl;
import dji.sdk.mission.waypoint.WaypointMissionOperator;
import dji.sdk.mission.waypoint.WaypointMissionOperatorListener;
import dji.sdk.products.Aircraft;
import dji.sdk.remotecontroller.RemoteController;
import dji.sdk.useraccount.UserAccountManager;
import dji.ux.widget.BatteryWidget;
import dji.ux.widget.GPSSignalWidget;
import dji.ux.widget.RemoteControlSignalWidget;
import dji.ux.widget.VideoSignalWidget;
import dji.ux.widget.VisionWidget;
import io.reactivex.disposables.Disposable;

import static com.ew.autofly.dialog.tool.SimulatorDialog.PARAM_SIMULATOR_LATITUDE;
import static com.ew.autofly.dialog.tool.SimulatorDialog.PARAM_SIMULATOR_LONGITUDE;

/**
 * Created by Administrator on 2016/3/2.
 * 这个类UI代码和逻辑代码耦合在一起，不宜再使用
 */
@Deprecated
public class BaseCollectFragment extends BaseFragment implements TopFragmentClickListener, FlyCheckingFragmentListener, IBaseCameraAction, IBaseDroneStateCheck, IBaseDroneStateCallback, IBaseMissionCheck, ConnectManager.OnProductConnectListener {

    protected String mAirCraftSerialNumber;

    protected String mBatterySerialNumber;

    private void getAirCraftSerialNumber() {
        FlightController flightController = AircraftManager.getFlightController();
        if (flightController != null) {
            flightController.getSerialNumber(new CommonCallbacks.CompletionCallbackWith<String>() {
                @Override
                public void onSuccess(String s) {
                    mAirCraftSerialNumber = s;
                }

                @Override
                public void onFailure(DJIError djiError) {

                }
            });
        }
    }

    private void getBatterySerialNumber() {
        Aircraft aircraft = AircraftManager.getAircraft();
        if (aircraft != null) {
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

    protected Handler mUIThreadHandler = new Handler(Looper.getMainLooper());

    protected RxManager mRxManager = new RxManager();

    private boolean mHasRtk = false;

    /**
     * 是否有RTK信号
     *
     * @return
     */
    protected boolean hasRTKSignal() {
        return mHasRtk;
    }


    protected void setHasRTk(boolean hasFlag) {
        mHasRtk = hasFlag;
    }

    protected BaseProgressDialog mLoadProgressDlg;

    protected SharedConfig config;

    protected View view;
    public double droneYaw = 0;
    protected BaseMapFragment fragMap;
    protected TopFragment fragTop;
    protected VideoSignalWidget imgBtnFigure;
    protected RemoteControlSignalWidget imgBtnSignal;
    protected GPSSignalWidget imgBtnGPSSignal;
    protected VisionWidget vwVisionDetection;
    protected BatteryWidget mBattery;
    protected TextView mTvBattery;
    protected TextView tvMonitorInfo;
    protected TextView tvTopTitle;


    protected DataBaseUtils mDB = null;
    protected LogUtilsOld log = null;

    public AirRouteParameter airRoutePara;
    protected boolean bInitDrone = false;

    protected FlightControllerState flightControllerState;
    public double homeLocationLatitude = 0;
    public double homeLocationLongitude = 0;
    public double aircraftLocationLatitude = 0;
    public double aircraftLocationLongitude = 0;
    protected float aircraftLocationAltitude = 0.0f;
    protected double mDistanceX;
    protected double mHorizontalSpeed;
    protected double mVerticalSpeed;
    protected long mRunTime;

    protected int lastTargetWPIndex = -1;
    public boolean bIsShootingPhoto = false;
    protected boolean bIsRecordingVideo = false;


    protected int beginPhotoIndex = 0;

    protected int currentPhotoIndex = 0;
    protected long startTimeMillisecond = 0;


    public AppConstant.OperateAction operateAction = AppConstant.OperateAction.Unknown;
    public boolean getHomePointFlag = false;
    public long mInitialRemainCaptureCount = 0;
    public long mRemainCaptureCount = 0;
    protected String DEFAULT_STATE = "云台角度:\t\t\t\t\t\t\n飞行高度:\n水平距离:\n水平速度:\n垂直速度:\n飞行时长:\n曝光数量:\n曝光补偿:\n相机模式:";
    public String mcuState;
    protected WaypointMissionOperator mWaypointMissionOperator;
    protected WaypointMissionOperatorListener waypointMissionOperatorListener;
    public WaypointMission mDJIWayPointMission;

    public Mission2 currentMission2;
    public MissionBatch2 currentMissionBatch2;

    public double satelliteCount = 0;
    public String flightModeString = "";
    public double remainPower = 0.0;
    public String mFlightSerialNumber = "";
    public BatteryCellVoltageLevel mCellVoltageLevel;
    public float mBatteryTemperature;

    public long mSdcardRemainSize = 0;
    public int totalFlyTask = 0;
    protected BaseProduct mProduct = null;

    protected String cameraMode = "";
    protected float mGimbalAngle = 0.0f;
    protected float mGimbalRoll = 0.0f;
    protected float mGimbalYaw = 0.0f;
    protected String mCameraModel;
    private int mCameraOpticalZoomFocalLength = 0;

    protected long exposureNum = 0;
    protected String cameraExposureCompensation;

    protected boolean isShowLowPowerReturn = true;

    private SystemState mCameraState = null;

    public int actionMode = AppConstant.ACTION_MODE_PHOTO;
    public int returnMode = AppConstant.RETURN_MODE_STRAIGHT;
    public int recodeMode = AppConstant.MIDWAY_RECODE_VIDEO;

    protected static final int SHOW_TOAST = 1;

    private GimbalState mGimbalState;
    private BatteryState mBatteryState;
    private HardwareState mHardwareState;

    private FlySettingKey mGimbalModeKey = FlySettingKey.create(FlySettingKey.GIMBAL_MODE);

    private GoHomeDialog mGoHomeDialog;


    public boolean isRecording = false;


    public AppConstant.RiverMissionMode RiverMode = AppConstant.RiverMissionMode.RiverPatrolStudy;

    public int mStandMode = 1;


    protected boolean isAlreadyFly = false;

    private AtomicBoolean isAlreadyUpdateMissionStart = new AtomicBoolean(false);
    private AtomicBoolean isAlreadyUpdateMissionEnd = new AtomicBoolean(false);


    private OnFlightLandingListener mOnFlightLandingListener;

    public void setOnFlightLandingListener(OnFlightLandingListener onFlightLandingListener) {
        this.mOnFlightLandingListener = onFlightLandingListener;
    }

    protected Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_TOAST:
                    if (isAdded())
                        ToastUtil.show(getActivity(), (String) msg.obj);
                    break;
            }
            return false;
        }
    });

    protected BroadcastReceiver mProductChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            onProductChanged();
        }
    };


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConnectManager.getInstance().register(this);
        try {
            if (!EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().register(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        initData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup
            container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_flycollect, null);
        try {
            mDB = DataBaseUtils.getInstance(getContext());
        } catch (Exception e) {
            ToastUtil.show(getActivity(), "数据库初始化错误,系统即将退出");
            getActivity().finish();
        }

        tvMonitorInfo = (TextView) view.findViewById(R.id.tvMonitorInfo);
        fragTop = (TopFragment) getChildFragmentManager().findFragmentById(R.id.id_fragment_topbar);
        imgBtnFigure = (dji.ux.widget.VideoSignalWidget) fragTop.getView().findViewById(R.id.imgBtnFigure);

        imgBtnSignal = (dji.ux.widget.RemoteControlSignalWidget) fragTop.getView().findViewById(R.id.imgBtnSingle);
        vwVisionDetection = (VisionWidget) fragTop.getView().findViewById(R.id.vw_vision_detection);
        imgBtnGPSSignal = (GPSSignalWidget) fragTop.getView().findViewById(R.id.imgBtnGPSSignal);
        mBattery = (dji.ux.widget.BatteryWidget) fragTop.getView().findViewById(R.id.imgBtnBattery);
        mTvBattery = (TextView) fragTop.getView().findViewById(R.id.tvBtnBattery);
        tvTopTitle = (TextView) fragTop.getView().findViewById(R.id.tvTopTitle);
        mcuState = DEFAULT_STATE;
        tvMonitorInfo.setText(mcuState, TextView.BufferType.SPANNABLE);
      /*  mBtSimulateSetting = (ImageButton) fragTop.getView().findViewById(R.id.imgBtnSimulateSetting);
        if (config.getSimulateState()) {
            mBtSimulateSetting.setVisibility(View.VISIBLE);
        } else {
            mBtSimulateSetting.setVisibility(View.GONE);
        }*/
        WaypointMission.Builder builder = new WaypointMission.Builder();
        mDJIWayPointMission = builder.build();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initProductChangeReceiver();
        VisionManager.getInstance().init();
        BatteryManager.getInstance().init();

        FlightPlatformManager.getInstance().init();
        FlightPlatformManager.getInstance().startService();
    }

    @Override
    public void onStart() {
        super.onStart();
        onProductChanged();
    }

    @Override
    public void onDestroy() {

        FlightPlatformManager.getInstance().destroy();

        FlightRecordManager.getInstance().onDestroy();

        try {
            if (mProductChangeReceiver != null) {
                getActivity().getApplicationContext().unregisterReceiver(mProductChangeReceiver);
            }
        } catch (IllegalArgumentException ex) {
            log.e("UnregisterReceiver时出错,错误信息为：" + ex.getMessage());
        }


        uninstallDroneEvent();

        try {
            if (EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().unregister(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        VisionManager.getInstance().release();
        BatteryManager.getInstance().release();

        ConnectManager.getInstance().unRegister(this);

        mRxManager.clear();

        super.onDestroy();
    }


    @Override
    public void onTopFragmentClick(final View v) {
        switch (v.getId()) {
            case R.id.iv_menu:
                ((OnTopLeftMenuClickListener) getActivity()).onMenuClick(v);
                break;
            case R.id.imgBtnSimulateSetting:
                if (mProduct == null || !mProduct.isConnected()) {
                    ToastUtil.show(getActivity(), "请连接飞机");
                    return;
                }
                showSimulateDialog();
                break;
            case R.id.imgBtnFigure:



            case R.id.imgBtnSingle:
            case R.id.imgBtnBattery:

            case R.id.tvBtnBattery:
                int visibility = tvMonitorInfo.getVisibility();
                tvMonitorInfo.setVisibility(visibility == View.GONE ? View.VISIBLE : View.GONE);
                break;
            case R.id.vw_vision_detection:
                if (EWApplication.getAircraftInstance() != null) {
                    FlightAssistant assistant = EWApplication.getAircraftInstance().getFlightController().getFlightAssistant();
                    assistant.getCollisionAvoidanceEnabled(new CommonCallbacks.CompletionCallbackWith<Boolean>() {
                        @Override
                        public void onSuccess(Boolean aBoolean) {
                            EWApplication.getAircraftInstance().getFlightController().getFlightAssistant().setCollisionAvoidanceEnabled(!aBoolean, null);
                        }

                        @Override
                        public void onFailure(DJIError djiError) {

                        }
                    });
                }
                break;
            case R.id.tvTopTitle:
                showAircraftStateDialog();
                break;
        }

        onSingleClick(v);
    }

    @SingleClick
    public void onSingleClick(View view) {
        switch (view.getId()) {
            case R.id.iv_more:
                MainSettingDialog settingDialog = new MainSettingDialog();
                settingDialog.show(getFragmentManager(), "MainSettingDialog");
                break;
        }
    }

    private void initData() {
        config = new SharedConfig(getContext());
        airRoutePara = new AirRouteParameter(config.getCameraName());
    }

    private void initProductChangeReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(AppConstant.BROADCAST_DJI_PRODUCT_CONNECTION_CHANGE);
        getActivity().getApplicationContext().registerReceiver(mProductChangeReceiver, filter);
    }

    private void onProductChanged() {

        mProduct = EWApplication.getProductInstance();
        updateProductInfo();
        initWayPointOperator();
        initDroneEvent();
        loginDjiAccountDelay();

    }


    private void updateProductInfo() {

        if (null != mProduct && mProduct.isConnected() && mProduct.getModel() != null) {

            Model model = mProduct.getModel();


            String modelName = model.getDisplayName();
            Camera camera = mProduct.getCamera();

            String cameraName = "";

            if (camera != null) {
                cameraName = camera.getDisplayName();
            }

            if (airRoutePara == null) {
                airRoutePara = new AirRouteParameter(cameraName);
            } else {
                airRoutePara.initializeCameraParams(cameraName);
            }

            config.setCameraName(cameraName);
            config.setAirDraftModel(modelName);

            RTKManager.initNetWorkRtkService();

        }

        if (AircraftManager.isOnlyFlightController()) {
            mBattery.setVisibility(View.GONE);
            mTvBattery.setVisibility(View.VISIBLE);
        } else {
            mBattery.setVisibility(View.VISIBLE);
            mTvBattery.setVisibility(View.GONE);
            mTvBattery.setText("N/A");
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
        if (accessoryAggregationState == null) {
            unInstallAccessoryAggregationView();
            return;
        }

        fragMap.imgSpotlight.setVisibility(accessoryAggregationState.isSpotlightConnected()
                ? View.VISIBLE : View.GONE);
        fragMap.imgSpeaker.setVisibility(accessoryAggregationState.isSpeakerConnected()
                ? View.VISIBLE : View.GONE);
        fragMap.imgBeacon.setVisibility(accessoryAggregationState.isBeaconConnected()
                ? View.VISIBLE : View.GONE);
    }

    private void unInstallAccessoryAggregationView() {
        fragMap.imgSpotlight.setVisibility(View.GONE);
        fragMap.imgSpeaker.setVisibility(View.GONE);
        fragMap.imgBeacon.setVisibility(View.GONE);
        fragMap.spotlightPanel.setVisibility(View.GONE);
        fragMap.speakerPanel.setVisibility(View.GONE);
    }


    protected void initDroneEvent() {

        if (null != mProduct) {

            FlightController flightController = AircraftManager.getFlightController();
            if (flightController != null) {
                flightController.setStateCallback(new FlightControllerState.Callback() {
                    @Override
                    public void onUpdate(@NonNull FlightControllerState state) {

                        onFlightControllerStateCallback(state);
                    }
                });

                FlightAssistant flightAssistant = flightController.getFlightAssistant();
                if (flightAssistant != null) {

                    flightAssistant.setVisionDetectionStateUpdatedCallback(new VisionDetectionState.Callback() {
                        @Override
                        public void onUpdate(@NonNull VisionDetectionState visionDetectionState) {
                            onVisionDetectionStateCallBack(visionDetectionState);
                        }
                    });
                }
            }

            final RemoteController rc = ((Aircraft) mProduct).getRemoteController();
            if (null != rc) {
                rc.setHardwareStateCallback(new HardwareState.HardwareStateCallback() {
                    @Override
                    public void onUpdate(@NonNull HardwareState hardwareState) {

                        onHardwareStateCallback(hardwareState);
                    }
                });
            }

            if (null != mProduct.getGimbal())

                mProduct.getGimbal().setStateCallback(new GimbalState.Callback() {
                    @Override
                    public void onUpdate(@NonNull GimbalState gimbalState) {

                        onGimbalStateCallback(gimbalState);
                    }
                });

            BatteryManager.setBatteryStateCallback(new BatteryState.Callback() {
                @Override
                public void onUpdate(BatteryState batteryState) {
                    onBatteryStateStateCallback(batteryState);
                }
            });

            Disposable batteryDispose = BatteryManager.getM200SeriesBattery(new BatteryState.Callback() {
                @Override
                public void onUpdate(BatteryState batteryState) {
                    onBatteryStateStateCallback(batteryState);
                }
            });

            mRxManager.add(batteryDispose);

            Camera camera = mProduct.getCamera();

            if (null != camera) {

                camera.setSystemStateCallback(new SystemState.Callback() {
                    @Override
                    public void onUpdate(@NonNull SystemState systemState) {
                        onCameraSystemStateCallback(systemState);
                    }
                });

                camera.setExposureSettingsCallback(new ExposureSettings.Callback() {
                    @Override
                    public void onUpdate(@NonNull ExposureSettings exposureSettings) {

                        onCameraExposureSettingsCallback(exposureSettings);
                    }
                });

                camera.setStorageStateCallBack(new StorageState.Callback() {
                    @Override
                    public void onUpdate(@NonNull StorageState storageState) {

                        onCameraStorageCallBack(storageState);

                    }
                });

                camera.setMediaFileCallback(new MediaFile.Callback() {
                    @Override
                    public void onNewFile(@NonNull final MediaFile mediaFile) {
                        onCameraMediaFileCallback(mediaFile);
                    }
                });

                setUpKeys();

            }



            KeyManager.getInstance().setValue(FlightControllerKey.create(FlightControllerKey.IS_LANDING_CONFIRMATION_NEEDED), false, null);

            FlightRecordManager.getInstance().init();

            getAirCraftSerialNumber();

            getBatterySerialNumber();
        }

        installAccessoryAggregation();
    }

    protected void uninstallDroneEvent() {
        try {

            if (mProduct != null) {
                FlightController flightController = AircraftManager.getFlightController();
                if (flightController != null) {
                    flightController.setStateCallback(null);
                    FlightAssistant flightAssistant = flightController.getFlightAssistant();
                    if (flightAssistant != null) {
                        flightAssistant.setVisionDetectionStateUpdatedCallback(null);
                    }
                }
                if (mProduct.getBattery() != null)
                    mProduct.getBattery().setStateCallback(null);
                if (mProduct.getAirLink().isLightbridgeLinkSupported())
                    mProduct.getAirLink().getLightbridgeLink().setRemoteControllerAntennaRSSICallback(null);
                if (mWaypointMissionOperator != null && waypointMissionOperatorListener != null) {

                    mWaypointMissionOperator.removeListener(waypointMissionOperatorListener);
                }

                if (mProduct.getCamera() != null) {
                    mProduct.getCamera().setSystemStateCallback(null);
                    mProduct.getCamera().setSDCardStateCallBack(null);
                    mProduct.getCamera().setMediaFileCallback(null);
                }
                if (mProduct.getGimbal() != null)
                    mProduct.getGimbal().setStateCallback(null);

                AccessoryManager.destroyAccessoryAggregation();

                tearDownKeys();
            }
        } catch (Exception ex) {

        }
    }

    private void initWayPointOperator() {

        if (null != mProduct && mProduct.isConnected()) {

            if (MissionControl.getInstance() != null) {
                mWaypointMissionOperator = MissionControl.getInstance().getWaypointMissionOperator();
                if (mWaypointMissionOperator != null) {
                    if (waypointMissionOperatorListener != null) {
                        mWaypointMissionOperator.removeListener(waypointMissionOperatorListener);
                    }
                    setUpWayPointMissionOperatorListener();
                    mWaypointMissionOperator.addListener(waypointMissionOperatorListener);
                }
            }
        }
    }

    protected void setUpWayPointMissionOperatorListener() {

        waypointMissionOperatorListener = new WaypointMissionOperatorListener() {
            @Override
            public void onDownloadUpdate(@NonNull WaypointMissionDownloadEvent downloadEvent) {

            }

            @Override
            public void onUploadUpdate(@NonNull WaypointMissionUploadEvent uploadEvent) {
                WaypointUploadProgress uploadProgress = uploadEvent.getProgress();
                if (uploadProgress != null && isAdded()) {
                    int uploaded = uploadProgress.uploadedWaypointIndex + 1;
                    int total = uploadProgress.totalWaypointCount;
                    int percent = uploaded * 100 / total;
                    Intent intent = new Intent("UPLOAD_PROGRESS_UPDATE");
                    intent.putExtra("progress", percent);
                    getActivity().sendBroadcast(intent);
                }
            }

            @Override
            public void onExecutionUpdate(@NonNull WaypointMissionExecutionEvent executionEvent) {
                final WaypointExecutionProgress progress = executionEvent.getProgress();
                if (null != progress) {
                    final WaypointMissionExecuteState executeState = progress.executeState;
                    if (WaypointMissionState.EXECUTING.equals(executionEvent.getCurrentState())) {

                        if (fragMap != null && (fragMap.currentMode == AppConstant.OperationMode.PositiveImage || fragMap.currentMode == AppConstant.OperationMode.TiltImage)) {

                            if (null != currentMission2 && progress.isWaypointReached &&
                                    progress.targetWaypointIndex > 2) {
                                if (currentMission2.getPointIndex() == 0)
                                    mDB.updateMissionPointIndex(currentMission2.getId(),
                                            progress.targetWaypointIndex + currentMission2.getPointIndex(), null);
                                else
                                    mDB.updateMissionPointIndex(currentMission2.getId(),
                                            progress.targetWaypointIndex + currentMission2.getPointIndex() - 2, null);
                            }
                        }

                        if (isAdded())
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (remainPower <= 30 && remainPower > 0) {
                                        if (isShowLowPowerReturn) {
                                            showLowPowerReturnDialog();
                                            isShowLowPowerReturn = false;
                                        }
                                    }
                                }
                            });
                    }

                    if (progress.executeState == WaypointMissionExecuteState.RETURN_TO_FIRST_WAYPOINT) {
                        updateMissionEnd();
                    }
                }
            }

            @Override
            public void onExecutionStart() {
                isAlreadyUpdateMissionStart.set(false);
                isAlreadyUpdateMissionEnd.set(false);
                FlightRecordManager.getInstance().endRecord();
                FlightRecordManager.getInstance()
                        .createNewRecord(getAirCraftLocation(), currentMission2);
            }

            @Override
            public void onExecutionFinish(@Nullable DJIError djiError) {
                if (djiError == null) {
                    if (operateAction == AppConstant.OperateAction.GoHome ||
                            (null != mProduct && null != ((Aircraft) mProduct).getFlightController() && ((Aircraft) mProduct).getFlightController().getState().isGoingHome())) {
                        return;
                    }
                    operateAction = AppConstant.OperateAction.FinishTask;
                    lastTargetWPIndex = -1;
                    handler.sendMessage(handler.obtainMessage(SHOW_TOAST, "航点任务执行完成"));
                    showAutoReturnDialog();
                    isShowLowPowerReturn = true;
                }
            }
        };
    }


    @Override
    public void onFlyCheckingComplete(boolean bResult) {
        if (!bResult) {
            operateAction = AppConstant.OperateAction.Unknown;
            return;
        }
        mWaypointMissionOperator.startMission(new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                if (djiError != null) {
                    operateAction = AppConstant.OperateAction.Unknown;
                    String ResultsString = "起飞的返回值为：" + djiError.getDescription();
                    handler.sendMessage(handler.obtainMessage(SHOW_TOAST, ResultsString));
                    mUIThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            onStartMissionFail();
                        }
                    });
                } else {
                    exposureNum = 0;
                    bIsShootingPhoto = false;
                    bIsRecordingVideo = false;

                    operateAction = AppConstant.OperateAction.ExecuteTask;
                    startTimeMillisecond = new Date().getTime();
                    mUIThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            onStartMissionSuccess();
                        }
                    });
                }
            }
        });
    }

    protected void onStartMissionFail() {

    }

    protected void onStartMissionSuccess() {
        tvMonitorInfo.setVisibility(View.VISIBLE);
        refreshMapButton(AppConstant.FlightAction.StartTask);
    }


    protected void pauseMission() {
        if (mWaypointMissionOperator == null)
            mWaypointMissionOperator = MissionControl.getInstance().getWaypointMissionOperator();
        mWaypointMissionOperator.pauseMission(new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                if (djiError == null) {
                    handler.sendMessage(handler.obtainMessage(SHOW_TOAST, "暂停任务成功"));
                    refreshMapButton(AppConstant.FlightAction.PauseTask);
                  /*  if (operateAction == AppConstant.OperateAction.ExecuteTask
                            && fragMap.currentMode != AppConstant.OperationMode.FinePatrol) {
                        if (actionMode == AppConstant.ACTION_MODE_VIDEO)
                            stopRecord();
                        else
                            stopTakePhoto();
                    }*/
                } else {
                    String resultString = "暂停任务失败，失败原因：" + djiError.getDescription();
                    handler.sendMessage(handler.obtainMessage(SHOW_TOAST, resultString));
                }
            }
        });
    }


    protected void resumeMission() {
        if (mWaypointMissionOperator == null)
            mWaypointMissionOperator = MissionControl.getInstance().getWaypointMissionOperator();
        mWaypointMissionOperator.resumeMission(new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                if (djiError == null) {
                    handler.sendMessage(handler.obtainMessage(SHOW_TOAST, "继续任务成功"));
                    refreshMapButton(AppConstant.FlightAction.ResumeTask);
                } else {
                    String resultString = "继续任务失败：" + djiError.getDescription();
                    handler.sendMessage(handler.obtainMessage(SHOW_TOAST, resultString));
                }
            }
        });
    }


    protected void goHome() {
        if (bIsShootingPhoto)
            stopTakePhoto();
        if (bIsRecordingVideo)
            stopRecord();





        ((Aircraft) mProduct).getFlightController().startGoHome(new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError result) {
                if (result == null) {
                    operateAction = AppConstant.OperateAction.GoHome;
                    handler.sendMessage(handler.obtainMessage(SHOW_TOAST, "一键返航成功"));
                    refreshMapButton(AppConstant.FlightAction.GoHomeTask);
                } else {
                    String resultString = "一键返航失败，失败原因：" + result.getDescription();
                    handler.sendMessage(handler.obtainMessage(SHOW_TOAST, resultString));
                }
            }
        });

    }

    /**
     * 刷新地图页面中的执行任务等几个按钮
     *
     * @param action
     */
    protected void refreshMapButton(final AppConstant.FlightAction action) {
        if (isAdded())
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (fragMap != null)
                        fragMap.refreshFlightButton(action);
                }
            });
    }

    protected void setIntervalParams() {
        if (isHasCamera()) {
            Camera camera = mProduct.getCamera();
            camera.setShootPhotoMode(SettingsDefinitions.ShootPhotoMode.INTERVAL, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError == null) {
                        camera.setPhotoTimeIntervalSettings(new SettingsDefinitions.PhotoTimeIntervalSettings(255, airRoutePara.getPhotoInterval()), new CommonCallbacks.CompletionCallback() {
                            @Override
                            public void onResult(DJIError djiError) {
                                if (djiError == null)
                                    log.i("设置间隔时长成功!" + airRoutePara.getPhotoInterval() + "s");
                                else
                                    log.e(djiError.getDescription());
                            }
                        });
                    }
                }
            });
        }







        setPhotoAspectRatio();
    }


    @Override
    public void setPhotoAspectRatio() {
        if (isHasCamera()) {

            Camera camera = mProduct.getCamera();
            camera.setPhotoAspectRatio(SettingsDefinitions.PhotoAspectRatio.RATIO_3_2, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError != null) {
                        mProduct.getCamera().setPhotoAspectRatio(SettingsDefinitions.PhotoAspectRatio.RATIO_4_3, new CommonCallbacks.CompletionCallback() {
                            @Override
                            public void onResult(DJIError djiError) {
                                if (djiError != null) {
                                    mProduct.getCamera().setPhotoAspectRatio(SettingsDefinitions.PhotoAspectRatio.RATIO_16_9, null);
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    protected void setDefaultMonitorText(String text) {
        DEFAULT_STATE = text;
    }

    protected void showMonitorText() {

        StringBuffer sbMonitorInfo = new StringBuffer();

        sbMonitorInfo.append("云台角度: ").append(String.format("%.0f°", mGimbalAngle)).append("\n");
        sbMonitorInfo.append("飞行高度: ").append(String.format("%.2fm", aircraftLocationAltitude)).append("\n");
        sbMonitorInfo.append("水平距离: ").append(String.format("%.2fm", mDistanceX)).append("\n");
        sbMonitorInfo.append("水平速度: ").append(String.format("%.2fm/s", mHorizontalSpeed)).append("\n");
        sbMonitorInfo.append("垂直速度: ").append(String.format("%.2fm/s", mVerticalSpeed)).append("\n");
        sbMonitorInfo.append("飞行时长: ").append(String.format("%.2f", mRunTime / 60.0f)).append("min\n");
        if (actionMode == AppConstant.ACTION_MODE_PHOTO) {
            sbMonitorInfo.append("曝光数量: ").append(exposureNum).append("\n");
        }
        if (CameraManager.isSupportCameraOpticalZoom()) {
            if (mCameraOpticalZoomFocalLength == 0) {
                sbMonitorInfo.append("光学焦距: ").append("N/A").append("\n");
            } else {
                sbMonitorInfo.append("光学焦距: ").append(
                        String.format("%.2f", mCameraOpticalZoomFocalLength / 10.0f) + "mm").append("\n");
            }
        }
        sbMonitorInfo.append("曝光补偿: ").append(cameraExposureCompensation).append("\n");
        sbMonitorInfo.append("相机模式: ").append(cameraMode);

        mcuState = sbMonitorInfo.toString();
    }


    protected void getCameraOpticalZoomFocalLength() {

        if (CameraManager.isSupportCameraOpticalZoom()) {
            if (isHasCamera()) {
                Camera camera = mProduct.getCamera();
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

    public void startRecordPoint() {
    }

    public void finishRecordPoint() {
    }


    protected void updateMissionStart(DataBaseUtils.onExecResult callback) {
        if (operateAction == AppConstant.OperateAction.ExecuteTask
                && currentMission2 != null && isAlreadyUpdateMissionStart.compareAndSet(false, true)) {
            mDB.updateMissionStartInfo2(currentMission2.getId(), currentPhotoIndex, callback);
        }
    }


    protected void updateMissionEnd() {
        if (currentMission2 != null && isAlreadyUpdateMissionEnd.compareAndSet(false, true)) {

            mDB.updateMissionEndInfo2(currentMission2.getId(), currentPhotoIndex, null);

            if (currentMissionBatch2 != null) {
                if (fragMap.currentMode == AppConstant.OperationMode.TiltImage) {
                    if (fragMap.currentTiltStep == CommonConstants.TiltStep.Step5) {
                        mDB.updateMissionBatchStatus(currentMissionBatch2.getId(), null);
                    }
                } else {
                    mDB.updateMissionBatchStatus(currentMissionBatch2.getId(), null);
                }
            }
        }
    }



    protected void insertMissionPhoto(MediaFile mediaFile) {

        if (operateAction == AppConstant.OperateAction.ExecuteTask
                && actionMode == AppConstant.ACTION_MODE_PHOTO
                && currentMission2 != null) {

            insertMissionPhoto(mediaFile, currentMission2.getId());


            mDB.updatePhotoNum(currentMission2.getId(), currentPhotoIndex, null);
        }
    }


    protected void insertMissionPhoto(MediaFile mediaFile, String missionID) {

        MissionPhoto photo = new MissionPhoto();
        photo.setId(SysUtils.newGUID());
        photo.setMissionId(missionID);
        photo.setCreateDate(DateHelperUtils.string2DateTime(mediaFile.getDateCreated()));
        photo.setPhotoPath(mediaFile.getFileName());
        photo.setPhotoIndex(mediaFile.getIndex());
        photo.setPoint(new Point(aircraftLocationLongitude, aircraftLocationLatitude));
        MissionPhotoDbHelper.getInstance().save(photo);
    }

    @Override
    public void startTakePhoto() {
        if (isHasCamera())
            mProduct.getCamera().startShootPhoto(new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError == null) {
                        bIsShootingPhoto = true;
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
            mProduct.getCamera().stopShootPhoto(new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError == null) {
                        bIsShootingPhoto = false;
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
            mProduct.getCamera().startRecordVideo(new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError == null) {
                        bIsRecordingVideo = true;
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
            mProduct.getCamera().stopRecordVideo(new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError == null) {
                        bIsRecordingVideo = false;

                    }/* else {
                        String result = "stopRecord::errorDescription =" + djiError.getDescription();
                        log.e(result);
                    }*/
                }
            });
        }
    }

    @Override
    public void setCameraShootPhoto() {
        if (isHasCamera()) {
            stopRecord();
            mProduct.getCamera().setMode(SettingsDefinitions.CameraMode.SHOOT_PHOTO, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError == null) {
                        cameraMode = "拍照模式";
                    } else {
                        log.e("设置相机拍照模式失败：" + djiError.getDescription());
                    }
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
            mProduct.getCamera().setMode(SettingsDefinitions.CameraMode.RECORD_VIDEO, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError == null) {
                        cameraMode = "视频模式";
                        if (startRecordImmediately) {
                            startRecord();
                        }
                    } else {
                        log.e("设置相机录像模式失败：" + djiError.getDescription());
                    }
                }
            });
        }
    }

    /**
     * 设置云台跟随模式
     *
     * @param gimbalMode
     */
    @Override
    public void setGimbalMode(GimbalMode gimbalMode) {
        if (isAirCraftConnect()) {

            Gimbal gimbal = mProduct.getGimbal();
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
    public float getGimbalPitch() {
        return mGimbalAngle;
    }


    @Override
    public void resetGimbal() {
        if (isAirCraftConnect()) {

            Gimbal gimbal = mProduct.getGimbal();
            if (gimbal != null) {
                gimbal.reset(new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        if (djiError != null) {

                        }
                    }
                });
            }
        }
    }

    /**
     * 检查Home点的位置是否已经得到
     *
     * @return
     */
    @Override
    public CheckError checkGetHomePoint() {
        CheckError error = null;
        if (!getHomePointFlag) {
            SoundPlayerUtil.getInstance(getContext()).playSound(R.raw.no_home_point, 0);
            error = new CheckError("未定位到Home的位置");
        }
        return error;
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

    /**
     * 检查当前的任务是否正在运行
     *
     * @return 正在运行时，返回false，否则返回true
     */
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
        return null;
    }

    @Override
    public void checkPitchRangeExtension(@NonNull CheckConditionManager checkConditionManager) {

    }


    private void loginDjiAccountDelay() {

        mUIThreadHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loginDjiAccount(false);
            }
        }, 2000);
    }

    public void loginDjiAccount(boolean recall) {
        if (getActivity() != null && AircraftManager.isAircraftConnected()) {
            UserManager.getInstance().logIntoDJIUserAccount(getActivity(), new CommonCallbacks.CompletionCallbackWith<UserAccountState>() {
                @Override
                public void onSuccess(UserAccountState userAccountState) {
                    showToast(EWApplication.getInstance().getString(R.string.message_login_to_dji_success));
                    mUIThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            fragMap.updateVideoFeed();
                        }
                    });
                }

                @Override
                public void onFailure(DJIError djiError) {
                    showToast(EWApplication.getInstance().getString(R.string.message_login_to_dji_fail));
                }
            }, recall);
        }
    }


    public CheckError checkCommonCondition() {

        CheckError checkError = null;

        checkError = checkGetHomePoint();
        if (checkError != null) {
            return checkError;
        }

        checkError = checkDJIAccountState();
        if (checkError != null) {
            return checkError;
        }

        checkError = checkMaxFlyHeight();
        if (checkError != null) {
            return checkError;
        }

        checkError = checkEnoughFlyTimeAndBattery();
        if (checkError != null) {
            return checkError;
        }

        return checkError;
    }

    /**
     * 检查dji账号是否登录
     *
     * @return
     */
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

        if (checkError != null) {
            CustomDialog.Builder deleteDialog = new CustomDialog.Builder(getContext());
            deleteDialog.setTitle(getString(R.string.notice))
                    .setMessage(checkError.getDescription())
                    .setPositiveButton(getString(R.string.sure), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            loginDjiAccount(true);
                        }
                    })
                    .setNegativeButton(getString(R.string.cancle), null)
                    .create()
                    .show();
        }

        return checkError;
    }

    /**
     * 检查点航高是否超过最大航高
     *
     * @return
     */
    @Override
    public CheckError checkMaxFlyHeight() {

        CheckError error = null;

        int altitude = airRoutePara.getAltitude() > airRoutePara.getEntryHeight() ? airRoutePara.getAltitude() : airRoutePara.getEntryHeight();

        int maxHeight;
        Object value = KeyManager.getInstance().getValue(FlightControllerKey.create(FlightControllerKey.MAX_FLIGHT_HEIGHT));
        if (value instanceof Integer) {
            maxHeight = (int) value;
        } else {
            error = new CheckError(getString(R.string.error_get_max_flight_height));
            return error;
        }


        if (altitude > maxHeight) {
            error = new CheckError(String.format(getString(R.string.check_max_flight_height_failed), altitude, maxHeight));
        }

        return error;
    }

    @Override
    public CheckError checkGoHomeFlyHeight() {
        return null;
    }

    /**
     * 检查预计时长及电量
     *
     * @return
     */
    @Override
    public CheckError checkEnoughFlyTimeAndBattery() {

        CheckError checkError = null;

        if (fragMap.fullImageFlyTime == 0) {
            checkError = new CheckError("预计飞行时长为0分钟，不能执行此任务");
            return checkError;
        }

        float maxTime = BatteryManager.getBatteryLifeTime();
        if (fragMap.fullImageFlyTime > maxTime) {
            checkError = new CheckError("预计飞行时长超过" + (int) maxTime + "分钟，不能执行此任务");
            return checkError;
        }












        return checkError;
    }

    @Override
    public boolean checkHasCamera() {
       /* boolean flag = mProduct != null && mProduct.getCamera() != null;
        if (!flag) {
            showToast( "未检测到云台相机");
        }
        return flag;*/
        return true;
    }

    @Override
    public boolean checkMultiCamera() {
        if (mProduct != null && mProduct.isConnected() && mProduct.getCameras() != null) {
            int cameraSize = ((Aircraft) mProduct).getCameras().size();
            return cameraSize > 1;
        }
        return false;
    }

    public boolean checkAltitudeSafe(double homeLocationLatitude, double homeLocationLongitude,
                                     List<WayPointTask> mStartingLine, int altitude) {
        double homeAltitude = fragMap.demReaderUtils.getZValue(new LatLngInfo(homeLocationLatitude, homeLocationLongitude));
        if (fragMap.demReaderUtils.checkZValue(homeAltitude)) {
            for (WayPointTask wayPointTask : mStartingLine) {
                double pointAltitude = fragMap.demReaderUtils.getZValue(wayPointTask.getPosition());
                if (fragMap.demReaderUtils.checkZValue(pointAltitude) && pointAltitude > altitude + homeAltitude) {
                    showToast("警告！部分航线低于地形高！");
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isHasCamera() {
        return isAirCraftConnect() && mProduct.getCamera() != null;
    }

    public boolean isAirCraftConnect() {
        return mProduct != null && mProduct.isConnected();
    }

    /**
     * 得到当前的Home点(GPS84)
     *
     * @return
     */
    public LatLngInfo getHomePoint() {
        if (this.getHomePointFlag && LocationCoordinateUtils.checkGpsCoordinate(homeLocationLatitude, homeLocationLongitude))
            return new LatLngInfo(this.homeLocationLatitude, this.homeLocationLongitude);
        else
            return null;
    }

    public LocationCoordinate getAirCraftLocation() {
        return new LocationCoordinate(aircraftLocationLatitude, aircraftLocationLongitude, aircraftLocationAltitude);
    }

    /**
     * 修改标题名
     *
     * @return
     */
    public void setTitleName(String name) {
        tvTopTitle.setText(name);
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

    protected void showSimulateDialog() {

        if (checkAirCraftConnect()) {

            Point mapCenterPoint = fragMap.mapView.getCenter();
            LatLngInfo mapLatLng = CoordinateUtils.mapMercatorToGps84(mapCenterPoint.getX(), mapCenterPoint.getY());
            SimulatorDialog simulatorDialog = new SimulatorDialog();
            Bundle args = new Bundle();
            args.putDouble(PARAM_SIMULATOR_LATITUDE, mapLatLng.latitude);
            args.putDouble(PARAM_SIMULATOR_LONGITUDE, mapLatLng.longitude);
            simulatorDialog.setArguments(args);
            simulatorDialog.setConfirmListener(new IConfirmListener() {
                @Override
                public void onConfirm(String tag, Object object) {
                    if (object instanceof LocationCoordinate) {
                        startSimulate((LocationCoordinate) object);
                    }
                }
            });
            simulatorDialog.show(getFragmentManager(), "SimulatorDialog");
        }
    }

    public void startSimulate(@NotNull LocationCoordinate locationCoordinate) {

        final Simulator simulator = getSimulator();
        if (simulator != null) {
            final LocationCoordinate2D simulateLoc = new LocationCoordinate2D(locationCoordinate.latitude, locationCoordinate.longitude);
            final InitializationData data = InitializationData.createInstance(simulateLoc, 10, 16);
            showLoadProgressDialog("开始模拟");
            if (simulator.isSimulatorActive()) {
                simulator.stop(new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        dismissLoadProgressDialog();
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
                dismissLoadProgressDialog();
                if (djiError != null) {
                    showToast("开始模拟失败" + djiError.getDescription());
                } else {
                    showToast("开始模拟成功");
                }
            }
        });
    }

    public void stopSimulate() {
        final Simulator simulator = getSimulator();
        if (simulator != null) {
            simulator.stop(new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    dismissLoadProgressDialog();
                    if (djiError != null) {
                        showToast("停止模拟失败" + djiError.getDescription());
                    } else {
                        showToast("停止模拟成功");
                    }
                }
            });
        }
    }

    protected void showLowPowerReturnDialog() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (mGoHomeDialog == null) {
                    mGoHomeDialog = new GoHomeDialog(getActivity());
                }
                if (!mGoHomeDialog.isShowing()) {
                    mGoHomeDialog.setTitle("电量低，请立即返航！");
                    mGoHomeDialog.setMessage("电量低警告！10秒后自动返航");
                    mGoHomeDialog.setGoHomeListener(new GoHomeDialog.GoHomeListener() {
                        @Override
                        public void confirm() {
                            goHome();
                        }

                        @Override
                        public void cancel() {
                        }
                    });
                    mGoHomeDialog.show();
                }
            }
        });
    }

    protected void showAutoReturnDialog() {
        if (isAdded())
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (mGoHomeDialog == null) {
                        mGoHomeDialog = new GoHomeDialog(getActivity());
                    }
                    if (!mGoHomeDialog.isShowing()) {
                        mGoHomeDialog.setTitle("任务执行完成");
                        mGoHomeDialog.setMessage("任务执行完成，10秒后自动返航");
                        mGoHomeDialog.setGoHomeListener(new GoHomeDialog.GoHomeListener() {
                            @Override
                            public void confirm() {
                                goHome();
                            }

                            @Override
                            public void cancel() {
                                if (bIsShootingPhoto)
                                    stopTakePhoto();
                                if (bIsRecordingVideo)
                                    stopRecord();
                                if (fragMap.currentMode == AppConstant.OperationMode.ManualFullImage) {
                                    refreshMapButton(AppConstant.FlightAction.Unknown);
                                }
                            }
                        });
                        mGoHomeDialog.show();
                    }
                }
            });
    }

    public void showSmartReturnDialog(GoHomeDialog.GoHomeListener goHomeListener) {
        if (mGoHomeDialog == null) {
            mGoHomeDialog = new GoHomeDialog(getActivity());
        }
        if (!mGoHomeDialog.isShowing()) {
            mGoHomeDialog.setTitle("低电量自动返航！");
            mGoHomeDialog.setMessage("当前电量仅足够返航路程，请立即返航，10秒后自动返航");
            mGoHomeDialog.setGoHomeListener(goHomeListener);
            mGoHomeDialog.show(false);
        }
    }

    public void showSmartReturnTimeCount(int countdown) {
        if (mGoHomeDialog != null) {
            mGoHomeDialog.updateMessage("当前电量仅足够返航路程，请立即返航，" + countdown + "秒后自动返航");
            if (countdown == 1) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mGoHomeDialog.dismiss();
                    }
                }, 1000);

            }
        }
    }

    private void showAircraftStateDialog() {
        if (fragMap.mFlightCheck.getVisibility() == View.GONE) {
           /* if (mProduct == null || !mProduct.isConnected()) {
                ToastUtil.show(getActivity(), "请连接飞机");
                return;
            }*/
            fragMap.mFlightCheck.setVisibility(View.VISIBLE);
        } else
            fragMap.mFlightCheck.setVisibility(View.GONE);
        AircraftStateDialog aircraftStateDialog = new AircraftStateDialog();
        aircraftStateDialog.show(getFragmentManager(), "aircraft_state");
//




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
                        if (isAdded() && getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (smartRTHCountdown >= 9) {
                                        showSmartReturnDialog(mSmartGoHomeListener);
                                    }
                                    showSmartReturnTimeCount(smartRTHCountdown);
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
        if (mProduct != null) {
            FlightController flightController = ((Aircraft) mProduct).getFlightController();
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
    }

    public void showLoadProgressDialog(@Nullable String message) {
        if (mLoadProgressDlg == null) {
            mLoadProgressDlg = new BaseProgressDialog(getContext());
            mLoadProgressDlg.setCancelable(false);
        }
        mLoadProgressDlg.setMessage(message);
        mLoadProgressDlg.show();
    }

    public void dismissLoadProgressDialog() {
        if (mLoadProgressDlg != null && mLoadProgressDlg.isShowing()) {
            mLoadProgressDlg.dismiss();
        }
    }

    protected void showToast(String message) {
        handler.sendMessage(handler.obtainMessage(SHOW_TOAST, message));
    }

    private FlyFlightControllerKey mFlightControllerKey = FlyFlightControllerKey.create(FlyFlightControllerKey.AIRCRAFT_LOCATION);

    private void setUpKeys() {
        KeyManager.getInstance().addListener(FlightControllerKey.create(FlightControllerKey.GO_HOME_ASSESSMENT), mGoHomeAssessmentListener);
    }

    private void tearDownKeys() {
        KeyManager.getInstance().removeListener(mGoHomeAssessmentListener);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSimulatorEvent(SimulatorEvent event) {
        if (event.isEnable()) {
            showSimulateDialog();
        } else {
            stopSimulate();
        }
    }

    @Override
    public void onFlightControllerStateCallback(FlightControllerState state) {
        try {
            flightControllerState = state;
            if (!LocationCoordinateUtils.checkGpsCoordinate(state.getHomeLocation().getLatitude(), state.getHomeLocation().getLongitude()))
                return;

            homeLocationLatitude = state.getHomeLocation().getLatitude();
            homeLocationLongitude = state.getHomeLocation().getLongitude();
            if ((operateAction != AppConstant.OperateAction.Unknown
                    && operateAction != AppConstant.OperateAction.FinishTask)
                    && !LocationCoordinateUtils.checkGpsCoordinate(homeLocationLatitude, homeLocationLongitude)) {
                return;
            }
            bInitDrone = true;
            if (LocationCoordinateUtils.checkGpsCoordinate(homeLocationLatitude, homeLocationLongitude)) {
                getHomePointFlag = true;
            } else {
                getHomePointFlag = false;

                mUIThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        refreshMapButton(AppConstant.FlightAction.Unknown);
                        mcuState = DEFAULT_STATE;
                        tvMonitorInfo.setText(mcuState, TextView.BufferType.SPANNABLE);
                    }
                });
            }
            if (!getHomePointFlag)
                return;
            aircraftLocationAltitude = state.getAircraftLocation().getAltitude();

            if (!hasRTKSignal()) {
                aircraftLocationLatitude = state.getAircraftLocation().getLatitude();
                aircraftLocationLongitude = state.getAircraftLocation().getLongitude();
            }

            mDistanceX = LatLngUtils.getDistance(aircraftLocationLatitude, aircraftLocationLongitude, homeLocationLatitude, homeLocationLongitude);
            if (!state.isFlying()) {
                if (operateAction == AppConstant.OperateAction.FinishTask) {
                    refreshMapButton(AppConstant.FlightAction.Unknown);
                    mUIThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mcuState = DEFAULT_STATE;
                            tvMonitorInfo.setText(mcuState, TextView.BufferType.SPANNABLE);
                        }
                    });
                }
            }
            satelliteCount = state.getSatelliteCount();
            droneYaw = state.getAttitude().yaw;

            flightModeString = state.getFlightMode().toString();

            if (startTimeMillisecond > 0) {
                long now = new Date().getTime();
                mRunTime = (now - startTimeMillisecond) / 1000;
            }
            final float velocityX = state.getVelocityX();
            final float velocityY = state.getVelocityY();
            final float velocityZ = state.getVelocityZ();

            mHorizontalSpeed = Math.sqrt(Math.pow(velocityX, 2) + Math.pow(velocityY, 2));

            mVerticalSpeed = Math.abs(state.getVelocityZ());

            showMonitorText();

            if (LocationCoordinateUtils.checkGpsCoordinate(aircraftLocationLatitude,
                    aircraftLocationLongitude)) {

                mUIThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        tvMonitorInfo.setText(mcuState, TextView.BufferType.SPANNABLE);
                        if (LocationCoordinateUtils.checkGpsCoordinate(aircraftLocationLatitude, aircraftLocationLongitude)) {
                            fragMap.updateDroneLocation(
                                    aircraftLocationLatitude, aircraftLocationLongitude, droneYaw);
                            if (getHomePointFlag && isAdded()) {
                                fragMap.addHomePointOnMap(homeLocationLatitude, homeLocationLongitude);
                                fragMap.addLineBetweenDroneAndHome(homeLocationLatitude, homeLocationLongitude, aircraftLocationLatitude, aircraftLocationLongitude);
                            }
                        }
                    }
                });
            }

            if (state.isFlying()) {

                isAlreadyFly = true;

            } else {
                if (isAlreadyFly) {
                    isAlreadyFly = false;
                    if (mOnFlightLandingListener != null) {
                        mOnFlightLandingListener.onLanded();
                    }
                    FlightRecordManager.getInstance().endRecord();
                }
            }

            FlyKeyManager.getInstance().setValue(mFlightControllerKey, getAirCraftLocation());

            LatLngInfo homePoint = getHomePoint();
            if (homePoint != null) {
                FlightRecordManager.getInstance().saveDetailRecord(getAirCraftLocation(),
                        new LocationCoordinate(homePoint.latitude, homePoint.longitude), state, mHardwareState, mBatteryState);
            }
      /*      FlightRecordManager.getInstance().uploadFlightMonitorInfo(RouteService.getUserName(), getAirCraftLocation(),
                    mRunTime, mDistanceX, state, mHardwareState, mBatteryState, mGimbalState);*/

            FlightUploadStates uploadStates = new FlightUploadStates();
            uploadStates.setFlightTime(mRunTime);
            uploadStates.setFlightDistance(mDistanceX);
            uploadStates.setFlightControllerState(state);
            uploadStates.setBatteryState(mBatteryState);
            uploadStates.setGimbalState(mGimbalState);
            uploadStates.setHardwareState(mHardwareState);
            uploadStates.setAircraftLocation(new LocationCoordinate(aircraftLocationLatitude, aircraftLocationLongitude, aircraftLocationAltitude));
            uploadStates.setAircraftSerialNumber(mAirCraftSerialNumber);
            uploadStates.setBatterySerialNumber(mBatterySerialNumber);

            FlightPlatformManager.getInstance().senData(uploadStates.getFlightUploadData());

            getCameraOpticalZoomFocalLength();

            switch (state.getFlightMode()) {
                case MANUAL:
                    break;
                case ATTI:
                case GPS_SPORT:
                    operateAction = AppConstant.OperateAction.Unknown;
                    refreshMapButton(AppConstant.FlightAction.Unknown);
                    if (bIsShootingPhoto)
                        stopTakePhoto();
                    if (bIsRecordingVideo)
                        stopRecord();
                    FlightRecordManager.getInstance().endRecord();
                    break;
                case AUTO_TAKEOFF:// 起飞

                    exposureNum = 0;
                case ASSISTED_TAKEOFF:

                    FlightRecordManager.getInstance()
                            .createNewRecord(getAirCraftLocation());
                    break;
                case AUTO_LANDING:// 降落
                    startTimeMillisecond = 0;
                    operateAction = AppConstant.OperateAction.Unknown;
                    break;
                case GO_HOME:

                    if (operateAction != AppConstant.OperateAction.GoHome && operateAction != AppConstant.OperateAction.FinishTask) {
                        if (bIsShootingPhoto)
                            stopTakePhoto();
                        if (bIsRecordingVideo)
                            stopRecord();
                    }
                    operateAction = AppConstant.OperateAction.GoHome;
                    refreshMapButton(AppConstant.FlightAction.Unknown);
                    break;
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onRTKStateCallback(RTKState rtkState) {

    }

    @Override
    public void onVisionControlStateCallback(VisionControlState visionControlState) {

    }

    @Override
    public void onVisionDetectionStateCallBack(VisionDetectionState visionDetectionState) {

    }

    @Override
    public void onHardwareStateCallback(@NonNull HardwareState hardwareState) {
        mHardwareState = hardwareState;

        CameraManager.shootPhotoAndRecordByRC(hardwareState, mCameraState);

        int mode = config.getMode();





//


//











//


        if (hardwareState.getGoHomeButton().isClicked()) {
            if (((Aircraft) mProduct).getFlightController().getSimulator() != null &&
                    ((Aircraft) mProduct).getFlightController().getState().isGoingHome()) {
                ((Aircraft) mProduct).getFlightController().cancelGoHome(new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        if (djiError != null) {
                            Logger.i("mmmm------cancel gohome error: " + djiError);
                        } else {
                            operateAction = AppConstant.OperateAction.Unknown;
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onGimbalStateCallback(@NonNull GimbalState gimbalState) {
        mGimbalState = gimbalState;
        mGimbalAngle = gimbalState.getAttitudeInDegrees().getPitch() + 90.0f;
        mGimbalRoll = gimbalState.getAttitudeInDegrees().getRoll();
        mGimbalYaw = gimbalState.getAttitudeInDegrees().getYaw();

        if (gimbalState.getMode() != null) {
            GimbalMode gimbalMode = gimbalState.getMode();
            FlyKeyManager.getInstance().setValue(mGimbalModeKey, gimbalMode);
        }
    }

    @Override
    public void onBatteryStateStateCallback(BatteryState batteryState) {

        if (AircraftManager.isOnlyFlightController()) {
            mBatteryState = batteryState;
            BatteryCellVoltageLevel cellVoltageLevel = batteryState.getCellVoltageLevel();
            mCellVoltageLevel = cellVoltageLevel;
            final double amountBattery = batteryState.getVoltage();
            if (isAdded())
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (amountBattery > 0) {
                            DecimalFormat df = new DecimalFormat("#.00");
                            String f1 = df.format((amountBattery / 1000));
                            mTvBattery.setText(f1 + " v");
                            if (cellVoltageLevel != null) {
                                switch (cellVoltageLevel) {
                                    default:
                                    case UNKNOWN:
                                    case LEVEL_0:
                                        mTvBattery.setTextColor(Color.WHITE);
                                        break;
                                    case LEVEL_1:
                                        mTvBattery.setTextColor(Color.YELLOW);
                                        break;
                                    case LEVEL_2:
                                    case LEVEL_3:
                                        mTvBattery.setTextColor(Color.RED);
                                        break;
                                }
                            }
                        }
                    }
                });
        } else {
            mBatteryState = batteryState;
            remainPower = batteryState.getChargeRemainingInPercent();
            mBatteryTemperature = batteryState.getTemperature();
            mCellVoltageLevel = batteryState.getCellVoltageLevel();
        }
    }

    @Override
    public void onCameraSystemStateCallback(@NonNull SystemState systemState) {
        mCameraState = systemState;
        if (systemState.getMode() == SettingsDefinitions.CameraMode.SHOOT_PHOTO) {


            if (systemState.isShootingSinglePhoto()) {
                cameraMode = "正在拍照";
            } else if (systemState.isShootingBurstPhoto())
                cameraMode = "正在连拍";
            else if (systemState.isShootingIntervalPhoto())
                cameraMode = "定时拍照";
            else
                cameraMode = "拍照模式";
        } else if (systemState.getMode() == SettingsDefinitions.CameraMode.RECORD_VIDEO) {
            if (systemState.isRecording())
                cameraMode = "正在录像";
            else
                cameraMode = "视频模式";


            if ((config.getMode() == 9 || config.getMode() == 5 || config.getMode() == 1) &&
                    (fragMap.currentMode == AppConstant.OperationMode.LinePatrolVideo)) {
                if (!isRecording && systemState.isRecording()) {
                    startRecordPoint();
                } else if (isRecording && !systemState.isRecording()) {
                    isRecording = false;
                    finishRecordPoint();
                }
            }
        }
    }

    @Override
    public void onCameraExposureSettingsCallback(@NonNull ExposureSettings exposureSettings) {
        cameraExposureCompensation = MyUtils.getExposureValue(exposureSettings.getExposureCompensation().value());

    }

    @Override
    public void onCameraStorageCallBack(@NonNull StorageState sdCardState) {
        if (mSdcardRemainSize != sdCardState.getRemainingSpaceInMB()) {
            mSdcardRemainSize = sdCardState.getRemainingSpaceInMB();

        }
    }

    @Override
    public void onCameraMediaFileCallback(@NonNull MediaFile mediaFile) {
        if (AppConstant.OperateAction.ExecuteTask == operateAction) {
            if (mediaFile.getMediaType() == MediaFile.MediaType.JPEG ||
                    mediaFile.getMediaType() == MediaFile.MediaType.RAW_DNG) {

                int index = mediaFile.getIndex();

                if (index > 0 && currentPhotoIndex != index) {

                    currentPhotoIndex = index;
                    exposureNum++;


                    updateMissionStart(new DataBaseUtils.onExecResult() {
                        @Override
                        public void execResult(boolean succ, String errStr) {
                            if (succ) {

                                insertMissionPhoto(mediaFile);
                            }
                        }

                        @Override
                        public void execResultWithResult(boolean succ, Object result, String errStr) {

                        }

                        @Override
                        public void setExecCount(int i, int count) {

                        }
                    });

                    if (exposureNum > 1) {
                        insertMissionPhoto(mediaFile);
                    }
                }
            }

        }
    }

    @Override
    public void onProductDisconnect() {

    }

    @Override
    public void onProductConnect(BaseProduct baseProduct) {

    }

    @Override
    public void onProductComponentChange(BaseProduct.ComponentKey componentKey, BaseComponent newComponent, boolean isConnected) {

    }
}