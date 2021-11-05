package com.ew.autofly.interfaces;

import com.ew.autofly.entity.AirRouteParameter;
import com.ew.autofly.mode.linepatrol.point.ui.model.FlyTask;

public interface OnSettingDialogClickListener {
    void onSettingDialogConfirm(String tag, Object obj, AirRouteParameter params);
}