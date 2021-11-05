package com.flycloud.autofly.control.service;

import com.alibaba.android.arouter.facade.template.IProvider;



public interface IFlightMonitorService extends IProvider{

  
    void upload(String monitorInfo);
}
