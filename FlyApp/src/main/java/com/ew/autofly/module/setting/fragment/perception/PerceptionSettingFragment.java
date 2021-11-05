package com.ew.autofly.module.setting.fragment.perception;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.CompoundButton;

import com.ew.autofly.R;
import com.ew.autofly.internal.key.base.FlyKeyManager;
import com.ew.autofly.module.setting.cache.FlySettingConfigKey;
import com.ew.autofly.module.setting.fragment.base.BaseSettingFragment;
import com.flycloud.autofly.design.view.setting.SettingCheckView;
import com.kyleduo.switchbutton.SwitchButton;

import dji.common.error.DJIError;
import dji.common.util.CommonCallbacks;
import dji.keysdk.callback.KeyListener;
import dji.sdk.flightcontroller.FlightAssistant;

/**
 * 视觉感知设置
 *
 * @description ：
 */
public class PerceptionSettingFragment extends BaseSettingFragment {

    private SettingCheckView mScRthObstacleAvoidance;
    private SettingCheckView mScRadar;
    private SettingCheckView mRadarSoundSc;
    private SettingCheckView mScVisionAssistedPosition;
    private SettingCheckView mScCollisionAvoidance;

    private FlySettingConfigKey mRadarDisplayEnableKey = FlySettingConfigKey.create(FlySettingConfigKey.RADAR_DISPLAY_ENABLE);

    private FlySettingConfigKey mRadarSoundEnableKey = FlySettingConfigKey.create(FlySettingConfigKey.RADAR_SOUND_ENABLE);


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
        return R.layout.fragment_setting_perception;
    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        mScCollisionAvoidance = rootView.findViewById(R.id.sc_collision_avoidance);
        mScRadar = rootView.findViewById(R.id.sc_radar);
        mRadarSoundSc = rootView.findViewById(R.id.sc_radar_sound);
        mScVisionAssistedPosition = rootView.findViewById(R.id.sc_vision_assisted_position);
        mScRthObstacleAvoidance = rootView.findViewById(R.id.sc_rth_obstacle_avoidance);

