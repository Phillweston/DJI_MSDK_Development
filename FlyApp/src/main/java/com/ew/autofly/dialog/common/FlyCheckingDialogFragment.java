package com.ew.autofly.dialog.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.ew.autofly.R;
import com.ew.autofly.constant.AppConstant;
import com.ew.autofly.dialog.CustomDialog;
import com.ew.autofly.dialog.base.BaseDialogFragment;
import com.ew.autofly.entity.FlyCheckStatus;
import com.ew.autofly.model.AircraftManager;
import com.ew.autofly.utils.SoundPlayerUtil;
import com.ew.autofly.utils.ToastUtil;
import com.ew.autofly.widgets.SlideFlightView;
import com.ew.autofly.widgets.controls.ExtImageButton;

import dji.common.battery.BatteryCellVoltageLevel;


public class FlyCheckingDialogFragment extends BaseDialogFragment {

    public final static String ARG_PARAM_FLYCHECK_STATUS = "FLYCHECK_STATUS";

    private ExtImageButton btnConnectInfo;
    private ExtImageButton btnGSInfo;
    private ExtImageButton btnGPSInfo;
    private ExtImageButton btnLocationInfo;
    private ExtImageButton btnHomeInfo;
    public ExtImageButton btnControlInfo;
    private ExtImageButton btnSMSStorageInfo;
    private ExtImageButton btnPhotoStorageInfo;
    private ExtImageButton btnBatteryInfo;
    public ExtImageButton btnTaskInfo;
    private SlideFlightView slideFlightView;
    private ImageButton btnClose;

    private FlyCheckStatus mFlyCheckStatus;

    private boolean bCheckResult = false;

    
    private boolean isSDRemainEnough = false;

    public void setFlyCheckingListener(FlyCheckingFragmentListener flyCheckingFragmentListener) {
        mFlyCheckingFragmentListener = flyCheckingFragmentListener;
    }

