package com.ew.autofly.struct.presenter;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.ew.autofly.base.BaseMvpPresenter;
import com.ew.autofly.model.AircraftManager;
import com.ew.autofly.model.BatteryManager;
import com.ew.autofly.model.ConnectManager;
import com.ew.autofly.model.ConnectManager.OnProductConnectListener;
import com.ew.autofly.struct.presenter.interfaces.IBaseDroneStateCallback;
import com.ew.autofly.struct.presenter.interfaces.IBaseFlightPresenter;
import com.ew.autofly.struct.view.IBaseFlightView;
import com.flycloud.autofly.base.framework.rx.RxManager;

import dji.common.battery.BatteryState;
import dji.common.camera.ExposureSettings;
import dji.common.camera.StorageState;
import dji.common.camera.SystemState;
import dji.common.error.DJIError;
import dji.common.flightcontroller.FlightControllerState;
import dji.common.flightcontroller.RTKState;
import dji.common.flightcontroller.VisionControlState;
import dji.common.flightcontroller.VisionDetectionState;
import dji.common.gimbal.GimbalState;
import dji.common.remotecontroller.HardwareState;
import dji.common.util.CommonCallbacks;
import dji.sdk.base.BaseComponent;
import dji.sdk.base.BaseProduct;
import dji.sdk.battery.Battery;
import dji.sdk.camera.Camera;
import dji.sdk.flightcontroller.FlightAssistant;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.flightcontroller.RTK;
import dji.sdk.gimbal.Gimbal;
import dji.sdk.media.MediaFile;
import dji.sdk.products.Aircraft;
import dji.sdk.remotecontroller.RemoteController;
import io.reactivex.disposables.Disposable;


public abstract class BaseFlightPresenterImpl<V extends IBaseFlightView> extends BaseMvpPresenter<V> implements IBaseFlightPresenter, IBaseDroneStateCallback, OnProductConnectListener {

    protected Handler mUIThreadHandler = new Handler(Looper.getMainLooper());

