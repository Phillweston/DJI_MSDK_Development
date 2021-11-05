package com.ew.autofly.dialog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.ew.autofly.R;
import com.ew.autofly.application.EWApplication;
import com.ew.autofly.config.SharedConfig;
import com.ew.autofly.dialog.base.BaseDialogFragment;
import com.ew.autofly.fragments.BaseCollectFragment;
import com.ew.autofly.interfaces.FlyCheckingFragmentListener;
import com.ew.autofly.model.AircraftManager;
import com.ew.autofly.utils.IOUtils;
import com.ew.autofly.utils.SoundPlayerUtil;
import com.ew.autofly.utils.ToastUtil;
import com.ew.autofly.widgets.SlideFlightView;
import com.ew.autofly.widgets.controls.ExtImageButton;

import dji.common.battery.BatteryCellVoltageLevel;
import dji.common.error.DJIError;
import dji.common.mission.waypoint.Waypoint;
import dji.common.mission.waypoint.WaypointAction;
import dji.common.mission.waypoint.WaypointActionType;
import dji.common.mission.waypoint.WaypointMission;
import dji.common.mission.waypoint.WaypointMissionState;
import dji.common.util.CommonCallbacks;
import dji.sdk.mission.MissionControl;
import dji.sdk.mission.waypoint.WaypointMissionOperator;
import dji.sdk.products.Aircraft;


@Deprecated
public class FlyCheckingDialogFragment extends BaseDialogFragment {
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
    private SharedConfig config;
    private WaypointMissionOperator operator;
    private SlideFlightView slideFlightView;
    private ImageButton btnClose;
    private BaseCollectFragment flyCollectFragment = null;


    
    private boolean isSDRemainEnough = false;

