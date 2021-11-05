package com.ew.autofly.interfaces;

import java.util.ArrayList;

import dji.common.flightcontroller.flyzone.SubFlyZoneInformation;



public interface OnGetNoFlyZoneDataListener {

    void onGetNoFlyZoneSuccess(ArrayList<SubFlyZoneInformation[]> map);

    void onGetNoFlyZoneFailed(final String error);
}
