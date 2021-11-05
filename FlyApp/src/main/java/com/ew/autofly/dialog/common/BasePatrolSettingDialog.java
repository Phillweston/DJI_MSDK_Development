package com.ew.autofly.dialog.common;

import android.os.Bundle;
import androidx.annotation.Nullable;

import com.ew.autofly.dialog.base.BaseDialogFragment;
import com.ew.autofly.entity.AirRouteParameter;


public class BasePatrolSettingDialog extends BaseDialogFragment {

    public static final String PARAMS_SETTING_AIRROUTEPARAMETER = "PARAMS_SETTING_AIRROUTEPARAMETER";


    protected AirRouteParameter mAirRouteParameter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    protected void initData(){
        this.mAirRouteParameter = (AirRouteParameter) getArguments().getSerializable(PARAMS_SETTING_AIRROUTEPARAMETER);
    }
}