    private boolean bCheckResult = true;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("UPLOAD_PROGRESS_UPDATE")) {
                int progress = intent.getIntExtra("progress", 0);
                btnTaskInfo.setTextViewText(String.format("航点已上传%d", progress) + "%");
                if (progress == 100 && slideFlightView != null) {
                    slideFlightView.mTextColor = "#000000";
                    slideFlightView.isPermitClick = true;
                    slideFlightView.invalidate();
                    btnTaskInfo.setImageResource(R.drawable.ok);
                    btnTaskInfo.setTextViewText("航点上传成功");
                    btnTaskInfo.setTextViewColor(getResources().getColor(R.color.white));
                }
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        config = new SharedConfig(getContext());
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
            IntentFilter filter = new IntentFilter("UPLOAD_PROGRESS_UPDATE");
            getActivity().registerReceiver(mReceiver, filter);
        }
        init();
    }

    @Override
    protected void onCreateSize() {
        setSize(0.58f, 0.75f);
    }

    @Override
    public void onStart() {
        initListener();
        super.onStart();
    }

    private void initListener() {
        slideFlightView.setOnUnLockListener(new SlideFlightView.OnUnLockListener() {
            @Override
            public void setUnLocked(boolean unLocked) {
                if (unLocked) {
                    if (bCheckResult) {
                        FlyCheckingFragmentListener listener = (FlyCheckingFragmentListener) getActivity().getSupportFragmentManager().findFragmentByTag(getTag().replace("_check", ""));
                        if (isSDRemainEnough) {
                            if (null != listener)
                                listener.onFlyCheckingComplete(true);
                            dismiss();
                        } else {
                            CustomDialog.Builder deleteDialog = new CustomDialog.Builder(getActivity());
                            deleteDialog.setTitle(getResources().getString(R.string.notice))
                                    .setMessage(getResources().getString(R.string.fly_check_sd_no_enough))
                                    .setPositiveButton(getResources().getString(R.string.sure), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (null != listener)
                                                listener.onFlyCheckingComplete(true);
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
                    FlyCheckingFragmentListener listener = (FlyCheckingFragmentListener) getActivity().getSupportFragmentManager().findFragmentByTag(getTag().replace("_check", ""));
                    if (null != listener) {
                        listener.onFlyCheckingComplete(false);
                        dismiss();
                    }
                }
            }
        });
    }

    private void init() {
        bCheckResult = true;

        switch (config.getMode()) {
            case 5:
                flyCollectFragment = (BaseCollectFragment) getActivity().getSupportFragmentManager().findFragmentByTag("line");
                break;
            case 3:
            case 6:
                flyCollectFragment = (BaseCollectFragment) getActivity().getSupportFragmentManager().findFragmentByTag("fast");
                break;
            case 4:
                flyCollectFragment = (BaseCollectFragment) getActivity().getSupportFragmentManager().findFragmentByTag("pano");
                break;
            case 7:
                flyCollectFragment = (BaseCollectFragment) getActivity().getSupportFragmentManager().findFragmentByTag("hotpoint");
                break;
            case 1:
                flyCollectFragment = (BaseCollectFragment) getActivity().getSupportFragmentManager().findFragmentByTag("channel");
                break;
            case 2:
                flyCollectFragment = (BaseCollectFragment) getActivity().getSupportFragmentManager().findFragmentByTag("tree");
                break;
            case 11:
                flyCollectFragment = (BaseCollectFragment) getActivity().getSupportFragmentManager().findFragmentByTag("belt");
                break;
            case 9:
                flyCollectFragment = (BaseCollectFragment) getActivity().getSupportFragmentManager().findFragmentByTag("river");
                break;
            case 10:
                flyCollectFragment = (BaseCollectFragment) getActivity().getSupportFragmentManager().findFragmentByTag("fine");
                break;
            case 12:
                flyCollectFragment = (BaseCollectFragment) getActivity().getSupportFragmentManager().findFragmentByTag("tilt");
                break;
            case 13:
                flyCollectFragment = (BaseCollectFragment) getActivity().getSupportFragmentManager().findFragmentByTag("multispectral");
                break;
            case 15:
                flyCollectFragment = (BaseCollectFragment) getActivity().getSupportFragmentManager().findFragmentByTag("stand");
        }
        if (flyCollectFragment.isAirCraftConnect()) {
            btnConnectInfo.setTextViewText("飞机连接成功");
            btnConnectInfo.setImageResource(R.drawable.ok);
            btnConnectInfo.setTextViewColor(getResources().getColor(R.color.white));
        } else {
            btnConnectInfo.setTextViewText("飞机连接失败");
            btnConnectInfo.setImageResource(R.drawable.unpass);
            btnConnectInfo.setTextViewColor(getResources().getColor(R.color.red));

            bCheckResult = false;
        }
        btnGPSInfo.setTextViewText("GPS获取了" + String.format("%.0f", flyCollectFragment.satelliteCount) + "/16颗卫星");
        if (flyCollectFragment.satelliteCount > 5) {
            btnGPSInfo.setImageResource(R.drawable.ok);
            btnGPSInfo.setTextViewColor(getResources().getColor(R.color.white));
        } else {
            btnGPSInfo.setImageResource(R.drawable.unpass);
            btnGPSInfo.setTextViewColor(getResources().getColor(R.color.red));

            bCheckResult = false;
        }

        Aircraft mProduct = EWApplication.getAircraftInstance();
        if (mProduct != null && mProduct.getFlightController() != null
                && !mProduct.getFlightController().getCompass().hasError()) {
            btnGSInfo.setImageResource(R.drawable.ok);
            btnGSInfo.setTextViewText("指南针正常");
            btnGSInfo.setTextViewColor(getResources().getColor(R.color.white));
        } else {
            btnGSInfo.setImageResource(R.drawable.unpass);
            btnGSInfo.setTextViewText("指南针异常，请移动飞机或校准指南针");
            btnGSInfo.setTextViewColor(getResources().getColor(R.color.red));

            bCheckResult = false;
        }

        if (checkFlightMode(flyCollectFragment.flightModeString)) {
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
            bCheckResult = false;
        }


        double handsetStorage = IOUtils.getAvailaleSize();
        btnSMSStorageInfo.setTextViewText("手机内存卡剩余" + String.format("%.2f", handsetStorage) + "G");

        if (AircraftManager.isOnlyFlightController()) {
            BatteryCellVoltageLevel cellVoltageLevel = flyCollectFragment.mCellVoltageLevel;
            if (cellVoltageLevel != null && cellVoltageLevel != BatteryCellVoltageLevel.LEVEL_0 && cellVoltageLevel != BatteryCellVoltageLevel.UNKNOWN) {
                btnBatteryInfo.setTextViewText("电池电压低");
                btnBatteryInfo.setTextViewColor(getResources().getColor(R.color.red));
                if (!AircraftManager.isSimulatorStart()) {
                    bCheckResult = false;
                }
            } else {
                btnBatteryInfo.setTextViewText("电池电压正常");
                btnBatteryInfo.setTextViewColor(getResources().getColor(R.color.white));
            }
        } else {
            btnBatteryInfo.setTextViewText("电池电量剩余" + String.format("%.0f", flyCollectFragment.remainPower) + "%");
            if (flyCollectFragment.remainPower > 40.0) {
                btnBatteryInfo.setImageResource(R.drawable.ok);
                btnBatteryInfo.setTextViewColor(getResources().getColor(R.color.white));
            } else {
                btnBatteryInfo.setImageResource(R.drawable.unpass);
                btnBatteryInfo.setTextViewColor(getResources().getColor(R.color.red));
                bCheckResult = false;
            }
        }

        btnPhotoStorageInfo
                .setTextViewText("相机内存卡剩余" + String.format("%.2f", flyCollectFragment.mSdcardRemainSize / 1024.0) + "G");
        if (flyCollectFragment.mSdcardRemainSize >= (flyCollectFragment.totalFlyTask * 5)) {
            btnPhotoStorageInfo.setImageResource(R.drawable.ok);
            btnPhotoStorageInfo.setTextViewColor(getResources().getColor(R.color.white));
            isSDRemainEnough = true;
        } else {
            btnPhotoStorageInfo.setImageResource(R.drawable.unpass);
            btnPhotoStorageInfo.setTextViewColor(getResources().getColor(R.color.red));

            isSDRemainEnough = false;
        }

        if (bCheckResult) {
            operator = MissionControl.getInstance().getWaypointMissionOperator();
            if (operator == null) {
                bCheckResult = false;
                return;
            }
            addResetGimbalAction(flyCollectFragment.mDJIWayPointMission);
            DJIError djiError = flyCollectFragment.mDJIWayPointMission.checkParameters();
            if (djiError != null) {
                ToastUtil.show(getActivity(), djiError.getDescription());
                return;
            }
            DJIError djiError2 = operator.loadMission(flyCollectFragment.mDJIWayPointMission);
            if (djiError2 != null) {
                ToastUtil.show(getActivity(), djiError2.getDescription());
                return;
            }
            if (operator.getCurrentState().equals(WaypointMissionState.READY_TO_UPLOAD) ||
                    operator.getCurrentState().equals(WaypointMissionState.READY_TO_RETRY_UPLOAD) ||
                    operator.getCurrentState().equals(WaypointMissionState.READY_TO_EXECUTE)) {
                operator.uploadMission(new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(final DJIError djiError) {
                        if (isAdded() && djiError != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtil.show(getActivity(), djiError.getDescription());
                                    btnTaskInfo.setTextViewText("航点上传失败" + djiError.getDescription());
                                    btnTaskInfo.setImageResource(R.drawable.unpass);
                                    btnTaskInfo.setTextViewColor(getResources().getColor(R.color.red));
                                    bCheckResult = false;
                                }
                            });
                        }
                    }
                });

            }

        } else {
            btnTaskInfo.setTextViewText("航点尚未上传");
            btnTaskInfo.setImageResource(R.drawable.unpass);
            btnTaskInfo.setTextViewColor(getResources().getColor(R.color.red));
        }
    }

    private boolean checkFlightMode(String flightMode) {
      /*return  isSimulatorStart() || flyCollectFragment.flightModeString != null
                && (flyCollectFragment.flightModeString.startsWith("F_")
                || flyCollectFragment.flightModeString.startsWith("P_")
                || flyCollectFragment.flightModeString.startsWith("F-")
                || flyCollectFragment.flightModeString.startsWith("P-")
                || flyCollectFragment.flightModeString.startsWith("GPS")
                || flyCollectFragment.flightModeString.startsWith("OPTI"));*/
        return flightMode != null && flightMode.startsWith("GPS");
    }

    private void addResetGimbalAction(WaypointMission mDJIWayPointMission) {
        Waypoint waypoint = mDJIWayPointMission.getWaypointList().get(mDJIWayPointMission.getWaypointCount() - 1);
        waypoint.addAction(new WaypointAction(WaypointActionType.GIMBAL_PITCH, 0));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mReceiver != null)
            getActivity().unregisterReceiver(mReceiver);
    }
}