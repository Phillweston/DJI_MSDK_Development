package com.ew.autofly.route;

import android.content.Context;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.ew.autofly.BuildConfig;
import com.flycloud.autofly.control.logic.RouteController;
import com.flycloud.autofly.control.service.IAppService;



@Route(path = RouteController.Service.App)
public class AppModuleService implements IAppService {

    private Context mContext;

    @Override
    public void init(Context context) {
        mContext=context;
    }

    @Override
    public int getAppVersionCode() {
        return BuildConfig.VERSION_CODE;
    }

    @Override
    public String getAppVersionName() {
        return BuildConfig.VERSION_NAME;
    }

    @Override
    public String getAppId() {
        return BuildConfig.APPLICATION_ID;
    }
}