        initCollisionAvoidance();
        initRadar();
        initRadarSound();
        initVisionAssistedPosition();
        initRthObstacleAvoidance();
    }

    private FlightAssistant getFlightAssistant() {
        FlightAssistant flightAssistant = null;

        if (getFlightController() != null) {
            flightAssistant = getFlightController().getFlightAssistant();
        }

        return flightAssistant;
    }

    
    private void initCollisionAvoidance() {

        mScCollisionAvoidance.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                FlightAssistant flightAssistant = getFlightAssistant();
                if (flightAssistant != null) {
                    flightAssistant.setCollisionAvoidanceEnabled(isChecked, new CommonCallbacks.CompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            if (djiError != null) {
                                setCheckedNoEvent(mScCollisionAvoidance, !isChecked);
                            } else {
                                setCheckedNoEvent(mScRthObstacleAvoidance, isChecked);
                                showToast("设置成功");
                            }
                        }
                    });
                } else {
                    setCheckedNoEvent(mScCollisionAvoidance, !isChecked);
                    showToast(getString(R.string.device_unconnected));
                }
            }
        });

        getCollisionAvoidanceEnable();

    }

    private void getCollisionAvoidanceEnable() {
        FlightAssistant flightAssistant = getFlightAssistant();
        if (flightAssistant != null) {
            flightAssistant.getCollisionAvoidanceEnabled(new CommonCallbacks.CompletionCallbackWith<Boolean>() {
                @Override
                public void onSuccess(Boolean aBoolean) {
                    setCheckedNoEvent(mScCollisionAvoidance, aBoolean);
                }

                @Override
                public void onFailure(DJIError djiError) {

                }
            });
        } else {
            setCheckedNoEvent(mScCollisionAvoidance, false);
        }
    }

    
    private void initRadar() {
        Object isOpenRadar = FlyKeyManager.getInstance().getValue(mRadarDisplayEnableKey);
        mScRadar.setCheck(isOpenRadar != null && (boolean) isOpenRadar);
        mScRadar.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                Object isOpenGrid = FlyKeyManager.getInstance().getValue(mRadarDisplayEnableKey);
                mScRadar.setCheckedNoEvent(isOpenGrid == null ? false : (Boolean) isOpenGrid);
                mScRadar.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        FlyKeyManager.getInstance().setValue(mRadarDisplayEnableKey, isChecked);
                    }

                });
            }
        });
    }

    
    private void initRadarSound() {
        Object isOpenRadar = FlyKeyManager.getInstance().getValue(mRadarSoundEnableKey);
        mRadarSoundSc.setCheck(isOpenRadar != null && (boolean) isOpenRadar);
        mRadarSoundSc.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                Object isOpenGrid = FlyKeyManager.getInstance().getValue(mRadarSoundEnableKey);
                mRadarSoundSc.setCheckedNoEvent(isOpenGrid == null ? false : (Boolean) isOpenGrid);
                mRadarSoundSc.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        FlyKeyManager.getInstance().setValue(mRadarSoundEnableKey, isChecked);
                    }
                });
            }
        });
    }

    
    private void initVisionAssistedPosition() {

        mScVisionAssistedPosition.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                FlightAssistant flightAssistant = getFlightAssistant();
                if (flightAssistant != null) {
                    flightAssistant.setVisionAssistedPositioningEnabled(isChecked, new CommonCallbacks.CompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            if (djiError != null) {
                                setCheckedNoEvent(mScVisionAssistedPosition, !isChecked);
                            }
                        }
                    });
                } else {
                    setCheckedNoEvent(mScVisionAssistedPosition, !isChecked);
                    showToast(getString(R.string.device_unconnected));
                }
            }
        });

        getVisionAssistedPositionEnable();
    }

    private void getVisionAssistedPositionEnable() {
        FlightAssistant flightAssistant = getFlightAssistant();
        if (flightAssistant != null) {
            flightAssistant.getVisionAssistedPositioningEnabled(new CommonCallbacks.CompletionCallbackWith<Boolean>() {
                @Override
                public void onSuccess(Boolean aBoolean) {
                    setCheckedNoEvent(mScVisionAssistedPosition, aBoolean);
                }

                @Override
                public void onFailure(DJIError djiError) {

                }
            });
        } else {
            setCheckedNoEvent(mScVisionAssistedPosition, false);
        }
    }

    
    private void initRthObstacleAvoidance() {
        mScRthObstacleAvoidance.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                FlightAssistant flightAssistant = getFlightAssistant();
                if (flightAssistant != null) {
                    flightAssistant.setRTHObstacleAvoidanceEnabled(isChecked, new CommonCallbacks.CompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            if (djiError != null) {
                                setCheckedNoEvent(mScRthObstacleAvoidance, !isChecked);
                            }
                        }
                    });
                } else {
                    setCheckedNoEvent(mScRthObstacleAvoidance, !isChecked);
                    showToast(getString(R.string.device_unconnected));
                }
            }
        });

        getRthObstacleAvoidanceEnable();
    }

    private void getRthObstacleAvoidanceEnable() {
        FlightAssistant flightAssistant = getFlightAssistant();
        if (flightAssistant != null) {
            flightAssistant.getRTHObstacleAvoidanceEnabled(new CommonCallbacks.CompletionCallbackWith<Boolean>() {
                @Override
                public void onSuccess(Boolean aBoolean) {
                    setCheckedNoEvent(mScRthObstacleAvoidance, aBoolean);
                }

                @Override
                public void onFailure(DJIError djiError) {

                }
            });
        } else {
            setCheckedNoEvent(mScRthObstacleAvoidance, false);
        }
    }

    private void setUpKeys() {
      /*  KeyManager.getInstance().addListener(FlightControllerKey.create(
                FlightControllerKey.RTH_OBSTACLE_AVOIDANCE_ENABLED), mRthObstacleAvoidanceEnableListener);*/
    }

    private void tearDownKeys() {

    }

    private KeyListener mRthObstacleAvoidanceEnableListener = new KeyListener() {
        @Override
        public void onValueChange(@Nullable Object oldValue, @Nullable Object newValue) {
            if (newValue instanceof Boolean) {
                setCheckedNoEvent(mScRthObstacleAvoidance, (Boolean) newValue);
            }
        }
    };

}
