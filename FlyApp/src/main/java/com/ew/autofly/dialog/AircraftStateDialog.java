package com.ew.autofly.dialog;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.ew.autofly.R;
import com.ew.autofly.application.EWApplication;
import com.ew.autofly.config.SharedConfig;
import com.ew.autofly.constant.AppConstant;
import com.ew.autofly.interfaces.OnAircraftDialogClickListener;
import com.ew.autofly.logger.Logger;
import com.ew.autofly.utils.ToastUtil;

import java.util.Timer;
import java.util.TimerTask;

import dji.common.airlink.ChannelInterference;
import dji.common.battery.BatteryState;
import dji.common.error.DJIError;
import dji.common.flightcontroller.CompassCalibrationState;
import dji.common.flightcontroller.FlightControllerState;
import dji.common.flightcontroller.imu.CalibrationState;
import dji.common.flightcontroller.imu.IMUState;
import dji.common.flightcontroller.imu.SensorState;
import dji.common.gimbal.GimbalMode;
import dji.common.gimbal.GimbalState;

import dji.common.util.CommonCallbacks;
import dji.keysdk.callback.ActionCallback;
import dji.sdk.airlink.LightbridgeLink;
import dji.sdk.base.BaseProduct;
import dji.sdk.products.Aircraft;
import dji.sdk.realname.AppActivationManager;
import dji.ux.workflow.CompassCalibratingWorkFlow;


