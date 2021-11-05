package com.ew.autofly.module.setting.fragment.battery;

import android.view.View;
import android.widget.CompoundButton;

import com.ew.autofly.R;
import com.ew.autofly.module.setting.fragment.base.BaseSettingFragment;
import com.flycloud.autofly.design.view.setting.SettingCheckView;
import com.kyleduo.switchbutton.SwitchButton;

import dji.common.error.DJIError;
import dji.common.util.CommonCallbacks;
import dji.sdk.flightcontroller.FlightController;


public class BatterySettingFragment extends BaseSettingFragment {

    private SettingCheckView mScSmartReturnHome;

    @Override
    protected int setRootView() {
        return R.layout.fragment_setting_battery;
    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        mScSmartReturnHome = rootView.findViewById(R.id.sc_smart_return_home);

        initSmartReturnToHome();
    }

    private void initSmartReturnToHome() {
        mScSmartReturnHome.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                FlightController flightController = getFlightController();
                if (flightController != null) {
                    flightController.setSmartReturnToHomeEnabled(isChecked, new CommonCallbacks.CompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            if (djiError != null) {
                                setCheckedNoEvent(mScSmartReturnHome, !isChecked);
                            }
                        }
                    });
                } else {
                    setCheckedNoEvent(mScSmartReturnHome, !isChecked);
                    showToast(getString(R.string.device_unconnected));
                }
            }
        });

        getSmartReturnToHomeEnable();
    }

    private void getSmartReturnToHomeEnable() {
        FlightController flightController = getFlightController();
        if (flightController != null) {
            flightController.getSmartReturnToHomeEnabled(new CommonCallbacks.CompletionCallbackWith<Boolean>() {
                @Override
                public void onSuccess(Boolean aBoolean) {
                    setCheckedNoEvent(mScSmartReturnHome, aBoolean);
                }

                @Override
                public void onFailure(DJIError djiError) {

                }
            });
        } else {
            setCheckedNoEvent(mScSmartReturnHome, false);
        }
    }


}
