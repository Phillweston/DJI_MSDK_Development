package com.ew.autofly.dialog.tower;

import android.os.Bundle;

import com.ew.autofly.constant.AppConstant;
import com.ew.autofly.dialog.common.BasePatrolSettingDialog;
import com.ew.autofly.entity.Tower;
import com.ew.autofly.interfaces.IConfirmListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.ew.autofly.dialog.tower.TowerAttitudeSettingDialog.PARAMS_TOWER_ALTITUDE_SELECTEDLIST;
import static com.ew.autofly.dialog.tower.TowerAttitudeSettingDialog.PARAMS_TOWER_DEFAULT_ALTITUDE;
import static com.ew.autofly.dialog.tower.TowerAttitudeSettingDialog.TAG_TOWER_ALTITUDE;


public class BaseTowerPatrolSettingDialog extends BasePatrolSettingDialog {

    public static final String PARAMS_SETTING_SELECTEDTOWERLIST = "PARAMS_SETTING_SELECTEDTOWERLIST";

    protected List<Tower> selectedTowerList = new ArrayList<>();

    @Override
    protected void initData() {
        super.initData();
        this.selectedTowerList = (List<Tower>) getArguments().getSerializable(PARAMS_SETTING_SELECTEDTOWERLIST);
    }

    protected void showTowerAltitudeSettingDialog(int defaultAltitude) {
        TowerAttitudeSettingDialog attitudeSettingDialog = new TowerAttitudeSettingDialog();
        Bundle bundle = new Bundle();
        bundle.putSerializable(PARAMS_TOWER_ALTITUDE_SELECTEDLIST, (Serializable) selectedTowerList);
        bundle.putInt(PARAMS_TOWER_DEFAULT_ALTITUDE, defaultAltitude);
        attitudeSettingDialog.setArguments(bundle);
        attitudeSettingDialog.setConfirmListener(new IConfirmListener() {
            @Override
            public void onConfirm(String tag, Object object) {
                StringBuilder listAltitude = new StringBuilder();
                int maxAltitude = AppConstant.MIN_ALTITUDE;
                int minAltitude = AppConstant.MAX_ALTITUDE;
                for (Tower tower : selectedTowerList) {
                    listAltitude.append(tower.getFlyAltitude()).append(",");
                    if (maxAltitude < tower.getFlyAltitude()) {
                        maxAltitude = tower.getFlyAltitude();
                    }
                    if (minAltitude > tower.getFlyAltitude()) {
                        minAltitude = tower.getFlyAltitude();
                    }
                }
                mAirRouteParameter.setFixedAltitudeList(listAltitude.toString());
                onTowerAltitudeSettingConfirm();
            }
        });
        attitudeSettingDialog.show(getFragmentManager(), TAG_TOWER_ALTITUDE);
    }

    
    protected void onTowerAltitudeSettingConfirm() {

    }

    /**
     * 获取所有杆塔里的最小航高
     *
     * @return
     */
    protected int getMiniTowerFlyAltitude() {
        int altitude = AppConstant.MIN_ALTITUDE;

        if (!selectedTowerList.isEmpty()) {

            for (int i = 0; i < selectedTowerList.size(); i++) {
                Tower tower = selectedTowerList.get(i);
                if (i == 0) {
                    altitude = tower.getFlyAltitude();
                } else {
                    if (altitude > tower.getFlyAltitude()) {
                        altitude = tower.getFlyAltitude();
                    }
                }
            }
        }
        if (altitude == Tower.ALTITUDE_NO_VALUE) {
            altitude = Tower.DEFAULT_SAFE_ALTITUDE;
        }
        return altitude;
    }

    /**
     * 获取所有杆塔里的最大航高
     *
     * @return
     */
    protected int getMaxTowerFlyAltitude() {
        int altitude = AppConstant.MAX_ALTITUDE;

        if (!selectedTowerList.isEmpty()) {

            for (int i = 0; i < selectedTowerList.size(); i++) {
                Tower tower = selectedTowerList.get(i);
                if (i == 0) {
                    altitude = tower.getFlyAltitude();
                } else {
                    if (altitude < tower.getFlyAltitude()) {
                        altitude = tower.getFlyAltitude();
                    }
                }
            }
        }
        if (altitude == Tower.ALTITUDE_NO_VALUE) {
            altitude = AppConstant.MAX_ALTITUDE;
        }
        return altitude;
    }

    /**
     * 如果没有值则给给杆塔的航高赋值
     * @param altitude
     */
    protected void assignTowerFlyAltitude(int altitude){
        if (!selectedTowerList.isEmpty()) {
            for (Tower tower : selectedTowerList) {
                if (tower.isNoSafeFlyAltitude()) {
                    tower.setFlyAltitude(altitude);
                }
            }
        }
    }
}
