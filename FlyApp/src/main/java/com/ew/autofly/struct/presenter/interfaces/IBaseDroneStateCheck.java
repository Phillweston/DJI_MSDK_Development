package com.ew.autofly.struct.presenter.interfaces;

import com.ew.autofly.internal.common.CheckError;



public interface IBaseDroneStateCheck {

    /**
     * 检查飞机是否已连接
     *
     * @return
     */
    boolean checkAirCraftConnect();

    /**
     * 检查指南针是否正常
     *
     * @return
     */
    boolean checkCompassOk();

    /**
     * 检查Home点的位置是否已经得到
     *
     * @return
     */
    CheckError checkGetHomePoint();

    /**
     * 检查dji账号是否登录
     *
     * @return
     */
    CheckError checkDJIAccountState();

    /**
     * 检查点航高是否超过最大航高
     *
     * @return
     */
    CheckError checkMaxFlyHeight();

    /**
     * 检查返航高度
     *
     * @return
     */
    CheckError checkGoHomeFlyHeight();

    /**
     * 检查预计时长及电量
     *
     * @return
     */
    CheckError checkEnoughFlyTimeAndBattery();

    /**
     * 检查是否有云台相机
     *
     * @return
     */
    boolean checkHasCamera();

    /**
     * 检查多个云台挂载
     *
     * @return
     */
    boolean checkMultiCamera();


}