    private FlyCheckingFragmentListener mFlyCheckingFragmentListener;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String error = intent.getStringExtra(AppConstant.BROADCAST_UPLOAD_MISSION_ERROR);
            if (TextUtils.isEmpty(error)) {
                int progress = intent.getIntExtra(AppConstant.BROADCAST_UPLOAD_MISSION_PROGRESS, 0);
                btnTaskInfo.setTextViewText(String.format("航点已上传%d", progress) + "%");
                if (progress == 100 && slideFlightView != null) {

                    if (bCheckResult) {
                        slideFlightView.mTextColor = "#000000";
                        slideFlightView.isPermitClick = true;
                        slideFlightView.invalidate();
                        btnTaskInfo.setImageResource(R.drawable.ok);
                        btnTaskInfo.setTextViewText("航点上传成功");
                        btnTaskInfo.setTextViewColor(getResources().getColor(R.color.white));
                    } else {
                        btnTaskInfo.setImageResource(R.drawable.unpass);
                        btnTaskInfo.setTextViewText("检查项未通过,请重新上传");
                        btnTaskInfo.setTextViewColor(getResources().getColor(R.color.red));
                    }
                }
            } else {
                ToastUtil.show(getActivity(), error);
                btnTaskInfo.setTextViewText("航点上传失败" + error);
                btnTaskInfo.setImageResource(R.drawable.unpass);
                btnTaskInfo.setTextViewColor(getResources().getColor(R.color.red));
                bCheckResult = false;
            }

        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFlyCheckStatus = getArguments().getParcelable(ARG_PARAM_FLYCHECK_STATUS);
        SoundPlayerUtil.getInstance(getContext()).playSound(R.raw.flight_check, 0);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_flychecking, container, false);
        bindField(view);
        return view;
    }

    private void bindField(View view) {
        btnConnectInfo = (ExtImageButton) view.findViewById(R.id.btn_connect_info);
        btnGSInfo = (ExtImageButton) view.findViewById(R.id.btn_gs_info);
        btnGPSInfo = (ExtImageButton) view.findViewById(R.id.btn_gps_info);
        btnLocationInfo = (ExtImageButton) view.findViewById(R.id.btn_location_info);
        btnHomeInfo = (ExtImageButton) view.findViewById(R.id.btn_home_info);
        btnControlInfo = (ExtImageButton) view.findViewById(R.id.btn_control_info);
        btnSMSStorageInfo = (ExtImageButton) view.findViewById(R.id.btn_sms_storage_info);
        btnPhotoStorageInfo = (ExtImageButton) view.findViewById(R.id.btn_photo_storage_info);
        btnBatteryInfo = (ExtImageButton) view.findViewById(R.id.btn_battery_info);
        btnTaskInfo = (ExtImageButton) view.findViewById(R.id.btn_task_info);
        slideFlightView = (SlideFlightView) view.findViewById(R.id.slidetoflight);
        btnClose = (ImageButton) view.findViewById(R.id.tv_close);
        btnControlInfo.setTextViewText("遥控模式检查中...");
        btnTaskInfo.setTextViewText("航点正在上传...");
        btnTaskInfo.setTextViewColor(getResources().getColor(R.color.white));

        btnLocationInfo.setTextViewText("飞机处于航飞区附近");
        btnHomeInfo.setTextViewText("返航点获取成功");

        if (AircraftManager.isOnlyFlightController()) {
            btnBatteryInfo.setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (isAdded()) {
            IntentFilter filter = new IntentFilter(AppConstant.BROADCAST_UPLOAD_MISSION);
            getActivity().registerReceiver(mReceiver, filter);
        }
        init();
    }

    @Override
    public void onStart() {
        super.onStart();
        setSize(0.58f, 0.75f);
        initListener();
    }

    private void initListener() {
        slideFlightView.setOnUnLockListener(new SlideFlightView.OnUnLockListener() {
            @Override
            public void setUnLocked(boolean unLocked) {
                if (unLocked) {
                    if (bCheckResult) {

                        if (isSDRemainEnough) {
                            if (mFlyCheckingFragmentListener != null) {
                                mFlyCheckingFragmentListener.onFlyCheckingComplete(true);
                            }
                            dismiss();
                        } else {
                            CustomDialog.Builder deleteDialog = new CustomDialog.Builder(getActivity());
                            deleteDialog.setTitle(getResources().getString(R.string.notice))
                                    .setMessage(getResources().getString(R.string.fly_check_sd_no_enough))
                                    .setPositiveButton(getResources().getString(R.string.sure), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (mFlyCheckingFragmentListener != null) {
                                                mFlyCheckingFragmentListener.onFlyCheckingComplete(true);
                                            }
                                            dismiss();
                                        }
                                    })
                                    .setNegativeButton(getResources().getString(R.string.cancle), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            slideFlightView.reset();
                                        }
                                    })
                                    .create()
                                    .show();
                        }
                    }
                }
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAdded()) {
                    if (mFlyCheckingFragmentListener != null) {

                        mFlyCheckingFragmentListener.onFlyCheckUpload(false);
                    }
                    dismiss();

                }
            }
        });
    }

    private void init() {
        boolean checkFlag = true;
        if (mFlyCheckStatus.isConnected()) {
            btnConnectInfo.setTextViewText("飞机连接成功");
            btnConnectInfo.setImageResource(R.drawable.ok);
            btnConnectInfo.setTextViewColor(getResources().getColor(R.color.white));
        } else {
            btnConnectInfo.setTextViewText("飞机连接失败");
            btnConnectInfo.setImageResource(R.drawable.unpass);
            btnConnectInfo.setTextViewColor(getResources().getColor(R.color.red));

            checkFlag = false;
        }
        btnGPSInfo.setTextViewText("GPS获取了" + mFlyCheckStatus.getSatelliteCount() + "/16颗卫星");
        if (mFlyCheckStatus.getSatelliteCount() > 5) {
            btnGPSInfo.setImageResource(R.drawable.ok);
            btnGPSInfo.setTextViewColor(getResources().getColor(R.color.white));
        } else {
            btnGPSInfo.setImageResource(R.drawable.unpass);
            btnGPSInfo.setTextViewColor(getResources().getColor(R.color.red));

            checkFlag = false;
        }

        if (mFlyCheckStatus.isCompassOk()) {
            btnGSInfo.setImageResource(R.drawable.ok);
            btnGSInfo.setTextViewText("指南针正常");
            btnGSInfo.setTextViewColor(getResources().getColor(R.color.white));
        } else {
            btnGSInfo.setImageResource(R.drawable.unpass);
            btnGSInfo.setTextViewText("指南针异常，请移动飞机或校准指南针");
            btnGSInfo.setTextViewColor(getResources().getColor(R.color.red));

            checkFlag = false;
        }

        String flightMode = mFlyCheckStatus.getFlightModeString();
        if (checkFlightMode(flightMode)) {
            btnControlInfo.setImageResource(R.drawable.ok);
            btnControlInfo.setTextViewText("遥控模式已设置为F/P档");
            btnControlInfo.setTextViewColor(getResources().getColor(R.color.white));
        } else {
            btnControlInfo.setImageResource(R.drawable.unpass);
            btnControlInfo.setTextViewText("遥控模式尚未置为F/P档");
            btnControlInfo.setTextViewColor(getResources().getColor(R.color.red));
            btnTaskInfo.setTextViewText("航点尚未上传");
            btnTaskInfo.setImageResource(R.drawable.unpass);
            btnTaskInfo.setTextViewColor(getResources().getColor(R.color.red));
            checkFlag = false;
        }


        double handsetStorage = mFlyCheckStatus.getHandsetStorage();
        btnSMSStorageInfo.setTextViewText("手机内存卡剩余" + String.format("%.2f", handsetStorage) + "G");

        if (AircraftManager.isOnlyFlightController()) {
            BatteryCellVoltageLevel cellVoltageLevel = mFlyCheckStatus.getCellVoltageLevel();
            if (cellVoltageLevel != null && cellVoltageLevel != BatteryCellVoltageLevel.LEVEL_0 && cellVoltageLevel != BatteryCellVoltageLevel.UNKNOWN) {
                btnBatteryInfo.setTextViewText("电池电压低");
                btnBatteryInfo.setTextViewColor(getResources().getColor(R.color.red));
                if (!AircraftManager.isSimulatorStart()) {
                    checkFlag = false;
                }
            } else {
                btnBatteryInfo.setTextViewText("电池电压正常");
                btnBatteryInfo.setTextViewColor(getResources().getColor(R.color.white));
            }
        } else {
            btnBatteryInfo.setTextViewText("电池电量剩余" + String.format("%.0f", mFlyCheckStatus.getRemainPower()) + "%");
            if (mFlyCheckStatus.getRemainPower() > 40.0) {
                btnBatteryInfo.setImageResource(R.drawable.ok);
                btnBatteryInfo.setTextViewColor(getResources().getColor(R.color.white));
            } else {
                btnBatteryInfo.setImageResource(R.drawable.unpass);
                btnBatteryInfo.setTextViewColor(getResources().getColor(R.color.red));
                checkFlag = false;
            }
        }

        btnPhotoStorageInfo
                .setTextViewText("相机内存卡剩余" + String.format("%.2f", mFlyCheckStatus.getSdcardRemainSize() / 1024.0) + "G");
        if (mFlyCheckStatus.isCheckSdCard()) {
            if (mFlyCheckStatus.getSdcardRemainSize() > (mFlyCheckStatus.getTotalFlyTaskPhotoNumber() * 5)) {
                btnPhotoStorageInfo.setImageResource(R.drawable.ok);
                btnPhotoStorageInfo.setTextViewColor(getResources().getColor(R.color.white));
                isSDRemainEnough = true;
            } else {
                btnPhotoStorageInfo.setImageResource(R.drawable.unpass);
                btnPhotoStorageInfo.setTextViewColor(getResources().getColor(R.color.red));
                isSDRemainEnough = false;

            }
        } else {
            btnPhotoStorageInfo.setImageResource(R.drawable.ok);
            btnPhotoStorageInfo.setTextViewColor(getResources().getColor(R.color.white));
            isSDRemainEnough = true;
        }

        bCheckResult = checkFlag;

        if (bCheckResult) {
            if (mFlyCheckingFragmentListener != null) {

                mFlyCheckingFragmentListener.onFlyCheckUpload(true);
            }
        } else {
            btnTaskInfo.setTextViewText("航点未上传:检查项未通过");
            btnTaskInfo.setImageResource(R.drawable.unpass);
            btnTaskInfo.setTextViewColor(getResources().getColor(R.color.red));
            if (mFlyCheckingFragmentListener != null) {
                mFlyCheckingFragmentListener.onFlyCheckUpload(false);
            }
        }
    }

    private boolean checkFlightMode(String flightMode) {

      /*  return  AircraftManager.isSimulatorStart() || flightMode != null &&
                (flightMode.startsWith("F_")
                        || flightMode.startsWith("P_")
                        || flightMode.startsWith("F-")
                        || flightMode.startsWith("P-")
                        || flightMode.startsWith("GPS")
                        || flightMode.startsWith("OPTI"));*/
        return flightMode != null && flightMode.startsWith("GPS");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mReceiver != null)
            getActivity().unregisterReceiver(mReceiver);
    }

    public interface FlyCheckingFragmentListener {
        void onFlyCheckingComplete(boolean blnResult);

        void onFlyCheckUpload(boolean bResult);
    }
}