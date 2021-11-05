package com.ew.autofly.module.setting.fragment.remote;

import android.view.View;

import com.ew.autofly.R;
import com.ew.autofly.model.AircraftManager;
import com.ew.autofly.module.setting.fragment.base.BaseSettingFragment;
import com.flycloud.autofly.design.view.setting.SettingSpinnerView;
import com.flycloud.autofly.ux.view.PopSpinnerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dji.common.error.DJIError;
import dji.common.remotecontroller.AircraftMappingStyle;
import dji.common.util.CommonCallbacks;
import dji.sdk.products.Aircraft;
import dji.sdk.remotecontroller.RemoteController;


public class RemoteSettingFragment extends BaseSettingFragment {

    private SettingSpinnerView mRockerModeSs;

    private String[] rockerModeArray;

    @Override
    protected int setRootView() {
        return R.layout.fragment_setting_remote;
    }

    protected void initView(View itemView) {

        mRockerModeSs = (SettingSpinnerView) itemView.findViewById(R.id.ss_rocker_mode);

        initData();

        initRockerMode();
    }

    private void initData() {
        rockerModeArray = getResources().getStringArray(R.array.remote_rocker_mode);
    }

    
    private void initRockerMode() {
        List<String> rockerModeList = new ArrayList<>(Arrays.asList(rockerModeArray));
        mRockerModeSs.init("", rockerModeList, new PopSpinnerView.OnSelectCallback() {
            @Override
            public void onSelect(int position) {
                AircraftMappingStyle aircraftMappingStyle = AircraftMappingStyle.STYLE_1;

                if (position > 2) {
                    position = 0;
                }
                switch (position) {
                    case 0:
                        aircraftMappingStyle = AircraftMappingStyle.STYLE_1;
                        break;
                    case 1:
                        aircraftMappingStyle = AircraftMappingStyle.STYLE_2;
                        break;
                    case 2:
                        aircraftMappingStyle = AircraftMappingStyle.STYLE_3;
                        break;
                }
                setAircraftMappingStyle(aircraftMappingStyle);
            }
        });
        getAircraftMappingStyle();
    }

    private void setAircraftMappingStyle(AircraftMappingStyle aircraftMappingStyle) {
        Aircraft aircraft = AircraftManager.getAircraft();
        if (aircraft != null) {
            RemoteController remoteController = aircraft.getRemoteController();
            if (remoteController != null) {
                remoteController.setAircraftMappingStyle(aircraftMappingStyle, new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        if (djiError != null) {
                            showToast("设置摇杆模式失败：" + djiError.getDescription());
                            getAircraftMappingStyle();
                        } else {
                            showToast("设置成功");
                        }
                    }
                });
            }
        }
    }

    private void getAircraftMappingStyle() {
        Aircraft aircraft = AircraftManager.getAircraft();
        if (aircraft != null) {
            RemoteController remoteController = aircraft.getRemoteController();
            if (remoteController != null) {
                remoteController.getAircraftMappingStyle(new CommonCallbacks.CompletionCallbackWith<AircraftMappingStyle>() {
                    @Override
                    public void onSuccess(AircraftMappingStyle aircraftMappingStyle) {
                        if (aircraftMappingStyle != null) {
                            mUIThreadHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    switch (aircraftMappingStyle) {
                                        case STYLE_1:
                                            mRockerModeSs.setSelect(0);
                                            break;
                                        case STYLE_2:
                                            mRockerModeSs.setSelect(1);
                                            break;
                                        case STYLE_3:
                                            mRockerModeSs.setSelect(2);
                                            break;
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(DJIError djiError) {

                    }
                });
            }
        }
    }
}