public class AircraftStateDialog extends DialogFragment implements View.OnClickListener, OnAircraftDialogClickListener {
    private BaseProduct mProduct;
    private TextView tvIMU;
    private TextView tvCompass;
    private TextView tvChlstatus;
    private TextView tvAircraftMode;
    private TextView tvRCBattery;
    private TextView tvBatteryTemp;
    private TextView tvGimbal;
    private TextView tvFirmware;
    private Button btnCompass;
    private boolean isFlying = false;
    private FlightControllerState mFlightControllerState;
    private BatteryState mBatteryState;
    private GimbalState mGimbalState;
    private Timer timer;
    private SharedConfig config;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mProduct = EWApplication.getProductInstance();
            if (mProduct != null && mProduct.isConnected() && mProduct.getCamera() != null) {
                bindState();
            } else {
                unBindState();
            }
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            dialog.getWindow().setGravity(Gravity.LEFT);
            if (dm.widthPixels > dm.heightPixels)
                dialog.getWindow().setLayout((int) (dm.widthPixels * 0.6), ViewGroup.LayoutParams.MATCH_PARENT);
            else
                dialog.getWindow().setLayout((int) (dm.heightPixels * 0.6), ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setCanceledOnTouchOutside(false);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int style = DialogFragment.STYLE_NO_TITLE, theme = 0;
        setStyle(style, theme);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_aircraft_state, container, false);
        config = new SharedConfig(getActivity());
        initView(view);
        if (isAdded()) {
            IntentFilter filter = new IntentFilter(AppConstant.BROADCAST_DJI_PRODUCT_CONNECTION_CHANGE);
            getActivity().registerReceiver(mReceiver, filter);
        }
        return view;
    }

    private void initView(View view) {
        view.findViewById(R.id.tv_cancel).setOnClickListener(this);
        view.findViewById(R.id.btn_check_list_compass).setOnClickListener(this);

        tvIMU = (TextView) view.findViewById(R.id.tv_check_list_imu);
        tvCompass = (TextView) view.findViewById(R.id.tv_check_list_compass);
        tvChlstatus = (TextView) view.findViewById(R.id.tv_check_list_chlstatus);
        tvAircraftMode = (TextView) view.findViewById(R.id.tv_check_list_aircraft_mode);
        tvRCBattery = (TextView) view.findViewById(R.id.tv_check_list_rc_battery);
        tvBatteryTemp = (TextView) view.findViewById(R.id.tv_check_list_battery_temp);
        tvGimbal = (TextView) view.findViewById(R.id.tv_check_list_gimbal);
        tvFirmware = (TextView) view.findViewById(R.id.tv_check_list_firmware);
        btnCompass = (Button) view.findViewById(R.id.btn_check_list_compass);

        mProduct = EWApplication.getProductInstance();
        bindState();
    }


    private void bindState() {
        if (mProduct != null && mProduct.isConnected()) {
            if (EWApplication.getAircraftInstance() != null && AppActivationManager.getInstance() != null) {
                btnCompass.setVisibility(View.VISIBLE);
                tvFirmware.setVisibility(View.VISIBLE);
                try {
                    initDron();
                    initFirmwareCheck();
                    initIMUState();
                    initCompass();

                    initFlightMode();
                    initElectricity();
                    initAircraftTemp();
                    initGimbalState();
                } catch (Exception e) {
                    Logger.i("AircraftState Error :" + e.toString() + "   \n");
                }
            }
        }
    }

    private void unBindState() {
        tvIMU.setText("N/A");
        tvIMU.setTextColor(Color.WHITE);
        tvCompass.setText("N/A");
        tvCompass.setTextColor(Color.WHITE);
        tvChlstatus.setText("N/A");
        tvChlstatus.setTextColor(Color.WHITE);
        tvAircraftMode.setText("N/A");
        tvRCBattery.setText("N/A");
        tvBatteryTemp.setText("N/A");
        tvGimbal.setText("N/A");
        tvGimbal.setTextColor(Color.WHITE);
        btnCompass.setVisibility(View.GONE);
        tvFirmware.setVisibility(View.GONE);
    }

    private void initDron() {
        if (mFlightControllerState != null && mFlightControllerState.isFlying()) {
            isFlying = true;
        } else {
            isFlying = false;
        }
    }

    private void initFirmwareCheck() {
        switch (AppActivationManager.getInstance().getAppActivationState()) {
            case NOT_SUPPORTED:
                tvFirmware.setText("请升级固件");
                tvFirmware.setTextColor(Color.RED);
                break;
        }
    }

    private void initGimbalState() {
        if (mGimbalState != null && mGimbalState.getMode() != GimbalMode.UNKNOWN) {
            tvGimbal.setText("正常");
            tvGimbal.setTextColor(Color.WHITE);
        } else {
            tvGimbal.setText("异常");
            tvGimbal.setTextColor(Color.RED);
        }
    }

    private void initAircraftTemp() {
        if (mBatteryState != null) {
            int temp = (int) mBatteryState.getTemperature();
            if (temp >= 60) {
                tvBatteryTemp.setText("电池温度过高");
                tvBatteryTemp.setTextColor(Color.RED);
            } else {
                tvBatteryTemp.setText(temp + ".0°C");
                tvBatteryTemp.setTextColor(Color.WHITE);
            }
        }
    }

    private void initElectricity() {






    }

    private void initFlightMode() {
        if (timer == null)
            timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (isAdded())
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mProduct != null && mProduct.isConnected()) {
                                String mode = ((Aircraft) mProduct).getFlightController().getState().getFlightModeString();
                                tvAircraftMode.setText(mode);
                            }
                        }
                    });
            }
        }, 0, 200);
    }

    private void initChlstatus() {
        mProduct.getAirLink().getLightbridgeLink().setChannelInterferenceCallback(new LightbridgeLink.ChannelInterferenceCallback() {
            @Override
            public void onResult(final ChannelInterference[] channelInterferences) {
                mProduct.getAirLink().getLightbridgeLink().getChannelNumber(new CommonCallbacks.CompletionCallbackWith<Integer>() {
                    @Override
                    public void onSuccess(final Integer integer) {
                        if (isAdded())
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    for (int i = 0; i < channelInterferences.length; i++) {
                                        if (channelInterferences[i].getChannel() + 13 == integer) {
                                            if (channelInterferences[i].getPower() < -90) {
                                                tvChlstatus.setText("良好");
                                                tvChlstatus.setTextColor(Color.GREEN);
                                            } else if (channelInterferences[i].getPower() < -70) {
                                                tvChlstatus.setText("一般");
                                                tvChlstatus.setTextColor(Color.YELLOW);
                                            } else {
                                                tvChlstatus.setText("较差");
                                                tvChlstatus.setTextColor(Color.RED);
                                            }
                                        }
                                    }
                                }
                            });
                    }

                    @Override
                    public void onFailure(DJIError djiError) {

                    }
                });
            }
        });
    }

    private void initCompass() {
        if (((Aircraft) mProduct).getFlightController().getCompass().getCalibrationState() ==
                CompassCalibrationState.NOT_CALIBRATING) {
            tvCompass.setText("正常");
            tvCompass.setTextColor(Color.WHITE);
        } else {
            tvCompass.setText("指南针异常");
            tvCompass.setTextColor(Color.RED);
        }
    }

    private void initIMUState() {
      
        tvIMU.setText(config.getIMUState() ? "正常" : "IMU异常");
        ((Aircraft) mProduct).getFlightController().setIMUStateCallback(new IMUState.Callback() {
            @Override
            public void onUpdate(@NonNull IMUState imuState) {
                if ((imuState.getAccelerometerState() != SensorState.UNKNOWN && imuState.getAccelerometerState() != SensorState.LARGE_BIAS) &&
                        (imuState.getCalibrationState() != CalibrationState.UNKNOWN && imuState.getCalibrationState() != CalibrationState.FAILED) &&
                        (imuState.getGyroscopeState() != SensorState.UNKNOWN && imuState.getGyroscopeState() != SensorState.LARGE_BIAS)) {
                    tvIMU.setText("正常");
                    config.setIMUState(true);
                    tvIMU.setTextColor(Color.WHITE);
                } else {
                    tvIMU.setText("IMU异常");
                    config.setIMUState(false);
                    tvIMU.setTextColor(Color.RED);
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel:
                this.dismiss();
                break;
            case R.id.btn_check_list_compass:
              
                if (mProduct != null && mProduct.isConnected()) {
                    if (isFlying) {
                        ToastUtil.show(getActivity(), "飞机已起飞，无法校验指南针");
                        return;
                    }
                    CompassCalibratingWorkFlow.startCalibration(new ActionCallback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onFailure(@NonNull final DJIError djiError) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtil.show(getActivity(), djiError.toString());
                                }
                            });
                        }
                    });
                } else
                    ToastUtil.show(getActivity(), "请连接飞机");
                break;
        }
    }

    @Override
    public void onDestroy() {
        if ((mProduct) != null) {
            ((Aircraft) mProduct).getFlightController().setIMUStateCallback(null);
            ((Aircraft) mProduct).getFlightController().getCompass().setCalibrationStateCallback(null);
            if (mProduct.getAirLink() != null && mProduct.getAirLink().getLightbridgeLink() != null)
                mProduct.getAirLink().getLightbridgeLink().setChannelInterferenceCallback(null);
            if (((Aircraft) mProduct).getRemoteController() != null)
                ((Aircraft) mProduct).getRemoteController().setChargeRemainingCallback(null);
        }

        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
        if (isAdded())
            getActivity().unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    @Override
    public void onFlightControllerState(FlightControllerState flightControllerState) {
        this.mFlightControllerState = flightControllerState;
    }

    @Override
    public void onBatteryState(BatteryState batteryState) {
        this.mBatteryState = batteryState;
    }

    @Override
    public void onGimbalState(GimbalState gimbalState) {
        this.mGimbalState = gimbalState;
    }
}
