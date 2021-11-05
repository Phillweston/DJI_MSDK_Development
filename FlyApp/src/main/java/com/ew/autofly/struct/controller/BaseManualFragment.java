package com.ew.autofly.struct.controller;

import android.view.View;

import com.ew.autofly.R;
import com.ew.autofly.struct.presenter.BaseManualPresenterImpl;
import com.ew.autofly.struct.view.IBaseFlightView;


public abstract class BaseManualFragment<V extends IBaseFlightView, P extends BaseManualPresenterImpl<V>>
        extends BaseFlightFragment<V, P> {

    @Override
    protected int setRootViewId() {
        return R.layout.frag_manual_base_main;
    }

    @Override
    protected void initRootView(View view) {

    }
}
