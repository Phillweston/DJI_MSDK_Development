package com.ew.autofly.config;

import android.content.Context;

import com.ew.autofly.constant.AppConstant;
import com.flycloud.autofly.base.util.SharedPreferencesUtil;

import dji.sdk.camera.Camera;

public class SharedConfig {

    private Context mContext;

    private SharedPreferencesUtil mSP;

    public SharedConfig(Context context) {
        mContext = context;
        mSP = new SharedPreferencesUtil(context, "sdf29wed1elr", true);
    }

    public SharedPreferencesUtil getSharedPreferences() {
        return mSP;
    }

    public void setAirDraftModel(String model) {
        mSP.putString(AppConstant.PREF_AIRCRAFT_MODEL, model);
    }

    public String getAirCraftModel() {
        return mSP.getString(AppConstant.PREF_AIRCRAFT_MODEL, "Phantom 4 Pro");
    }

    public void setCameraName(String name) {
        mSP.putString(AppConstant.PREF_CAMERA_NAME, name);
    }

    public String getCameraName() {
        return mSP.getString(AppConstant.PREF_CAMERA_NAME, Camera.DisplayNamePhantom4Camera);
    }

    /**
     * 设置模拟飞行状态
     *
     * @param state
     */
    public void setSimulateState(boolean state) {
        mSP.putBoolean(AppConstant.PREF_SIMULATOR, state);
    }

    /**
     * 获取模拟飞行状态
     *
     * @return
     */
    public boolean getSimulateState() {
        return mSP.getBoolean(AppConstant.PREF_SIMULATOR, false);
    }

    /**
     * 设置是否允许psdk介入任务
     *
     * @param enable
     */
    public void setPsdkEnable(boolean enable) {
        mSP.putBoolean(AppConstant.PREF_PSDK_ENABLE, enable);
    }

    /**
     * 获取是否允许psdk介入任务
     *
     * @return
     */
    public boolean getPsdkEnable() {
        return mSP.getBoolean(AppConstant.PREF_PSDK_ENABLE, false);
    }

    /**
     * 设置地图类型(0:Google   1:高德)
     *
     * @param type
     */
    public void setMapType(int type) {
        mSP.putInt(AppConstant.PREF_MAP_TYPE, type);
    }

    /**
     * 获取地图类型
     *
     * @return
     */
    public int getMapType() {
        return mSP.getInt(AppConstant.PREF_MAP_TYPE, 0);
    }

    public void setMode(int mode) {
        mSP.putInt(AppConstant.PREF_MODE, mode);
    }

    /**
     * 1：通道巡检  2：树障巡检  3：快速测绘--正射影像  4：全景采集 5：线状调查 6：快速测绘--倾斜影像
     * 7：环绕采集  8：手动采集  9：河道巡检  10：精细巡检  11：带状正射  12：带状倾斜 13:多光谱正射
     * 14.点云采集 15：立面正射  16：单线激光
     *
     * @return
     */
    public int getMode() {
        return mSP.getInt(AppConstant.PREF_MODE, 5);
    }


    public String getExpireDate() {
        return mSP.getString(AppConstant.PREF_EXPIRE_DATE, "1970-1-1");
    }

    /**
     * 设置上传图片地址
     *
     * @param url
     */
    public void setUploadImgUrl(String url) {
        mSP.putString(AppConstant.PREF_UPLOAD_IMAGE, url);
    }

    /**
     * 获取上传图片地址
     *
     * @return
     */
    public String getUploadImgUrl() {
        return mSP.getString(AppConstant.PREF_UPLOAD_IMAGE, "");
    }

    /**
     * 设置图片下载地址
     *
     * @param url
     */
    public void setDownloadImgUrl(String url) {
        mSP.putString(AppConstant.PREF_DOWNLOAD_IMAGE, url);
    }

    /**
     * 获取图片下载地址
     *
     * @return
     */
    public String getDownloadImgUrl() {
        return mSP.getString(AppConstant.PREF_DOWNLOAD_IMAGE, "");
    }

    public void setSimulateLat(String lat) {
        mSP.putString(AppConstant.PREF_SIMULATE_LATITUDE, lat);
    }

    public String getSimulateLat() {
        return mSP.getString(AppConstant.PREF_SIMULATE_LATITUDE, AppConstant.DEFAULT_SIMULATE_LAT);
    }

    public void setSimulateLon(String lon) {
        mSP.putString(AppConstant.PREF_SIMULATE_LONGITUDE, lon);
    }

    public String getSimulateLon() {
        return mSP.getString(AppConstant.PREF_SIMULATE_LONGITUDE, AppConstant.DEFAULT_SIMULATE_LON);
    }

    public void setGoHomeHeight(int goHomeHeight) {
        mSP.putInt(AppConstant.PREF_GOHOME_HEIGHT, goHomeHeight);
    }

    public int getGoHomeHeight() {
        return mSP.getInt(AppConstant.PREF_GOHOME_HEIGHT, 200);
    }

    public void setMaxFlyHeight(int height) {
        mSP.putInt(AppConstant.PREF_MAX_FLY_HEIGHT, height);
    }

    public int getMaxFlyHeight() {
        return mSP.getInt(AppConstant.PREF_MAX_FLY_HEIGHT, 200);
    }

    public void setIMUState(boolean isIMUState) {
        mSP.putBoolean(AppConstant.IMU_STATE, isIMUState);
    }

    public boolean getIMUState() {
        return mSP.getBoolean(AppConstant.IMU_STATE, true);
    }

    public void setDemDownloadList(String demList) {
        mSP.putString(AppConstant.DEM_LIST, demList);
    }

    public String getDemList() {
        return mSP.getString(AppConstant.DEM_LIST, "");
    }

    public void setCameraStorageLocation(String location) {
        mSP.putString(AppConstant.STORAGE_LOCATION, location);
    }

    public String getCameraStorageLocation() {
        return mSP.getString(AppConstant.STORAGE_LOCATION,"");
    }

}
