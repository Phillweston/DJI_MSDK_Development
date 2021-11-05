package com.flycloud.autofly.control.service;

import android.content.Context;

import com.alibaba.android.arouter.facade.template.IProvider;
import com.flycloud.autofly.control.bean.DevRegUserInfo;



public interface IDeviceService extends IProvider {

    void checkDeviceStatus(Context context, String appChannelCode);

    boolean hasCopyright();

    DevRegUserInfo getDeviceRegUserInfo();


    String getDeviceExpireDate();


    String getDeviceInfo();


    String getDeviceChannelCode();


    String getDeviceSeverModes();
}
