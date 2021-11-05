package com.flycloud.autofly.control.logic;

import com.alibaba.android.arouter.launcher.ARouter;



public class RouteController {

    public static class Activity {
        public static final String App_Main = "/app/main";
        public static final String User_Login = "/user/login";
        public static final String User_Register = "/user/register";
        public static final String User_Edit = "/user/edit";
        public static final String Device_Register = "/device/register";
        public static final String Device_CheckRegister = "/device/check_register";
        public static final String FlightMonitor_Main = "/flightMonitor/main";
    }

    public static class Fragment {

    }

    public static class Service {
        public static final String App = "/app/service";
        public static final String User = "/user/service";
        public static final String Device = "/device/service";
        public static final String FlightMonitor = "/flightMonitor/service";
    }

    public static void gotoAppMainActivity() {
        ARouter.getInstance().build(Activity.App_Main).navigation();
    }

    public static void gotoLoginActivity() {
        ARouter.getInstance().build(Activity.User_Login).navigation();
    }

    public static void gotoLoginActivity(boolean isShowSkip) {
        ARouter.getInstance().build(Activity.User_Login).withBoolean("isShowSkip", isShowSkip).navigation();
    }

    public static void gotoUserRegisterActivity() {
        ARouter.getInstance().build(Activity.User_Register).navigation();
    }

    public static void gotoUserEditActivity() {
        ARouter.getInstance().build(Activity.User_Edit).navigation();
    }

    public static void gotoDeviceRegisterActivity() {
        ARouter.getInstance().build(Activity.Device_Register).navigation();
    }

    public static void gotoDeviceCheckRegisterActivity(String message) {
        ARouter.getInstance().build(Activity.Device_CheckRegister).withString("message", message).navigation();
    }

    public static void gotoFlightMonitorActivity() {
        ARouter.getInstance().build(Activity.FlightMonitor_Main).navigation();
    }
}
