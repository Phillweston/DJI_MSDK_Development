package com.ew.autofly.module.setting.fragment.flight;

import android.view.View;
import android.widget.CompoundButton;

import com.ew.autofly.R;
import com.ew.autofly.internal.key.base.FlyKeyManager;
import com.ew.autofly.model.AircraftManager;
import com.ew.autofly.module.setting.cache.FlySettingConfigKey;
import com.ew.autofly.module.setting.fragment.base.BaseSettingSecondFragment;
import com.flycloud.autofly.design.view.setting.SettingCheckView;
import com.kyleduo.switchbutton.SwitchButton;

import dji.common.error.DJIError;
import dji.common.util.CommonCallbacks;
import dji.sdk.flightcontroller.FlightController;


public class AISettingFragment extends BaseSettingSecondFragment {

    private FlySettingConfigKey mAIEnableKey = FlySettingConfigKey.create(FlySettingConfigKey.AI_ENABLE);

    private SettingCheckView mScAi;
    private SettingCheckView mAiPowerSc;

    @Override
    protected int setRootView() {
        return R.layout.fragment_setting_flight_ai;
    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        mScAi = rootView.findViewById(R.id.sc_ai);
        mAiPowerSc = (SettingCheckView) rootView.findViewById(R.id.sc_ai_power);
        initAI();
        initPower();
    }

    
    private void initAI() {

        Object isOpenAI = FlyKeyManager.getInstance().getValue(mAIEnableKey);
        mScAi.setCheck(isOpenAI != null && (boolean) isOpenAI);
        mScAi.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                Object isOpenGrid = FlyKeyManager.getInstance().getValue(mAIEnableKey);
                mScAi.setCheckedNoEvent(isOpenGrid == null ? false : (Boolean) isOpenGrid);
                mScAi.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        FlyKeyManager.getInstance().setValue(mAIEnableKey, isChecked);
                    }

                });
            }
        });
    }

    private void initPower() {
        mAiPowerSc.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {

                FlightController flightController = AircraftManager.getFlightController();
                if (flightController != null) {
                    flightController.setPowerSupplyPortEnabled(isChecked, new CommonCallbacks.CompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            if (djiError != null) {
                                showToast("设置失败：" + djiError.getDescription());
                                setCheckedNoEvent(mAiPowerSc, !isChecked);
                            } else {
                                showToast("设置成功");
                                setCheckedNoEvent(mAiPowerSc, isChecked);
                            }
                        }
                    });

                } else {
                    setCheckedNoEvent(mAiPowerSc, !isChecked);
                    showToast(getString(R.string.device_unconnected));
                }
            }
        });

        getPowerState();
    }

    private void getPowerState() {
        FlightController flightController = AircraftManager.getFlightController();
        if (flightController != null) {
            flightController.getPowerSupplyPortEnabled(new CommonCallbacks.CompletionCallbackWith<Boolean>() {
                @Override
                public void onSuccess(Boolean aBoolean) {
                    setCheckedNoEvent(mAiPowerSc, aBoolean);
                }

                @Override
                public void onFailure(DJIError djiError) {

                }
            });
        }
    }

}
