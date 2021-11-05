package com.ew.autofly.module.setting.fragment.flight;

import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.CompoundButton;

import com.ew.autofly.R;
import com.ew.autofly.config.SharedConfig;
import com.ew.autofly.constant.AppConstant;
import com.ew.autofly.dialog.CustomDialog;
import com.ew.autofly.event.SimulatorEvent;
import com.ew.autofly.internal.key.base.FlyKeyManager;
import com.ew.autofly.model.AircraftManager;
import com.ew.autofly.module.setting.cache.FlySettingConfigKey;
import com.ew.autofly.module.setting.fragment.base.BaseSettingFragment;
import com.ew.autofly.module.setting.fragment.base.BaseSettingSecondFragment;
import com.flycloud.autofly.design.view.setting.SettingCheckView;
import com.flycloud.autofly.design.view.setting.SettingEditView;
import com.flycloud.autofly.design.view.setting.SettingEnterView;
import com.kyleduo.switchbutton.SwitchButton;

import org.greenrobot.eventbus.EventBus;

import dji.common.error.DJIError;
import dji.common.util.CommonCallbacks;
import dji.keysdk.FlightControllerKey;
import dji.keysdk.KeyManager;
import dji.sdk.flightcontroller.FlightController;



public class FlightSettingFragment extends BaseSettingFragment implements View.OnClickListener {

    private int returnHeight = 0;
    private int maxHeight = 0;

    private final int MENU_ID_RTK = 0;
    private final int MENU_ID_AI = 1;

    private SettingCheckView mScSimulator;

    private SettingEditView mSetReturnHeight;
    private SettingEditView mSetMaxHeight;

    private SettingEnterView mRtkSe;

    private SettingEnterView mAISe;

    @Override
    protected int setRootView() {
        return R.layout.fragment_setting_flight;
    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        rootView.findViewById(R.id.se_rtk).setOnClickListener(this);
        mScSimulator = rootView.findViewById(R.id.sc_simulator);
        mSetReturnHeight = rootView.findViewById(R.id.set_return_height);
        mSetMaxHeight = rootView.findViewById(R.id.set_max_height);
        mAISe = rootView.findViewById(R.id.se_ai_setting);
        mRtkSe = (SettingEnterView) rootView.findViewById(R.id.se_rtk);

        initReturnHeight();
        initMaxFlightHeight();
        initSimulator();

        initAI();

        initRTK();

      

    }

    
    private void initReturnHeight() {

        mSetReturnHeight.setValueScale(AppConstant.MIN_ALTITUDE, AppConstant.MAX_ALTITUDE, "m");

        mSetReturnHeight.setOnEditListener(new SettingEditView.OnEditListener() {
            @Override
            public void onDone(int value) {
                setReturnHeight(value);
            }
        });

        getReturnHeight();
    }

