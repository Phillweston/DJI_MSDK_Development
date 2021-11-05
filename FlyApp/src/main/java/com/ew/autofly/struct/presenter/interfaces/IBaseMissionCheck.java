package com.ew.autofly.struct.presenter.interfaces;

import androidx.annotation.NonNull;

import com.ew.autofly.internal.common.CheckError;
import com.ew.autofly.model.mission.CheckConditionManager;


public interface IBaseMissionCheck {

    /**
     * 检查当前的任务是否正在运行
     *
     * @return 正在运行时，返回false，否则返回true
     */
    boolean checkTaskFree();

    /**
     * 检查当前的任务是否正在运行
     *
     * @param showMsg 是否显示提示
     * @return 正在运行时，返回false，否则返回true
     */
    boolean checkTaskFree(boolean showMsg);

    /**
     * 检测航点参数
     * @return
     */
    CheckError checkWaypointParams();

    /**
     * 检查云台限位
     * @param checkConditionManager
     */
    void checkPitchRangeExtension(@NonNull CheckConditionManager checkConditionManager);
}
