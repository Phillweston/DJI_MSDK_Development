package com.flycloud.autofly.control.service;

import com.alibaba.android.arouter.facade.template.IProvider;



public interface IAppService extends IProvider{

  
    int getAppVersionCode();

  
    String getAppVersionName();

  
    String getAppId();
}
