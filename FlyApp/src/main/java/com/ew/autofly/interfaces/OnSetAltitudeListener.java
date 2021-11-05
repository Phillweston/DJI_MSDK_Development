package com.ew.autofly.interfaces;

import com.ew.autofly.entity.WayPointTask;

import java.util.List;



public interface OnSetAltitudeListener {
    void onSetAltitudeComplete(boolean result, List<WayPointTask> taskList);
}