    protected RxManager mRxManager = new RxManager();

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

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        ConnectManager.getInstance().register(this);
        initAirCraftStateCallback();
        setUpKeys();
    }

    public void detachView() {
        super.detachView();
        ConnectManager.getInstance().unRegister(this);
    }

    @Override
    public boolean isAirCraftConnect() {
        return AircraftManager.isAircraftConnected();
    }

    @Override
    public void initAirCraftStateCallback() {

        Aircraft aircraft = AircraftManager.getAircraft();

        FlightController flightController = AircraftManager.getFlightController();
        if (flightController != null) {
            flightController.setStateCallback(new FlightControllerState.Callback() {
                @Override
                public void onUpdate(@NonNull FlightControllerState state) {

                    onFlightControllerStateCallback(state);
                }
            });

            RTK djiRtk = flightController.getRTK();
            if (djiRtk != null) {
                djiRtk.setStateCallback(new RTKState.Callback() {
                    @Override
                    public void onUpdate(@NonNull RTKState djiRtkState) {
                        onRTKStateCallback(djiRtkState);
                    }
                });
            }

            FlightAssistant flightAssistant = flightController.getFlightAssistant();
            if (flightAssistant != null) {
                flightAssistant.setVisionControlStateUpdatedcallback(new VisionControlState.Callback() {
                    @Override
                    public void onUpdate(VisionControlState visionControlState) {
                        onVisionControlStateCallback(visionControlState);
                    }
                });

                flightAssistant.setVisionDetectionStateUpdatedCallback(new VisionDetectionState.Callback() {
                    @Override
                    public void onUpdate(@NonNull VisionDetectionState visionDetectionState) {
                        onVisionDetectionStateCallBack(visionDetectionState);
                    }
                });
            }

        }

        if (aircraft != null) {
            final RemoteController rc = aircraft.getRemoteController();
            if (null != rc) {
                rc.setHardwareStateCallback(new HardwareState.HardwareStateCallback() {
                    @Override
                    public void onUpdate(@NonNull HardwareState hardwareState) {
                        onHardwareStateCallback(hardwareState);
                    }
                });
            }


            Gimbal gimbal = aircraft.getGimbal();
            if (gimbal != null) {
              
                gimbal.setStateCallback(new GimbalState.Callback() {
                    @Override
                    public void onUpdate(@NonNull GimbalState gimbalState) {
                        onGimbalStateCallback(gimbalState);
                    }

                });
            }


            Camera camera = aircraft.getCamera();
            if (camera != null) {
              
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
            }

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
            if (mRxManager != null) {
                mRxManager.add(batteryDispose);
            }

            getAirCraftSerialNumber();

            getBatterySerialNumber();
        }
    }

    @Override
    public void onFlightControllerStateCallback(FlightControllerState state) {

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

    }

    @Override
    public void onGimbalStateCallback(@NonNull GimbalState gimbalState) {

    }

    @Override
    public void onBatteryStateStateCallback(BatteryState batteryState) {

    }

    @Override
    public void onCameraSystemStateCallback(@NonNull SystemState systemState) {

    }

    @Override
    public void onCameraExposureSettingsCallback(@NonNull ExposureSettings exposureSettings) {

    }

    @Override
    public void onCameraStorageCallBack(@NonNull StorageState sdCardState) {

    }

    @Override
    public void onCameraMediaFileCallback(@NonNull MediaFile mediaFile) {

    }

    @Override
    public void onProductDisconnect() {

    }

    @Override
    public void onProductConnect(BaseProduct baseProduct) {
        initAirCraftStateCallback();
    }

    @Override
    public void onProductComponentChange(BaseProduct.ComponentKey componentKey, BaseComponent newComponent, boolean isConnected) {
        if (isConnected) {
            initAirCraftStateCallback();
        }
    }

    protected void showToastDialog(final String msg) {
        mUIThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (isViewAttached()) {
                    getView().showToastDialog(msg);
                }
            }
        });
    }

    protected void showToast(final String msg) {
        mUIThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (isViewAttached()) {
                    getView().showToast(msg);
                }
            }
        });
    }

    protected void showLoading(final boolean isShow, final String loadingMsg) {
        mUIThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (isViewAttached()) {
                    getView().showLoading(isShow, loadingMsg);
                }
            }
        });
    }

    @Override
    public void removeAircraftStateCallback() {
        try {
            Aircraft aircraft = AircraftManager.getAircraft();
            if (aircraft != null) {

                FlightController flightController = AircraftManager.getFlightController();
                if (flightController != null) {
                    flightController.setStateCallback(null);
                    RTK djiRtk = flightController.getRTK();
                    if (djiRtk != null) {
                        djiRtk.setStateCallback(null);
                    }
                    FlightAssistant flightAssistant = flightController.getFlightAssistant();
                    if (flightAssistant != null) {
                        flightAssistant.setVisionControlStateUpdatedcallback(null);
                    }
                }
                if (aircraft.getBattery() != null)
                    aircraft.getBattery().setStateCallback(null);
                if (aircraft.getAirLink().isLightbridgeLinkSupported())
                    aircraft.getAirLink().getLightbridgeLink().setRemoteControllerAntennaRSSICallback(null);
                if (aircraft.getCamera() != null) {
                    aircraft.getCamera().setSystemStateCallback(null);
                    aircraft.getCamera().setStorageStateCallBack(null);
                    aircraft.getCamera().setMediaFileCallback(null);
                }
                if (aircraft.getGimbal() != null)
                    aircraft.getGimbal().setStateCallback(null);

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void setUpKeys() {

    }

    @Override
    public void tearDownKeys() {

    }

    protected void testMessageShow(String msg) {
        showToastDialog(msg);
    }

    @Override
    public void onDestroy() {
        removeAircraftStateCallback();
        tearDownKeys();
        if (mRxManager != null) {
            mRxManager.clear();
        }
    }
}
