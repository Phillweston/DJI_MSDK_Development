package com.flycloud.autofly.control.service;

import android.content.Context;
import androidx.annotation.Nullable;

import com.alibaba.android.arouter.launcher.ARouter;
import com.flycloud.autofly.control.bean.DevRegUserInfo;



public class RouteService {



    public static void logout() {
        IUserService userModuleService = ARouter.getInstance().navigation(IUserService.class);
        if (userModuleService != null) {
            userModuleService.logout();
        }
    }

    public static long getUserID() {
        IUserService userModuleService = ARouter.getInstance().navigation(IUserService.class);
        if (userModuleService != null) {
            return userModuleService.getUserID();
        }
        return 0;
    }

    public static boolean isSkipLogin(){
        IUserService userModuleService = ARouter.getInstance().navigation(IUserService.class);
        if (userModuleService != null) {
            return userModuleService.isSkipLogin();
        }
        return false;
    }

    public static String getUserName() {
        IUserService userModuleService = ARouter.getInstance().navigation(IUserService.class);
        if (userModuleService != null) {
            return userModuleService.getUserName();
        }
        return "";
    }

    public static String getUserToken() {
        IUserService userModuleService = ARouter.getInstance().navigation(IUserService.class);
        if (userModuleService != null) {
            return userModuleService.getUserToken();
        }
        return "";
    }

    public static String getUserAvatar() {
        IUserService userModuleService = ARouter.getInstance().navigation(IUserService.class);
        if (userModuleService != null) {
            return userModuleService.getUserAvatar();
        }
        return "";
    }

    public static boolean hasCopyright() {
        boolean hasCopyright = false;
        IDeviceService deviceService = ARouter.getInstance().navigation(IDeviceService.class);
        if (deviceService != null) {
            hasCopyright = deviceService.hasCopyright();
        }
        return hasCopyright;
    }

    public static void checkDeviceStatus(String appChannelCode) {
        checkDeviceStatus(null, appChannelCode);
    }


    public static void checkDeviceStatus(@Nullable Context context, String appChannelCode) {
        IDeviceService deviceService = ARouter.getInstance().navigation(IDeviceService.class);
        if (deviceService != null) {
            deviceService.checkDeviceStatus(context, appChannelCode);
        }
    }

    public static DevRegUserInfo getDeviceRegUserInfo() {
        IDeviceService deviceService = ARouter.getInstance().navigation(IDeviceService.class);
        if (deviceService != null) {
            return deviceService.getDeviceRegUserInfo();
        }

        return null;
    }

    public static String getDeviceInfo() {
        IDeviceService deviceService = ARouter.getInstance().navigation(IDeviceService.class);
        if (deviceService != null) {
            return deviceService.getDeviceInfo();
        }
        return "";
    }

    public static String getDeviceChannelCode() {
        IDeviceService deviceService = ARouter.getInstance().navigation(IDeviceService.class);
        if (deviceService != null) {
            return deviceService.getDeviceChannelCode();
        }
        return "";
    }

    public static String getDeviceChannelModes() {
        IDeviceService deviceService = ARouter.getInstance().navigation(IDeviceService.class);
        if (deviceService != null) {
            return deviceService.getDeviceSeverModes();
        }
        return "";
    }

    public static String getDeviceExpireDate() {
        IDeviceService deviceService = ARouter.getInstance().navigation(IDeviceService.class);
        if (deviceService != null) {
            return deviceService.getDeviceExpireDate();
        }
        return "";
    }

    public static String getAppVersionName() {
        IAppService appService = ARouter.getInstance().navigation(IAppService.class);
        if (appService != null) {
            return appService.getAppVersionName();
        }
        return "";
    }

    public static int getAppVersionCode() {
        IAppService appService = ARouter.getInstance().navigation(IAppService.class);
        if (appService != null) {
            return appService.getAppVersionCode();
        }
        return 0;
    }

    public static String getAppId() {
        IAppService appService = ARouter.getInstance().navigation(IAppService.class);
        if (appService != null) {
            return appService.getAppId();
        }
        return "";
    }

    public static void uploadFlightMonitor(String monitorInfo){
        IFlightMonitorService flightMonitorService=ARouter.getInstance().navigation(IFlightMonitorService.class);
        if(flightMonitorService!=null){
           flightMonitorService.upload(monitorInfo);
        }
    }
}