    private void setReturnHeight(int value) {
        FlightController flightController = getFlightController();
        if (flightController != null) {

            if (value > maxHeight) {
                CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
                Dialog dialog = builder.setTitle(getActivity().getString(R.string.notice))
                        .setMessage("无法设置高于当前限高的返航高度")
                        .setPositiveButton(getActivity().getString(R.string.sure), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mSetReturnHeight.setValue(returnHeight);
                            }
                        })
                        .create();
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        mSetReturnHeight.setValue(returnHeight);
                    }
                });
                dialog.show();
            } else {

                flightController.setGoHomeHeightInMeters(value, new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        if (djiError != null) {
                            showToast("设置返航高度失败:" + djiError.getDescription());
                            getReturnHeight();
                        } else {
                            showToast("设置返航高度成功");
                            returnHeight = value;
                        }
                    }
                });
            }

        } else {
            showToast(getString(R.string.device_unconnected));
        }
    }

    
    private void getReturnHeight() {
       /* FlightController flightController = getFlightController();
        if (flightController != null) {

            flightController.getGoHomeHeightInMeters(new CommonCallbacks.CompletionCallbackWith<Integer>() {
                @Override
                public void onSuccess(final Integer var) {
                    returnHeight = var;
                    mUIThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mSetReturnHeight.setValue(var);
                        }
                    });
                }

                @Override
                public void onFailure(DJIError djiError) {

                }
            });
        }*/

        Object value = KeyManager.getInstance().getValue(FlightControllerKey.create(FlightControllerKey.GO_HOME_HEIGHT_IN_METERS));

        if (value instanceof Integer) {
            returnHeight = (int) value;
            mSetReturnHeight.setValue(returnHeight);
        }

    }

    
    private void initMaxFlightHeight() {

        mSetMaxHeight.setValueScale(AppConstant.MIN_ALTITUDE, AppConstant.MAX_ALTITUDE, "m");

        mSetMaxHeight.setOnEditListener(new SettingEditView.OnEditListener() {
            @Override
            public void onDone(int value) {
                setMaxHeight(value);
            }
        });

        getMaxFlightHeight();
    }

    private void setMaxHeight(int value) {
        FlightController flightController = getFlightController();
        if (flightController != null) {
            if (value < returnHeight) {
                CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
                Dialog dialog = builder.setTitle(getActivity().getString(R.string.notice))
                        .setMessage("返航高度不能高于最大飞行高度")
                        .setPositiveButton(getActivity().getString(R.string.sure), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mSetMaxHeight.setValue(maxHeight);
                            }
                        })
                        .create();
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        mSetMaxHeight.setValue(maxHeight);
                    }
                });
                dialog.show();
            } else {
                flightController.setMaxFlightHeight(value, new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        if (djiError != null) {
                            showToast("设置最大飞行高度失败:" + djiError.getDescription());
                            getMaxFlightHeight();
                        } else {
                            showToast("设置最大飞行高度成功");
                            maxHeight = value;
                        }
                    }
                });
            }

        } else {
            showToast(getString(R.string.device_unconnected));
        }
    }

    
    private void getMaxFlightHeight() {
       /* FlightController flightController = getFlightController();
        if (flightController != null) {

            flightController.getMaxFlightHeight(new CommonCallbacks.CompletionCallbackWith<Integer>() {
                @Override
                public void onSuccess(final Integer var) {
                    maxHeight = var;
                    mUIThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mSetMaxHeight.setValue(var);
                        }
                    });
                }

                @Override
                public void onFailure(DJIError djiError) {

                }
            });
        }*/

        Object value = KeyManager.getInstance().getValue(FlightControllerKey.create(FlightControllerKey.MAX_FLIGHT_HEIGHT));

        if (value instanceof Integer) {
            maxHeight = (int) value;
            mSetMaxHeight.setValue(maxHeight);
        }
    }

    
    private void initSimulator() {

        mScSimulator.setCheck(AircraftManager.isSimulatorStart());
        mScSimulator.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                EventBus.getDefault().post(new SimulatorEvent(isChecked));
                if (!isChecked) {
                    FlyKeyManager.getInstance().setValue(FlySettingConfigKey.create(FlySettingConfigKey.SIMULATE_ALTITUDE), 0.0f);
                }
                exitMainSetting();
            }
        });
    }

    
    private void initAI() {

        if (AircraftManager.isSupportAIOnBoard()) {
            SharedConfig config = new SharedConfig(getContext());
            mAISe.setVisibility(View.GONE);
            mAISe.setOnClickListener(this);
        }

    }

    private void initRTK() {

        if (AircraftManager.isSupportNetWorkRtkSetting()) {
            mRtkSe.setVisibility(View.VISIBLE);
            mRtkSe.setOnClickListener(this);
        }

    }

    private void initRTKNoP4R() {
        mRtkSe.setVisibility(View.VISIBLE);
        mRtkSe.setOnClickListener(this);
    }

    
    private void replaceSecondMenu(int id) {
        switch (id) {
            case MENU_ID_RTK:
                BaseSettingSecondFragment rtkSettingFragment = new RtkSettingFragment();
                enterToSecondMenu(rtkSettingFragment, "RTK设置");
                break;
            case MENU_ID_AI:
                BaseSettingSecondFragment aiSettingFragment = new AISettingFragment();
                enterToSecondMenu(aiSettingFragment, "AI设置");
                break;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.se_rtk:
                replaceSecondMenu(MENU_ID_RTK);
                break;
            case R.id.se_ai_setting:
                replaceSecondMenu(MENU_ID_AI);
                break;
        }
    }
}
