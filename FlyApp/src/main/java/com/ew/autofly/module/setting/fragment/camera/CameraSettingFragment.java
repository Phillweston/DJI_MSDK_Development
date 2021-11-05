package com.ew.autofly.module.setting.fragment.camera;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.CompoundButton;

import com.ew.autofly.R;
import com.ew.autofly.application.EWApplication;
import com.ew.autofly.internal.key.base.FlyKeyManager;
import com.ew.autofly.internal.key.callback.KeyListener;
import com.ew.autofly.model.GimbalManager;
import com.ew.autofly.module.setting.cache.FlySettingKey;
import com.ew.autofly.module.setting.fragment.base.BaseSettingFragment;
import com.flycloud.autofly.design.view.setting.SettingCheckView;
import com.flycloud.autofly.design.view.setting.SettingSpinnerView;
import com.flycloud.autofly.design.view.setting.SettingTextView;
import com.flycloud.autofly.ux.view.PopSpinnerView;
import com.kyleduo.switchbutton.SwitchButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import dji.common.error.DJIError;
import dji.common.gimbal.CapabilityKey;
import dji.common.gimbal.GimbalMode;
import dji.common.util.CommonCallbacks;
import dji.common.util.DJIParamCapability;
import dji.sdk.gimbal.Gimbal;
import dji.sdk.products.Aircraft;


public class CameraSettingFragment extends BaseSettingFragment implements View.OnClickListener {

    private SettingTextView mStGimbalCenter;
    private SettingSpinnerView mSsGimbalMode;
    private SettingCheckView mScGimbalExtend;

    private String[] gimbalModeArray;

    private boolean isGetGimbalMode = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpKeys();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        tearDownKeys();
    }

    @Override
    protected int setRootView() {
        return R.layout.fragment_setting_camera;
    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        mStGimbalCenter = rootView.findViewById(R.id.st_gimbal_center);
        mStGimbalCenter.setText("云台回中/朝下");
        mStGimbalCenter.setOnClickListener(this);
        mSsGimbalMode = rootView.findViewById(R.id.ss_gimbal_mode);

        mScGimbalExtend = rootView.findViewById(R.id.sc_gimbal_extend);

        initData();
        initGimbalMode();

        getCimbalCapabilities();
    }

    private void initData() {
        gimbalModeArray = getResources().getStringArray(R.array.gimbal_mode);
    }

    private Gimbal getGimbal() {
        Aircraft aircraft = EWApplication.getAircraftInstance();
        Gimbal gimbal = null;

        if (aircraft != null && aircraft.isConnected()) {
            gimbal = aircraft.getGimbal();
        }
        return gimbal;
    }

    private void initGimbalMode() {

        List<String> mapTypeList = new ArrayList<>(Arrays.asList(gimbalModeArray));

        PopSpinnerView.OnSelectCallback callback = new PopSpinnerView.OnSelectCallback() {
            @Override
            public void onSelect(int position) {
                setGimbalMode(position);
            }
        };

        mSsGimbalMode.init("", mapTypeList, callback);
    }

    private void setGimbalMode(int position) {
        Gimbal gimbal = getGimbal();
        if (gimbal != null) {
            GimbalMode gimbalMode = GimbalMode.UNKNOWN;
            switch (position) {
                case 0:
                    gimbalMode = GimbalMode.FREE;
                    break;
                case 1:
                    gimbalMode = GimbalMode.FPV;
                    break;
                case 2:
                    gimbalMode = GimbalMode.YAW_FOLLOW;
                    break;
            }
            gimbal.setMode(gimbalMode, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError == null) {
                        showToast("设置成功");
                    } else {

                        showToast("设置失败：云台不支持该模式，或内部异常");
                    }
                }
            });
        }
    }

    private void reSet() {
        Gimbal gimbal = getGimbal();
        if (gimbal != null) {
            gimbal.reset(new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {

                }
            });
        }
    }

    private void initGimbalExtend(boolean isSupport) {

        if (isSupport) {
            mScGimbalExtend.setVisibility(View.VISIBLE);
        } else {
            mScGimbalExtend.setVisibility(View.GONE);
            return;
        }

        mScGimbalExtend.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {

                Gimbal gimbal = GimbalManager.getGimbal();
                if (gimbal != null) {
                    gimbal.setPitchRangeExtensionEnabled(isChecked, new CommonCallbacks.CompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            if (djiError != null) {
                                setCheckedNoEvent(mScGimbalExtend, !isChecked);
                            }
                        }
                    });
                } else {
                    setCheckedNoEvent(mScGimbalExtend, !isChecked);
                    showToast(getString(R.string.device_unconnected));
                }
            }
        });

        getPitchRangeExtensionEnabled();
    }

    private void getPitchRangeExtensionEnabled() {
        Gimbal gimbal = GimbalManager.getGimbal();
        if (gimbal != null) {
            gimbal.getPitchRangeExtensionEnabled(new CommonCallbacks.CompletionCallbackWith<Boolean>() {
                @Override
                public void onSuccess(Boolean aBoolean) {
                    setCheckedNoEvent(mScGimbalExtend, aBoolean);
                }

                @Override
                public void onFailure(DJIError djiError) {

                }
            });
        } else {
            setCheckedNoEvent(mScGimbalExtend, false);
        }
    }

    private void getCimbalCapabilities() {
        Gimbal gimbal = getGimbal();
        if (gimbal != null) {
            Map<CapabilityKey, DJIParamCapability> capabilities = gimbal.getCapabilities();
            DJIParamCapability djiParamCapability = capabilities.get(CapabilityKey.PITCH_RANGE_EXTENSION);
            if (djiParamCapability != null) {
                initGimbalExtend(djiParamCapability.isSupported());
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.st_gimbal_center:
                reSet();
                break;
        }
    }

    private void setUpKeys() {
        FlyKeyManager.getInstance().addListener(FlySettingKey.create(FlySettingKey.GIMBAL_MODE), mGimbalModeListener);
    }

    private void tearDownKeys() {
        FlyKeyManager.getInstance().removeListener(mGimbalModeListener);
    }

    private KeyListener mGimbalModeListener = new KeyListener() {
        @Override
        public void onValueChange(@Nullable Object oldValue, @Nullable final Object newValue) {
            if (newValue instanceof GimbalMode) {
                mUIThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (!isGetGimbalMode) {
                            switch ((GimbalMode) newValue) {
                                case FREE:
                                    mSsGimbalMode.setSelect(0);
                                    break;
                                case FPV:
                                    mSsGimbalMode.setSelect(1);
                                    break;
                                case YAW_FOLLOW:
                                    mSsGimbalMode.setSelect(2);
                                    break;
                            }
                            isGetGimbalMode = true;
                        }
                    }
                });
            }
        }
    };
}
