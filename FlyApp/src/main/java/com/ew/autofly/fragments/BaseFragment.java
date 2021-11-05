package com.ew.autofly.fragments;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.bulong.rudeness.RudenessScreenHelper;
import com.ew.autofly.constant.AppConstant;

/**
 *
 */
public class BaseFragment extends com.flycloud.autofly.base.base.BaseFragment implements IBaseFragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RudenessScreenHelper.resetDensity(getContext(), AppConstant.DESIGN_RESOLUTION_WIDTH);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        RudenessScreenHelper.resetDensity(getContext(), AppConstant.DESIGN_RESOLUTION_WIDTH);
    }
}